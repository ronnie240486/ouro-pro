package com.ouropro.player.improvements;

import android.util.Base64;
import android.util.Xml;

import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.remote.APIService;
import com.ouropro.player.remote.RetroClass;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import org.xmlpull.v1.XmlPullParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Carrega EPG XMLTV quando a API curta/completa não retorna programação. */
public final class XmlTvEpgLoader {
    private static final ExecutorService PARSER = Executors.newSingleThreadExecutor();

    public interface Listener {
        void onLoaded(List<CatchUpEpg> programs);
        void onError(Throwable error);
    }

    private XmlTvEpgLoader() {
    }

    public static void load(String baseUrl, boolean allowHttp, String username, String password,
                            String channelId, String channelName, Listener listener) {
        try {
            APIService service = RetroClass.getAPIService(baseUrl, allowHttp);
            service.getEpgXml(username, password).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() == null) {
                        listener.onError(new IllegalStateException("XMLTV vazio"));
                        return;
                    }
                    ResponseBody body = response.body();
                    PARSER.execute(() -> {
                        try (InputStream input = body.byteStream()) {
                            List<CatchUpEpg> result = parse(input, channelId, channelName);
                            listener.onLoaded(result);
                        } catch (Throwable error) {
                            listener.onError(error);
                        } finally {
                            body.close();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    listener.onError(throwable);
                }
            });
        } catch (Throwable error) {
            listener.onError(error);
        }
    }

    private static List<CatchUpEpg> parse(InputStream input, String channelId, String channelName) throws Exception {
        ArrayList<CatchUpEpg> result = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(input, null);
        int event = parser.getEventType();
        String programmeChannel = null;
        String startRaw = null;
        String stopRaw = null;
        String title = null;
        String description = null;
        boolean inProgramme = false;
        boolean inTitle = false;
        boolean inDescription = false;
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                if ("programme".equalsIgnoreCase(tag)) {
                    inProgramme = true;
                    programmeChannel = parser.getAttributeValue(null, "channel");
                    startRaw = parser.getAttributeValue(null, "start");
                    stopRaw = parser.getAttributeValue(null, "stop");
                    title = "";
                    description = "";
                } else if (inProgramme && "title".equalsIgnoreCase(tag)) {
                    inTitle = true;
                } else if (inProgramme && ("desc".equalsIgnoreCase(tag) || "description".equalsIgnoreCase(tag))) {
                    inDescription = true;
                }
            } else if (event == XmlPullParser.TEXT) {
                if (inTitle) title = parser.getText();
                if (inDescription) description = parser.getText();
            } else if (event == XmlPullParser.END_TAG) {
                String tag = parser.getName();
                if ("title".equalsIgnoreCase(tag)) inTitle = false;
                if ("desc".equalsIgnoreCase(tag) || "description".equalsIgnoreCase(tag)) inDescription = false;
                if ("programme".equalsIgnoreCase(tag) && inProgramme) {
                    inProgramme = false;
                    if (matches(programmeChannel, channelId, channelName)) {
                        CatchUpEpg program = createProgram(programmeChannel, startRaw, stopRaw, title, description);
                        if (program != null) result.add(program);
                    }
                }
            }
            event = parser.next();
        }
        long now = System.currentTimeMillis();
        int firstCurrentOrNext = -1;
        for (int i = 0; i < result.size(); i++) {
            CatchUpEpg item = result.get(i);
            if (item.getStop_timestamp() * 1000L >= now) {
                firstCurrentOrNext = i;
                break;
            }
        }
        if (firstCurrentOrNext > 0) {
            return new ArrayList<>(result.subList(firstCurrentOrNext, result.size()));
        }
        return result;
    }

    private static boolean matches(String xmlChannel, String channelId, String channelName) {
        if (xmlChannel == null) return false;
        String xml = normalize(xmlChannel);
        if (!isBlank(channelId)) {
            for (String alias : channelId.split("\\|")) {
                if (!isBlank(alias) && xml.equals(normalize(alias))) return true;
            }
        }
        if (!isBlank(channelName)) {
            for (String alias : channelName.split("\\|")) {
                if (!isBlank(alias) && xml.equals(normalize(alias))) return true;
            }
        }
        return false;
    }

    private static CatchUpEpg createProgram(String channel, String startRaw, String stopRaw,
                                             String title, String description) {
        long start = parseXmlTvTime(startRaw);
        long stop = parseXmlTvTime(stopRaw);
        if (start <= 0L || stop <= start) return null;
        CatchUpEpg item = new CatchUpEpg();
        item.setChannel_id(channel == null ? "" : channel);
        item.setStart_timestamp(start / 1000L);
        item.setStop_timestamp(stop / 1000L);
        item.setStart(formatTime(start));
        item.setEnd(formatTime(stop));
        item.setTitle(encode(title));
        item.setDescription(encode(description));
        long now = System.currentTimeMillis();
        item.setNow_playing(now >= start && now < stop ? 1 : 0);
        return item;
    }

    private static long parseXmlTvTime(String value) {
        if (isBlank(value)) return 0L;
        String raw = value.trim();
        String[] patterns = {"yyyyMMddHHmmss Z", "yyyyMMddHHmmss", "yyyyMMddHHmm Z"};
        for (String pattern : patterns) {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ROOT);
            if (!pattern.contains("Z")) format.setTimeZone(TimeZone.getDefault());
            ParsePosition position = new ParsePosition(0);
            Date date = format.parse(raw, position);
            if (date != null && position.getIndex() > 0) return date.getTime();
        }
        return 0L;
    }

    private static String formatTime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(timestamp));
    }

    private static String encode(String value) {
        return Base64.encodeToString((value == null ? "" : value).getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

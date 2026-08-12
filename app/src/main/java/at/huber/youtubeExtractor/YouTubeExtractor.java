package at.huber.youtubeExtractor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.common.net.HttpHeaders;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public abstract class YouTubeExtractor extends AsyncTask<String, Void, SparseArray<YtFile>> {
    public static final /* synthetic */ int $r8$clinit = 0;
    private static final String CACHE_FILE_NAME = "decipher_js_funct";
    private static final SparseArray<Format> FORMAT_MAP;
    private static final String LOG_TAG = "YouTubeExtractor";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.98 Safari/537.36";
    private static String decipherFunctionName;
    private static String decipherFunctions;
    private static String decipherJsFileName;
    private final String cacheDirPath;
    private volatile String decipheredSignature;
    private final Condition jsExecuting;
    private final Lock lock;
    private final WeakReference<Context> refContext;
    private String videoID;
    private VideoMeta videoMeta;
    private static final Pattern patYouTubePageLink = Pattern.compile("(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)");
    private static final Pattern patYouTubeShortLink = Pattern.compile("(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)");
    private static final Pattern patPlayerResponse = Pattern.compile("var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\s*;");
    private static final Pattern patSigEncUrl = Pattern.compile("url=(.+?)(\\u0026|$)");
    private static final Pattern patSignature = Pattern.compile("s=(.+?)(\\u0026|$)");
    private static final Pattern patVariableFunction = Pattern.compile("([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\(");
    private static final Pattern patFunction = Pattern.compile("([{; =])([a-zA-Z$_][a-zA-Z0-9$]{0,2})\\(");
    private static final Pattern patDecryptionJsFile = Pattern.compile("\\\\/s\\\\/player\\\\/([^\"]+?)\\.js");
    private static final Pattern patDecryptionJsFileWithoutSlash = Pattern.compile("/s/player/([^\"]+?).js");
    private static final Pattern patSignatureDecFunction = Pattern.compile("(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{1,4})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)");

    static {
        SparseArray<Format> sparseArray = new SparseArray<>();
        FORMAT_MAP = sparseArray;
        Format.VCodec vCodec = Format.VCodec.MPEG4;
        Format.ACodec aCodec = Format.ACodec.AAC;
        sparseArray.put(17, new Format(17, "3gp", 144, 24));
        sparseArray.put(36, new Format(36, "3gp", PsExtractor.VIDEO_STREAM_MASK, 32));
        Format.VCodec vCodec2 = Format.VCodec.H263;
        Format.ACodec aCodec2 = Format.ACodec.MP3;
        sparseArray.put(5, new Format(5, "flv", PsExtractor.VIDEO_STREAM_MASK, 64));
        Format.VCodec vCodec3 = Format.VCodec.VP8;
        Format.ACodec aCodec3 = Format.ACodec.VORBIS;
        sparseArray.put(43, new Format(43, "webm", 360, 128));
        Format.VCodec vCodec4 = Format.VCodec.H264;
        sparseArray.put(18, new Format(18, "mp4", 360, 96));
        sparseArray.put(22, new Format(22, "mp4", 720, PsExtractor.AUDIO_STREAM));
        Format.ACodec aCodec4 = Format.ACodec.NONE;
        sparseArray.put(160, new Format(160, "mp4", 144));
        sparseArray.put(133, new Format(133, "mp4", PsExtractor.VIDEO_STREAM_MASK));
        sparseArray.put(TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, new Format(TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, "mp4", 360));
        sparseArray.put(TsExtractor.TS_STREAM_TYPE_E_AC3, new Format(TsExtractor.TS_STREAM_TYPE_E_AC3, "mp4", 480));
        sparseArray.put(136, new Format(136, "mp4", 720));
        sparseArray.put(137, new Format(137, "mp4", 1080));
        sparseArray.put(264, new Format(264, "mp4", 1440));
        sparseArray.put(266, new Format(266, "mp4", 2160));
        sparseArray.put(298, new Format(298, "mp4", 720, vCodec4, 60, aCodec4, true));
        sparseArray.put(299, new Format(299, "mp4", 1080, vCodec4, 60, aCodec4, true));
        Format.VCodec vCodec5 = Format.VCodec.NONE;
        sparseArray.put(140, new Format(140, "m4a", vCodec5, aCodec, 128, true));
        sparseArray.put(141, new Format(141, "m4a", vCodec5, aCodec, 256, true));
        sparseArray.put(256, new Format(256, "m4a", vCodec5, aCodec, PsExtractor.AUDIO_STREAM, true));
        sparseArray.put(258, new Format(258, "m4a", vCodec5, aCodec, 384, true));
        Format.VCodec vCodec6 = Format.VCodec.VP9;
        sparseArray.put(278, new Format(278, "webm", 144));
        sparseArray.put(242, new Format(242, "webm", PsExtractor.VIDEO_STREAM_MASK));
        sparseArray.put(243, new Format(243, "webm", 360));
        sparseArray.put(244, new Format(244, "webm", 480));
        sparseArray.put(247, new Format(247, "webm", 720));
        sparseArray.put(248, new Format(248, "webm", 1080));
        sparseArray.put(271, new Format(271, "webm", 1440));
        sparseArray.put(313, new Format(313, "webm", 2160));
        sparseArray.put(302, new Format(302, "webm", 720, vCodec6, 60, aCodec4, true));
        sparseArray.put(308, new Format(308, "webm", 1440, vCodec6, 60, aCodec4, true));
        sparseArray.put(303, new Format(303, "webm", 1080, vCodec6, 60, aCodec4, true));
        sparseArray.put(315, new Format(315, "webm", 2160, vCodec6, 60, aCodec4, true));
        sparseArray.put(171, new Format(171, "webm", vCodec5, aCodec3, 128, true));
        Format.ACodec aCodec5 = Format.ACodec.OPUS;
        sparseArray.put(249, new Format(249, "webm", vCodec5, aCodec5, 48, true));
        sparseArray.put(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, new Format(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, "webm", vCodec5, aCodec5, 64, true));
        sparseArray.put(251, new Format(251, "webm", vCodec5, aCodec5, 160, true));
        sparseArray.put(91, new Format(91, 144, 48));
        sparseArray.put(92, new Format(92, PsExtractor.VIDEO_STREAM_MASK, 48));
        sparseArray.put(93, new Format(93, 360, 128));
        sparseArray.put(94, new Format(94, 480, 128));
        sparseArray.put(95, new Format(95, 720, 256));
        sparseArray.put(96, new Format(96, 1080, 256));
    }

    public YouTubeExtractor(@NonNull Context context) {
        ReentrantLock reentrantLock = new ReentrantLock();
        this.lock = reentrantLock;
        this.jsExecuting = reentrantLock.newCondition();
        this.refContext = new WeakReference<>(context);
        this.cacheDirPath = context.getCacheDir().getAbsolutePath();
    }

    private boolean decipherSignature(SparseArray<String> sparseArray) throws Throwable {
        String string;
        if (decipherFunctionName == null || decipherFunctions == null) {
            StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("https://youtube.com");
            sbM.append(decipherJsFileName);
            BufferedReader bufferedReader = null;
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(sbM.toString()).openConnection();
            httpURLConnection.setRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT);
            try {
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                try {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = bufferedReader2.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                        sb.append(" ");
                    }
                    String string2 = sb.toString();
                    bufferedReader2.close();
                    httpURLConnection.disconnect();
                    Matcher matcher = patSignatureDecFunction.matcher(string2);
                    if (!matcher.find()) {
                        return false;
                    }
                    decipherFunctionName = matcher.group(1);
                    StringBuilder sbM2 = Insets$$ExternalSyntheticOutline0.m("(var |\\s|,|;)");
                    sbM2.append(decipherFunctionName.replace("$", "\\$"));
                    sbM2.append("(=function\\((.{1,3})\\)\\{)");
                    Matcher matcher2 = Pattern.compile(sbM2.toString()).matcher(string2);
                    if (matcher2.find()) {
                        StringBuilder sbM3 = Insets$$ExternalSyntheticOutline0.m("var ");
                        sbM3.append(decipherFunctionName);
                        sbM3.append(matcher2.group(2));
                        string = sbM3.toString();
                    } else {
                        StringBuilder sbM4 = Insets$$ExternalSyntheticOutline0.m("function ");
                        sbM4.append(decipherFunctionName.replace("$", "\\$"));
                        sbM4.append("(\\((.{1,3})\\)\\{)");
                        matcher2 = Pattern.compile(sbM4.toString()).matcher(string2);
                        if (!matcher2.find()) {
                            return false;
                        }
                        StringBuilder sbM5 = Insets$$ExternalSyntheticOutline0.m("function ");
                        sbM5.append(decipherFunctionName);
                        sbM5.append(matcher2.group(2));
                        string = sbM5.toString();
                    }
                    int iEnd = matcher2.end();
                    int i = 1;
                    for (int i2 = iEnd; i2 < string2.length(); i2++) {
                        if (i == 0 && iEnd + 5 < i2) {
                            StringBuilder sbM6 = Insets$$ExternalSyntheticOutline0.m(string);
                            sbM6.append(string2.substring(iEnd, i2));
                            sbM6.append(";");
                            string = sbM6.toString();
                            break;
                        }
                        if (string2.charAt(i2) == '{') {
                            i++;
                        } else if (string2.charAt(i2) == '}') {
                            i--;
                        }
                    }
                    decipherFunctions = string;
                    Matcher matcher3 = patVariableFunction.matcher(string);
                    while (matcher3.find()) {
                        StringBuilder sbM7 = Insets$$ExternalSyntheticOutline0.m("var ");
                        sbM7.append(matcher3.group(2));
                        sbM7.append("={");
                        String string3 = sbM7.toString();
                        if (!decipherFunctions.contains(string3)) {
                            int length = string3.length() + string2.indexOf(string3);
                            int i3 = 1;
                            for (int i4 = length; i4 < string2.length(); i4++) {
                                if (i3 == 0) {
                                    decipherFunctions += string3 + string2.substring(length, i4) + ";";
                                    break;
                                }
                                if (string2.charAt(i4) == '{') {
                                    i3++;
                                } else if (string2.charAt(i4) == '}') {
                                    i3--;
                                }
                            }
                        }
                    }
                    Matcher matcher4 = patFunction.matcher(string);
                    while (matcher4.find()) {
                        StringBuilder sbM8 = Insets$$ExternalSyntheticOutline0.m("function ");
                        sbM8.append(matcher4.group(2));
                        sbM8.append("(");
                        String string4 = sbM8.toString();
                        if (!decipherFunctions.contains(string4)) {
                            int length2 = string4.length() + string2.indexOf(string4);
                            int i5 = 0;
                            for (int i6 = length2; i6 < string2.length(); i6++) {
                                if (i5 == 0 && length2 + 5 < i6) {
                                    decipherFunctions += string4 + string2.substring(length2, i6) + ";";
                                    break;
                                }
                                if (string2.charAt(i6) == '{') {
                                    i5++;
                                } else if (string2.charAt(i6) == '}') {
                                    i5--;
                                }
                            }
                        }
                    }
                    decipherViaWebView(sparseArray);
                    writeDeciperFunctToChache();
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = bufferedReader2;
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    httpURLConnection.disconnect();
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } else {
            decipherViaWebView(sparseArray);
        }
        return true;
    }

    private void decipherViaWebView(SparseArray<String> sparseArray) {
        final Context context = this.refContext.get();
        if (context == null) {
            return;
        }
        final StringBuilder sb = new StringBuilder(Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), decipherFunctions, " function decipher("));
        sb.append("){return ");
        for (int i = 0; i < sparseArray.size(); i++) {
            int iKeyAt = sparseArray.keyAt(i);
            if (i < sparseArray.size() - 1) {
                sb.append(decipherFunctionName);
                sb.append("('");
                sb.append(sparseArray.get(iKeyAt));
                sb.append("')+\"\\n\"+");
            } else {
                sb.append(decipherFunctionName);
                sb.append("('");
                sb.append(sparseArray.get(iKeyAt));
                sb.append("')");
            }
        }
        sb.append("};decipher();");
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: at.huber.youtubeExtractor.YouTubeExtractor.1
            @Override // java.lang.Runnable
            public void run() {
                new JsEvaluator(context).evaluate(sb.toString(), new JsCallback() { // from class: at.huber.youtubeExtractor.YouTubeExtractor.1.1
                    @Override // com.evgenii.jsevaluator.interfaces.JsCallback
                    public void onError(String str) {
                        YouTubeExtractor.this.lock.lock();
                        try {
                            int i2 = YouTubeExtractor.$r8$clinit;
                            YouTubeExtractor.this.jsExecuting.signal();
                        } finally {
                            YouTubeExtractor.this.lock.unlock();
                        }
                    }

                    @Override // com.evgenii.jsevaluator.interfaces.JsCallback
                    public void onResult(String str) {
                        YouTubeExtractor.this.lock.lock();
                        try {
                            YouTubeExtractor.this.decipheredSignature = str;
                            YouTubeExtractor.this.jsExecuting.signal();
                        } finally {
                            YouTubeExtractor.this.lock.unlock();
                        }
                    }
                });
            }
        });
    }

    private SparseArray<YtFile> getStreamUrls() throws Throwable {
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;
        String str;
        String str2;
        String str3;
        String str4;
        JSONArray jSONArray;
        String str5;
        String str6;
        SparseArray<String> sparseArray = new SparseArray<>();
        SparseArray<YtFile> sparseArray2 = new SparseArray<>();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("https://youtube.com/watch?v=");
        sbM.append(this.videoID);
        try {
            httpURLConnection = (HttpURLConnection) new URL(sbM.toString()).openConnection();
            try {
                httpURLConnection.setRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT);
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                try {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    String string = sb.toString();
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    Matcher matcher = patPlayerResponse.matcher(string);
                    if (matcher.find()) {
                        JSONObject jSONObject = new JSONObject(matcher.group(1));
                        JSONObject jSONObject2 = jSONObject.getJSONObject("streamingData");
                        JSONArray jSONArray2 = jSONObject2.getJSONArray("formats");
                        int i = 0;
                        while (true) {
                            str3 = "&";
                            str4 = "\\u0026";
                            str = string;
                            if (i >= jSONArray2.length()) {
                                break;
                            }
                            JSONObject jSONObject3 = jSONArray2.getJSONObject(i);
                            String strOptString = jSONObject3.optString("type");
                            if (strOptString == null || !strOptString.equals("FORMAT_STREAM_TYPE_OTF")) {
                                int i2 = jSONObject3.getInt("itag");
                                SparseArray<Format> sparseArray3 = FORMAT_MAP;
                                if (sparseArray3.get(i2) != null) {
                                    if (jSONObject3.has(ImagesContract.URL)) {
                                        sparseArray2.append(i2, new YtFile(sparseArray3.get(i2), jSONObject3.getString(ImagesContract.URL).replace("\\u0026", "&")));
                                    } else if (jSONObject3.has("signatureCipher")) {
                                        Matcher matcher2 = patSigEncUrl.matcher(jSONObject3.getString("signatureCipher"));
                                        Matcher matcher3 = patSignature.matcher(jSONObject3.getString("signatureCipher"));
                                        if (matcher2.find() && matcher3.find()) {
                                            String strDecode = URLDecoder.decode(matcher2.group(1), "UTF-8");
                                            String strDecode2 = URLDecoder.decode(matcher3.group(1), "UTF-8");
                                            sparseArray2.append(i2, new YtFile(sparseArray3.get(i2), strDecode));
                                            sparseArray.append(i2, strDecode2);
                                        }
                                    }
                                }
                            }
                            i++;
                            string = str;
                        }
                        JSONArray jSONArray3 = jSONObject2.getJSONArray("adaptiveFormats");
                        int i3 = 0;
                        while (i3 < jSONArray3.length()) {
                            JSONObject jSONObject4 = jSONArray3.getJSONObject(i3);
                            String strOptString2 = jSONObject4.optString("type");
                            if (strOptString2 == null || !strOptString2.equals("FORMAT_STREAM_TYPE_OTF")) {
                                int i4 = jSONObject4.getInt("itag");
                                jSONArray = jSONArray3;
                                SparseArray<Format> sparseArray4 = FORMAT_MAP;
                                if (sparseArray4.get(i4) != null) {
                                    if (jSONObject4.has(ImagesContract.URL)) {
                                        str5 = str3;
                                        sparseArray2.append(i4, new YtFile(sparseArray4.get(i4), jSONObject4.getString(ImagesContract.URL).replace(str4, str3)));
                                    } else {
                                        str5 = str3;
                                        if (jSONObject4.has("signatureCipher")) {
                                            str6 = str4;
                                            Matcher matcher4 = patSigEncUrl.matcher(jSONObject4.getString("signatureCipher"));
                                            Matcher matcher5 = patSignature.matcher(jSONObject4.getString("signatureCipher"));
                                            if (matcher4.find() && matcher5.find()) {
                                                String strDecode3 = URLDecoder.decode(matcher4.group(1), "UTF-8");
                                                String strDecode4 = URLDecoder.decode(matcher5.group(1), "UTF-8");
                                                sparseArray2.append(i4, new YtFile(sparseArray4.get(i4), strDecode3));
                                                sparseArray.append(i4, strDecode4);
                                            }
                                        }
                                        i3++;
                                        jSONArray3 = jSONArray;
                                        str3 = str5;
                                        str4 = str6;
                                    }
                                }
                                str6 = str4;
                                i3++;
                                jSONArray3 = jSONArray;
                                str3 = str5;
                                str4 = str6;
                            } else {
                                jSONArray = jSONArray3;
                            }
                            str5 = str3;
                            str6 = str4;
                            i3++;
                            jSONArray3 = jSONArray;
                            str3 = str5;
                            str4 = str6;
                        }
                        JSONObject jSONObject5 = jSONObject.getJSONObject("videoDetails");
                        this.videoMeta = new VideoMeta(jSONObject5.getString("videoId"), jSONObject5.getString("title"), jSONObject5.getString("author"), jSONObject5.getString("channelId"), Long.parseLong(jSONObject5.getString("lengthSeconds")), Long.parseLong(jSONObject5.getString("viewCount")), jSONObject5.getBoolean("isLiveContent"), jSONObject5.getString("shortDescription"));
                    } else {
                        str = string;
                        Log.d(LOG_TAG, "ytPlayerResponse was not found");
                    }
                    if (sparseArray.size() > 0) {
                        if (decipherJsFileName == null || decipherFunctions == null || decipherFunctionName == null) {
                            readDecipherFunctFromCache();
                        }
                        String str7 = str;
                        Matcher matcher6 = patDecryptionJsFile.matcher(str7);
                        if (!matcher6.find()) {
                            matcher6 = patDecryptionJsFileWithoutSlash.matcher(str7);
                        }
                        if (matcher6.find()) {
                            String strReplace = matcher6.group(0).replace("\\/", "/");
                            String str8 = decipherJsFileName;
                            if (str8 == null || !str8.equals(strReplace)) {
                                str2 = null;
                                decipherFunctions = null;
                                decipherFunctionName = null;
                            } else {
                                str2 = null;
                            }
                            decipherJsFileName = strReplace;
                        } else {
                            str2 = null;
                        }
                        this.decipheredSignature = str2;
                        if (decipherSignature(sparseArray)) {
                            this.lock.lock();
                            try {
                                this.jsExecuting.await(7L, TimeUnit.SECONDS);
                                this.lock.unlock();
                            } catch (Throwable th) {
                                this.lock.unlock();
                                throw th;
                            }
                        }
                        String str9 = this.decipheredSignature;
                        if (str9 == null) {
                            return null;
                        }
                        String[] strArrSplit = str9.split("\n");
                        for (int i5 = 0; i5 < sparseArray.size() && i5 < strArrSplit.length; i5++) {
                            int iKeyAt = sparseArray.keyAt(i5);
                            StringBuilder sbM13m = Insets$$ExternalSyntheticOutline0.m13m(sparseArray2.get(iKeyAt).getUrl(), "&sig=");
                            sbM13m.append(strArrSplit[i5]);
                            sparseArray2.put(iKeyAt, new YtFile(FORMAT_MAP.get(iKeyAt), sbM13m.toString()));
                        }
                    }
                    if (sparseArray2.size() == 0) {
                        return null;
                    }
                    return sparseArray2;
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = null;
            }
        } catch (Throwable th4) {
            th = th4;
            httpURLConnection = null;
            bufferedReader = null;
        }
    }

    private void readDecipherFunctFromCache() throws Throwable {
        BufferedReader bufferedReader;
        File file = new File(Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.cacheDirPath, "/", CACHE_FILE_NAME));
        if (!file.exists() || System.currentTimeMillis() - file.lastModified() >= 1209600000) {
            return;
        }
        BufferedReader bufferedReader2 = null;
        try {
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                    try {
                        decipherJsFileName = bufferedReader.readLine();
                        decipherFunctionName = bufferedReader.readLine();
                        decipherFunctions = bufferedReader.readLine();
                        bufferedReader.close();
                    } catch (Exception e) {
                        e = e;
                        bufferedReader2 = bufferedReader;
                        e.printStackTrace();
                        if (bufferedReader2 == null) {
                        } else {
                            bufferedReader2.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            } catch (Exception e4) {
                e = e4;
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedReader = bufferedReader2;
        }
    }

    private void writeDeciperFunctToChache() throws Throwable {
        BufferedWriter bufferedWriter;
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.cacheDirPath, "/", CACHE_FILE_NAME))), "UTF-8"));
                    try {
                        bufferedWriter.write(decipherJsFileName + "\n");
                        bufferedWriter.write(decipherFunctionName + "\n");
                        bufferedWriter.write(decipherFunctions);
                        bufferedWriter.close();
                    } catch (Exception e) {
                        e = e;
                        bufferedWriter2 = bufferedWriter;
                        e.printStackTrace();
                        if (bufferedWriter2 == null) {
                        } else {
                            bufferedWriter2.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (bufferedWriter != null) {
                            try {
                                bufferedWriter.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedWriter = bufferedWriter2;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    @Override // android.os.AsyncTask
    public final SparseArray<YtFile> doInBackground(String[] strArr) {
        this.videoID = null;
        String str = strArr[0];
        if (str == null) {
            return null;
        }
        Matcher matcher = patYouTubePageLink.matcher(str);
        if (matcher.find()) {
            this.videoID = matcher.group(3);
        } else {
            Matcher matcher2 = patYouTubeShortLink.matcher(str);
            if (matcher2.find()) {
                this.videoID = matcher2.group(3);
            } else if (str.matches("\\p{Graph}+?")) {
                this.videoID = str;
            }
        }
        if (this.videoID == null) {
            Log.e(LOG_TAG, "Wrong YouTube link format");
            return null;
        }
        try {
            return getStreamUrls();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Extraction failed", e);
            return null;
        }
    }

    public void extract(String str, boolean z, boolean z2) {
        execute(str);
    }

    public abstract void onExtractionComplete(@Nullable SparseArray<YtFile> sparseArray, @Nullable VideoMeta videoMeta);

    @Override // android.os.AsyncTask
    public final void onPostExecute(SparseArray<YtFile> sparseArray) {
        onExtractionComplete(sparseArray, this.videoMeta);
    }

    public void setDefaultHttpProtocol(boolean z) {
    }

    public void setIncludeWebM(boolean z) {
    }

    public void setParseDashManifest(boolean z) {
    }

    public void extract(String str) {
        execute(str);
    }
}

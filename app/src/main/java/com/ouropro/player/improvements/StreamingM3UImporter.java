package com.ouropro.player.improvements;

import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iptv.m3u.parser.M3UItem;

/** Leitor M3U incremental: entrega lotes sem aguardar os 329 mil itens. */
public final class StreamingM3UImporter {
    private static final int BATCH_SIZE = 1200;
    private static final Pattern ATTRIBUTE = Pattern.compile("([A-Za-z0-9_-]+)\\s*=\\s*(?:\\\"([^\\\"]*)\\\"|'([^']*)'|([^\\s,]+))");

    public interface Listener {
        void onBatch(List<M3UItem> batch);
        void onComplete();
        void onError(Exception error);
    }

    public void execute(String playlistUrl, Listener listener) {
        try {
            File file = cachedFile(playlistUrl);
            boolean cached = file.exists() && isToday(file);
            InputStream source;
            FileOutputStream copy = null;
            if (cached) {
                source = new FileInputStream(file);
            } else {
                URLConnection connection = new URL(playlistUrl).openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                source = connection.getInputStream();
                copy = new FileOutputStream(file, false);
                source = new TeeInputStream(source, copy);
            }
            try {
                readItems(source, listener);
            } finally {
                try {
                    source.close();
                } finally {
                    if (copy != null) {
                        copy.close();
                    }
                }
            }
            LTVApp.instance.setM3uDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date()));
            listener.onComplete();
        } catch (Exception error) {
            listener.onError(error);
        }
    }

    private void readItems(InputStream source, Listener listener) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(source, StandardCharsets.UTF_8), 64 * 1024);
        String line;
        String extinf = null;
        ArrayList<M3UItem> batch = new ArrayList<>(BATCH_SIZE);
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#EXTM3U") || line.startsWith("#EXTVLCOPT") || line.startsWith("#KODIPROP")) {
                continue;
            }
            if (line.startsWith("#EXTINF")) {
                extinf = line;
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            if (extinf == null) {
                continue;
            }
            M3UItem item = parseItem(extinf, line);
            extinf = null;
            if (item != null) {
                batch.add(item);
                if (batch.size() >= BATCH_SIZE) {
                    listener.onBatch(new ArrayList<>(batch));
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) {
            listener.onBatch(batch);
        }
    }

    private M3UItem parseItem(String extinf, String streamUrl) {
        try {
            M3UItem item = new M3UItem();
            Map<String, String> attrs = attributes(extinf);
            String name = attr(attrs, "tvg-name", "channel_name");
            int comma = extinf.indexOf(',');
            if (name == null || name.trim().isEmpty()) {
                name = comma >= 0 ? extinf.substring(comma + 1).trim() : "";
            }
            String duration = extinf.substring(7, comma >= 0 ? comma : extinf.length()).trim();
            item.setChannelID(attr(attrs, "tvg-id", "id"));
            item.setChannelName(name);
            item.setDuration(parseInt(duration));
            item.setLogoURL(attr(attrs, "tvg-logo", "logo"));
            item.setGroupTitle(attr(attrs, "group-title", "group"));
            item.setType(attr(attrs, "type"));
            item.setDLNAExtras(attr(attrs, "dlna_extras"));
            item.setPlugin(attr(attrs, "plugin"));
            item.setStreamURL(streamUrl);
            return item;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Map<String, String> attributes(String line) {
        Map<String, String> result = new HashMap<>();
        Matcher matcher = ATTRIBUTE.matcher(line);
        while (matcher.find()) {
            String value = matcher.group(2);
            if (value == null) value = matcher.group(3);
            if (value == null) value = matcher.group(4);
            result.put(matcher.group(1).toLowerCase(Locale.ROOT), value == null ? "" : value);
        }
        return result;
    }

    private String attr(Map<String, String> attrs, String... names) {
        for (String name : names) {
            String value = attrs.get(name.toLowerCase(Locale.ROOT));
            if (value != null && !value.isEmpty()) return value;
        }
        return "";
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return -1;
        }
    }

    private File cachedFile(String playlistUrl) {
        return new File(LTVApp.getInstance().getExternalFilesDir(null), Utils.getUserId(playlistUrl) + ".m3u");
    }

    private boolean isToday(File file) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date(file.lastModified()));
        return date.equals(new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date()));
    }

    private static final class TeeInputStream extends FilterInputStream {
        private final FileOutputStream copy;
        TeeInputStream(InputStream input, FileOutputStream copy) {
            super(input);
            this.copy = copy;
        }
        @Override
        public int read() throws IOException {
            int value = super.read();
            if (value >= 0) copy.write(value);
            return value;
        }
        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int count = super.read(buffer, offset, length);
            if (count > 0) copy.write(buffer, offset, count);
            return count;
        }
    }
}

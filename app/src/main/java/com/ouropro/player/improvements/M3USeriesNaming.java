package com.ouropro.player.improvements;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iptv.m3u.parser.M3UItem;

/** Regras tolerantes para listas M3U de séries, sem depender de um único padrão de provedor. */
public final class M3USeriesNaming {
    private static final Pattern SEASON_EPISODE = Pattern.compile("(?i)(?:^|[\\s._\\-\\[\\(])(?:(?:s|t)\\s*0*(\\d{1,2})\\s*(?:e|ep|x)\\s*0*(\\d{1,3})|(?:season|temporada)\\s*0*(\\d{1,2})\\s*(?:episode|epis[oó]dio|ep)\\s*0*(\\d{1,3})|(?:episode|epis[oó]dio|ep)\\s*0*(\\d{1,3}))(?:$|[\\s._\\-\\]\\)])");
    private static final Pattern X_EPISODE = Pattern.compile("(?i)(?:^|[\\s._\\-\\[\\(])\\d{1,2}\\s*[x×]\\s*\\d{1,3}(?:$|[\\s._\\-\\]\\)])");

    private M3USeriesNaming() {
    }

    public static boolean isSeriesItem(M3UItem item) {
        if (item == null) {
            return false;
        }
        String url = lower(item.getStreamURL());
        String group = lower(item.getGroupTitle());
        String title = item.getChannelName() == null ? "" : item.getChannelName();
        if (url.contains("/series/") || url.contains("/series?") || url.contains("=series")) {
            return true;
        }
        if (group.contains("series") || group.contains("série") || group.contains("season") || group.contains("temporada") || group.contains("episode") || group.contains("episódio") || group.contains("anime") || group.contains("novela")) {
            return true;
        }
        return hasEpisodeMarker(title);
    }

    public static boolean hasEpisodeMarker(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        return SEASON_EPISODE.matcher(title).find() || X_EPISODE.matcher(title).find();
    }

    public static String seriesName(String title) {
        if (title == null) {
            return "";
        }
        String normalized = title.replace('_', ' ').replace('.', ' ').replaceAll("\\s+", " ").trim();
        Matcher matcher = SEASON_EPISODE.matcher(normalized);
        int cut = matcher.find() ? matcher.start() : -1;
        if (cut < 0) {
            Matcher xMatcher = X_EPISODE.matcher(normalized);
            cut = xMatcher.find() ? xMatcher.start() : -1;
        }
        String result;
        if (cut > 0) {
            result = normalized.substring(0, cut);
        } else if (cut == 0) {
            Matcher marker = SEASON_EPISODE.matcher(normalized);
            if (marker.find() && marker.end() < normalized.length()) {
                result = normalized.substring(marker.end());
            } else {
                result = normalized;
            }
        } else {
            result = normalized;
        }
        result = result.replaceAll("^[\\s\\-–—|:]+|[\\s\\-–—|:]+$", "").trim();
        return result.isEmpty() ? normalized : result;
    }

    public static String seasonName(String title) {
        if (title == null) {
            return "";
        }
        Matcher matcher = SEASON_EPISODE.matcher(title);
        if (matcher.find()) {
            String token = matcher.group().trim().replaceAll("^[^A-Za-z0-9]+|[^A-Za-z0-9]+$", "");
            return seriesName(title) + " " + token;
        }
        return seriesName(title);
    }

    private static String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}

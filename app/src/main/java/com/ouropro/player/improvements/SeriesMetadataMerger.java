package com.ouropro.player.improvements;

import com.ouropro.player.models.SeriesModel;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Combina a identidade da M3U com metadados oficiais do catálogo Xtream. */
public final class SeriesMetadataMerger {
    private SeriesMetadataMerger() {
    }

    public static List<SeriesModel> merge(List<SeriesModel> m3uSeries, List<SeriesModel> officialSeries) {
        Map<String, SeriesModel> exact = new HashMap<>();
        Map<String, SeriesModel> byName = new HashMap<>();
        Map<String, SeriesModel> byId = new HashMap<>();
        if (officialSeries != null) {
            for (SeriesModel official : officialSeries) {
                if (official == null || empty(official.getName())) {
                    continue;
                }
                String nameKey = normalize(official.getName());
                String categoryKey = normalize(official.getCategory_name());
                if (!nameKey.isEmpty()) {
                    byName.putIfAbsent(nameKey, official);
                    exact.putIfAbsent(categoryKey + "|" + nameKey, official);
                }
                if (!empty(official.getSeries_id())) {
                    byId.putIfAbsent(official.getSeries_id().trim(), official);
                }
            }
        }

        ArrayList<SeriesModel> result = new ArrayList<>();
        if (m3uSeries == null) {
            return result;
        }
        for (SeriesModel fromM3u : m3uSeries) {
            if (fromM3u == null) {
                continue;
            }
            SeriesModel official = null;
            if (!empty(fromM3u.getSeries_id())) {
                official = byId.get(fromM3u.getSeries_id().trim());
            }
            if (official == null) {
                official = exact.get(normalize(fromM3u.getCategory_name()) + "|" + normalize(fromM3u.getName()));
            }
            if (official == null) {
                official = byName.get(normalize(fromM3u.getName()));
            }
            result.add(copyWithOfficialMetadata(fromM3u, official));
        }
        return result;
    }

    private static SeriesModel copyWithOfficialMetadata(SeriesModel base, SeriesModel official) {
        SeriesModel result = new SeriesModel();
        result.setName(base.getName());
        result.setCategory_name(base.getCategory_name());
        result.setStream_icon(firstNonEmpty(official == null ? "" : official.getStream_icon(), base.getStream_icon()));
        result.setSeries_id(firstNonEmpty(official == null ? "" : official.getSeries_id(), base.getSeries_id()));
        if (official != null) {
            result.setCategory_id(official.getCategory_id());
            result.setPlot(official.getPlot());
            result.setGenre(official.getGenre());
            result.setCast(official.getCast());
            result.setDirector(official.getDirector());
            result.setReleaseDate(official.getReleaseDate());
            result.setRating(official.getRating() <= 0 ? "" : String.valueOf(official.getRating() * 2.0f));
            result.setRating_5based(official.getRating_5based());
            result.setLast_modified(official.getLast_modified());
            result.setYoutube(official.getYoutube());
            result.tmdb = official.tmdb;
        }
        return result;
    }

    private static String firstNonEmpty(String preferred, String fallback) {
        return empty(preferred) ? (fallback == null ? "" : fallback) : preferred;
    }

    private static boolean empty(String value) {
        return value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim());
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
        return normalized;
    }
}

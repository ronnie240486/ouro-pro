package com.ouropro.player.improvements;

import com.ouropro.player.models.EpisodeModel;
import com.ouropro.player.models.SeriesModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Agrupa episódios M3U em séries sem tocar no Realm ou na thread da interface. */
public final class M3USeriesRebuilder {
    private M3USeriesRebuilder() {
    }

    public static List<SeriesModel> build(List<EpisodeModel> episodes) {
        Map<String, SeriesModel> byName = new LinkedHashMap<>();
        if (episodes == null) {
            return new ArrayList<>();
        }
        for (EpisodeModel episode : episodes) {
            if (episode == null || episode.getSeries_name() == null || episode.getSeries_name().trim().isEmpty()) {
                continue;
            }
            String name = episode.getSeries_name().trim();
            String key = name.toLowerCase(Locale.ROOT);
            SeriesModel series = byName.get(key);
            if (series == null) {
                series = new SeriesModel();
                series.setName(name);
                series.setCategory_name(episode.getCategory_name() == null || episode.getCategory_name().trim().isEmpty() ? "All" : episode.getCategory_name());
                series.setStream_icon(episode.getStream_icon());
                byName.put(key, series);
            } else if ((series.getStream_icon() == null || series.getStream_icon().isEmpty()) && episode.getStream_icon() != null) {
                series.setStream_icon(episode.getStream_icon());
            }
        }
        return new ArrayList<>(byName.values());
    }
}

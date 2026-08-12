package com.ouropro.player.improvements;

import com.ouropro.player.models.SeriesModel;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Recupera capas próprias de séries a partir do catálogo Xtream sem substituir
 * categorias ou apagar os registros criados pela playlist M3U.
 */
public final class SeriesPosterRepair {
    private SeriesPosterRepair() {
    }

    public static int apply(Realm realm, List<SeriesModel> catalog) {
        if (realm == null || catalog == null || catalog.isEmpty()) {
            return 0;
        }
        final Map<String, String> posters = new HashMap<>();
        for (SeriesModel model : catalog) {
            if (model == null || isBlank(model.getName()) || isBlank(model.getStream_icon())) {
                continue;
            }
            posters.put(key(model.getName()), model.getStream_icon().trim());
        }
        if (posters.isEmpty()) {
            return 0;
        }

        final int[] updated = {0};
        realm.executeTransaction(transactionRealm -> {
            RealmResults<SeriesModel> localSeries = transactionRealm.where(SeriesModel.class).findAll();
            for (SeriesModel local : localSeries) {
                String poster = posters.get(key(local.getName()));
                if (!isBlank(poster) && !poster.equals(local.getStream_icon())) {
                    local.setStream_icon(poster);
                    updated[0]++;
                }
            }
        });
        return updated[0];
    }

    /** Normaliza apenas para comparação; o título salvo no card nunca é alterado. */
    public static String key(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("(?i)\\b(?:s|t)\\s*0*\\d{1,2}\\s*e\\s*0*\\d{1,3}\\b", " ")
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
        return normalized.replaceAll("\\s+", " ");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim());
    }
}

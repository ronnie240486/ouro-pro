package com.ouropro.player.improvements;

import com.ouropro.player.models.SeriesModel;
import com.ouropro.player.models.EpisodeModel;

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
        if (realm == null) {
            return 0;
        }
        final Map<String, String> posters = new HashMap<>();
        if (catalog != null) for (SeriesModel model : catalog) {
            if (model == null || isBlank(model.getName()) || isBlank(model.getStream_icon())) {
                continue;
            }
            posters.putIfAbsent(key(model.getName()), model.getStream_icon().trim());
        }
        // Para listas M3U, a capa original do episódio é a fonte de verdade.
        final Map<String, String> originalM3UPosters = new HashMap<>();
        final Map<String, String> uniqueNamePosters = new HashMap<>();
        for (EpisodeModel episode : realm.where(EpisodeModel.class).findAll()) {
            if (episode == null || isBlank(episode.getSeries_name()) || isBlank(episode.getStream_icon())) {
                continue;
            }
            String nameKey = key(episode.getSeries_name());
            String categoryKey = key(episode.getCategory_name());
            String scopedKey = categoryKey + "|" + nameKey;
            originalM3UPosters.putIfAbsent(scopedKey, episode.getStream_icon().trim());
            uniqueNamePosters.putIfAbsent(nameKey, episode.getStream_icon().trim());
        }
        if (posters.isEmpty() && originalM3UPosters.isEmpty()) {
            return 0;
        }

        final int[] updated = {0};
        realm.executeTransaction(transactionRealm -> {
            RealmResults<SeriesModel> localSeries = transactionRealm.where(SeriesModel.class).findAll();
            for (SeriesModel local : localSeries) {
                String normalizedName = key(local.getName());
                String scopedName = key(local.getCategory_name()) + "|" + normalizedName;
                String originalM3UPoster = originalM3UPosters.get(scopedName);
                if (isBlank(originalM3UPoster)) {
                    originalM3UPoster = uniqueNamePosters.get(normalizedName);
                }
                String poster = !isBlank(originalM3UPoster) ? originalM3UPoster : posters.get(normalizedName);
                // Nunca substitui uma capa M3U original por uma capa remota/genérica.
                if (!isBlank(poster) && !poster.equals(local.getStream_icon())
                        && (!isBlank(originalM3UPoster) || isBlank(local.getStream_icon()))) {
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
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .trim();
        return normalized.replaceAll("\\s+", " ");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim());
    }
}

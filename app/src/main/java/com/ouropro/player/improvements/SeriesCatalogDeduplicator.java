package com.ouropro.player.improvements;

import com.ouropro.player.models.SeriesModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Mantém um único registro visual por série e categoria sem depender de uma
 * chave primária Realm. O schema legado não possui @PrimaryKey em SeriesModel;
 * por isso Realm.insertOrUpdate() não atualiza registros desse modelo.
 */
public final class SeriesCatalogDeduplicator {
    private SeriesCatalogDeduplicator() {
    }

    public static int deduplicate(Realm realm) {
        return upsert(realm, null);
    }

    public static int upsert(Realm realm, List<SeriesModel> incoming) {
        if (realm == null) {
            return 0;
        }
        final int[] changed = {0};
        realm.executeTransaction(transactionRealm -> {
            Map<String, SeriesModel> canonical = new LinkedHashMap<>();
            RealmResults<SeriesModel> existing = transactionRealm.where(SeriesModel.class).findAll();
            List<SeriesModel> redundant = new ArrayList<>();

            for (SeriesModel stored : existing) {
                if (stored == null || isBlank(stored.getName())) {
                    continue;
                }
                String key = key(stored.getCategory_name(), stored.getName());
                SeriesModel keeper = canonical.get(key);
                if (keeper == null) {
                    canonical.put(key, stored);
                } else {
                    mergeInto(keeper, stored);
                    redundant.add(stored);
                    changed[0]++;
                }
            }
            for (SeriesModel duplicate : redundant) {
                if (duplicate.isValid()) {
                    duplicate.deleteFromRealm();
                }
            }

            if (incoming == null) {
                return;
            }
            for (SeriesModel model : incoming) {
                if (model == null || isBlank(model.getName())) {
                    continue;
                }
                String key = key(model.getCategory_name(), model.getName());
                SeriesModel keeper = canonical.get(key);
                if (keeper == null) {
                    keeper = transactionRealm.copyToRealm(model);
                    canonical.put(key, keeper);
                    changed[0]++;
                } else {
                    mergeInto(keeper, model);
                }
            }
        });
        return changed[0];
    }

    private static void mergeInto(SeriesModel target, SeriesModel source) {
        if (isBlank(target.getCategory_name()) && !isBlank(source.getCategory_name())) {
            target.setCategory_name(source.getCategory_name());
        }
        if (isBlank(target.getCategory_id()) && !isBlank(source.getCategory_id())) {
            target.setCategory_id(source.getCategory_id());
        }
        if (isBlank(target.getStream_icon()) && !isBlank(source.getStream_icon())) {
            target.setStream_icon(source.getStream_icon().trim());
        }
        if (isBlank(target.getSeries_id()) && !isBlank(source.getSeries_id())) {
            target.setSeries_id(source.getSeries_id());
        }
        if (isBlank(target.getPlot()) && !isBlank(source.getPlot())) {
            target.setPlot(source.getPlot());
        }
        if (isBlank(target.getCast()) && !isBlank(source.getCast())) {
            target.setCast(source.getCast());
        }
        if (isBlank(target.getDirector()) && !isBlank(source.getDirector())) {
            target.setDirector(source.getDirector());
        }
        if (isBlank(target.getGenre()) && !isBlank(source.getGenre())) {
            target.setGenre(source.getGenre());
        }
        if (isBlank(target.getReleaseDate()) && !isBlank(source.getReleaseDate())) {
            target.setReleaseDate(source.getReleaseDate());
        }
        if (isBlank(target.getUrl()) && !isBlank(source.getUrl())) {
            target.setUrl(source.getUrl());
        }
        if (isBlank(target.getYoutube()) && !isBlank(source.getYoutube())) {
            target.setYoutube(source.getYoutube());
        }
        if (isBlank(target.getTmdb()) && !isBlank(source.getTmdb())) {
            target.tmdb = source.getTmdb();
        }
        if ("0".equals(target.getLast_modified()) && !isBlank(source.getLast_modified())) {
            target.setLast_modified(source.getLast_modified());
        }
        if (target.getNum() == 0 && source.getNum() != 0) {
            target.setNum(source.getNum());
        }
        if (target.getRating_5based() == 0 && source.getRating_5based() != 0) {
            target.setRating_5based(source.getRating_5based());
        }
        if (target.getSeason_pos() == 0 && source.getSeason_pos() != 0) {
            target.setSeason_pos(source.getSeason_pos());
        }
        if (target.getEpisode_pos() == 0 && source.getEpisode_pos() != 0) {
            target.setEpisode_pos(source.getEpisode_pos());
        }
        if (target.getRating() == 0 && source.getRating() != 0) {
            target.setRating(String.valueOf(source.getRating()));
        }
        target.setIs_favorite(target.isIs_favorite() || source.isIs_favorite());
        target.setIs_recent(target.isIs_recent() || source.isIs_recent());
        target.setIs_watched(target.isIs_watched() || source.isIs_watched());
    }

    public static String key(String categoryName, String seriesName) {
        return normalizeCategory(categoryName) + "|" + SeriesPosterRepair.key(seriesName);
    }

    private static String normalizeCategory(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .replaceAll("\\s+", " ").trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim());
    }
}

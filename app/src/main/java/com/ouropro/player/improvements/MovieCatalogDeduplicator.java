package com.ouropro.player.improvements;

import com.ouropro.player.models.MovieModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Consolida filmes sem depender de uma chave primária no schema Realm legado.
 */
public final class MovieCatalogDeduplicator {
    private MovieCatalogDeduplicator() {
    }

    public static int deduplicate(Realm realm) {
        return upsert(realm, null);
    }

    public static int upsert(Realm realm, List<MovieModel> incoming) {
        if (realm == null) {
            return 0;
        }
        final int[] changed = {0};
        realm.executeTransaction(transactionRealm -> {
            Map<String, MovieModel> canonical = new LinkedHashMap<>();
            RealmResults<MovieModel> existing = transactionRealm.where(MovieModel.class).findAll();
            List<MovieModel> redundant = new ArrayList<>();
            for (MovieModel stored : existing) {
                if (stored == null || isBlank(stored.getName())) {
                    continue;
                }
                String key = key(stored.getCategory_name(), stored.getName(), stored.getStream_id(), stored.getUrl());
                MovieModel keeper = canonical.get(key);
                if (keeper == null) {
                    canonical.put(key, stored);
                } else {
                    mergeInto(keeper, stored);
                    redundant.add(stored);
                    changed[0]++;
                }
            }
            for (MovieModel duplicate : redundant) {
                if (duplicate.isValid()) {
                    duplicate.deleteFromRealm();
                }
            }
            if (incoming == null) {
                return;
            }
            for (MovieModel model : incoming) {
                if (model == null || isBlank(model.getName())) {
                    continue;
                }
                String key = key(model.getCategory_name(), model.getName(), model.getStream_id(), model.getUrl());
                MovieModel keeper = canonical.get(key);
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

    private static void mergeInto(MovieModel target, MovieModel source) {
        if (isBlank(target.getCategory_name()) && !isBlank(source.getCategory_name())) {
            target.setCategory_name(source.getCategory_name());
        }
        if (isBlank(target.getCategory_id()) && !isBlank(source.getCategory_id())) {
            target.setCategory_id(source.getCategory_id());
        }
        if (isBlank(target.getStream_icon()) && !isBlank(source.getStream_icon())) {
            target.setStream_icon(source.getStream_icon().trim());
        }
        if (isBlank(target.getStream_id()) && !isBlank(source.getStream_id())) {
            target.setStream_id(source.getStream_id());
        }
        if (isBlank(target.getUrl()) && !isBlank(source.getUrl())) {
            target.setUrl(source.getUrl());
        }
        if (isBlank(target.getExtension()) && !isBlank(source.getExtension())) {
            target.setExtension(source.getExtension());
        }
        if (isBlank(target.getAdded()) && !isBlank(source.getAdded())) {
            target.setAdded(source.getAdded());
        }
        if (isBlank(target.getRating()) && !isBlank(source.getRating())) {
            target.setRating(source.getRating());
        }
        if (isBlank(target.getTmdb_id()) && !isBlank(source.getTmdb_id())) {
            target.setTmdb_id(source.getTmdb_id());
        }
        if (target.getNum() == 0 && source.getNum() != 0) {
            target.setNum(source.getNum());
        }
        if (target.getPro() == 0 && source.getPro() != 0) {
            target.setPro(source.getPro());
        }
        if (target.getTime() == 0 && source.getTime() != 0) {
            target.setTime(source.getTime());
        }
        if (target.getRecent_mil() == 0 && source.getRecent_mil() != 0) {
            target.setRecent_mil(source.getRecent_mil());
        }
        target.setIs_favorite(target.isIs_favorite() || source.isIs_favorite());
        target.setIs_locked(target.isIs_locked() || source.isIs_locked());
        target.setIs_recent(target.isIs_recent() || source.isIs_recent());
    }

    public static String key(String categoryName, String name, String streamId, String url) {
        // O card do catálogo representa o título, não cada URL/qualidade.
        // Stream ID e URL continuam sendo preservados no registro mantido.
        return normalize(categoryName) + "|" + normalize(name);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim());
    }
}

package com.ouropro.player.improvements;

import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SeriesModel;

import io.realm.RealmResults;

/** Resolve títulos de filmes e séries no catálogo atualmente carregado. */
public final class VoiceMediaMatcher {
    private VoiceMediaMatcher() {
    }

    public static MovieModel findUniqueMovie(RealmResults<MovieModel> models, String query) {
        if (models == null || query == null || query.trim().isEmpty()) {
            return null;
        }
        String normalizedQuery = VoiceCommand.normalize(query);
        MovieModel best = null;
        int bestScore = 0;
        boolean tied = false;
        for (MovieModel model : models) {
            if (model == null || model.getName() == null) {
                continue;
            }
            int score = score(VoiceCommand.normalize(model.getName()), normalizedQuery);
            if (score > bestScore) {
                best = model;
                bestScore = score;
                tied = false;
            } else if (score > 0 && score == bestScore) {
                tied = true;
            }
        }
        return bestScore == 0 || tied ? null : best;
    }

    public static SeriesModel findUniqueSeries(RealmResults<SeriesModel> models, String query) {
        if (models == null || query == null || query.trim().isEmpty()) {
            return null;
        }
        String normalizedQuery = VoiceCommand.normalize(query);
        SeriesModel best = null;
        int bestScore = 0;
        boolean tied = false;
        for (SeriesModel model : models) {
            if (model == null || model.getName() == null) {
                continue;
            }
            int score = score(VoiceCommand.normalize(model.getName()), normalizedQuery);
            if (score > bestScore) {
                best = model;
                bestScore = score;
                tied = false;
            } else if (score > 0 && score == bestScore) {
                tied = true;
            }
        }
        return bestScore == 0 || tied ? null : best;
    }

    private static int score(String title, String query) {
        if (title.equals(query)) {
            return 3;
        }
        if (title.startsWith(query)) {
            return 2;
        }
        return title.contains(query) ? 1 : 0;
    }
}

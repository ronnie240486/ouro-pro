package com.ouropro.player.improvements;

import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SeriesModel;

import java.util.ArrayList;
import java.util.List;

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
        for (MovieModel model : models) {
            if (model == null || model.getName() == null) {
                continue;
            }
            String normalizedTitle = VoiceCommand.normalize(model.getName());
            if (normalizedTitle.equals(normalizedQuery) || meaningful(normalizedTitle).equals(meaningful(normalizedQuery))) {
                return model;
            }
            int score = score(normalizedTitle, normalizedQuery);
            if (score == 0 && meaningful(normalizedTitle).contains(meaningful(normalizedQuery))) {
                score = 2;
            }
            if (score > bestScore) {
                best = model;
                bestScore = score;
            }
        }
        return bestScore == 0 ? null : best;
    }

    public static SeriesModel findUniqueSeries(RealmResults<SeriesModel> models, String query) {
        if (models == null || query == null || query.trim().isEmpty()) {
            return null;
        }
        String normalizedQuery = VoiceCommand.normalize(query);
        SeriesModel best = null;
        int bestScore = 0;
        for (SeriesModel model : models) {
            if (model == null || model.getName() == null) {
                continue;
            }
            String normalizedTitle = VoiceCommand.normalize(model.getName());
            if (normalizedTitle.equals(normalizedQuery) || meaningful(normalizedTitle).equals(meaningful(normalizedQuery))) {
                return model;
            }
            int score = score(normalizedTitle, normalizedQuery);
            if (score == 0 && meaningful(normalizedTitle).contains(meaningful(normalizedQuery))) {
                score = 2;
            }
            if (score > bestScore) {
                best = model;
                bestScore = score;
            }
        }
        return bestScore == 0 ? null : best;
    }

    public static List<MovieModel> findMovies(Iterable<MovieModel> models, String query) {
        List<MovieModel> matches = new ArrayList<>();
        if (models == null) {
            return matches;
        }
        String normalizedQuery = VoiceCommand.normalize(query);
        if (normalizedQuery.isEmpty()) {
            return matches;
        }
        String meaningfulQuery = meaningful(normalizedQuery);
        for (MovieModel model : models) {
            if (model == null || model.getName() == null) {
                continue;
            }
            String title = VoiceCommand.normalize(model.getName());
            String meaningfulTitle = meaningful(title);
            if (title.contains(normalizedQuery)
                    || meaningfulTitle.contains(meaningfulQuery)
                    || containsAllWords(meaningfulTitle, meaningfulQuery)) {
                matches.add(model);
            }
        }
        return matches;
    }

    public static List<SeriesModel> findSeries(Iterable<SeriesModel> models, String query) {
        List<SeriesModel> matches = new ArrayList<>();
        if (models == null) {
            return matches;
        }
        String normalizedQuery = VoiceCommand.normalize(query);
        if (normalizedQuery.isEmpty()) {
            return matches;
        }
        String meaningfulQuery = meaningful(normalizedQuery);
        for (SeriesModel model : models) {
            if (model == null || model.getName() == null) {
                continue;
            }
            String title = VoiceCommand.normalize(model.getName());
            String meaningfulTitle = meaningful(title);
            if (title.contains(normalizedQuery)
                    || meaningfulTitle.contains(meaningfulQuery)
                    || containsAllWords(meaningfulTitle, meaningfulQuery)) {
                matches.add(model);
            }
        }
        return matches;
    }

    private static boolean containsAllWords(String title, String query) {
        if (query.isEmpty()) {
            return false;
        }
        String[] words = query.split(" ");
        for (String word : words) {
            if (!word.isEmpty() && !title.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private static String meaningful(String value) {
        return value.replaceAll("\\b(o|a|os|as|um|uma|uns|umas|de|da|do|das|dos|para|e)\\b", " ")
                .replaceAll("\\s+", " ").trim();
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

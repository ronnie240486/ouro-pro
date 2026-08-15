package com.ouropro.player.improvements;

import java.text.Normalizer;
import java.util.Locale;

/** Comando de voz normalizado e seguro para navegação e seleção de conteúdo. */
public final class VoiceCommand {
    public enum Action {
        OPEN_CHANNEL,
        OPEN_LIVE,
        OPEN_MOVIES,
        OPEN_MOVIE_ITEM,
        SEARCH_MOVIE,
        OPEN_SERIES,
        OPEN_SERIES_ITEM,
        SEARCH_SERIES,
        OPEN_SETTINGS,
        SEARCH_CHANNEL,
        NEXT_CHANNEL,
        PREVIOUS_CHANNEL,
        PLAY,
        PAUSE,
        OPEN_TITLE,
        UNKNOWN
    }

    private final Action action;
    private final String query;
    private final String originalText;

    private VoiceCommand(Action action, String query, String originalText) {
        this.action = action;
        this.query = query;
        this.originalText = originalText;
    }

    public static VoiceCommand parse(String rawText) {
        String original = rawText == null ? "" : rawText.trim();
        String normalized = normalize(original);
        if (normalized.isEmpty()) {
            return new VoiceCommand(Action.UNKNOWN, "", original);
        }

        if (matches(normalized, "abrir tv", "abrir canais", "abrir canal ao vivo", "ir para canais", "abrir ao vivo")) {
            return new VoiceCommand(Action.OPEN_LIVE, "", original);
        }
        if (matches(normalized, "abrir filmes", "ir para filmes", "abrir movies")) {
            return new VoiceCommand(Action.OPEN_MOVIES, "", original);
        }
        if (matches(normalized, "abrir series", "ir para series")) {
            return new VoiceCommand(Action.OPEN_SERIES, "", original);
        }
        if (matches(normalized, "abrir configuracoes", "abrir ajustes", "ir para configuracoes")) {
            return new VoiceCommand(Action.OPEN_SETTINGS, "", original);
        }
        if (matches(normalized, "o que posso assistir", "quero assistir alguma coisa", "me mostre opcoes", "me mostra opcoes", "sugira algo para assistir", "o que tem para assistir")) {
            return new VoiceCommand(Action.OPEN_MOVIES, "", original);
        }
        if (matches(normalized, "proximo canal", "canal seguinte", "mudar canal para frente")) {
            return new VoiceCommand(Action.NEXT_CHANNEL, "", original);
        }
        if (matches(normalized, "canal anterior", "voltar canal", "mudar canal para tras")) {
            return new VoiceCommand(Action.PREVIOUS_CHANNEL, "", original);
        }
        if (matches(normalized, "pausar", "pausar canal", "pause")) {
            return new VoiceCommand(Action.PAUSE, "", original);
        }
        if (matches(normalized, "continuar", "tocar", "reproduzir", "play")) {
            return new VoiceCommand(Action.PLAY, "", original);
        }

        String query = removePrefix(normalized,
                "abrir filme ", "abrir o filme ", "assistir filme ", "assistir o filme ",
                "ver filme ", "ver o filme ", "buscar filme ", "buscar o filme ",
                "pesquisar filme ", "pesquisar o filme ", "me mostre filmes de ",
                "mostre filmes de ", "procure filmes de ", "buscar filmes de ",
                "pesquisar filmes de ", "eu quero assistir um filme de ",
                "eu quero assistir filmes de ", "eu quero ver um filme de ",
                "eu quero ver filmes de ", "quero assistir um filme de ",
                "quero assistir filmes de ", "quero ver um filme de ",
                "quero ver filmes de ", "filmes de ", "filme ");
        if (!query.isEmpty()) {
            Action action = normalized.startsWith("buscar ") || normalized.startsWith("pesquisar ")
                    ? Action.SEARCH_MOVIE : Action.OPEN_MOVIE_ITEM;
            return new VoiceCommand(action, query, original);
        }

        query = removePrefix(normalized,
                "abrir serie ", "abrir a serie ", "assistir serie ", "assistir a serie ",
                "ver serie ", "ver a serie ", "buscar serie ", "buscar a serie ",
                "pesquisar serie ", "pesquisar a serie ", "me mostre series de ",
                "mostre series de ", "procure series de ", "buscar series de ",
                "pesquisar series de ", "eu quero assistir uma serie de ",
                "eu quero assistir series de ", "eu quero ver uma serie de ",
                "eu quero ver series de ", "quero assistir uma serie de ",
                "quero assistir series de ", "quero ver uma serie de ",
                "quero ver series de ", "series de ", "serie ");
        if (!query.isEmpty()) {
            Action action = normalized.startsWith("buscar ") || normalized.startsWith("pesquisar ")
                    ? Action.SEARCH_SERIES : Action.OPEN_SERIES_ITEM;
            return new VoiceCommand(action, query, original);
        }

        query = removePrefix(normalized,
                "abrir canal ", "abrir o canal ", "assistir canal ", "assistir o canal ",
                "ver canal ", "ver o canal ", "buscar canal ", "buscar o canal ",
                "pesquisar canal ", "pesquisar o canal ", "me mostre canais de ",
                "mostre canais de ", "procure canais de ", "buscar canais de ",
                "pesquisar canais de ", "abra canais e pesquise ",
                "abrir canais e pesquisar ", "eu quero ver canais de ",
                "eu quero assistir canais de ", "quero ver canais de ",
                "quero assistir canais de ", "canais de ", "canal ");
        if (!query.isEmpty()) {
            Action action = normalized.startsWith("buscar ") || normalized.startsWith("pesquisar ")
                    ? Action.SEARCH_CHANNEL : Action.OPEN_CHANNEL;
            return new VoiceCommand(action, query, original);
        }

        String untypedQuery = removePrefix(normalized, "abrir ", "assistir ", "ver ", "tocar ",
                "me mostre ", "mostre ", "procure ", "pesquise ", "encontre ", "quero ver ", "quero assistir ");
        if (!untypedQuery.isEmpty()) {
            return new VoiceCommand(Action.OPEN_TITLE, untypedQuery, original);
        }

        // Sem prefixo, a frase é tratada como um título e será resolvida no catálogo da tela atual.
        return new VoiceCommand(Action.OPEN_TITLE, normalized, original);
    }

    private static boolean matches(String text, String... options) {
        for (String option : options) {
            if (text.equals(option)) {
                return true;
            }
        }
        return false;
    }

    private static String removePrefix(String text, String... prefixes) {
        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                return text.substring(prefix.length()).trim();
            }
        }
        return "";
    }

    public static String normalize(String value) {
        String decomposed = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public Action getAction() {
        return action;
    }

    public String getQuery() {
        return query;
    }

    public String getOriginalText() {
        return originalText;
    }
}

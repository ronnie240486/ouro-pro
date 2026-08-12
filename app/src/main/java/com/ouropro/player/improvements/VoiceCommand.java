package com.ouropro.player.improvements;

import java.text.Normalizer;
import java.util.Locale;

/** Comando de voz normalizado e seguro para ações de navegação no app. */
public final class VoiceCommand {
    public enum Action {
        OPEN_CHANNEL,
        OPEN_LIVE,
        OPEN_MOVIES,
        OPEN_SERIES,
        OPEN_SETTINGS,
        SEARCH_CHANNEL,
        NEXT_CHANNEL,
        PREVIOUS_CHANNEL,
        PLAY,
        PAUSE,
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
        if (matches(normalized, "abrir series", "abrir séries", "ir para series", "ir para séries")) {
            return new VoiceCommand(Action.OPEN_SERIES, "", original);
        }
        if (matches(normalized, "abrir configuracoes", "abrir configurações", "abrir ajustes", "ir para configuracoes", "ir para configurações")) {
            return new VoiceCommand(Action.OPEN_SETTINGS, "", original);
        }
        if (matches(normalized, "proximo canal", "próximo canal", "canal seguinte", "mudar canal para frente")) {
            return new VoiceCommand(Action.NEXT_CHANNEL, "", original);
        }
        if (matches(normalized, "canal anterior", "voltar canal", "mudar canal para tras", "mudar canal para trás")) {
            return new VoiceCommand(Action.PREVIOUS_CHANNEL, "", original);
        }
        if (matches(normalized, "pausar", "pausar canal", "pause")) {
            return new VoiceCommand(Action.PAUSE, "", original);
        }
        if (matches(normalized, "continuar", "tocar", "reproduzir", "play")) {
            return new VoiceCommand(Action.PLAY, "", original);
        }

        String query = removePrefix(normalized,
                "abrir canal ",
                "abrir o canal ",
                "assistir canal ",
                "assistir o canal ",
                "ver canal ",
                "ver o canal ",
                "buscar canal ",
                "buscar o canal ",
                "pesquisar canal ",
                "pesquisar o canal ");
        if (!query.isEmpty()) {
            Action action = normalized.startsWith("buscar ") || normalized.startsWith("pesquisar ")
                    ? Action.SEARCH_CHANNEL : Action.OPEN_CHANNEL;
            return new VoiceCommand(action, query, original);
        }

        return new VoiceCommand(Action.UNKNOWN, "", original);
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

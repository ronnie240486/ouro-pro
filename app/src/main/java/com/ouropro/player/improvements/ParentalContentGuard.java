package com.ouropro.player.improvements;

import java.util.Locale;

/** Centraliza a identificação de conteúdo adulto e a regra de PIN configurado. */
public final class ParentalContentGuard {
    private ParentalContentGuard() {
    }

    public static boolean isAdult(String... values) {
        StringBuilder combined = new StringBuilder();
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    if (combined.length() > 0) {
                        combined.append(' ');
                    }
                    combined.append(value.toLowerCase(Locale.US));
                }
            }
        }
        String text = combined.toString();
        return text.contains("adult")
                || text.contains("xxx")
                || text.contains("porn")
                || text.contains("18+")
                || text.contains("18 ")
                || text.contains("sex")
                || text.contains("sexy")
                || text.contains("erotic")
                || text.contains("erotico")
                || text.contains("playboy")
                || text.contains("venus")
                || text.contains("hot ")
                || text.contains("redtube");
    }

    public static boolean isConfigured(String pin) {
        if (pin == null) {
            return false;
        }
        String value = pin.trim();
        return !value.isEmpty() && !"0000".equals(value);
    }
}

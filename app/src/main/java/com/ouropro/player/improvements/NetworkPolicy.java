package com.ouropro.player.improvements;

import android.net.Uri;

import java.util.Locale;

/**
 * Valida endpoints configurados pelo usuário antes de entregá-los ao Retrofit.
 * HTTP em claro só é aceito quando o usuário o habilita explicitamente para um
 * servidor local ou legado; o padrão de produção exige HTTPS.
 */
public final class NetworkPolicy {
    private NetworkPolicy() {
    }

    public static String requireBaseUrl(String rawUrl, boolean allowCleartext) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do servidor não pode ficar vazio");
        }

        String normalized = rawUrl.trim();
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }

        Uri uri = Uri.parse(normalized);
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        String host = uri.getHost();
        if (!("https".equals(scheme) || (allowCleartext && "http".equals(scheme)))) {
            throw new IllegalArgumentException("Use um endereço HTTPS válido");
        }
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do servidor precisa conter um host");
        }
        if (uri.getUserInfo() != null) {
            throw new IllegalArgumentException("Não coloque usuário e senha dentro da URL");
        }
        return normalized;
    }
}

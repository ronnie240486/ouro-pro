package com.ouropro.player.improvements;

import android.net.Uri;

/** Extrai apenas metadados transitórios da URL M3U; nunca persiste a URL completa. */
public final class M3UAccountEndpoint {
    private M3UAccountEndpoint() {
    }

    public static Credentials fromPlaylistUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            return null;
        }
        Uri uri = Uri.parse(rawUrl.trim());
        String username = first(uri.getQueryParameter("username"), uri.getQueryParameter("user"));
        String password = first(uri.getQueryParameter("password"), uri.getQueryParameter("pass"));
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (scheme == null || host == null || username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        StringBuilder base = new StringBuilder(scheme).append("://").append(host);
        if (uri.getPort() > 0) {
            base.append(':').append(uri.getPort());
        }
        base.append('/');
        return new Credentials(base.toString(), username, password);
    }

    private static String first(String first, String second) {
        return first == null || first.isEmpty() ? second : first;
    }

    public static final class Credentials {
        private final String baseUrl;
        private final String username;
        private final String password;

        private Credentials(String baseUrl, String username, String password) {
            this.baseUrl = baseUrl;
            this.username = username;
            this.password = password;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}


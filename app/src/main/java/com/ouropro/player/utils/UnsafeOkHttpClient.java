package com.ouropro.player.utils;

import okhttp3.OkHttpClient;

/**
 * Compatibilidade para chamadas antigas. A implementação original aceitava
 * qualquer certificado e qualquer hostname; isso foi removido. O nome antigo
 * permanece temporariamente para que telas recuperadas do APK não quebrem.
 */
@Deprecated
public final class UnsafeOkHttpClient {
    private UnsafeOkHttpClient() {
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }
}

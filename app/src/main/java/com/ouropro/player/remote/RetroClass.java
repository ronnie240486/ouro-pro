package com.ouropro.player.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ouropro.player.improvements.NetworkPolicy;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Construtor centralizado da camada HTTP. O código recuperado originalmente
 * registrava o corpo inteiro das requisições; esta versão nunca expõe tokens,
 * usuários ou senhas no logcat.
 */
public final class RetroClass {
    private RetroClass() {
    }

    public static APIService getAPIService(String rawBaseUrl) {
        return getRetrofitInstance(rawBaseUrl, false).create(APIService.class);
    }

    public static APIService getAPIService(String rawBaseUrl, boolean allowLegacyCleartext) {
        return getRetrofitInstance(rawBaseUrl, allowLegacyCleartext).create(APIService.class);
    }

    private static Retrofit getRetrofitInstance(String rawBaseUrl, boolean allowCleartext) {
        String baseUrl = NetworkPolicy.requireBaseUrl(rawBaseUrl, allowCleartext);
        Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);

        TimeUnit unit = TimeUnit.SECONDS;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30L, unit)
                .readTimeout(60L, unit)
                .writeTimeout(30L, unit)
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}

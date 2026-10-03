package com.ouropro.player.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import java.io.File;
import java.io.InputStream;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/* JADX INFO: loaded from: classes.dex */
@GlideModule
public class MyGlideModule extends AppGlideModule {
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        super.applyOptions(context, glideBuilder);
    }

    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // Bug real relatado: o fundo e os ícones do Home (icon_settings,
        // icon_movies etc. e /api/v4/bg.php) demoravam demais ou simplesmente
        // não apareciam em algumas TV boxes. Causa: esses dois lugares
        // (Back.java e HomeActivity.java) usam DiskCacheStrategy.NONE pra
        // sempre pegar a versão atual quando o revendedor troca a imagem no
        // painel -- só que isso joga fora QUALQUER cache, inclusive o HTTP,
        // obrigando a baixar a imagem inteira de novo (e o servidor refaz o
        // proxy pro S3 de novo) toda vez que a tela abre. Numa TV box com
        // internet fraca isso é lento ou simplesmente estoura o timeout sem
        // nunca terminar.
        //
        // O painel (server/apiRoutes.ts, rotas /api/v4/bg.php e
        // /api/v4/logo.php) já manda um ETag pensado exatamente pra isso: se
        // o cliente reenviar esse mesmo ETag em If-None-Match, o servidor
        // responde 304 na hora, sem reenviar a imagem. Só que sem um Cache
        // HTTP configurado aqui, o OkHttp nunca guarda nem reenvia esse
        // ETag -- a otimização do servidor nunca era usada. Com esse cache,
        // o OkHttp passa a validar com o servidor (rápido, poucos bytes) em
        // vez de sempre baixar tudo de novo, e o Glide ainda assim buscava
        // a imagem posta mais atual sempre que ela muda de verdade.
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new Cache(new File(context.getCacheDir(), "http_image_cache"), 25L * 1024 * 1024))
                .build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }
}

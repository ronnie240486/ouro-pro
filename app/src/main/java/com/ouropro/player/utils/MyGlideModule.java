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
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
@GlideModule
public class MyGlideModule extends AppGlideModule {
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        super.applyOptions(context, glideBuilder);
    }

    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(UnsafeOkHttpClient.getUnsafeOkHttpClient()));
    }
}

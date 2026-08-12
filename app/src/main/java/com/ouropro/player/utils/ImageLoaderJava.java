package com.ouropro.player.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import org.jetbrains.annotations.NotNull;

/* JADX INFO: loaded from: classes.dex */
public class ImageLoaderJava {
    public static void imageLoadUrlWithVodHolder(Context context, @NonNull final ImageView imageView, @NonNull String str, int i, @NonNull final ImageView imageView2) {
        try {
            Glide.with(context).clear(imageView);
            imageView.setImageDrawable(null);
            imageView.setVisibility(android.view.View.INVISIBLE);
            imageView2.setImageResource(i);
            imageView2.setVisibility(android.view.View.VISIBLE);
            if (str == null || str.trim().isEmpty() || "null".equalsIgnoreCase(str.trim())) {
                return;
            }
            Glide.with(context).load(str.trim()).override(300, 450).listener(new RequestListener<Drawable>() { // from class: com.ouropro.player.utils.ImageLoaderJava.2
                public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                    imageView2.setVisibility(0);
                    imageView.setVisibility(0);
                    return false;
                }

                public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                    imageView.setVisibility(0);
                    imageView2.setVisibility(8);
                    return false;
                }
            }).into(imageView);
        } catch (Exception unused) {
        }
    }

    public static void imageLoaderUrlWitPlaceHolder(Context context, @NotNull ImageView imageView, @org.jetbrains.annotations.Nullable String str, int i) {
        Glide.with(context).load(str.trim()).placeholder(i).override(Integer.MIN_VALUE, Integer.MIN_VALUE).listener(new RequestListener<Drawable>() { // from class: com.ouropro.player.utils.ImageLoaderJava.1
            public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                return false;
            }

            public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                return false;
            }
        }).into(imageView);
    }
}

package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/** Compatibility ImageView required by the original 6.1 layouts. */
public class Logo extends ImageView {
    private static final String DEFAULT_LOGO_URL = "https://renciaapp-production.up.railway.app/api/v4/logo.php";

    public Logo(Context context) {
        super(context);
        initialize(context);
    }

    public Logo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public Logo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        try {
            // Mesmo motivo do Back.java: sem isso o Glide guarda a logo em
            // cache pra sempre associado a essa URL fixa, e nunca busca de
            // novo quando o painel troca a imagem.
            Glide.with(context)
                    .load(DEFAULT_LOGO_URL)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(this);
        } catch (Throwable ignored) {
            // The drawable supplied by XML remains visible as a safe fallback.
        }
    }
}


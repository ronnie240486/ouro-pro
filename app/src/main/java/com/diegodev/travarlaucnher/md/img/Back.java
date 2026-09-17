package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Compatibility view required by the original 6.1 layouts.
 * The catalog and Realm flows do not depend on this view.
 */
public class Back extends ImageView {
    private static final String DEFAULT_IMAGE_URL = "https://renciaapp-production.up.railway.app/api/v4/bg.php";

    public Back(Context context) {
        super(context);
        initialize(context);
    }

    public Back(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public Back(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setBackgroundColor(0xFF000000);
        try {
            // O endpoint sempre manda a imagem atual (sem cache HTTP), mas por
            // padrão o Glide guarda o bitmap em disco/memória associado a essa
            // mesma URL pra sempre — então mesmo trocando a imagem no painel,
            // o app nunca ia buscar de novo. DiskCacheStrategy.NONE +
            // skipMemoryCache forçam sempre buscar na rede a cada tela aberta.
            Glide.with(context)
                    .load(DEFAULT_IMAGE_URL)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(this);
        } catch (Throwable ignored) {
            // Keep the black fallback; startup must not fail because of the background.
        }
    }
}


package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
            // Bug real relatado: em algumas TV boxes com internet fraca o
            // fundo demorava demais pra aparecer, ou simplesmente nunca
            // aparecia (ficava só a tela preta). DiskCacheStrategy.NONE +
            // skipMemoryCache forçavam baixar a imagem inteira de novo (e o
            // /api/v4/bg.php refazer o proxy pro S3 de novo) toda vez que
            // essa view era criada -- mesmo numa troca de tela dentro do
            // próprio app. Isso ignorava por completo o ETag que o
            // /api/v4/bg.php já manda pensado exatamente pra validar rápido
            // sem rebaixar nada (ver MyGlideModule, onde o OkHttpClient do
            // Glide agora tem um Cache HTTP de verdade pra aproveitar esse
            // ETag). Com o cache HTTP cuidando da validação, não precisa
            // mais jogar fora o cache do Glide aqui -- e ainda ganha um
            // placeholder, pra não ficar com a tela preta parada enquanto
            // a imagem carrega ou se a rede falhar.
            Glide.with(context)
                    .load(DEFAULT_IMAGE_URL)
                    .placeholder(android.R.color.black)
                    .into(this);
        } catch (Throwable ignored) {
            // Keep the black fallback; startup must not fail because of the background.
        }
    }
}


package com.ouropro.player.utils;

import android.content.Context;
import com.bumptech.glide.Glide;

/**
 * Dispara o download do fundo e dos ícones da Home BEM CEDO (assim que a
 * tela de loading abre), em paralelo com a checagem de MAC/lista. Antes,
 * esse download só começava quando a HomeActivity era de fato criada --
 * ou seja, depois de toda a espera da checagem da lista. Isso fazia o
 * fundo demorar alguns segundos A MAIS pra aparecer depois que a Home
 * finalmente abria, mesmo com o cache HTTP do MyGlideModule.
 *
 * Usa Glide.preload(), que só baixa e guarda no cache (disco/memória) --
 * não aplica em nenhuma View. Quando o Back.java e o HomeActivity.java
 * realmente carregarem essas mesmas URLs, o Glide já encontra tudo pronto
 * (ou quase pronto) no cache e mostra na hora, sem esperar o download de
 * novo.
 */
public final class HomeAssetsPreloader {
    private static final String BASE = "https://renciaapp-production.up.railway.app";

    private static final String[] URLS = {
            BASE + "/api/v4/bg.php",
            BASE + "/api/v4/icon/movies",
            BASE + "/api/v4/icon/series",
            BASE + "/api/v4/icon/account",
            BASE + "/api/v4/icon/change_playlist",
            BASE + "/api/v4/icon/settings",
            BASE + "/api/v4/icon/reload",
            BASE + "/api/v4/icon/exit",
    };

    private HomeAssetsPreloader() {
    }

    public static void preload(Context context) {
        try {
            Context appContext = context.getApplicationContext();
            for (String url : URLS) {
                Glide.with(appContext).load(url).preload();
            }
        } catch (Throwable ignored) {
            // Preload é só uma otimização de velocidade -- se falhar por
            // qualquer motivo, a Home ainda carrega essas mesmas imagens
            // normalmente (só que do jeito de antes, sem o adianto).
        }
    }
}

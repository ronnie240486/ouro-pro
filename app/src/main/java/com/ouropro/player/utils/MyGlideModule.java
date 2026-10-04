package com.ouropro.player.utils;

import android.content.Context;
import android.os.Build;
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
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
        // não apareciam em algumas TV boxes. Causa 1 (cache): esses dois
        // lugares (Back.java e HomeActivity.java) usam DiskCacheStrategy.NONE
        // pra sempre pegar a versão atual quando o revendedor troca a imagem
        // no painel -- só que isso joga fora QUALQUER cache, inclusive o
        // HTTP, obrigando a baixar a imagem inteira de novo toda vez que a
        // tela abre. O painel (apiRoutes.ts, /api/v4/bg.php e /api/v4/logo.php)
        // já manda um ETag pensado pra isso -- com um Cache HTTP de verdade
        // aqui, o OkHttp passa a validar rápido com o servidor em vez de
        // sempre baixar tudo de novo.
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(new Cache(new File(context.getCacheDir(), "http_image_cache"), 25L * 1024 * 1024))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        // Causa 2, a real raiz do caso da TV box "Mxq" (confirmada): em
        // Android ANTIGO (abaixo da versão 8 / API 26), esse aparelho não
        // consegue fechar o handshake HTTPS com qualquer host que exija o
        // conjunto de cifras/âncoras de confiança mais novas -- firmware
        // desses boxes genéricos nunca recebeu atualização de certificados
        // raiz. O próprio app já sabia disso: EncryptedApiCaller chama
        // SSLUtils.ignoreSSL() (que só faz algo em SDK < 26) ANTES de bater
        // no guim.php via HttpsURLConnection, e por isso a LISTA sempre
        // carregou certo mesmo nessa box antiga. Só que esse contorno ajusta
        // o SSLSocketFactory padrão do HttpsURLConnection, não o do OkHttp
        // -- então o cliente do Glide (usado pelo fundo e pelos ícones)
        // nunca foi coberto por ele, falhando silenciosamente (iam pro
        // .error()/placeholder) só nessas boxes antigas, enquanto qualquer
        // Android >= 26 (todas as outras TVs, celular) nunca teve esse
        // problema porque não precisa do contorno. Aplicando aqui o MESMO
        // contorno que o app já usa em SSLUtils, só pra SDK < 26.
        if (Build.VERSION.SDK_INT < 26) {
            try {
                final TrustManager[] trustAllCerts = {new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }};
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            } catch (Exception ignored) {
                // Mantém o cliente sem o contorno; nesse caso a imagem pode
                // continuar falhando nessa box específica, mas o resto do
                // app segue funcionando normalmente.
            }
        }

        OkHttpClient client = builder.build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }
}

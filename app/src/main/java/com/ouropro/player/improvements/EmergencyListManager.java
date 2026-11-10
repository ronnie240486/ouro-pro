package com.ouropro.player.improvements;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.utils.TestPlaylistClient;
import com.ouropro.player.utils.Utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista de EMERGÊNCIA para cliente cadastrado com UMA lista só.
 *
 * Quando o player acusa erro, o app confere se a lista do cliente caiu de verdade (duas tentativas).
 * Se caiu e não existe outra lista pra trocar, pede o teste ao painel (mesma rota do botão TESTE, sem
 * cadastrar lead) e coloca essa lista como "EMERGÊNCIA" na primeira posição -- sem mexer em bloqueio,
 * validade ou cadastro do cliente. A cada ciclo do monitor (60 s) confere se a lista do cliente voltou e,
 * quando volta, tira a emergência e reabre a lista dele. Ao reabrir o app a configuração nova do painel
 * já traz só a lista do cliente; se ela ainda estiver fora, o ciclo se repete sozinho.
 */
public final class EmergencyListManager {
    private EmergencyListManager() {
    }

    private static final String PREFS = "ouropro_emergency";
    private static final String KEY_URL = "emergency_url";
    private static final String KEY_AT = "emergency_at";
    private static final String EMERGENCY_NAME = "EMERGÊNCIA";
    private static final long MIN_GAP_MS = 120000L;
    private static final long CACHE_TTL_MS = 12L * 60L * 60L * 1000L;
    private static volatile boolean busy;
    private static volatile long lastRun;

    public static boolean isEmergencyActive(AppInfoModel info) {
        if (info == null || info.getResult() == null) {
            return false;
        }
        for (AppInfoModel.UrlModel model : info.getResult()) {
            if (model != null && EMERGENCY_NAME.equalsIgnoreCase(model.getName())) {
                return true;
            }
        }
        return false;
    }

    /** Chamado quando o player acusa erro (junto com o aviso ao painel). */
    public static void onPlaybackFailure(Context context) {
        if (context == null) {
            return;
        }
        final Context app = context.getApplicationContext() != null ? context.getApplicationContext() : context;
        long now = System.currentTimeMillis();
        if (busy || now - lastRun < MIN_GAP_MS) {
            return;
        }
        busy = true;
        lastRun = now;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runFailure(app);
                } catch (Throwable ignored) {
                } finally {
                    busy = false;
                }
            }
        }).start();
    }

    private static void runFailure(Context app) throws Exception {
        PreferenceHelper prefs = new PreferenceHelper(app);
        AppInfoModel info = prefs.getSharedPreferenceAppInfo();
        if (info == null || info.getResult() == null || isEmergencyActive(info)) {
            return;
        }
        List<AppInfoModel.UrlModel> urls = info.getResult();
        // Só cliente cadastrado, liberado e com uma lista só (teste/demonstração e quem tem reserva ficam de fora).
        if (urls.size() != 1 || info.getIs_trial() != 0 || info.isIs_google_pay() || info.getLock() == 1) {
            return;
        }
        String mainUrl = urls.get(0).getUrl();
        if (mainUrl == null || mainUrl.trim().isEmpty()) {
            return;
        }
        // Um canal ruim não derruba a lista: só segue se a lista inteira falhar duas vezes seguidas.
        if (isListReachable(mainUrl)) {
            return;
        }
        Thread.sleep(3000L);
        if (isListReachable(mainUrl)) {
            return;
        }
        String mac = prefs.getSharedPreferenceMacAddress();
        if (mac == null || mac.trim().isEmpty()) {
            return;
        }
        String emergencyUrl = cachedUrl(app);
        if (emergencyUrl.isEmpty() || !isListReachable(emergencyUrl)) {
            emergencyUrl = TestPlaylistClient.requestEmergencyPlaylistUrl(mac.trim());
            if (emergencyUrl == null || emergencyUrl.trim().isEmpty()) {
                showToast(app, "Sua lista está fora do ar e não foi possível ativar a lista de emergência agora.");
                return;
            }
            saveCache(app, emergencyUrl);
        }
        // relê: a configuração pode ter mudado enquanto o teste era pedido.
        info = prefs.getSharedPreferenceAppInfo();
        if (info == null || info.getResult() == null || info.getResult().size() != 1 || isEmergencyActive(info)) {
            return;
        }
        AppInfoModel.UrlModel emergency = new AppInfoModel.UrlModel();
        emergency.setId("emergencia-" + System.currentTimeMillis());
        emergency.setName(EMERGENCY_NAME);
        emergency.setUrl(emergencyUrl);
        emergency.setType("general");
        List<AppInfoModel.UrlModel> updated = new ArrayList<>(info.getResult());
        updated.add(0, emergency);
        info.setResult(updated);
        prefs.setSharedPreferenceAppInfo(info);
        Utils.saveToFile(info);
        prefs.setSharedPreferencePlaylistPosition(0);
        showToast(app, "Sua lista saiu do ar. Ativando a lista de emergência...");
        PlaylistFailoverManager.reloadPlaylistNow(app, emergencyUrl);
    }

    /** Chamado a cada ciclo do monitor: se a emergência está ativa e a lista do cliente voltou, restaura. */
    public static void checkRecovery(Context context) {
        try {
            if (context == null) {
                return;
            }
            Context app = context.getApplicationContext() != null ? context.getApplicationContext() : context;
            PreferenceHelper prefs = new PreferenceHelper(app);
            AppInfoModel info = prefs.getSharedPreferenceAppInfo();
            if (!isEmergencyActive(info)) {
                return;
            }
            List<AppInfoModel.UrlModel> rest = new ArrayList<>();
            for (AppInfoModel.UrlModel model : info.getResult()) {
                if (model != null && !EMERGENCY_NAME.equalsIgnoreCase(model.getName())) {
                    rest.add(model);
                }
            }
            if (rest.isEmpty()) {
                return;
            }
            String mainUrl = rest.get(0).getUrl();
            if (mainUrl == null || mainUrl.trim().isEmpty() || !isListReachable(mainUrl)) {
                return;
            }
            info.setResult(rest);
            prefs.setSharedPreferenceAppInfo(info);
            Utils.saveToFile(info);
            prefs.setSharedPreferencePlaylistPosition(0);
            showToast(app, "Sua lista voltou. Reabrindo a lista principal...");
            PlaylistFailoverManager.reloadPlaylistNow(app, mainUrl);
        } catch (Throwable ignored) {
        }
    }

    /** True se a lista responde 2xx e devolve conteúdo de lista (M3U ou JSON do servidor). */
    static boolean isListReachable(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url.trim()).openConnection();
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(8000);
            connection.setUseCaches(false);
            connection.setRequestProperty("User-Agent", "smart-tv");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                return false;
            }
            InputStream in = connection.getInputStream();
            byte[] buffer = new byte[512];
            int read = in.read(buffer);
            if (read <= 0) {
                return false;
            }
            String head = new String(buffer, 0, read, "UTF-8").trim();
            return head.startsWith("#EXTM3U") || head.startsWith("{") || head.startsWith("[");
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String cachedUrl(Context app) {
        try {
            SharedPreferences prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String url = prefs.getString(KEY_URL, "");
            long at = prefs.getLong(KEY_AT, 0L);
            if (url == null || url.isEmpty() || System.currentTimeMillis() - at > CACHE_TTL_MS) {
                return "";
            }
            return url;
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static void saveCache(Context app, String url) {
        try {
            app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                    .putString(KEY_URL, url).putLong(KEY_AT, System.currentTimeMillis()).apply();
        } catch (Throwable ignored) {
        }
    }

    private static void showToast(final Context app, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(app, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}

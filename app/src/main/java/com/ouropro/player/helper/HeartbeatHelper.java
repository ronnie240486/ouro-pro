package com.ouropro.player.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.utils.TestPlaylistClient;
import com.ouropro.player.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class HeartbeatHelper {
    // Evita reabrir o app várias vezes se vierem vários heartbeats "blocked" seguidos.
    private static volatile boolean blockedHandled = false;
    private static volatile boolean deletedHandled = false;

    public static void sendHeartbeat(String mac, String content, final String serverUrl) {
        if (mac == null || mac.trim().isEmpty() || content == null || content.trim().isEmpty() || serverUrl == null || serverUrl.trim().isEmpty()) {
            return;
        }
        final String body;
        try {
            JSONObject json = new JSONObject();
            json.put("mac", mac.trim());
            json.put("content", content.trim());
            body = json.toString();
        } catch (Exception ignored) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(serverUrl).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(body.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    int code = connection.getResponseCode();
                    if (code >= 200 && code < 300) {
                        handleResponse(connection.getInputStream());
                    } else if (code == 403 && connection.getErrorStream() != null) {
                        // o painel responde 403 com {"unregistered":true} quando o cliente foi excluído.
                        handleResponse(connection.getErrorStream());
                    }
                } catch (Exception ignored) {
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * Bloqueio TOTAL: o painel responde {"blocked": true} no heartbeat quando o
     * cliente foi bloqueado. Se o app estiver tocando algo, marca o acesso como
     * bloqueado no cache local e reabre o app do zero -- a tela inicial consulta
     * o painel de novo e mostra o bloqueio, sem esperar o cliente fechar o app.
     */
    private static void handleResponse(InputStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && sb.length() < 4096) sb.append(line);
            reader.close();
            JSONObject json = new JSONObject(sb.toString());
            if (json.optBoolean("blocked", false)) {
                applyBlock();
            } else if (json.optBoolean("unregistered", false)) {
                // cliente excluído do painel: trava (só se era cliente cadastrado) e reabre o app.
                if (!deletedHandled) {
                    deletedHandled = true;
                    com.ouropro.player.utils.AccessGuard.lockIfDeleted(LTVApp.getInstance());
                }
            } else {
                blockedHandled = false;
                deletedHandled = false;
            }
        } catch (Exception ignored) {
            // resposta fora do formato esperado: segue a reprodução normalmente.
        }
    }

    private static void applyBlock() {
        if (blockedHandled) return;
        blockedHandled = true;
        try {
            final Context app = LTVApp.getInstance();
            if (app == null) return;
            com.ouropro.player.helper.PreferenceHelper prefs = new com.ouropro.player.helper.PreferenceHelper(app);
            AppInfoModel info = prefs.getSharedPreferenceAppInfo();
            if (info != null) {
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                info.setLock(1);
                info.setExpiredDate(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(yesterday.getTime()));
                prefs.setSharedPreferenceAppInfo(info);
                Utils.saveToFile(info);
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    TestPlaylistClient.restartApp(app);
                }
            });
        } catch (Throwable ignored) {
            blockedHandled = false;
        }
    }
}

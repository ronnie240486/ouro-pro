package com.ouropro.player.improvements;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.remote.GetDataRequest;
import com.ouropro.player.utils.Security;
import com.ouropro.player.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Consulta somente o MAC local e aplica as decisões de failover do painel. */
public final class PlaylistFailoverManager {
    public static final String ACTION_PLAYLIST_FAILOVER_SYNC = "com.ouropro.player.PLAYLIST_FAILOVER_SYNC";
    public static final String ACTION_EXPIRATION_NOTICE = "com.ouropro.player.EXPIRATION_NOTICE";
    public static final String EXTRA_PLAYLIST_URL = "playlist_url";
    public static final String EXTRA_PLAYLIST_POSITION = "playlist_position";
    public static final String EXTRA_MESSAGE = "playlist_sync_message";
    public static final String EXTRA_EXPIRATION_KEY = "expiration_modal_key";
    public static final String EXTRA_EXPIRATION_TITLE = "expiration_modal_title";
    public static final String EXTRA_EXPIRATION_MESSAGE = "expiration_modal_message";

    private static final String NOTIFICATIONS_URL = "https://renciaapp.manus.space/api/v5/list-notifications";
    private static final String ACK_URL = "https://renciaapp.manus.space/api/v5/list-notifications/ack";
    private static final long POLL_INTERVAL_MS = 60000L;
    private static final Gson GSON = new Gson();
    private static PlaylistFailoverManager instance;

    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running;
    private final Runnable poller = new Runnable() {
        @Override
        public void run() {
            if (!running) {
                return;
            }
            poll();
            handler.postDelayed(this, POLL_INTERVAL_MS);
        }
    };

    private PlaylistFailoverManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized void start(Context context) {
        if (context == null) {
            return;
        }
        if (instance == null) {
            instance = new PlaylistFailoverManager(context);
        }
        if (!instance.running) {
            instance.running = true;
            instance.handler.postDelayed(instance.poller, 3000L);
        }
    }

    private void poll() {
        PreferenceHelper helper = new PreferenceHelper(context);
        String mac = helper.getSharedPreferenceMacAddress();
        if (mac == null || mac.trim().isEmpty()) {
            return;
        }
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(NOTIFICATIONS_URL + "?mac=" + URLEncoder.encode(mac.trim(), "UTF-8"));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(7000);
                connection.setRequestProperty("Accept", "application/json");
                int status = connection.getResponseCode();
                if (status < 200 || status >= 300) {
                    return;
                }
                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        body.append(line);
                    }
                }
                JSONObject payload = new JSONObject(body.toString());
                if (payload.optBoolean("success", false)) {
                    handlePayload(mac.trim(), payload);
                }
            } catch (Exception ignored) {
                // Falhas de consulta não interrompem a reprodução nem removem a última lista válida.
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void handlePayload(String mac, JSONObject payload) {
        handleExpiration(payload);
        handleNotifications(mac, payload);
        boolean syncRequired = payload.optBoolean("playlist_sync_required", false);
        long transitionId = payload.optLong("failover_transition_id", 0L);
        PreferenceHelper helper = new PreferenceHelper(context);
        if (!syncRequired || transitionId <= 0L || transitionId == helper.getSharedPreferenceFailoverTransitionId()) {
            return;
        }
        int listNumber = payload.optInt("active_list_number", 0);
        String message = payload.optString("playlist_sync_message", "").trim();
        refreshConfigurationAndApply(mac, listNumber, transitionId, message);
    }

    private void refreshConfigurationAndApply(String mac, int listNumber, long transitionId, String message) {
        try {
            PreferenceHelper helper = new PreferenceHelper(context);
            String requestData = Security.getStringData(Utils.getDeviceId(context), LTVApp.version_name, false, helper.getSharedPreferenceDeviceType()).trim();
            GetDataRequest request = new GetDataRequest(context, 9100);
            request.setOnGetResponseListener((response, code) -> {
                AppInfoModel info = decodeAppInfo(response);
                if (info == null) {
                    info = new PreferenceHelper(context).getSharedPreferenceAppInfo();
                }
                applySelectedPlaylist(mac, info, listNumber, transitionId, message);
            });
            request.getResponse(Security.getJsonData(requestData), Constants.second_response_url);
        } catch (Exception ignored) {
            applySelectedPlaylist(mac, new PreferenceHelper(context).getSharedPreferenceAppInfo(), listNumber, transitionId, message);
        }
    }

    private AppInfoModel decodeAppInfo(JSONObject response) {
        try {
            if (response == null || !response.has("data")) {
                return null;
            }
            String decoded = Security.getDecodedString(response.getString("data"));
            return GSON.fromJson(new JSONObject(decoded).toString(), AppInfoModel.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void applySelectedPlaylist(String mac, AppInfoModel info, int listNumber, long transitionId, String message) {
        if (info == null || listNumber <= 0 || info.getResult().size() < listNumber) {
            return;
        }
        AppInfoModel.UrlModel selected = info.getResult().get(listNumber - 1);
        if (selected == null || selected.getUrl() == null || selected.getUrl().trim().isEmpty()) {
            return;
        }
        PreferenceHelper helper = new PreferenceHelper(context);
        helper.setSharedPreferenceAppInfo(info);
        Utils.saveToFile(info);
        helper.setSharedPreferencePlaylistPosition(listNumber - 1);
        helper.setSharedPreferenceFailoverTransitionId(transitionId);
        Intent intent = new Intent(ACTION_PLAYLIST_FAILOVER_SYNC);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRA_PLAYLIST_URL, selected.getUrl().trim());
        intent.putExtra(EXTRA_PLAYLIST_POSITION, listNumber - 1);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    private void handleExpiration(JSONObject payload) {
        JSONObject expiration = payload.optJSONObject("expiration");
        if (expiration == null || !expiration.optBoolean("show_modal", false)) {
            return;
        }
        String key = expiration.optString("modal_key", "").trim();
        String title = expiration.optString("modal_title", "").trim();
        String message = expiration.optString("modal_message", "").trim();
        if (key.isEmpty() || title.isEmpty() || message.isEmpty()) {
            return;
        }
        PreferenceHelper helper = new PreferenceHelper(context);
        if (helper.hasShownExpirationModal(key)) {
            return;
        }
        Intent intent = new Intent(ACTION_EXPIRATION_NOTICE);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRA_EXPIRATION_KEY, key);
        intent.putExtra(EXTRA_EXPIRATION_TITLE, title);
        intent.putExtra(EXTRA_EXPIRATION_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    private void handleNotifications(String mac, JSONObject payload) {
        JSONArray notifications = payload.optJSONArray("notifications");
        if (notifications == null) {
            return;
        }
        PreferenceHelper helper = new PreferenceHelper(context);
        for (int i = 0; i < notifications.length(); i++) {
            JSONObject notification = notifications.optJSONObject(i);
            if (notification == null || notification.optBoolean("acknowledged", false)) {
                continue;
            }
            long id = notification.optLong("id", 0L);
            if (id <= 0L || helper.hasAcknowledgedFailoverAlert(id)) {
                continue;
            }
            String message = notification.optString("message", "").trim();
            if (!message.isEmpty() && !payload.optBoolean("failover_active", false)) {
                showMessage(message);
            }
            helper.markFailoverAlertAcknowledged(id);
            acknowledgeAsync(mac, id);
        }
    }

    private void showMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        handler.post(() -> Toast.makeText(context, message.trim(), Toast.LENGTH_LONG).show());
    }

    private void acknowledgeAsync(String mac, long alertId) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(ACK_URL).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                JSONObject body = new JSONObject();
                body.put("mac", mac);
                body.put("alert_id", alertId);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }
                connection.getResponseCode();
            } catch (Exception ignored) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}

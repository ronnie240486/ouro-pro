package com.ouropro.player.improvements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ouropro.player.apps.BaseActivity;
import com.ouropro.player.apps.BaseTVActivity;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.remote.GetDataRequest;
import com.ouropro.player.utils.Security;
import com.ouropro.player.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Consulta o failover definido pelo painel sem decidir a troca localmente. */
public final class PlaylistFailoverManager {
    public static final String ACTION_PLAYLIST_FAILOVER_SYNC = "com.ouropro.player.PLAYLIST_FAILOVER_SYNC";
    public static final String EXTRA_PLAYLIST_URL = "playlist_url";
    public static final String EXTRA_PLAYLIST_POSITION = "playlist_position";
    public static final String EXTRA_MESSAGE = "playlist_sync_message";
    public static final String ACTION_EXPIRATION_NOTICE = "com.ouropro.player.EXPIRATION_NOTICE";
    public static final String EXTRA_EXPIRATION_KEY = "expiration_modal_key";
    public static final String EXTRA_EXPIRATION_TITLE = "expiration_modal_title";
    public static final String EXTRA_EXPIRATION_MESSAGE = "expiration_modal_message";
    private static final String NOTIFICATIONS_URL = "https://renciaapp.manus.space/api/v5/list-notifications";
    private static final String ACK_URL = "https://renciaapp.manus.space/api/v5/list-notifications/ack";
    private static final String PLAYBACK_FAILURE_URL = "https://renciaapp.manus.space/api/v5/playback-failure";
    private static final long POLL_INTERVAL_MS = 60000L;
    private static final Gson GSON = new Gson();
    private static PlaylistFailoverManager instance;

    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running;
    private volatile boolean playbackFailureInFlight;
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
        if (instance.running) {
            return;
        }
        instance.running = true;
        instance.poll();
        instance.handler.postDelayed(instance.poller, POLL_INTERVAL_MS);
    }

    private void poll() {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        String mac = preferenceHelper.getSharedPreferenceMacAddress();
        if (mac == null || mac.trim().isEmpty()) {
            return;
        }
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(NOTIFICATIONS_URL + "?mac=" + java.net.URLEncoder.encode(mac.trim(), "UTF-8"));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(7000);
                connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() < 200 || connection.getResponseCode() >= 300) {
                    return;
                }
                String response = read(connection.getInputStream());
                JSONObject payload = new JSONObject(response);
                if (!payload.optBoolean("success", false)) {
                    return;
                }
                handlePayload(mac.trim(), payload);
            } catch (Exception ignored) {
                // Falha de aviso nunca interrompe o player nem apaga a lista válida.
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void handlePayload(String mac, JSONObject payload) {
        handleExpiration(payload);
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        long transitionId = payload.optLong("failover_transition_id", 0L);
        boolean syncRequired = payload.optBoolean("playlist_sync_required", false);
        boolean transitionAlreadyProcessed = transitionId > 0L && transitionId == preferenceHelper.getSharedPreferenceFailoverTransitionId();
        boolean primaryRestored = "primary_restored".equalsIgnoreCase(payload.optString("failover_state", ""));
        if ((syncRequired || primaryRestored) && transitionId > 0L && !transitionAlreadyProcessed) {
            int activeListNumber = payload.optInt("active_list_number", 0);
            requestFreshPlaylistConfig(mac, activeListNumber, transitionId, payload.optString("playlist_sync_message", ""), payload);
            return;
        }
        showAndAcknowledgeNotifications(mac, payload, transitionAlreadyProcessed ? "" : payload.optString("playlist_sync_message", ""));
    }

    private void requestFreshPlaylistConfig(String mac, int activeListNumber, long transitionId, String message, JSONObject notificationPayload) {
        String requestData = Security.getStringData(Utils.getDeviceId(context), LTVApp.version_name, false, new PreferenceHelper(context).getSharedPreferenceDeviceType()).trim();
        GetDataRequest request = new GetDataRequest(context, 9100);
        request.setOnGetResponseListener((jsonObject, requestCode) -> {
            AppInfoModel freshInfo = decodeAppInfo(jsonObject);
            AppInfoModel activeInfo = freshInfo == null ? new PreferenceHelper(context).getSharedPreferenceAppInfo() : freshInfo;
            AppInfoModel.UrlModel activeUrl = findActiveUrl(activeInfo, activeListNumber);
            if (activeUrl == null || activeUrl.getUrl().trim().isEmpty()) {
                return;
            }
            PreferenceHelper helper = new PreferenceHelper(context);
            if (freshInfo != null) {
                helper.setSharedPreferenceAppInfo(freshInfo);
                Utils.saveToFile(freshInfo);
            }
            int position = Math.max(0, activeListNumber - 1);
            helper.setSharedPreferencePlaylistPosition(position);
            helper.setSharedPreferenceFailoverTransitionId(transitionId);
            android.content.Intent intent = new android.content.Intent(ACTION_PLAYLIST_FAILOVER_SYNC);
            intent.setPackage(context.getPackageName());
            intent.putExtra(EXTRA_PLAYLIST_URL, activeUrl.getUrl());
            intent.putExtra(EXTRA_PLAYLIST_POSITION, position);
            intent.putExtra(EXTRA_MESSAGE, message);
            reloadActivePlaylist(activeUrl.getUrl(), position);
            showAndAcknowledgeNotifications(mac, notificationPayload, message);
        });
        request.getResponse(Security.getJsonData(requestData), Constants.second_response_url);
    }

    private void reloadActivePlaylist(String playlistUrl, int position) {
        Activity activity = ForegroundActivityTracker.get();
        if (activity == null || playlistUrl == null || playlistUrl.trim().isEmpty()) {
            return;
        }
        PreferenceHelper helper = new PreferenceHelper(activity);
        helper.setSharedPreferencePlaylistPosition(Math.max(0, position));
        WordModels words = GetSharedInfo.getWordModel(activity);
        String url = playlistUrl.trim();
        boolean xtream = url.toLowerCase(Locale.ROOT).contains("username");
        if (activity instanceof BaseTVActivity) {
            BaseTVActivity base = (BaseTVActivity) activity;
            if (xtream) {
                base.goToLogin(url, words);
            } else {
                helper.setSharedPreferenceISM3U(true);
                base.reloadM3UData(url, words);
            }
        } else if (activity instanceof BaseActivity) {
            BaseActivity base = (BaseActivity) activity;
            if (xtream) {
                base.goToLogin(url, words);
            } else {
                helper.setSharedPreferenceISM3U(true);
                base.reloadM3UData(url, words);
            }
        }
    }

    public static void reportPlaybackFailure(Context context) {
        if (context == null) {
            return;
        }
        if (instance == null) {
            start(context);
        }
        instance.sendPlaybackFailure();
    }

    private void sendPlaybackFailure() {
        if (playbackFailureInFlight) {
            return;
        }
        PreferenceHelper helper = new PreferenceHelper(context);
        String mac = helper.getSharedPreferenceMacAddress();
        if (mac == null || mac.trim().isEmpty()) {
            return;
        }
        playbackFailureInFlight = true;
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(PLAYBACK_FAILURE_URL).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(7000);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                JSONObject body = new JSONObject();
                body.put("mac", mac.trim());
                body.put("active_list_number", helper.getSharedPreferencePlaylistPosition() + 1);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }
                int code = connection.getResponseCode();
                String response = code >= 200 && code < 300 ? read(connection.getInputStream()) : "";
                if (code >= 200 && code < 300 && new JSONObject(response).optBoolean("switch_applied", false)) {
                    poll();
                }
            } catch (Exception ignored) {
            } finally {
                playbackFailureInFlight = false;
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
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

    private AppInfoModel.UrlModel findActiveUrl(AppInfoModel info, int activeListNumber) {
        if (info == null || activeListNumber <= 0 || info.getResult().size() < activeListNumber) {
            return null;
        }
        return info.getResult().get(activeListNumber - 1);
    }

    private void handleExpiration(JSONObject payload) {
        try {
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
            showExpirationDialog(key, title, message);
        } catch (Exception ignored) {
        }
    }

    private void showExpirationDialog(String key, String title, String message) {
        handler.post(() -> {
            Activity activity = ForegroundActivityTracker.get();
            if (activity == null) {
                return;
            }
            new PreferenceHelper(context).markExpirationModalShown(key);
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private void showAndAcknowledgeNotifications(String mac, JSONObject payload, String preferredMessage) {
        if (payload == null) {
            showMessage(preferredMessage);
            return;
        }
        String message = preferredMessage == null ? "" : preferredMessage.trim();
        JSONArray notifications = payload.optJSONArray("notifications");
        List<Long> idsToAck = new ArrayList<>();
        PreferenceHelper helper = new PreferenceHelper(context);
        if (notifications != null) {
            for (int i = 0; i < notifications.length(); i++) {
                JSONObject notification = notifications.optJSONObject(i);
                if (notification == null || notification.optBoolean("acknowledged", false)
                        || !"failure".equalsIgnoreCase(notification.optString("status", ""))) {
                    continue;
                }
                long id = notification.optLong("id", 0L);
                if (id <= 0L || helper.hasAcknowledgedFailoverAlert(id)) {
                    continue;
                }
                if (message.isEmpty()) {
                    message = notification.optString("message", "");
                }
                idsToAck.add(id);
            }
        }
        showMessage(message);
        for (Long id : idsToAck) {
            acknowledgeAsync(mac, id.longValue());
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
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }
                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    new PreferenceHelper(context).markFailoverAlertAcknowledged(alertId);
                }
            } catch (Exception ignored) {
                // A falha no ack não interrompe a reprodução.
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private String read(InputStream inputStream) throws Exception {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}

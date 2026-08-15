package com.ouropro.player.helper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class HeartbeatHelper {
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
                    connection.getResponseCode();
                } catch (Exception ignored) {
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}

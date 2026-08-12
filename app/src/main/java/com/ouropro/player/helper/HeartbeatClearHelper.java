package com.ouropro.player.helper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes.dex */
public class HeartbeatClearHelper {
    public static void clearHeartbeat(String str, final String str2) {
        if (str == null || str.isEmpty() || str2 == null || str2.isEmpty()) {
            return;
        }
        final String str3 = "{\"mac\":\"" + str + "\",\"content\":\"\"}";
        new Thread(new Runnable() { // from class: com.ouropro.player.helper.HeartbeatClearHelper.1
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str2).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(5000);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(str3.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    httpURLConnection.getResponseCode();
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                }
            }
        }).start();
    }
}

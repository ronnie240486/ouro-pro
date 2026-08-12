package com.ouropro.player.helper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes.dex */
public class HeartbeatHelper {
    public static void sendHeartbeat(String str, String str2, final String str3) {
        if (str == null || str.isEmpty() || str3 == null || str3.isEmpty() || str2 == null || str2.isEmpty()) {
            return;
        }
        final String str4 = "{\"mac\":\"" + str + "\",\"content\":\"" + str2 + "\"}";
        new Thread(new Runnable() { // from class: com.ouropro.player.helper.HeartbeatHelper.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str3).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(5000);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(str4.getBytes("UTF-8"));
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

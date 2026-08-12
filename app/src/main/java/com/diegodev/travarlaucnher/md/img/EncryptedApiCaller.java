package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class EncryptedApiCaller {
    public static void callEncryptedMoviesApi(final Context context) {
        SSLUtils.ignoreSSL();
        new Thread(new Runnable() { // from class: com.diegodev.travarlaucnher.md.img.EncryptedApiCaller$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                EncryptedApiCaller.lambda$callEncryptedMoviesApi$0(context);
            }
        }).start();
    }

    static /* synthetic */ void lambda$callEncryptedMoviesApi$0(Context context) {
        InputStream errorStream;
        String mac = context.getSharedPreferences("UserSetting", 0).getString("MacMobile", "");
        String uid = mac.isEmpty() ? CryptoHelper.getDeviceIdentifier(context) : mac;
        String uidB64 = Base64.encodeToString(uid.getBytes(StandardCharsets.UTF_8), 2);
        try {
            JSONObject json = new JSONObject();
            json.put("app_device_id", uidB64);
            json.put("app_type", "tv");
            json.put("version", "5.0");
            json.put("is_paid", false);
            String encryptedData = CryptoHelper.getEncodedString(json);
            Log.d("EncryptedApiCaller", "Enviando para: https://renciaapp-ldyffp73.manus.space/api/guim.php");
            Log.d("EncryptedApiCaller", "Body: " + encryptedData);
            URL url = new URL("https://renciaapp-ldyffp73.manus.space/api/guim.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("device-id", uid);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            JSONObject bodyObj = new JSONObject();
            bodyObj.put("data", encryptedData);
            OutputStream os = conn.getOutputStream();
            try {
                byte[] input = bodyObj.toString().getBytes("UTF-8");
                os.write(input, 0, input.length);
                if (os != null) {
                    os.close();
                }
                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 400) {
                    errorStream = conn.getInputStream();
                } else {
                    errorStream = conn.getErrorStream();
                }
                InputStream inputStream = errorStream;
                StringBuilder response = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    try {
                        String line = br.readLine();
                        if (line != null) {
                            response.append(line);
                        } else {
                            br.close();
                            Log.d("EncryptedApiCaller", "Resposta: " + response.toString());
                            return;
                        }
                    } catch (Throwable th) {
                        try {
                            br.close();
                            throw th;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            throw th;
                        }
                    }
                    Log.e("EncryptedApiCaller", "Erro na chamada da API", e);
                }
            } catch (Throwable th3) {
                if (os == null) {
                    throw th3;
                }
                try {
                    os.close();
                    throw th3;
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                    throw th3;
                }
            }
        } catch (Exception e) {
            Log.e("EncryptedApiCaller", "Erro na chamada da API", e);
        }
    }
}

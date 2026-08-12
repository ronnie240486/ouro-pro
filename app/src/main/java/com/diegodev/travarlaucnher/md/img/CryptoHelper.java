package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class CryptoHelper {
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String getDecodedString(String str) {
        try {
            String lastChar = str.substring(str.length() - 1);
            String secondLastChar = str.substring(str.length() - 2, str.length() - 1);
            int pos1 = ALLOWED_CHARACTERS.indexOf(secondLastChar);
            int pos2 = ALLOWED_CHARACTERS.indexOf(lastChar);
            String main = str.substring(0, str.length() - 2);
            String part1 = main.substring(0, pos1);
            String part2 = main.substring(pos1 + pos2);
            String finalBase64 = part1 + part2;
            byte[] decoded = Base64.decode(finalBase64, 0);
            return new String(decoded, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            Log.e("CryptoHelper", "Erro ao decodificar string: " + e.getMessage(), e);
            return null;
        }
    }

    public static String getEncodedString(JSONObject jsonObject) {
        try {
            String base64 = Base64.encodeToString(jsonObject.toString().getBytes(StandardCharsets.UTF_8), 2);
            String trimmed = base64.trim();
            Random random = new Random();
            int keySize = random.nextInt(20);
            int insertPos = random.nextInt(trimmed.length());
            if (insertPos > 42) {
                insertPos = 42;
            }
            StringBuilder keyBuilder = new StringBuilder();
            for (int i = 0; i < keySize; i++) {
                keyBuilder.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
            }
            String encryptKey = keyBuilder.toString();
            String part1 = trimmed.substring(0, insertPos);
            String part2 = trimmed.substring(insertPos);
            String encoded = part1 + encryptKey + part2;
            return (encoded + ALLOWED_CHARACTERS.charAt(insertPos)) + ALLOWED_CHARACTERS.charAt(keySize);
        } catch (Exception e) {
            Log.e("CryptoHelper", "Erro ao codificar string: " + e.getMessage(), e);
            return null;
        }
    }

    public static String getDeviceIdentifier(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserSetting", 0);
        String savedIdentifier = prefs.getString("MacSalved", "");
        if (!savedIdentifier.isEmpty()) {
            Log.d("DeviceUtils", "📦 Identificador já salvo: " + savedIdentifier);
            return savedIdentifier;
        }
        Log.d("DeviceUtils", "🔍 Iniciando leitura do /proc/cpuinfo para identificar o dispositivo...");
        String identifier = "";
        if ("".isEmpty() || "".matches("^0+$") || "".equals("0000000000000001")) {
            Log.w("DeviceUtils", "⚠️ Identificador inválido. Usando ANDROID_ID como fallback...");
            identifier = Settings.Secure.getString(context.getContentResolver(), "android_id");
            Log.d("DeviceUtils", "🔁 ANDROID_ID: " + identifier);
        }
        if (identifier.length() > 13) {
            Log.d("DeviceUtils", "✂️ Cortando identificador para 13 caracteres");
            identifier = identifier.substring(0, 13);
        }
        prefs.edit().putString("MacSalved", identifier).apply();
        Log.i("DeviceUtils", "✅ Identificador final salvo e retornado: " + identifier);
        return identifier;
    }
}

package com.ouropro.player.improvements;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.util.Map;
import java.util.Set;

/**
 * Abre preferências protegidas pelo Android Keystore e migra uma instalação
 * antiga uma única vez. O fallback é mantido somente para aparelhos onde o
 * provider criptográfico não está disponível.
 */
public final class SecurePreferenceStore {
    private SecurePreferenceStore() {
    }

    public static SharedPreferences open(Context context, String legacyName) {
        SharedPreferences legacy = context.getSharedPreferences(legacyName, Context.MODE_PRIVATE);
        String secureName = legacyName + "_secure";
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences secure = EncryptedSharedPreferences.create(
                    context,
                    secureName,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            migrateIfNeeded(legacy, secure);
            return secure;
        } catch (RuntimeException unavailable) {
            return legacy;
        }
    }

    private static void migrateIfNeeded(SharedPreferences legacy, SharedPreferences secure) {
        Map<String, ?> values = legacy.getAll();
        if (values.isEmpty() || !secure.getAll().isEmpty()) {
            return;
        }
        SharedPreferences.Editor target = secure.edit();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                target.putString(entry.getKey(), (String) value);
            } else if (value instanceof Boolean) {
                target.putBoolean(entry.getKey(), (Boolean) value);
            } else if (value instanceof Integer) {
                target.putInt(entry.getKey(), (Integer) value);
            } else if (value instanceof Long) {
                target.putLong(entry.getKey(), (Long) value);
            } else if (value instanceof Float) {
                target.putFloat(entry.getKey(), (Float) value);
            } else if (value instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> strings = (Set<String>) value;
                target.putStringSet(entry.getKey(), strings);
            }
        }
        if (target.commit()) {
            legacy.edit().clear().apply();
        }
    }
}

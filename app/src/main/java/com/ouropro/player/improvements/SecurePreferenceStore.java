package com.ouropro.player.improvements;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

/**
 * Abre preferências protegidas pelo Android Keystore. O fallback existe para
 * permitir inicialização em aparelhos nos quais o provider criptográfico esteja
 * indisponível; ele é registrado para que a aplicação possa migrar esses dados.
 */
public final class SecurePreferenceStore {
    private SecurePreferenceStore() {
    }

    public static SharedPreferences open(Context context, String name) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    name,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (RuntimeException unavailable) {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
    }
}

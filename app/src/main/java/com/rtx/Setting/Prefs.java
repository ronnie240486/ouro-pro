package com.rtx.Setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public final class Prefs {
    private static final String DEFAULT_SUFFIX = "";
    private static final String LENGTH = "#LENGTH";
    private static SharedPreferences mPrefs;

    @Deprecated
    public static void initPrefs(Context context) {
        new Builder().setContext(context).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void initPrefs(Context context, String prefsName, int mode) {
        mPrefs = context.getSharedPreferences(prefsName, mode);
    }

    public static SharedPreferences getPreferences() {
        SharedPreferences sharedPreferences = mPrefs;
        if (sharedPreferences != null) {
            return sharedPreferences;
        }
        throw new RuntimeException("Prefs class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate.");
    }

    public static Map<String, ?> getAll() {
        return getPreferences().getAll();
    }

    public static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static int getInt(String key) {
        return getPreferences().getInt(key, 0);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static boolean getBoolean(String key) {
        return getPreferences().getBoolean(key, false);
    }

    public static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static long getLong(String key) {
        return getPreferences().getLong(key, 0L);
    }

    public static double getDouble(String key, double defValue) {
        return Double.longBitsToDouble(getPreferences().getLong(key, Double.doubleToLongBits(defValue)));
    }

    public static double getDouble(String key) {
        return Double.longBitsToDouble(getPreferences().getLong(key, Double.doubleToLongBits(0.0d)));
    }

    public static float getFloat(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static float getFloat(String key) {
        return getPreferences().getFloat(key, 0.0f);
    }

    public static String getString(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static String getString(String key) {
        return getPreferences().getString(key, "");
    }

    public static Set<String> getStringSet(String key, Set<String> defValue) {
        SharedPreferences prefs = getPreferences();
        if (Build.VERSION.SDK_INT >= 11) {
            return prefs.getStringSet(key, defValue);
        }
        return getOrderedStringSet(key, defValue);
    }

    public static Set<String> getOrderedStringSet(String key, Set<String> defValue) {
        SharedPreferences prefs = getPreferences();
        if (prefs.contains(key + LENGTH)) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            int stringSetLength = prefs.getInt(key + LENGTH, -1);
            if (stringSetLength >= 0) {
                for (int i = 0; i < stringSetLength; i++) {
                    set.add(prefs.getString(key + "[" + i + "]", null));
                }
            }
            return set;
        }
        return defValue;
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putDouble(String key, double value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.apply();
    }

    public static void putFloat(String key, float value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putStringSet(String key, Set<String> value) {
        if (Build.VERSION.SDK_INT >= 11) {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putStringSet(key, value);
            editor.apply();
            return;
        }
        putOrderedStringSet(key, value);
    }

    public static void putOrderedStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        int stringSetLength = 0;
        if (mPrefs.contains(key + LENGTH)) {
            stringSetLength = mPrefs.getInt(key + LENGTH, -1);
        }
        editor.putInt(key + LENGTH, value.size());
        int i = 0;
        for (String aValue : value) {
            editor.putString(key + "[" + i + "]", aValue);
            i++;
        }
        while (i < stringSetLength) {
            editor.remove(key + "[" + i + "]");
            i++;
        }
        editor.apply();
    }

    public static void remove(String key) {
        SharedPreferences prefs = getPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(key + LENGTH)) {
            int stringSetLength = prefs.getInt(key + LENGTH, -1);
            if (stringSetLength >= 0) {
                editor.remove(key + LENGTH);
                for (int i = 0; i < stringSetLength; i++) {
                    editor.remove(key + "[" + i + "]");
                }
            }
        }
        editor.remove(key);
        editor.apply();
    }

    public static boolean contains(String key) {
        return getPreferences().contains(key);
    }

    public static SharedPreferences.Editor clear() {
        SharedPreferences.Editor editor = getPreferences().edit().clear();
        editor.apply();
        return editor;
    }

    public static SharedPreferences.Editor edit() {
        return getPreferences().edit();
    }

    public static final class Builder {
        private Context mContext;
        private String mKey;
        private int mMode = -1;
        private boolean mUseDefault = false;

        public Builder setPrefsName(String prefsName) {
            this.mKey = prefsName;
            return this;
        }

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setMode(int mode) {
            if (mode == 0 || mode == 1 || mode == 2 || mode == 4) {
                this.mMode = mode;
                return this;
            }
            throw new RuntimeException("The mode in the SharedPreference can only be set too ContextWrapper.MODE_PRIVATE, ContextWrapper.MODE_WORLD_READABLE, ContextWrapper.MODE_WORLD_WRITEABLE or ContextWrapper.MODE_MULTI_PROCESS");
        }

        public Builder setUseDefaultSharedPreference(boolean defaultSharedPreference) {
            this.mUseDefault = defaultSharedPreference;
            return this;
        }

        public void build() {
            if (this.mContext == null) {
                throw new RuntimeException("Context not set, please set context before building the Prefs instance.");
            }
            if (TextUtils.isEmpty(this.mKey)) {
                this.mKey = this.mContext.getPackageName();
            }
            if (this.mUseDefault) {
                this.mKey += "";
            }
            if (this.mMode == -1) {
                this.mMode = 0;
            }
            Prefs.initPrefs(this.mContext, this.mKey, this.mMode);
        }
    }
}

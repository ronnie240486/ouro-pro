package androidx.core.graphics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.realm.Realm;
import io.realm.RealmModel;

public final class Insets$$ExternalSyntheticOutline0 {
    private Insets$$ExternalSyntheticOutline0() { }
    public static StringBuilder m(String prefix) { return new StringBuilder(prefix); }
    public static StringBuilder m13m(String first, String second) { return new StringBuilder(first).append(second); }
    public static String m(String prefix, int value) { return prefix + value; }
    public static String m(String first, String second) { return first + second; }
    public static String m(String prefix, int value, String suffix) { return prefix + value + suffix; }
    public static String m(String first, String second, String third) { return first + second + third; }
    public static StringBuilder m(StringBuilder builder, Object value, char separator, String suffix) { return builder.append(value).append(separator).append(suffix); }
    public static String m(StringBuilder builder, Object value, String suffix) { return builder.append(value).append(suffix).toString(); }
    public static String m(StringBuilder builder, Object value, String middle, String suffix) { return builder.append(value).append(middle).append(suffix).toString(); }
    public static String m(StringBuilder builder, Object a, Object b, Object c, Object d) { return builder.append(a).append(b).append(c).append(d).toString(); }
    public static String m(StringBuilder builder, int value, char suffix) { return builder.append(value).append(suffix).toString(); }
    public static View m(ViewGroup parent, int layoutId, ViewGroup root, boolean attachToRoot) { return LayoutInflater.from(parent.getContext()).inflate(layoutId, root, attachToRoot); }
    public static <T extends RealmModel> T m(Realm realm, Class<T> modelClass, String fieldName, String fieldValue) { return realm.where(modelClass).equalTo(fieldName, fieldValue).findFirst(); }
    public static void m(Context context, int colorResId, TextView textView) { textView.setTextColor(context.getResources().getColor(colorResId)); }
    public static void m(FragmentTransaction transaction, Fragment fragment, String unused) { transaction.remove(fragment).commitAllowingStateLoss(); }
}

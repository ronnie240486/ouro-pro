package com.ouropro.player.improvements;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** Evita que valores nulos vindos de catálogos ou traduções apareçam literalmente na interface. */
public final class NullTextGuard {
    private NullTextGuard() {
    }

    public static void sanitize(Activity activity) {
        if (activity != null) {
            sanitize(activity.getWindow().getDecorView());
        }
    }

    public static void sanitize(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            CharSequence text = textView.getText();
            if (isLiteralNull(text)) {
                textView.setText("");
            }
            CharSequence description = textView.getContentDescription();
            if (isLiteralNull(description)) {
                textView.setContentDescription("");
            }
        } else if (isLiteralNull(view.getContentDescription())) {
            view.setContentDescription("");
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                sanitize(group.getChildAt(i));
            }
        }
    }

    private static boolean isLiteralNull(CharSequence value) {
        return value != null && "null".equalsIgnoreCase(value.toString().trim());
    }
}

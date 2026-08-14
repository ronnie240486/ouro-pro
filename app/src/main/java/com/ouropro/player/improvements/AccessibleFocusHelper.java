package com.ouropro.player.improvements;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

/** Foco unificado para toque no celular e controle remoto na TV Box. */
public final class AccessibleFocusHelper {
    private static final int FOCUS_GOLD = Color.rgb(255, 211, 42);

    private AccessibleFocusHelper() {
    }

    public static void apply(View view, String description) {
        if (view == null) {
            return;
        }
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setClickable(true);
        view.setContentDescription(description);
        view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        view.setOnFocusChangeListener((focusedView, focused) -> {
            focusedView.setScaleX(focused ? 1.08f : 1.0f);
            focusedView.setScaleY(focused ? 1.08f : 1.0f);
            GradientDrawable focusBackground = new GradientDrawable();
            focusBackground.setColor(Color.TRANSPARENT);
            focusBackground.setCornerRadius(48.0f);
            if (focused) {
                focusBackground.setStroke(3, FOCUS_GOLD);
            }
            focusedView.setBackground(focusBackground);
        });
    }
}

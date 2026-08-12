package com.ouropro.player.improvements;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;

import com.ouropro.player.R;

/** Cria o acionador visual de voz usado nas telas do aplicativo. */
public final class VoiceButtonFactory {
    private VoiceButtonFactory() {
    }

    public static ImageButton create(Context context, String description, View.OnClickListener listener) {
        ImageButton button = new ImageButton(context);
        button.setImageResource(R.drawable.ic_microphone_voice);
        button.setContentDescription(description);
        button.setBackgroundColor(Color.TRANSPARENT);
        int padding = dp(context, 10);
        button.setPadding(padding, padding, padding, padding);
        button.setFocusable(true);
        button.setClickable(true);
        button.setOnClickListener(listener);
        if (Build.VERSION.SDK_INT >= 21) {
            button.setElevation(dp(context, 6));
        }
        return button;
    }

    private static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}

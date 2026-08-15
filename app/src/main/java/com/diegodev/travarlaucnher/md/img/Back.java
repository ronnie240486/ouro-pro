package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Compatibility view required by the original 6.1 layouts.
 * The catalog and Realm flows do not depend on this view.
 */
public class Back extends ImageView {
    private static final String DEFAULT_IMAGE_URL = "https://renciaapp-ldyffp73.manus.space/api/v4/bg.php";

    public Back(Context context) {
        super(context);
        initialize(context);
    }

    public Back(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public Back(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setBackgroundColor(0xFF000000);
        try {
            Glide.with(context).load(DEFAULT_IMAGE_URL).into(this);
        } catch (Throwable ignored) {
            // Keep the black fallback; startup must not fail because of the background.
        }
    }
}


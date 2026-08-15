package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/** Compatibility ImageView required by the original 6.1 layouts. */
public class Logo extends ImageView {
    private static final String DEFAULT_LOGO_URL = "https://renciaapp-ldyffp73.manus.space/api/v4/logo.php";

    public Logo(Context context) {
        super(context);
        initialize(context);
    }

    public Logo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public Logo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        try {
            Glide.with(context).load(DEFAULT_LOGO_URL).into(this);
        } catch (Throwable ignored) {
            // The drawable supplied by XML remains visible as a safe fallback.
        }
    }
}


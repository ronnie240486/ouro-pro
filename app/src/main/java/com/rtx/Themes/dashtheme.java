package com.rtx.Themes;

import android.content.Context;
import com.ouropro.player.R;
import com.rtx.Setting.Prefs;
import java.util.Random;

/* JADX INFO: loaded from: classes2.dex */
public class dashtheme {
    public static Context context;
    public static String mDashTheme;
    public static int[] mRandom = {R.layout.activity_home, R.layout.activity_home_1, R.layout.activity_home_2, R.layout.activity_home_3, R.layout.activity_home_4, R.layout.activity_home_5};

    public static int mNewDashtheme() {
        try {
            String string = Prefs.getString("mLayout", "theme_d");
            mDashTheme = string;
            if (string.equals("theme_d")) {
                return R.layout.activity_home;
            }
            if (mDashTheme.equals("theme_1")) {
                return R.layout.activity_home_1;
            }
            if (mDashTheme.equals("theme_2")) {
                return R.layout.activity_home_2;
            }
            if (mDashTheme.equals("theme_3")) {
                return R.layout.activity_home_3;
            }
            if (mDashTheme.equals("theme_4")) {
                return R.layout.activity_home_4;
            }
            if (mDashTheme.equals("theme_5")) {
                return R.layout.activity_home_5;
            }
            if (mDashTheme.equals("random")) {
                return mRandom[new Random().nextInt(mRandom.length)];
            }
            return 0;
        } catch (Exception e) {
            return R.layout.activity_home;
        }
    }
}

package com.ouropro.player.apps;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import androidx.multidex.MultiDexApplication;
import com.diegodev.travarlaucnher.md.img.EncryptedApiCaller;
import com.evgenii.jsevaluator.BuildConfig;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.CategoryModel;
import com.rtx.DNS.mConfig;
import com.rtx.Setting.JsonParserTask;
import com.rtx.Setting.Prefs;
import iptv.m3u.parser.M3UItem;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LTVApp extends MultiDexApplication {
    public static float SCREEN_HEIGHT = 1080.0f;
    public static float SCREEN_WIDTH = 1920.0f;
    public static long SEVER_OFFSET;
    public static String channelName;
    public static LTVApp instance;
    public static List<CategoryModel> vod_categories_filter = new ArrayList();
    public static List<CategoryModel> live_categories_filter = new ArrayList();
    public static List<CategoryModel> series_categories_filter = new ArrayList();
    public static String version_name = BuildConfig.VERSION_NAME;
    public static HomeType homeType = HomeType.live;
    private List<M3UItem> m3UChannelsItems = new ArrayList();
    private List<M3UItem> m3UVideosItems = new ArrayList();
    private List<M3UItem> m3USeriesItems = new ArrayList();

    public static LTVApp getInstance() {
        if (instance == null) {
            instance = new LTVApp();
        }
        return instance;
    }

    public List<M3UItem> getM3UChannelsItems() {
        return this.m3UChannelsItems;
    }

    public List<M3UItem> getM3USeriesItems() {
        return this.m3USeriesItems;
    }

    public List<M3UItem> getM3UVideosItems() {
        return this.m3UVideosItems;
    }

    public String getM3uDate() {
        return new PreferenceHelper(this).getSharedPreferenceLastM3uDate();
    }

    public void getNavigationBarHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        float f = displayMetrics.heightPixels;
        SCREEN_HEIGHT = f;
        float f2 = displayMetrics.widthPixels;
        SCREEN_WIDTH = f2;
        if (f2 < f) {
            SCREEN_WIDTH = f;
            SCREEN_HEIGHT = f2;
        }
    }

    public void loadVersion() {
        PackageInfo packageInfo;
        loadimage();
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        version_name = packageInfo.versionName;
    }

    public void onCreate() {
        super.onCreate();
        EncryptedApiCaller.callEncryptedMoviesApi(this);
        instance = this;
    }

    public void setM3UChannelsItems(List<M3UItem> list) {
        this.m3UChannelsItems = list;
    }

    public void setM3USeriesItems(List<M3UItem> list) {
        this.m3USeriesItems = list;
    }

    public void setM3UVideosItems(List<M3UItem> list) {
        this.m3UVideosItems = list;
    }

    public void setM3uDate(String str) {
        new PreferenceHelper(this).setSharedPreferenceLastM3uDate(str);
    }

    public void versionCheck() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    }

    public void loadimage() {
        try {
            new Prefs.Builder().setContext(this).setMode(0).setPrefsName("rtx_rebrand_by_sanoj").setUseDefaultSharedPreference(true).build();
            String url = mConfig.mAPI + "setting.php";
            JsonParserTask jsonParserTask = new JsonParserTask();
            jsonParserTask.execute(url);
        } catch (Exception e) {
        }
    }
}

package com.ouropro.player;

import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Base64;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ouropro.player.activities.ChangePlaylistActivity;
import com.ouropro.player.activities.HomeActivity;
import com.ouropro.player.apps.BaseActivity;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.dlgfragment.DescriptionDlgFragment;
import com.ouropro.player.dlgfragment.ExitDlgFragment;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SeriesModel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.remote.GetDataRequest;
import com.ouropro.player.utils.Security;
import com.ouropro.player.utils.Utils;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;
import pl.droidsonroids.gif.GifImageView;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends BaseActivity implements GetDataRequest.OnGetResponseListener {
    public AppInfoModel appInfoModel;
    public AppInfoModel.UrlModel currentUrlModel;
    public DescriptionDlgFragment descriptionDlgFragment;
    public ExitDlgFragment exitDlgFragment;
    public GifImageView image_loader;
    public PreferenceHelper preferenceHelper;
    public String subscription = "";
    public String description = "";
    public WordModels wordModels = new WordModels();
    public int failed_count = 0;
    public int playlist_position = 0;
    public String device_type = "tv";

    @RequiresApi(api = 23)
    private void CheckSDK23Permission() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (!addPermission(arrayList2, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            arrayList.add("READ / WRITE SD CARD");
        }
        if (arrayList2.size() > 0) {
            requestPermissions((String[]) arrayList2.toArray(new String[arrayList2.size()]), 124);
        } else {
            getMacAddress();
        }
    }

    @RequiresApi(api = 23)
    private boolean addPermission(List<String> list, String str) {
        if (ContextCompat.checkSelfPermission(this, str) == 0) {
            return true;
        }
        list.add(str);
        return shouldShowRequestPermissionRationale(str);
    }

    private void checkAppInfoModel(AppInfoModel appInfoModel) {
        long time;
        this.wordModels = GetSharedInfo.getWordModel(this);
        this.failed_count = 0;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd").parse(appInfoModel.getExpiredDate()).getTime();
        } catch (Exception unused) {
            time = 0;
        }
        if (time - new Date().getTime() >= 604800000 || appInfoModel.isIs_google_pay()) {
            if (appInfoModel.getResult().size() > 0) {
                loadingData();
                return;
            }
            this.subscription = "";
            String no_playlist_description = this.wordModels.getNo_playlist_description();
            this.description = no_playlist_description;
            showDescriptionDlgFragment(this.subscription, no_playlist_description, 0);
            return;
        }
        if (time - new Date().getTime() <= 0 || time - new Date().getTime() >= 604800000) {
            if (appInfoModel.getIs_trial() == 1) {
                this.subscription = this.wordModels.getTrial_ended();
            } else {
                this.subscription = this.wordModels.getTv_mac_expired();
            }
            String to_continue = this.wordModels.getTo_continue();
            this.description = to_continue;
            showDescriptionDlgFragment(this.subscription, to_continue, -1);
            return;
        }
        String str = this.wordModels.getSub_remaining() + " " + ((int) ((((time - new Date().getTime()) / 1000) / 3600) / 24)) + " " + this.wordModels.getDays();
        if (appInfoModel.getResult().size() <= 0 || appInfoModel.getResult().get(0).getId().equalsIgnoreCase("0")) {
            this.description = this.wordModels.getNo_playlist_description();
        } else {
            this.description = this.wordModels.getTo_add_manage();
        }
        showDescriptionDlgFragment(str, this.description, appInfoModel.getResult().size());
    }

    private void checkLocalStorageAccount() {
        if (Utils.ReadFile().isEmpty()) {
            AppInfoModel sharedPreferenceAppInfo = this.preferenceHelper.getSharedPreferenceAppInfo();
            this.appInfoModel = sharedPreferenceAppInfo;
            if (sharedPreferenceAppInfo != null) {
                checkAppInfoModel(sharedPreferenceAppInfo);
                return;
            }
            int i = this.failed_count + 1;
            this.failed_count = i;
            if (i < 3) {
                getUserInfoModel();
                return;
            } else {
                Toast.makeText(this, "Network Error! Please check your internet connection!", 0).show();
                return;
            }
        }
        AppInfoModel appInfoModel = (AppInfoModel) new Gson().fromJson(new String(Base64.decode(Utils.ReadFile(), 0), StandardCharsets.UTF_8).trim(), new TypeToken<AppInfoModel>() { // from class: com.ouropro.player.MainActivity.1
        }.getType());
        this.appInfoModel = appInfoModel;
        if (appInfoModel != null) {
            this.preferenceHelper.setSharedPreferenceAppInfo(appInfoModel);
            checkAppInfoModel(this.appInfoModel);
            this.preferenceHelper.setSharedPreferenceMacAddress(this.appInfoModel.getMac_address());
        } else {
            int i2 = this.failed_count + 1;
            this.failed_count = i2;
            if (i2 < 3) {
                getUserInfoModel();
            } else {
                Toast.makeText(this, "Network Error! Please check your internet connection!", 0).show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishApp() {
        List<ActivityManager.AppTask> appTasks;
        System.exit(0);
        Process.killProcess(Process.myPid());
        ActivityManager activityManager = (ActivityManager) getSystemService("activity");
        if (activityManager != null && (appTasks = activityManager.getAppTasks()) != null && appTasks.size() > 0) {
            appTasks.get(0).setExcludeFromRecents(true);
        }
        finishAndRemoveTask();
    }

    private void getMacAddress() {
        if (this.preferenceHelper.getSharedPreferenceLanguageCode() != null) {
            this.preferenceHelper.getSharedPreferenceLanguageCode();
        } else {
            this.preferenceHelper.setSharedPreferenceLanguageCode(Locale.getDefault().getLanguage());
        }
        if (this.preferenceHelper.getSharedPreferenceSubtitleSize() == 0) {
            if (GetSharedInfo.isTVDevice(this)) {
                this.preferenceHelper.setSharedPreferenceSubtitleSize(46);
            } else {
                this.preferenceHelper.setSharedPreferenceSubtitleSize(12);
            }
            if (Utils.IsAmazonDevice()) {
                this.preferenceHelper.setSharedPreferenceSubtitleSize(35);
            }
        }
        LTVApp.instance.versionCheck();
        LTVApp.instance.loadVersion();
        if (hasUsableLocalCatalog()) {
            openHomeFromCache();
            return;
        }
        getUserInfoModel();
    }

    private boolean hasUsableLocalCatalog() {
        try {
            if (this.preferenceHelper.getSharedPreferenceISM3U()
                    && this.realm.where(SeriesModel.class).count() < 100) {
                return false;
            }
            return this.realm.where(MovieModel.class).count() > 0
                    || this.realm.where(EPGChannel.class).count() > 0
                    || this.realm.where(SeriesModel.class).count() > 0;
        } catch (Exception unused) {
            return false;
        }
    }

    private void openHomeFromCache() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getUserInfoModel() {
        String strTrim = Security.getStringData(Utils.getDeviceId(this), LTVApp.version_name, false, this.preferenceHelper.getSharedPreferenceDeviceType()).trim();
        GetDataRequest getDataRequest = new GetDataRequest(this, 1000);
        getDataRequest.getResponse(Security.getJsonData(strTrim), Constants.second_response_url);
        getDataRequest.setOnGetResponseListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goToWebsite() {
        if (GetSharedInfo.isTVDevice(this)) {
            finishApp();
            return;
        }
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("Suporte com seu revendedor"));
            intent.addFlags(276856832);
            startActivity(intent);
        } catch (Exception unused) {
        }
    }

    private void setLoaderVisibility(int visibility) {
        if (this.image_loader != null) {
            this.image_loader.setVisibility(visibility);
        }
    }

    private boolean isLoaderVisible() {
        return this.image_loader != null && this.image_loader.getVisibility() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadingData() {
        setLoaderVisibility(0);
        this.playlist_position = GetSharedInfo.getPlaylistPosition(this);
        try {
            try {
                AppInfoModel appInfoModel = this.appInfoModel;
                if (appInfoModel == null || appInfoModel.getResult() == null || this.appInfoModel.getResult().size() <= 0 || this.appInfoModel.getResult().get(0).getId().equalsIgnoreCase("0")) {
                    startActivity(new Intent(this, (Class<?>) ChangePlaylistActivity.class));
                    finish();
                } else {
                    AppInfoModel.UrlModel urlModel = this.appInfoModel.getResult().get(this.playlist_position);
                    this.currentUrlModel = urlModel;
                    if (urlModel.getUrl().contains("username")) {
                        this.preferenceHelper.setSharedPreferenceISM3U(false);
                        goToLogin(this.currentUrlModel.getUrl(), this.wordModels);
                    } else if (GetSharedInfo.checkXUILink(this.currentUrlModel.getUrl())) {
                        this.preferenceHelper.setSharedPreferenceISM3U(false);
                        goToXUILogin(this.currentUrlModel.getUrl(), this.wordModels);
                    } else {
                        this.preferenceHelper.setSharedPreferenceISM3U(true);
                        reloadM3UData(this.currentUrlModel.getUrl(), this.wordModels);
                    }
                }
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            startActivity(new Intent(this, (Class<?>) ChangePlaylistActivity.class));
            finish();
        }
    }

    private void showDescriptionDlgFragment(String str, String str2, final int i) {
        setLoaderVisibility(8);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_description");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        DescriptionDlgFragment descriptionDlgFragmentNewInstance = DescriptionDlgFragment.newInstance(getApplicationContext(), str, str2, i);
        this.descriptionDlgFragment = descriptionDlgFragmentNewInstance;
        descriptionDlgFragmentNewInstance.setButtonClickListener(new DescriptionDlgFragment.ButtonClickListener() { // from class: com.ouropro.player.MainActivity.2
            public void onCancelClick() {
                MainActivity.this.finishApp();
            }

            public void onContinueClick() {
                if (i == -1) {
                    MainActivity.this.getUserInfoModel();
                } else {
                    MainActivity.this.loadingData();
                }
            }
        });
        this.descriptionDlgFragment.show(supportFragmentManager, "fragment_description");
    }

    private void showExitDlgFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_exit");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        ExitDlgFragment exitDlgFragmentNewInstance = ExitDlgFragment.newInstance(this.wordModels.getExit(), this.wordModels.getExit_description(), this.wordModels.getStr_yes(), this.wordModels.getNo());
        this.exitDlgFragment = exitDlgFragmentNewInstance;
        exitDlgFragmentNewInstance.setOkButtonClickListener(new ExitDlgFragment.OkButtonClickListener() { // from class: com.ouropro.player.MainActivity.3
            public void onCancelClick() {
            }

            public void onOkClick() {
                List<ActivityManager.AppTask> appTasks;
                System.exit(0);
                Process.killProcess(Process.myPid());
                ActivityManager activityManager = (ActivityManager) MainActivity.this.getSystemService("activity");
                if (activityManager != null && (appTasks = activityManager.getAppTasks()) != null && appTasks.size() > 0) {
                    appTasks.get(0).setExcludeFromRecents(true);
                }
                MainActivity.this.finishAndRemoveTask();
            }
        });
        this.exitDlgFragment.show(supportFragmentManager, "fragment_exit");
    }

    public void OnGetResponseResult(JSONObject jSONObject, int i) {
        if (jSONObject == null) {
            checkLocalStorageAccount();
            return;
        }
        if (i != 1000) {
            try {
                if (jSONObject.getBoolean(NotificationCompat.CATEGORY_STATUS)) {
                    Toast.makeText(this, this.wordModels.getActivate_success(), 0).show();
                    this.appInfoModel.setIs_trial(2);
                    this.appInfoModel.setIs_google_pay(true);
                    this.preferenceHelper.setSharedPreferenceIsPlaylistChanged(true);
                    this.preferenceHelper.setSharedPreferenceAppInfo(this.appInfoModel);
                    Utils.saveToFile(this.appInfoModel);
                    loadingData();
                    return;
                }
                return;
            } catch (Exception unused) {
                this.appInfoModel.setIs_trial(2);
                this.appInfoModel.setIs_google_pay(true);
                this.preferenceHelper.setSharedPreferenceIsPlaylistChanged(true);
                this.preferenceHelper.setSharedPreferenceAppInfo(this.appInfoModel);
                Utils.saveToFile(this.appInfoModel);
                loadingData();
                return;
            }
        }
        if (!jSONObject.has("data")) {
            checkLocalStorageAccount();
            return;
        }
        try {
            try {
                AppInfoModel appInfoModel = (AppInfoModel) new Gson().fromJson(new JSONObject(Security.getDecodedString(jSONObject.getString("data"))).toString(), AppInfoModel.class);
                this.appInfoModel = appInfoModel;
                this.preferenceHelper.setSharedPreferenceAppInfo(appInfoModel);
                this.preferenceHelper.setSharedPreferenceIsPlaylistChanged(false);
                this.preferenceHelper.setSharedPreferenceFirstLunch(false);
                this.preferenceHelper.setSharedPreferenceMacAddress(this.appInfoModel.getMac_address());
                this.preferenceHelper.setSharedPreferenceDeviceKey(this.appInfoModel.getDevice_key());
                Utils.saveToFile(this.appInfoModel);
                checkAppInfoModel(this.appInfoModel);
            } catch (Exception e) {
                checkLocalStorageAccount();
                e.printStackTrace();
            }
        } catch (Exception unused2) {
            checkLocalStorageAccount();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 4) {
            return super.dispatchKeyEvent(keyEvent);
        }
        if (isLoaderVisible()) {
            setStop(true);
            startActivity(new Intent(this, (Class<?>) ChangePlaylistActivity.class));
            finish();
        } else {
            setStop(true);
            showExitDlgFragment();
        }
        return true;
    }

    public final void doNextTask(boolean z) {
        if (z) {
            this.preferenceHelper.setSharedPreferenceLastPlaylistDate(System.currentTimeMillis() / 1000);
            startActivity(new Intent(this, (Class<?>) HomeActivity.class));
        } else {
            setLoaderVisibility(8);
            Toast.makeText(this, "" + this.wordModels.getPlaylist_is_not_working(), 0).show();
            startActivity(new Intent(this, (Class<?>) ChangePlaylistActivity.class));
        }
        finish();
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        Utils.FullScreenCall(this);
        this.preferenceHelper = new PreferenceHelper(this);
        this.image_loader = (GifImageView) findViewById(R.id.image_loader);
        ((TextView) findViewById(R.id.txt_resolution)).setText("");
        if (Utils.checkIsTelevision(this)) {
            this.device_type = "tv";
        } else {
            this.device_type = "mobile";
        }
        if (this.preferenceHelper.getSharedPreferenceDeviceType() == null) {
            this.preferenceHelper.setSharedPreferenceDeviceType(this.device_type);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            CheckSDK23Permission();
        } else {
            getMacAddress();
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        getMacAddress();
        super.onRequestPermissionsResult(i, strArr, iArr);
    }
}

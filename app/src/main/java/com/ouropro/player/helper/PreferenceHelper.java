package com.ouropro.player.helper;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.EpisodeModel;
import com.ouropro.player.models.LoginModel;
import com.ouropro.player.models.ResumeModel;
import com.ouropro.player.models.ResumeSeriesModel;
import com.ouropro.player.models.SubTitleUserModel;
import com.ouropro.player.improvements.SecurePreferenceStore;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PreferenceHelper {
    private static final String APP_INFO_MODEL = "APP_INFO_MODEL";
    private static final String CATEGORY_POS = "category_pos";
    private static final String CHANNEL_POS = "channel_pos";
    private static final String DEVICE_KEY = "device_key";
    private static final String DEVICE_TYPE = "device_type";
    private static final String EPISODE_MODELS = "episode_models";
    private static final String EPISODE_RESUME_MODEL = "episode_resume_model";
    private static final String EXTERNAL_PLAYER = "external_player";
    private static final String FAILOVER_TRANSITION_ID = "failover_transition_id";
    private static final String FAILOVER_ACKED_ALERTS = "failover_acked_alerts";
    private static final String FIRST_LUNCH = "first_lunch";
    private static final String FORWARD_STEP = "forward_step";
    private static final String INVISIBLE_LIVE_CATEGORIES = "invisible_live_categories";
    private static final String INVISIBLE_SERIES_CATEGORIES = "invisible_series_categories";
    private static final String INVISIBLE_VOD_CATEGORIES = "invisible_vod_categories";
    private static final String IS_DEMO = "is_demo";
    private static final String IS_GRID = "is_grid";
    private static final String IS_M3U = "is_m3u";
    private static final String IS_PLAYLIST_CHANGED = "is_changed";
    private static final String LANGUAGE_CODE = "language_code";
    private static final String LAST_EPG_DATE = "last_epg_date";
    private static final String LAST_M3U_DATE = "LastM3uDate";
    private static final String LAST_PLAYLIST_DATE = "last_playlist_date";
    private static final String M3U_EPG_URL = "m3u_epg_url";
    private static final String LIVE_CATEGORY = "live_category";
    private static final String LIVE_FAV_NAMES = "live_fav_names";
    private static final String LIVE_ORDER = "live_order";
    private static final String LIVE_STREAM_FORMAT = "live_stream_format";
    private static final String LOGIN_MODEL = "login_models";
    private static final String MAC_ADDRESS = "mac_address";
    private static final String PARENT_CONTROL = "parent_control";
    private static final String PASSWORD = "password";
    private static final String PLAYLIST_POSITION = "playlist_position";
    private static final String PREF_FILE = "PREF";
    private static final String SERIES_CATEGORY = "series_category";
    private static final String SERIES_FAV_NAMES = "series_fav_names";
    private static final String SERIES_ORDER = "series_order";
    private static final String SERIES_RECENT_NAMES = "series_recent_models";
    private static final String SERVER_URL = "server_url";
    private static final String SUBTITLE_BG_COLOR = "subtitle_bg_color";
    private static final String SUBTITLE_COLOR = "subtitle_color";
    private static final String SUBTITLE_ENABLE = "subtitle_enable";
    private static final String SUBTITLE_FONT = "subtitle_font";
    private static final String SUBTITLE_LOGIN = "subtitle_login";
    private static final String TIME_FORMAT = "time_format";
    private static final String UPDATE_PERIOD = "update_period";
    private static final String USERNAME = "user_name";
    private static final String USER_ID = "user_id";
    private static final String VOD_CATEGORY = "vod_category";
    private static final String VOD_FAV_NAMES = "vod_fav_names";
    private static final String VOD_ORDER = "vod_order";
    private static final String VOD_RESUME_MODEL = "vod_resume_model";
    public Gson gson = new Gson();
    public SharedPreferences settings;

    public PreferenceHelper(Context context) {
        this.settings = SecurePreferenceStore.open(context, PREF_FILE);
    }

    public List<CategoryModel> getSharedLiveCategoryModels() {
        try {
            String string = this.settings.getString(LIVE_CATEGORY, "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<CategoryModel>>() { // from class: com.ouropro.player.helper.PreferenceHelper.3
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public long getSharedPreferenceFailoverTransitionId() {
        try {
            return this.settings.getLong(FAILOVER_TRANSITION_ID, 0L);
        } catch (Exception unused) {
            return 0L;
        }
    }

    public boolean hasAcknowledgedFailoverAlert(long alertId) {
        try {
            String stored = this.settings.getString(FAILOVER_ACKED_ALERTS, "");
            if (stored == null || stored.isEmpty()) {
                return false;
            }
            String wanted = String.valueOf(alertId);
            for (String value : stored.split(",")) {
                if (wanted.equals(value)) {
                    return true;
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    public void markFailoverAlertAcknowledged(long alertId) {
        if (alertId <= 0L || hasAcknowledgedFailoverAlert(alertId)) {
            return;
        }
        String stored = this.settings.getString(FAILOVER_ACKED_ALERTS, "");
        String updated = stored == null || stored.isEmpty() ? String.valueOf(alertId) : stored + "," + alertId;
        String[] values = updated.split(",");
        int start = Math.max(0, values.length - 50);
        StringBuilder bounded = new StringBuilder();
        for (int i = start; i < values.length; i++) {
            if (bounded.length() > 0) {
                bounded.append(',');
            }
            bounded.append(values[i]);
        }
        this.settings.edit().putString(FAILOVER_ACKED_ALERTS, bounded.toString()).apply();
    }

    public AppInfoModel getSharedPreferenceAppInfo() {
        try {
            String string = this.settings.getString(APP_INFO_MODEL, "");
            if (string != null && !string.isEmpty()) {
                return (AppInfoModel) this.gson.fromJson(string, AppInfoModel.class);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public int getSharedPreferenceCategoryPos() {
        try {
            return this.settings.getInt(CATEGORY_POS + getSharedPreferenceUserId(), 0);
        } catch (Exception unused) {
            return 0;
        }
    }

    public int getSharedPreferenceChannelPos() {
        try {
            return this.settings.getInt(CHANNEL_POS + getSharedPreferenceUserId(), 0);
        } catch (Exception unused) {
            return 0;
        }
    }

    public String getSharedPreferenceDeviceKey() {
        try {
            return this.settings.getString(DEVICE_KEY, "");
        } catch (Exception unused) {
            return "";
        }
    }

    public String getSharedPreferenceDeviceType() {
        try {
            String string = this.settings.getString(DEVICE_TYPE, "");
            if (string == null || string.isEmpty()) {
                return null;
            }
            return string;
        } catch (Exception unused) {
            return null;
        }
    }

    public List<EpisodeModel> getSharedPreferenceEpisodeModels() {
        try {
            String string = this.settings.getString(EPISODE_MODELS, "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<EpisodeModel>>() { // from class: com.ouropro.player.helper.PreferenceHelper.13
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public int getSharedPreferenceExternalPlayer() {
        try {
            return this.settings.getInt(EXTERNAL_PLAYER, 0);
        } catch (Exception unused) {
            return 0;
        }
    }

    public boolean getSharedPreferenceFirstLunch() {
        try {
            return this.settings.getBoolean(FIRST_LUNCH, true);
        } catch (Exception unused) {
            return true;
        }
    }

    public int getSharedPreferenceForwardSetting() {
        try {
            return this.settings.getInt(FORWARD_STEP, 10);
        } catch (Exception unused) {
            return 10;
        }
    }

    public boolean getSharedPreferenceISM3U() {
        try {
            return this.settings.getBoolean(IS_M3U, false);
        } catch (Exception unused) {
            return false;
        }
    }

    public List<String> getSharedPreferenceInvisibleLiveCategories() {
        try {
            String string = this.settings.getString(INVISIBLE_LIVE_CATEGORIES + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<String>>() { // from class: com.ouropro.player.helper.PreferenceHelper.10
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public List<String> getSharedPreferenceInvisibleSeriesCategories() {
        try {
            String string = this.settings.getString(INVISIBLE_SERIES_CATEGORIES + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<String>>() { // from class: com.ouropro.player.helper.PreferenceHelper.12
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public List<String> getSharedPreferenceInvisibleVodCategories() {
        try {
            String string = this.settings.getString(INVISIBLE_VOD_CATEGORIES + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<String>>() { // from class: com.ouropro.player.helper.PreferenceHelper.11
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public boolean getSharedPreferenceIsDemo() {
        try {
            return this.settings.getBoolean(IS_DEMO, false);
        } catch (Exception unused) {
            return false;
        }
    }

    public boolean getSharedPreferenceIsGrid() {
        try {
            return this.settings.getBoolean(IS_GRID, false);
        } catch (Exception unused) {
            return false;
        }
    }

    public boolean getSharedPreferenceIsPlaylistChanged() {
        try {
            return this.settings.getBoolean(IS_PLAYLIST_CHANGED, false);
        } catch (Exception unused) {
            return false;
        }
    }

    public String getSharedPreferenceLanguageCode() {
        try {
            String string = this.settings.getString(LANGUAGE_CODE, "");
            if (string == null || string.isEmpty()) {
                return null;
            }
            return string;
        } catch (Exception unused) {
            return null;
        }
    }

    public String getSharedPreferenceLastEpgDate() {
        SharedPreferences sharedPreferences = this.settings;
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(LAST_EPG_DATE);
        sbM.append(getSharedPreferenceUserId());
        return sharedPreferences.getString(sbM.toString(), "");
    }

    public String getSharedPreferenceLastM3uDate() {
        try {
            String string = this.settings.getString(LAST_M3U_DATE + getSharedPreferenceUserId(), "");
            return (string == null || string.isEmpty()) ? "" : string;
        } catch (Exception unused) {
            return "";
        }
    }

    public String getSharedPreferenceM3UEpgUrl() {
        try {
            String value = this.settings.getString(M3U_EPG_URL + getSharedPreferenceUserId(), "");
            return value == null ? "" : value;
        } catch (Exception ignored) {
            return "";
        }
    }

    public long getSharedPreferenceLastPlaylistDate() {
        return this.settings.getLong(LAST_PLAYLIST_DATE, 0L);
    }

    public List<String> getSharedPreferenceLiveFavChannels() {
        try {
            String string = this.settings.getString(LIVE_FAV_NAMES + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<String>>() { // from class: com.ouropro.player.helper.PreferenceHelper.6
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public int getSharedPreferenceLiveOrder() {
        return this.settings.getInt(LIVE_ORDER, 0);
    }

    public String getSharedPreferenceLiveStreamFormat() {
        try {
            String string = this.settings.getString(LIVE_STREAM_FORMAT, "");
            return (string == null || string.isEmpty()) ? "ts" : string;
        } catch (Exception unused) {
            return "ts";
        }
    }

    public LoginModel getSharedPreferenceLoginModel() {
        try {
            String string = this.settings.getString(LOGIN_MODEL, "");
            if (string != null && !string.isEmpty()) {
                return (LoginModel) this.gson.fromJson(string, LoginModel.class);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public String getSharedPreferenceMacAddress() {
        try {
            String string = this.settings.getString(MAC_ADDRESS, "");
            if (string != null && !string.trim().isEmpty()) {
                return string;
            }
            AppInfoModel cachedInfo = getSharedPreferenceAppInfo();
            if (cachedInfo != null && cachedInfo.getMac_address() != null && !cachedInfo.getMac_address().trim().isEmpty()) {
                String cachedMac = cachedInfo.getMac_address().trim();
                this.settings.edit().putString(MAC_ADDRESS, cachedMac).apply();
                return cachedMac;
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public boolean isParentPasswordConfigured() {
        try {
            String value = this.settings.getString(PARENT_CONTROL, "");
            return value != null && value.matches("\\d{4}") && !"0000".equals(value);
        } catch (Exception unused) {
            return false;
        }
    }

    public String getSharedPreferenceParentPassword() {
        try {
            String string = this.settings.getString(PARENT_CONTROL, "");
            return string == null || string.isEmpty() || "0000".equals(string) ? "" : string;
        } catch (Exception unused) {
            return "";
        }
    }

    public String getSharedPreferencePassword() {
        try {
            String string = this.settings.getString(PASSWORD, "");
            return string == null ? "" : string;
        } catch (Exception unused) {
            return "";
        }
    }

    public int getSharedPreferencePlaylistPosition() {
        return this.settings.getInt(PLAYLIST_POSITION, 0);
    }

    public void setSharedPreferenceFailoverTransitionId(long transitionId) {
        this.settings.edit().putLong(FAILOVER_TRANSITION_ID, transitionId).apply();
    }

    public List<ResumeSeriesModel> getSharedPreferenceRecentSeriesNames() {
        try {
            String string = this.settings.getString(SERIES_RECENT_NAMES + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<ResumeSeriesModel>>() { // from class: com.ouropro.player.helper.PreferenceHelper.9
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public List<ResumeModel> getSharedPreferenceResumeModel() {
        try {
            String string = this.settings.getString(VOD_RESUME_MODEL + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<ResumeModel>>() { // from class: com.ouropro.player.helper.PreferenceHelper.1
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public List<CategoryModel> getSharedPreferenceSeriesCategoryModel() {
        try {
            String string = this.settings.getString(SERIES_CATEGORY, "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<CategoryModel>>() { // from class: com.ouropro.player.helper.PreferenceHelper.5
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public List<String> getSharedPreferenceSeriesFavNames() {
        try {
            String string = this.settings.getString(SERIES_FAV_NAMES + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<String>>() { // from class: com.ouropro.player.helper.PreferenceHelper.8
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public int getSharedPreferenceSeriesOrder() {
        return this.settings.getInt(SERIES_ORDER, 1);
    }

    public List<ResumeModel> getSharedPreferenceSeriesResumeModel() {
        try {
            String string = this.settings.getString(EPISODE_RESUME_MODEL + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<ResumeModel>>() { // from class: com.ouropro.player.helper.PreferenceHelper.2
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public String getSharedPreferenceServerUrl() {
        try {
            String string = this.settings.getString(SERVER_URL, "");
            return string == null ? "" : string;
        } catch (Exception unused) {
            return "";
        }
    }

    public String getSharedPreferenceSubtitleBgColor() {
        try {
            return this.settings.getString(SUBTITLE_BG_COLOR, "#00ffffff");
        } catch (Exception unused) {
            return "#00ffffff";
        }
    }

    public String getSharedPreferenceSubtitleColor() {
        try {
            return this.settings.getString(SUBTITLE_COLOR, "#ffffff");
        } catch (Exception unused) {
            return "#ffffff";
        }
    }

    public boolean getSharedPreferenceSubtitleEnable() {
        try {
            return this.settings.getBoolean(SUBTITLE_ENABLE, false);
        } catch (Exception unused) {
            return false;
        }
    }

    public int getSharedPreferenceSubtitleSize() {
        try {
            return this.settings.getInt(SUBTITLE_FONT, 0);
        } catch (Exception unused) {
            return 0;
        }
    }

    public SubTitleUserModel getSharedPreferenceSubtitleUserModel() {
        try {
            String string = this.settings.getString(SUBTITLE_LOGIN, "");
            if (string != null && !string.isEmpty()) {
                return (SubTitleUserModel) this.gson.fromJson(string, SubTitleUserModel.class);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public int getSharedPreferenceTimeFormat() {
        return this.settings.getInt(TIME_FORMAT, 0);
    }

    public int getSharedPreferenceUpdatePeriod() {
        return this.settings.getInt(UPDATE_PERIOD, 1);
    }

    public String getSharedPreferenceUserId() {
        try {
            return this.settings.getString(USER_ID, "");
        } catch (Exception unused) {
            return "";
        }
    }

    public String getSharedPreferenceUsername() {
        try {
            String string = this.settings.getString(USERNAME, "");
            return string == null ? "" : string;
        } catch (Exception unused) {
            return "";
        }
    }

    public List<CategoryModel> getSharedPreferenceVodCategory() {
        try {
            String string = this.settings.getString(VOD_CATEGORY, "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<CategoryModel>>() { // from class: com.ouropro.player.helper.PreferenceHelper.4
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public List<String> getSharedPreferenceVodFavNames() {
        try {
            String string = this.settings.getString(VOD_FAV_NAMES + getSharedPreferenceUserId(), "");
            if (string != null && !string.isEmpty()) {
                return (List) this.gson.fromJson(string, new TypeToken<List<String>>() { // from class: com.ouropro.player.helper.PreferenceHelper.7
                }.getType());
            }
            return new ArrayList();
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    public int getSharedPreferenceVodOrder() {
        return this.settings.getInt(VOD_ORDER, 1);
    }

    public void setSharedPreferenceAppInfo(AppInfoModel appInfoModel) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(APP_INFO_MODEL, this.gson.toJson(appInfoModel));
        editorEdit.apply();
        editorEdit.commit();
    }

    public void setSharedPreferenceCategoryPos(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(CATEGORY_POS);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putInt(sbM.toString(), i);
        editorEdit.apply();
    }

    public void setSharedPreferenceChannelPos(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(CHANNEL_POS);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putInt(sbM.toString(), i);
        editorEdit.apply();
    }

    public void setSharedPreferenceDeviceKey(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(DEVICE_KEY, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceDeviceType(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(DEVICE_TYPE, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceEpisodeModels(List<EpisodeModel> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(EPISODE_MODELS, this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceExternalPlayer(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(EXTERNAL_PLAYER, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceFirstLunch(boolean z) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putBoolean(FIRST_LUNCH, z);
        editorEdit.apply();
    }

    public void setSharedPreferenceForwardSetting(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(FORWARD_STEP, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceISM3U(boolean z) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putBoolean(IS_M3U, z);
        editorEdit.apply();
    }

    public void setSharedPreferenceInvisibleLiveCategories(List<String> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(INVISIBLE_LIVE_CATEGORIES);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceInvisibleSeriesCategories(List<String> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(INVISIBLE_SERIES_CATEGORIES);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceInvisibleVodCategories(List<String> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(INVISIBLE_VOD_CATEGORIES);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceIsDemo(boolean z) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putBoolean(IS_DEMO, z);
        editorEdit.apply();
    }

    public void setSharedPreferenceIsGrid(boolean z) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putBoolean(IS_GRID, z);
        editorEdit.apply();
    }

    public void setSharedPreferenceIsPlaylistChanged(boolean z) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putBoolean(IS_PLAYLIST_CHANGED, z);
        editorEdit.apply();
    }

    public void setSharedPreferenceLanguageCode(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(LANGUAGE_CODE, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceLastEpgDate(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(LAST_EPG_DATE);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), str);
        editorEdit.apply();
    }

    public void setSharedPreferenceLastM3uDate(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(LAST_M3U_DATE);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), str);
        editorEdit.apply();
    }

    public void setSharedPreferenceM3UEpgUrl(String url) {
        SharedPreferences.Editor editor = this.settings.edit();
        editor.putString(M3U_EPG_URL + getSharedPreferenceUserId(), url == null ? "" : url.trim());
        editor.apply();
    }

    public void setSharedPreferenceLastPlaylistDate(long j) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putLong(LAST_PLAYLIST_DATE, j);
        editorEdit.apply();
    }

    public void setSharedPreferenceLiveCategory(List<CategoryModel> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(LIVE_CATEGORY, this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceLiveFavChannels(List<String> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(LIVE_FAV_NAMES);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceLiveOrder(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(LIVE_ORDER, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceLiveStreamFormat(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(LIVE_STREAM_FORMAT, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceLoginModel(LoginModel loginModel) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(LOGIN_MODEL, this.gson.toJson(loginModel));
        editorEdit.apply();
    }

    public void setSharedPreferenceMacAddress(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(MAC_ADDRESS, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceParentPassword(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(PARENT_CONTROL, str);
        editorEdit.apply();
    }

    public void setSharedPreferencePassword(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(PASSWORD, str);
        editorEdit.apply();
    }

    public void setSharedPreferencePlaylistPosition(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(PLAYLIST_POSITION, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceRecentSeriesNames(List<ResumeSeriesModel> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(SERIES_RECENT_NAMES);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceResumeModel(List<ResumeModel> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(VOD_RESUME_MODEL);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
        editorEdit.commit();
    }

    public void setSharedPreferenceSeriesCategory(List<CategoryModel> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(SERIES_CATEGORY, this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceSeriesFavNames(List<String> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(SERIES_FAV_NAMES);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceSeriesOrder(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(SERIES_ORDER, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceSeriesResumeModel(List<ResumeModel> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(EPISODE_RESUME_MODEL);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
        editorEdit.commit();
    }

    public void setSharedPreferenceServerUrl(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(SERVER_URL, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceSubtitleBgColor(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(SUBTITLE_BG_COLOR, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceSubtitleColor(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(SUBTITLE_COLOR, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceSubtitleEnable(boolean z) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putBoolean(SUBTITLE_ENABLE, z);
        editorEdit.apply();
    }

    public void setSharedPreferenceSubtitleLoginModel(SubTitleUserModel subTitleUserModel) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(SUBTITLE_LOGIN, this.gson.toJson(subTitleUserModel));
        editorEdit.apply();
    }

    public void setSharedPreferenceSubtitleSize(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(SUBTITLE_FONT, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceTimeFormat(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(TIME_FORMAT, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceUpdatePeriod(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(UPDATE_PERIOD, i);
        editorEdit.apply();
    }

    public void setSharedPreferenceUserId(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(USER_ID, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceUsername(String str) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(USERNAME, str);
        editorEdit.apply();
    }

    public void setSharedPreferenceVodCategory(List<CategoryModel> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putString(VOD_CATEGORY, this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceVodFavNames(List<String> list) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m(VOD_FAV_NAMES);
        sbM.append(getSharedPreferenceUserId());
        editorEdit.putString(sbM.toString(), this.gson.toJson(list));
        editorEdit.apply();
    }

    public void setSharedPreferenceVodOrder(int i) {
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt(VOD_ORDER, i);
        editorEdit.apply();
    }
}

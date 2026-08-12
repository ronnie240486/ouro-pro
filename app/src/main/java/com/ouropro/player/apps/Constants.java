package com.ouropro.player.apps;

import android.content.Context;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.utils.EnigmaUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import okio.Utf8;

/* JADX INFO: loaded from: classes.dex */
public class Constants {
    public static final String API_KEY;
    public static final String APP_PNAME = "com.ouropro.player";
    public static final int CHANNEL_TYPE = 0;
    public static final String EPISODE_SUBTITLE_SEARCH;
    public static final String IMDB_API;
    public static final String IMDB_API_SERIES;
    public static final String IMDB_IMAGE_PREF;
    public static final String IMDB_KEY = "d96abf17668601f56b3d7b8336a61933";
    public static final int NO_MEDIA_TYPE = -1;
    public static final String PASSWORD;
    public static final String PERSON_API;
    public static final int SERIES_TYPE = 2;
    public static final String SUBTITLE_DOWNLOAD;
    public static final String SUBTITLE_LOGIN;
    public static final String SUBTITLE_SEARCH;
    public static final String USERNAME;
    public static final int VIDEO_TYPE = 1;
    public static final String WEB_URL = "/manage-playlists/login/";
    public static final String activate_pin = "";
    public static final String all_id = "all_id";
    public static final int create_request_code = 1000;
    public static final String create_url;
    public static final long date_mils = 72000;
    public static final int delete_request_code = 2000;
    public static final String delete_url;
    public static final String fav_id = "fav_id";
    public static final String response_url;
    public static final String resume_id = "resume_id";
    public static final String second_create_url;
    public static final String second_delete_url;
    public static final int second_request_code = 3000;
    public static final String second_response_url;
    public static SimpleDateFormat stampFormat;
    public static List<String> xxx_live_categories = new ArrayList();
    public static List<String> xxx_vod_categories = new ArrayList();

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    static {
        EnigmaUtils.enigmatization(new byte[]{90, 95, 34, Ascii.RS, -34, -107, -29, 48, 117, -87, -13, -123, 83, 76, -37, 102, -37, -69, Utf8.REPLACEMENT_BYTE, 7, 97, -40, -45, 117, -10, -79, 95, -71, 65, -30, 60, Ascii.RS, 65, -86, -73, -28, -57, 100, -8, 67, 50, -97, Ascii.GS, 19, Ascii.RS, 102, -98, -113});
        response_url = "https://renciaapp.manus.space/api/guim.php";
        second_response_url = "https://renciaapp.manus.space/api/guim.php";
        EnigmaUtils.enigmatization(new byte[]{90, 95, 34, Ascii.RS, -34, -107, -29, 48, 117, -87, -13, -123, 83, 76, -37, 102, -122, -114, 17, 117, -16, -107, 93, -48, -5, 86, -33, -24, -64, 107, 58, 60, 18, -22, 13, -118, 125, -68, 107, -64, 74, -91, 6, -67, -45, -19, -23, 80});
        create_url = "https://renciaapp.manus.space/api/";
        delete_url = EnigmaUtils.enigmatization(new byte[]{90, 95, 34, Ascii.RS, -34, -107, -29, 48, 117, -87, -13, -123, 83, 76, -37, 102, -122, -114, 17, 117, -16, -107, 93, -48, -5, 86, -33, -24, -64, 107, 58, 60, 125, 79, -95, 94, -22, -42, -101, -39, 113, Ascii.ESC, -15, 61, -48, 107, -97, -85});
        second_create_url = EnigmaUtils.enigmatization(new byte[]{75, 90, -32, 17, -98, -81, 92, -128, -101, 95, 38, -58, 105, -18, -22, -116, -16, 94, -72, 109, -69, -11, 82, 68, -101, -115, 68, 123, 111, -124, -70, 72, 51, -10, Ascii.NAK, 92, -73, Utf8.REPLACEMENT_BYTE, SignedBytes.MAX_POWER_OF_TWO, -70, -62, 93, 34, 54, 60, -92, 6, 120});
        second_delete_url = EnigmaUtils.enigmatization(new byte[]{75, 90, -32, 17, -98, -81, 92, -128, -101, 95, 38, -58, 105, -18, -22, -116, -16, 94, -72, 109, -69, -11, 82, 68, -101, -115, 68, 123, 111, -124, -70, 72, 101, 95, -124, 36, -91, -73, -72, -119, -21, 83, -22, -38, -83, 14, -91, -112});
        stampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        IMDB_API = EnigmaUtils.enigmatization(new byte[]{67, 60, -83, Ascii.GS, -74, -53, 68, -37, -52, -79, -15, 90, -11, -30, 57, -112, Ascii.DC4, 1, 93, 18, 103, -80, -62, 123, -88, -98, -12, -25, Ascii.DC4, 97, -121, 119, 126, -68, -37, 109, -114, 101, 74, -64, 2, 81, -29, 9, -82, 126, -100, -41});
        IMDB_API_SERIES = EnigmaUtils.enigmatization(new byte[]{67, 60, -83, Ascii.GS, -74, -53, 68, -37, -52, -79, -15, 90, -11, -30, 57, -112, -18, -87, -32, 12, 113, 74, 4, -3, 105, -49, -43, 74, -111, -26, 53, 1, -85, 113, -110, -91, -15, -86, -60, 117, -11, 73, -85, -104, -60, 58, -6, -96});
        PERSON_API = EnigmaUtils.enigmatization(new byte[]{67, 60, -83, Ascii.GS, -74, -53, 68, -37, -52, -79, -15, 90, -11, -30, 57, -112, 40, 91, 82, -122, -100, -78, Ascii.CAN, 82, Ascii.US, 109, 52, -35, -124, 111, 11, -104, -80, 37, -87, 16, -45, 66, 82, 105, 76, -106, 72, 37, Utf8.REPLACEMENT_BYTE, -24, -42, -114});
        IMDB_IMAGE_PREF = EnigmaUtils.enigmatization(new byte[]{98, -81, -26, -118, 32, 17, 91, 9, -124, -125, 115, -61, -102, 71, 98, -7, Ascii.CAN, 68, 58, 7, -94, 1, -32, -19, -36, 96, 111, -63, 8, -103, Ascii.GS, 116});
        SUBTITLE_LOGIN = EnigmaUtils.enigmatization(new byte[]{-62, 49, Ascii.SYN, -89, -68, 11, -113, 118, -18, Utf8.REPLACEMENT_BYTE, 88, 77, -79, 57, 80, -84, 94, -72, -115, -114, -114, 126, -1, 95, -20, 55, 50, 112, -15, -9, 67, 100, Ascii.RS, -99, -27, -28, -117, 71, -87, -20, 37, 17, -60, -49, 17, -67, -30, -21});
        USERNAME = EnigmaUtils.enigmatization(new byte[]{-37, 43, -47, -3, -21, 0, -75, -42, -40, 14, 124, Ascii.RS, 41, 89, -24, -74});
        PASSWORD = EnigmaUtils.enigmatization(new byte[]{6, -26, -12, 124, Ascii.EM, 9, 39, -51, 126, 7, -44, 125, 95, -61, -63, -45});
        SUBTITLE_SEARCH = EnigmaUtils.enigmatization(new byte[]{-62, 49, Ascii.SYN, -89, -68, 11, -113, 118, -18, Utf8.REPLACEMENT_BYTE, 88, 77, -79, 57, 80, -84, 94, -72, -115, -114, -114, 126, -1, 95, -20, 55, 50, 112, -15, -9, 67, 100, -97, 125, 73, -42, Ascii.RS, 121, -76, 3, -42, -54, -121, 62, -69, -38, -42, -48, -97, -39, -8, -50, -52, 110, 17, -76, -16, 71, -114, -73, -33, 99, -125, 116});
        EPISODE_SUBTITLE_SEARCH = EnigmaUtils.enigmatization(new byte[]{-62, 49, Ascii.SYN, -89, -68, 11, -113, 118, -18, Utf8.REPLACEMENT_BYTE, 88, 77, -79, 57, 80, -84, 94, -72, -115, -114, -114, 126, -1, 95, -20, 55, 50, 112, -15, -9, 67, 100, -97, 125, 73, -42, Ascii.RS, 121, -76, 3, -42, -54, -121, 62, -69, -38, -42, -48, -2, -3, -49, -106, 3, -55, 112, 13, -79, 80, -5, 107, Ascii.ESC, 7, 75, -78, Ascii.ESC, -124, 5, -78, -102, -65, Ascii.DC4, -72, 95, -64, -32, -1, 106, 69, 19, -99});
        SUBTITLE_DOWNLOAD = EnigmaUtils.enigmatization(new byte[]{-62, 49, Ascii.SYN, -89, -68, 11, -113, 118, -18, Utf8.REPLACEMENT_BYTE, 88, 77, -79, 57, 80, -84, 94, -72, -115, -114, -114, 126, -1, 95, -20, 55, 50, 112, -15, -9, 67, 100, 92, 82, 127, 5, -113, 4, 44, -60, 95, 54, 73, 76, -101, 88, -97, Ascii.RS});
        API_KEY = EnigmaUtils.enigmatization(new byte[]{-24, 77, -25, -8, -76, 17, -18, 80, -97, 50, Ascii.SYN, -47, 34, -102, -109, 81, 76, 33, 71, -101, 11, -105, 84, -102, 19, 117, -72, -54, 0, 119, 73, 105, 96, Ascii.GS, 90, 52, -19, -9, -10, -87, -40, -103, -62, -105, -5, Ascii.RS, -2, -83});
    }

    public static void getLiveGroupModels(List<String> list, Context context) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        LTVApp.live_categories_filter = new ArrayList();
        List<CategoryModel> sharedLiveCategoryModels = preferenceHelper.getSharedLiveCategoryModels();
        LTVApp.live_categories_filter.addAll(sharedLiveCategoryModels);
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < sharedLiveCategoryModels.size(); i++) {
            CategoryModel categoryModel = sharedLiveCategoryModels.get(i);
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().equalsIgnoreCase(categoryModel.getId())) {
                    LTVApp.live_categories_filter.remove(categoryModel);
                }
            }
        }
    }

    public static void getSeriesGroupModels(List<String> list, Context context) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        LTVApp.series_categories_filter = new ArrayList();
        List<CategoryModel> sharedPreferenceSeriesCategoryModel = preferenceHelper.getSharedPreferenceSeriesCategoryModel();
        LTVApp.series_categories_filter.addAll(sharedPreferenceSeriesCategoryModel);
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < sharedPreferenceSeriesCategoryModel.size(); i++) {
            CategoryModel categoryModel = sharedPreferenceSeriesCategoryModel.get(i);
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().equalsIgnoreCase(categoryModel.getId())) {
                    LTVApp.series_categories_filter.remove(categoryModel);
                }
            }
        }
    }

    public static void getVodGroupModels(List<String> list, Context context) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        LTVApp.vod_categories_filter = new ArrayList();
        List<CategoryModel> sharedPreferenceVodCategory = preferenceHelper.getSharedPreferenceVodCategory();
        LTVApp.vod_categories_filter.addAll(sharedPreferenceVodCategory);
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < sharedPreferenceVodCategory.size(); i++) {
            CategoryModel categoryModel = sharedPreferenceVodCategory.get(i);
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().equalsIgnoreCase(categoryModel.getId())) {
                    LTVApp.vod_categories_filter.remove(categoryModel);
                }
            }
        }
    }

    public static void setServerTimeOffset(long j, String str) {
        try {
            LTVApp.SEVER_OFFSET = (j * 1000) - stampFormat.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

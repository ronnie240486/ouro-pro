package com.ouropro.player.apps;

import android.accounts.NetworkErrorException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.fragment.app.FragmentActivity;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.EpisodeModel;
import com.ouropro.player.models.LoginResponse;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.ResumeModel;
import com.ouropro.player.models.ResumeSeriesModel;
import com.ouropro.player.models.SeriesModel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.improvements.M3USeriesNaming;
import com.ouropro.player.improvements.M3USeriesRebuilder;
import com.ouropro.player.improvements.M3UAccountEndpoint;
import com.ouropro.player.improvements.PlaylistFailoverManager;
import com.ouropro.player.improvements.NullTextGuard;
import com.ouropro.player.improvements.StreamingM3UImporter;
import com.ouropro.player.improvements.SeriesCatalogDeduplicator;
import com.ouropro.player.improvements.MovieCatalogDeduplicator;
import com.ouropro.player.net.FetchChannelsTask;
import com.ouropro.player.net.FetchEpisodeTask;
import com.ouropro.player.net.FetchM3uItemsTask;
import com.ouropro.player.net.FetchVideosTask;
import com.ouropro.player.net.LoadChannelsCommand;
import com.ouropro.player.net.LoadEpisodeCommand;
import com.ouropro.player.net.LoadVideosCommand;
import com.ouropro.player.net.NetworkTask;
import com.ouropro.player.remote.RetroClass;
import com.ouropro.player.utils.Utils;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmResults;
import iptv.m3u.parser.M3UItem;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        NullTextGuard.sanitize(this);
    }

    private static final int M3U_SERIES_SCHEMA_VERSION = 6;
    private static final String M3U_MIGRATION_PREFS = "ouropro_migrations";
    public static boolean busy;
    private HashMap<String, String> categoryHashMap;
    private HashMap<String, List<EpisodeModel>> episodeModelHashMap;
    private NetworkTask<Void, Void, List<EPGChannel>> fetchChannelsTask;
    private NetworkTask<Void, Void, List<EpisodeModel>> fetchEpisodesTask;
    private NetworkTask<Void, Void, List<M3UItem>> fetchM3uItemsTask;
    private NetworkTask<Void, Void, List<MovieModel>> fetchVideosTask;
    private volatile boolean m3uVisibleNavigationSent;
    public String password;
    public PreferenceHelper preferenceHelper;
    public Realm realm;
    public String user;
    public String user_id;
    public WordModels wordModels = new WordModels();
    public boolean is_stop = false;
    public int error_account = 0;
    private final LTVApp model = LTVApp.getInstance();
    private boolean failoverReceiverRegistered;
    private final BroadcastReceiver failoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (PlaylistFailoverManager.ACTION_EXPIRATION_NOTICE.equals(intent.getAction())) {
                String key = intent.getStringExtra(PlaylistFailoverManager.EXTRA_EXPIRATION_KEY);
                String title = intent.getStringExtra(PlaylistFailoverManager.EXTRA_EXPIRATION_TITLE);
                String message = intent.getStringExtra(PlaylistFailoverManager.EXTRA_EXPIRATION_MESSAGE);
                if (key == null || title == null || message == null) {
                    return;
                }
                new android.app.AlertDialog.Builder(BaseActivity.this).setTitle(title).setMessage(message).setPositiveButton("OK", (dialog, which) -> {
                    new PreferenceHelper(BaseActivity.this).markExpirationModalShown(key);
                }).setOnDismissListener(dialog -> new PreferenceHelper(BaseActivity.this).markExpirationModalShown(key)).show();
                return;
            }
            if (!PlaylistFailoverManager.ACTION_PLAYLIST_FAILOVER_SYNC.equals(intent.getAction())) {
                return;
            }
            String playlistUrl = intent.getStringExtra(PlaylistFailoverManager.EXTRA_PLAYLIST_URL);
            if (playlistUrl == null || playlistUrl.trim().isEmpty()) {
                return;
            }
            preferenceHelper = new PreferenceHelper(BaseActivity.this);
            preferenceHelper.setSharedPreferencePlaylistPosition(intent.getIntExtra(PlaylistFailoverManager.EXTRA_PLAYLIST_POSITION, 0));
            preferenceHelper.setSharedPreferenceISM3U(true);
            setStop(false);
            if (BaseActivity.isBusy()) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> reloadM3UData(playlistUrl, wordModels), 10000L);
            } else {
                reloadM3UData(playlistUrl, wordModels);
            }
        }
    };

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseActivity$10, reason: invalid class name */
    public class AnonymousClass10 implements Callback<List<SeriesModel>> {
        public final /* synthetic */ List val$series_favorites;
        public final /* synthetic */ List val$series_recent;

        public AnonymousClass10(List list, List list2) {
            this.val$series_favorites = list;
            this.val$series_recent = list2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$0(Response response, Realm realm) {
            Object body = response.body();
            if (body instanceof Collection && !((Collection) body).isEmpty()) {
                realm.insertOrUpdate((Collection<? extends RealmModel>) body);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$1(String str, Realm realm) {
            SeriesModel seriesModel = (SeriesModel) Insets$$ExternalSyntheticOutline0.m(realm, SeriesModel.class, "name", str);
            if (seriesModel != null) {
                seriesModel.setIs_favorite(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$2(ResumeSeriesModel resumeSeriesModel, Realm realm) {
            SeriesModel seriesModel = (SeriesModel) realm.where(SeriesModel.class).equalTo("name", resumeSeriesModel.getName()).findFirst();
            if (seriesModel != null) {
                seriesModel.setIs_recent(true);
                seriesModel.setSeason_pos(resumeSeriesModel.getSeason_pos());
                seriesModel.setEpisode_pos(resumeSeriesModel.getEpisode_pos());
            }
        }

        public void onFailure(@NonNull Call<List<SeriesModel>> call, @NonNull Throwable th) {
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            int i = baseActivity.error_account;
            if (i >= 2) {
                baseActivity.doNextTask(true);
            } else {
                baseActivity.error_account = i + 1;
                baseActivity.getSecondSeriesStreams();
            }
        }

        public void onResponse(@NonNull Call<List<SeriesModel>> call, @NonNull Response<List<SeriesModel>> response) {
            int i = 1;
            if (response.body() != null) {
                BaseActivity.this.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, i));
                if (this.val$series_favorites.size() > 0) {
                    Iterator it = this.val$series_favorites.iterator();
                    while (it.hasNext()) {
                        BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), i));
                    }
                }
                if (this.val$series_recent.size() > 0) {
                    Iterator it2 = this.val$series_recent.iterator();
                    while (it2.hasNext()) {
                        BaseActivity.this.realm.executeTransactionAsync(new BaseActivity$$ExternalSyntheticLambda6((ResumeSeriesModel) it2.next(), i));
                    }
                }
            }
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            baseActivity.doNextTask(true);
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseActivity$5, reason: invalid class name */
    public class AnonymousClass5 implements Callback<List<EPGChannel>> {
        public final /* synthetic */ List val$live_favorites;

        public AnonymousClass5(List list) {
            this.val$live_favorites = list;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$0(Response response, Realm realm) {
            realm.where(EPGChannel.class).findAll().deleteAllFromRealm();
            realm.insertOrUpdate((Collection<? extends RealmModel>) response.body());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$1(String str, Realm realm) {
            EPGChannel ePGChannel = (EPGChannel) Insets$$ExternalSyntheticOutline0.m(realm, EPGChannel.class, "name", str);
            if (ePGChannel != null) {
                ePGChannel.setIs_favorite(true);
            }
        }

        public void onFailure(@NonNull Call<List<EPGChannel>> call, @NonNull Throwable th) {
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            int i = baseActivity.error_account;
            if (i >= 2) {
                baseActivity.getSecondLiveStreams();
            } else {
                baseActivity.error_account = i + 1;
                baseActivity.getLiveStreams();
            }
        }

        public void onResponse(@NonNull Call<List<EPGChannel>> call, @NonNull Response<List<EPGChannel>> response) {
            if (response.body() == null) {
                BaseActivity baseActivity = BaseActivity.this;
                if (baseActivity.is_stop) {
                    return;
                }
                baseActivity.getSecondLiveStreams();
                return;
            }
            int i = 0;
            BaseActivity.this.error_account = 0;
            if (response.body().size() <= 0) {
                BaseActivity.this.getSecondLiveStreams();
                return;
            }
            BaseActivity.this.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, i));
            if (this.val$live_favorites.size() > 0) {
                Iterator it = this.val$live_favorites.iterator();
                while (it.hasNext()) {
                    BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 2));
                }
            }
            BaseActivity baseActivity2 = BaseActivity.this;
            if (baseActivity2.is_stop) {
                return;
            }
            baseActivity2.getVodStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseActivity$6, reason: invalid class name */
    public class AnonymousClass6 implements Callback<List<EPGChannel>> {
        public final /* synthetic */ List val$live_favorites;

        public AnonymousClass6(List list) {
            this.val$live_favorites = list;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$0(Response response, Realm realm) {
            realm.where(EPGChannel.class).findAll().deleteAllFromRealm();
            realm.insertOrUpdate((Collection<? extends RealmModel>) response.body());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$1(String str, Realm realm) {
            EPGChannel ePGChannel = (EPGChannel) Insets$$ExternalSyntheticOutline0.m(realm, EPGChannel.class, "name", str);
            if (ePGChannel != null) {
                ePGChannel.setIs_favorite(true);
            }
        }

        public void onFailure(@NonNull Call<List<EPGChannel>> call, @NonNull Throwable th) {
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            int i = baseActivity.error_account;
            if (i >= 2) {
                baseActivity.getSecondVodStreams();
            } else {
                baseActivity.error_account = i + 1;
                baseActivity.getSecondLiveStreams();
            }
        }

        public void onResponse(@NonNull Call<List<EPGChannel>> call, @NonNull Response<List<EPGChannel>> response) {
            if (response.body() != null) {
                BaseActivity baseActivity = BaseActivity.this;
                baseActivity.error_account = 0;
                baseActivity.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 2));
                if (this.val$live_favorites.size() > 0) {
                    Iterator it = this.val$live_favorites.iterator();
                    while (it.hasNext()) {
                        BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 3));
                    }
                }
            }
            BaseActivity baseActivity2 = BaseActivity.this;
            if (baseActivity2.is_stop) {
                return;
            }
            baseActivity2.getSecondVodStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseActivity$7, reason: invalid class name */
    public class AnonymousClass7 implements Callback<List<MovieModel>> {
        public final /* synthetic */ List val$resumeModels;
        public final /* synthetic */ List val$vod_favorites;

        public AnonymousClass7(List list, List list2) {
            this.val$vod_favorites = list;
            this.val$resumeModels = list2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$0(Response response, Realm realm) {
            realm.where(MovieModel.class).findAll().deleteAllFromRealm();
            realm.insertOrUpdate((Collection<? extends RealmModel>) response.body());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$1(String str, Realm realm) {
            MovieModel movieModel = (MovieModel) Insets$$ExternalSyntheticOutline0.m(realm, MovieModel.class, "name", str);
            if (movieModel != null) {
                movieModel.setIs_favorite(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$2(ResumeModel resumeModel, Realm realm) {
            MovieModel movieModel = (MovieModel) realm.where(MovieModel.class).equalTo("name", resumeModel.getName()).findFirst();
            if (movieModel != null) {
                movieModel.setIs_recent(true);
                movieModel.setPro(resumeModel.getPro());
                movieModel.setTime(resumeModel.getLast_position());
                movieModel.setTmdb_id(resumeModel.getTmdb_id());
            }
        }

        public void onFailure(@NonNull Call<List<MovieModel>> call, @NonNull Throwable th) {
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            int i = baseActivity.error_account;
            if (i >= 2) {
                baseActivity.getSeriesStreams();
            } else {
                baseActivity.error_account = i + 1;
                baseActivity.getVodStreams();
            }
        }

        public void onResponse(@NonNull Call<List<MovieModel>> call, @NonNull Response<List<MovieModel>> response) {
            if (response.body() != null) {
                BaseActivity baseActivity = BaseActivity.this;
                baseActivity.error_account = 0;
                baseActivity.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 3));
                if (this.val$vod_favorites.size() > 0) {
                    Iterator it = this.val$vod_favorites.iterator();
                    while (it.hasNext()) {
                        BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 4));
                    }
                }
                if (this.val$resumeModels.size() > 0) {
                    Iterator it2 = this.val$resumeModels.iterator();
                    while (it2.hasNext()) {
                        BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda5((ResumeModel) it2.next(), 1));
                    }
                }
            }
            BaseActivity baseActivity2 = BaseActivity.this;
            if (baseActivity2.is_stop) {
                return;
            }
            baseActivity2.getSeriesStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseActivity$8, reason: invalid class name */
    public class AnonymousClass8 implements Callback<List<MovieModel>> {
        public final /* synthetic */ List val$resumeModels;
        public final /* synthetic */ List val$vod_favorites;

        public AnonymousClass8(List list, List list2) {
            this.val$vod_favorites = list;
            this.val$resumeModels = list2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$0(Response response, Realm realm) {
            realm.where(MovieModel.class).findAll().deleteAllFromRealm();
            realm.insertOrUpdate((Collection<? extends RealmModel>) response.body());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$1(String str, Realm realm) {
            MovieModel movieModel = (MovieModel) Insets$$ExternalSyntheticOutline0.m(realm, MovieModel.class, "name", str);
            if (movieModel != null) {
                movieModel.setIs_favorite(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$2(ResumeModel resumeModel, Realm realm) {
            MovieModel movieModel = (MovieModel) realm.where(MovieModel.class).equalTo("name", resumeModel.getName()).findFirst();
            if (movieModel != null) {
                movieModel.setIs_recent(true);
                movieModel.setPro(resumeModel.getPro());
                movieModel.setTime(resumeModel.getLast_position());
                movieModel.setTmdb_id(resumeModel.getTmdb_id());
            }
        }

        public void onFailure(@NonNull Call<List<MovieModel>> call, @NonNull Throwable th) {
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            int i = baseActivity.error_account;
            if (i >= 2) {
                baseActivity.getSecondSeriesStreams();
            } else {
                baseActivity.error_account = i + 1;
                baseActivity.getSecondVodStreams();
            }
        }

        public void onResponse(@NonNull Call<List<MovieModel>> call, @NonNull Response<List<MovieModel>> response) {
            if (response.body() != null) {
                BaseActivity baseActivity = BaseActivity.this;
                baseActivity.error_account = 0;
                baseActivity.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 4));
                if (this.val$vod_favorites.size() > 0) {
                    Iterator it = this.val$vod_favorites.iterator();
                    while (it.hasNext()) {
                        BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 5));
                    }
                }
                if (this.val$resumeModels.size() > 0) {
                    Iterator it2 = this.val$resumeModels.iterator();
                    while (it2.hasNext()) {
                        BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda5((ResumeModel) it2.next(), 2));
                    }
                }
            }
            BaseActivity baseActivity2 = BaseActivity.this;
            if (baseActivity2.is_stop) {
                return;
            }
            baseActivity2.getSecondSeriesStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseActivity$9, reason: invalid class name */
    public class AnonymousClass9 implements Callback<List<SeriesModel>> {
        public final /* synthetic */ List val$series_favorites;
        public final /* synthetic */ List val$series_recent;

        public AnonymousClass9(List list, List list2) {
            this.val$series_favorites = list;
            this.val$series_recent = list2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$0(Response response, Realm realm) {
            Object body = response.body();
            if (body instanceof Collection && !((Collection) body).isEmpty()) {
                realm.insertOrUpdate((Collection<? extends RealmModel>) body);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$1(String str, Realm realm) {
            SeriesModel seriesModel = (SeriesModel) Insets$$ExternalSyntheticOutline0.m(realm, SeriesModel.class, "name", str);
            if (seriesModel != null) {
                seriesModel.setIs_favorite(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onResponse$2(ResumeSeriesModel resumeSeriesModel, Realm realm) {
            SeriesModel seriesModel = (SeriesModel) realm.where(SeriesModel.class).equalTo("name", resumeSeriesModel.getName()).findFirst();
            if (seriesModel != null) {
                seriesModel.setIs_recent(true);
                seriesModel.setSeason_pos(resumeSeriesModel.getSeason_pos());
                seriesModel.setEpisode_pos(resumeSeriesModel.getEpisode_pos());
            }
        }

        public void onFailure(@NonNull Call<List<SeriesModel>> call, @NonNull Throwable th) {
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            int i = baseActivity.error_account;
            if (i >= 2) {
                baseActivity.doNextTask(true);
            } else {
                baseActivity.error_account = i + 1;
                baseActivity.getSeriesStreams();
            }
        }

        public void onResponse(@NonNull Call<List<SeriesModel>> call, @NonNull Response<List<SeriesModel>> response) {
            if (response.body() != null) {
                BaseActivity.this.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 5));
                if (this.val$series_favorites.size() > 0) {
                    Iterator it = this.val$series_favorites.iterator();
                    while (it.hasNext()) {
                        BaseActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 6));
                    }
                }
                if (this.val$series_recent.size() > 0) {
                    Iterator it2 = this.val$series_recent.iterator();
                    while (it2.hasNext()) {
                        BaseActivity.this.realm.executeTransactionAsync(new BaseActivity$$ExternalSyntheticLambda6((ResumeSeriesModel) it2.next(), 2));
                    }
                }
            }
            BaseActivity baseActivity = BaseActivity.this;
            if (baseActivity.is_stop) {
                return;
            }
            baseActivity.doNextTask(true);
        }
    }

    private void addChannelToCategory(EPGChannel ePGChannel) {
        String keyFromCategoryName = getKeyFromCategoryName(ePGChannel.getCategory_name());
        if (this.categoryHashMap.get(keyFromCategoryName) == null) {
            keyFromCategoryName = new Date().getTime() + "!@#%" + ePGChannel.getCategory_name();
        }
        this.categoryHashMap.put(keyFromCategoryName, ePGChannel.getCategory_name());
    }

    private void addEpisodeToSeries(EpisodeModel episodeModel) {
        String series_name = episodeModel.getSeries_name();
        if (series_name == null || series_name.equals("null")) {
            series_name = "All";
        }
        List<EpisodeModel> list = this.episodeModelHashMap.get(series_name);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(episodeModel);
        this.episodeModelHashMap.put(series_name, list);
    }

    private void addMovieToCategory(MovieModel movieModel) {
        String keyFromCategoryName = getKeyFromCategoryName(movieModel.getCategory_name());
        if (this.categoryHashMap.get(keyFromCategoryName) == null) {
            keyFromCategoryName = new Date().getTime() + "!@#%" + movieModel.getCategory_name();
        }
        this.categoryHashMap.put(keyFromCategoryName, movieModel.getCategory_name());
    }

    private void addSeriesToCategory(SeriesModel seriesModel) {
        String keyFromCategoryName = getKeyFromCategoryName(seriesModel.getCategory_name());
        if (this.categoryHashMap.get(keyFromCategoryName) == null) {
            keyFromCategoryName = new Date().getTime() + "!@#%" + seriesModel.getCategory_name();
        }
        this.categoryHashMap.put(keyFromCategoryName, seriesModel.getCategory_name());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void authentication(final String str) {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).authentication(this.user, this.password).enqueue(new Callback<LoginResponse>() { // from class: com.ouropro.player.apps.BaseActivity.1
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable th) {
                    BaseActivity baseActivity = BaseActivity.this;
                    if (baseActivity.is_stop) {
                        return;
                    }
                    int i = baseActivity.error_account;
                    if (i < 2) {
                        baseActivity.error_account = i + 1;
                        baseActivity.authentication(str);
                    } else {
                        baseActivity.preferenceHelper.setSharedPreferenceISM3U(true);
                        BaseActivity baseActivity2 = BaseActivity.this;
                        baseActivity2.reloadM3UData(str, baseActivity2.wordModels);
                    }
                }

                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.body() == null || response.body().getUser_info() == null || response.body().getUser_info().getStatus() == null) {
                        BaseActivity.this.preferenceHelper.setSharedPreferenceISM3U(true);
                        BaseActivity baseActivity = BaseActivity.this;
                        baseActivity.reloadM3UData(str, baseActivity.wordModels);
                        return;
                    }
                    BaseActivity.this.error_account = 0;
                    if (!response.body().getUser_info().getStatus().equalsIgnoreCase("Active")) {
                        Toast.makeText(BaseActivity.this.getApplicationContext(), BaseActivity.this.wordModels.getAccount_expired(), 1).show();
                        BaseActivity.this.doNextTask(false);
                        return;
                    }
                    BaseActivity.this.preferenceHelper.setSharedPreferenceLoginModel(response.body().getUser_info());
                    BaseActivity baseActivity2 = BaseActivity.this;
                    baseActivity2.preferenceHelper.setSharedPreferenceUsername(baseActivity2.user);
                    BaseActivity baseActivity3 = BaseActivity.this;
                    baseActivity3.preferenceHelper.setSharedPreferencePassword(baseActivity3.password);
                    Constants.setServerTimeOffset(response.body().getServerModel().getTimestamp_now(), response.body().getServerModel().getTime_now());
                    BaseActivity baseActivity4 = BaseActivity.this;
                    if (baseActivity4.is_stop) {
                        return;
                    }
                    baseActivity4.getLiveCategory();
                }
            });
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            this.preferenceHelper.setSharedPreferenceISM3U(true);
            reloadM3UData(str, this.wordModels);
        }
    }

    private void fetchM3UItems(String str) {
        NetworkTask<Void, Void, List<M3UItem>> networkTask = this.fetchM3uItemsTask;
        if (networkTask != null && !networkTask.isComplete()) {
            this.fetchM3uItemsTask.abort();
        }
        FetchM3uItemsTask fetchM3uItemsTask = new FetchM3uItemsTask(str, null);
        this.fetchM3uItemsTask = fetchM3uItemsTask;
        fetchM3uItemsTask.setOnCompleteListener(new BaseActivity$$ExternalSyntheticLambda4(this, 2));
        this.fetchM3uItemsTask.setOnGenericExceptionListener(new BaseActivity$$ExternalSyntheticLambda4(this, 3));
        this.fetchM3uItemsTask.setOnNetworkUnavailableListener(new BaseActivity$$ExternalSyntheticLambda4(this, 4));
        this.fetchM3uItemsTask.execute();
    }

    private String getCategoryNameFromKey(String str) {
        return str.contains("!@#%") ? str.split("!@#%")[1] : "All";
    }

    private void getChannelModels() {
        RealmResults realmResultsFindAll = this.realm.where(EPGChannel.class).findAll();
        List<String> sharedPreferenceLiveFavChannels = this.preferenceHelper.getSharedPreferenceLiveFavChannels();
        if (realmResultsFindAll.size() != 0) {
            if (System.currentTimeMillis() / 1000 <= (((long) this.preferenceHelper.getSharedPreferenceUpdatePeriod()) * Constants.date_mils) + this.preferenceHelper.getSharedPreferenceLastPlaylistDate()) {
                if (this.is_stop) {
                    return;
                }
                getLiveCategoryModels(new ArrayList(realmResultsFindAll));
                return;
            }
        }
        NetworkTask<Void, Void, List<EPGChannel>> networkTask = this.fetchChannelsTask;
        if (networkTask != null && !networkTask.isComplete()) {
            this.fetchChannelsTask.abort();
        }
        FetchChannelsTask fetchChannelsTask = new FetchChannelsTask();
        this.fetchChannelsTask = fetchChannelsTask;
        fetchChannelsTask.setOnCompleteListener(new BaseActivity$$ExternalSyntheticLambda2((FragmentActivity) this, realmResultsFindAll, (List) sharedPreferenceLiveFavChannels, 0));
        this.fetchChannelsTask.setOnGenericExceptionListener(new BaseActivity$$ExternalSyntheticLambda4(this, 1));
        this.fetchChannelsTask.execute();
    }

    private void getEpisodeModels() {
        RealmResults realmResultsFindAll = this.realm.where(EpisodeModel.class).findAll();
        if (realmResultsFindAll.size() != 0) {
            boolean m3uNeedsRebuild = needsM3USeriesRebuild();
            if (!m3uNeedsRebuild && System.currentTimeMillis() / 1000 <= (((long) this.preferenceHelper.getSharedPreferenceUpdatePeriod()) * Constants.date_mils) + this.preferenceHelper.getSharedPreferenceLastPlaylistDate()) {
                if (this.is_stop) {
                    return;
                }
                getSeriesFromEpisodes(new ArrayList(realmResultsFindAll));
                return;
            }
        }
        NetworkTask<Void, Void, List<EpisodeModel>> networkTask = this.fetchEpisodesTask;
        if (networkTask != null && !networkTask.isComplete()) {
            this.fetchEpisodesTask.abort();
        }
        FetchEpisodeTask fetchEpisodeTask = new FetchEpisodeTask();
        this.fetchEpisodesTask = fetchEpisodeTask;
        fetchEpisodeTask.setOnCompleteListener(new BaseActivity$$ExternalSyntheticLambda1(this, realmResultsFindAll, 0));
        this.fetchEpisodesTask.setOnGenericExceptionListener(new BaseActivity$$ExternalSyntheticLambda4(this, 5));
        this.fetchEpisodesTask.execute();
    }

    private String getKeyFromCategoryName(String str) {
        for (String str2 : (TreeSet<String>) (TreeSet) new TreeSet(this.categoryHashMap.keySet())) {
            if (getCategoryNameFromKey(str2).equalsIgnoreCase(str)) {
                return str2;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getLiveCategory() {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_live_categories(this.user, this.password).enqueue(new Callback<List<CategoryModel>>() { // from class: com.ouropro.player.apps.BaseActivity.2
                public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable th) {
                    BaseActivity baseActivity = BaseActivity.this;
                    if (baseActivity.is_stop) {
                        return;
                    }
                    int i = baseActivity.error_account;
                    if (i >= 2) {
                        baseActivity.getVodCategory();
                    } else {
                        baseActivity.error_account = i + 1;
                        baseActivity.getLiveCategory();
                    }
                }

                public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                    BaseActivity.this.error_account = 0;
                    List<CategoryModel> listBody = response.body();
                    if (listBody == null) {
                        listBody = new ArrayList<>();
                    }
                    listBody.add(0, new CategoryModel(Constants.resume_id, BaseActivity.this.wordModels.getRecently_viewed()));
                    listBody.add(1, new CategoryModel(Constants.all_id, BaseActivity.this.wordModels.getAll()));
                    listBody.add(2, new CategoryModel(Constants.fav_id, BaseActivity.this.wordModels.getFavorite()));
                    for (CategoryModel categoryModel : listBody) {
                        String lowerCase = categoryModel.getName().toLowerCase();
                        if (lowerCase.contains("adult") || lowerCase.contains("xxx") || lowerCase.contains("porn")) {
                            Constants.xxx_live_categories.add(categoryModel.getId());
                        }
                    }
                    BaseActivity.this.preferenceHelper.setSharedPreferenceLiveCategory(listBody);
                    BaseActivity baseActivity = BaseActivity.this;
                    if (baseActivity.is_stop) {
                        return;
                    }
                    baseActivity.getVodCategory();
                }
            });
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            getVodCategory();
        }
    }

    private void getLiveCategoryModels(List<EPGChannel> list) {
        this.categoryHashMap = new HashMap<>();
        Iterator<EPGChannel> it = list.iterator();
        while (it.hasNext()) {
            addChannelToCategory(it.next());
        }
        TreeSet treeSet = new TreeSet(this.categoryHashMap.keySet());
        int i = 0;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new CategoryModel(Constants.resume_id, this.wordModels.getRecently_viewed()));
        arrayList.add(new CategoryModel(Constants.all_id, this.wordModels.getAll()));
        arrayList.add(new CategoryModel(Constants.fav_id, this.wordModels.getFavorite()));
        Iterator it2 = treeSet.iterator();
        while (it2.hasNext()) {
            arrayList.add(new CategoryModel(String.valueOf(i), (String) it2.next()));
            i++;
        }
        this.preferenceHelper.setSharedPreferenceLiveCategory(arrayList);
        if (this.is_stop) {
            return;
        }
        getMovieModels();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getLiveStreams() {
        if (this.realm.where(EPGChannel.class).findAll().size() != 0) {
            if (System.currentTimeMillis() / 1000 <= (((long) this.preferenceHelper.getSharedPreferenceUpdatePeriod()) * Constants.date_mils) + this.preferenceHelper.getSharedPreferenceLastPlaylistDate()) {
                if (this.is_stop) {
                    return;
                }
                getVodStreams();
                return;
            }
        }
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_live_streams(this.user, this.password).enqueue(new AnonymousClass5(this.preferenceHelper.getSharedPreferenceLiveFavChannels()));
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            getSecondLiveStreams();
        }
    }

    private int getMediaType(M3UItem m3UItem) {
        if (m3UItem == null) {
            return -1;
        }
        String streamURL = m3UItem.getStreamURL();
        if (streamURL == null || streamURL.trim().isEmpty()) {
            return -1;
        }
        String lowerUrl = streamURL.toLowerCase(Locale.ROOT);
        if (lowerUrl.contains("movie/") || lowerUrl.contains("=movie") || lowerUrl.contains("==movie")
                || lowerUrl.contains("movies/") || lowerUrl.contains("vod/") || lowerUrl.contains("video/")) {
            return 1;
        }
        if (M3USeriesNaming.isSeriesItem(m3UItem)) {
            return 2;
        }
        return 0;
    }

    private void getMovieCategoryModels(List<MovieModel> list) {
        this.categoryHashMap = new HashMap<>();
        Iterator<MovieModel> it = list.iterator();
        while (it.hasNext()) {
            addMovieToCategory(it.next());
        }
        TreeSet treeSet = new TreeSet(this.categoryHashMap.keySet());
        int i = 0;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new CategoryModel(Constants.resume_id, this.wordModels.getResume_to_watch()));
        arrayList.add(new CategoryModel(Constants.all_id, this.wordModels.getAll()));
        arrayList.add(new CategoryModel(Constants.fav_id, this.wordModels.getFavorite()));
        Iterator it2 = treeSet.iterator();
        while (it2.hasNext()) {
            arrayList.add(new CategoryModel(String.valueOf(i), (String) it2.next()));
            i++;
        }
        this.preferenceHelper.setSharedPreferenceVodCategory(arrayList);
        if (this.is_stop) {
            return;
        }
        getEpisodeModels();
    }

    private void getMovieModels() {
        RealmResults realmResultsFindAll = this.realm.where(MovieModel.class).findAll();
        List<String> sharedPreferenceVodFavNames = this.preferenceHelper.getSharedPreferenceVodFavNames();
        List<ResumeModel> sharedPreferenceResumeModel = this.preferenceHelper.getSharedPreferenceResumeModel();
        if (realmResultsFindAll.size() != 0) {
            if (System.currentTimeMillis() / 1000 <= (((long) this.preferenceHelper.getSharedPreferenceUpdatePeriod()) * Constants.date_mils) + this.preferenceHelper.getSharedPreferenceLastPlaylistDate()) {
                if (this.is_stop) {
                    return;
                }
                getMovieCategoryModels(new ArrayList(realmResultsFindAll));
                return;
            }
        }
        NetworkTask<Void, Void, List<MovieModel>> networkTask = this.fetchVideosTask;
        if (networkTask != null && !networkTask.isComplete()) {
            this.fetchVideosTask.abort();
        }
        FetchVideosTask fetchVideosTask = new FetchVideosTask();
        this.fetchVideosTask = fetchVideosTask;
        fetchVideosTask.setOnCompleteListener(new BaseActivity$$ExternalSyntheticLambda3(this, realmResultsFindAll, sharedPreferenceVodFavNames, sharedPreferenceResumeModel, 0));
        this.fetchVideosTask.setOnGenericExceptionListener(new BaseActivity$$ExternalSyntheticLambda4(this, 0));
        this.fetchVideosTask.execute();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSecondLiveStreams() {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_second_live_streams(this.user, this.password).enqueue(new AnonymousClass6(this.preferenceHelper.getSharedPreferenceLiveFavChannels()));
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            getSecondVodStreams();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSecondSeriesStreams() {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_second_series(this.user, this.password).enqueue(new AnonymousClass10(this.preferenceHelper.getSharedPreferenceSeriesFavNames(), this.preferenceHelper.getSharedPreferenceRecentSeriesNames()));
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            doNextTask(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSecondVodStreams() {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_second_vod_streams(this.user, this.password).enqueue(new AnonymousClass8(this.preferenceHelper.getSharedPreferenceVodFavNames(), this.preferenceHelper.getSharedPreferenceResumeModel()));
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            getSecondSeriesStreams();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSeriesCategory() {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_series_categories(this.user, this.password).enqueue(new Callback<List<CategoryModel>>() { // from class: com.ouropro.player.apps.BaseActivity.4
                public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable th) {
                    BaseActivity baseActivity = BaseActivity.this;
                    if (baseActivity.is_stop) {
                        return;
                    }
                    int i = baseActivity.error_account;
                    if (i >= 2) {
                        baseActivity.getLiveStreams();
                    } else {
                        baseActivity.error_account = i + 1;
                        baseActivity.getSeriesCategory();
                    }
                }

                public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                    BaseActivity.this.error_account = 0;
                    List<CategoryModel> listBody = response.body();
                    if (listBody == null) {
                        listBody = new ArrayList<>();
                    }
                    listBody.add(0, new CategoryModel(Constants.resume_id, BaseActivity.this.wordModels.getRecently_viewed()));
                    listBody.add(1, new CategoryModel(Constants.all_id, BaseActivity.this.wordModels.getAll()));
                    listBody.add(2, new CategoryModel(Constants.fav_id, BaseActivity.this.wordModels.getFavorite()));
                    BaseActivity.this.preferenceHelper.setSharedPreferenceSeriesCategory(listBody);
                    BaseActivity baseActivity = BaseActivity.this;
                    if (baseActivity.is_stop) {
                        return;
                    }
                    baseActivity.getLiveStreams();
                }
            });
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            getLiveStreams();
        }
    }

    private void getSeriesCategoryModels(List<SeriesModel> list) {
        this.categoryHashMap = new HashMap<>();
        Iterator<SeriesModel> it = list.iterator();
        while (it.hasNext()) {
            addSeriesToCategory(it.next());
        }
        this.categoryHashMap.keySet();
        TreeSet treeSet = new TreeSet(this.categoryHashMap.keySet());
        int i = 0;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new CategoryModel(Constants.resume_id, this.wordModels.getRecently_viewed()));
        arrayList.add(new CategoryModel(Constants.all_id, this.wordModels.getAll()));
        arrayList.add(new CategoryModel(Constants.fav_id, this.wordModels.getFavorite()));
        Iterator it2 = treeSet.iterator();
        while (it2.hasNext()) {
            arrayList.add(new CategoryModel(String.valueOf(i), (String) it2.next()));
            i++;
        }
        this.preferenceHelper.setSharedPreferenceSeriesCategory(arrayList);
        this.preferenceHelper.setSharedPreferenceLastPlaylistDate(System.currentTimeMillis() / 1000);
        if (this.is_stop) {
            return;
        }
        doNextTask(true);
    }

    private void getSeriesFromEpisodes(List<EpisodeModel> list) {
        int i;
        List<EpisodeModel> list2;
        List<String> sharedPreferenceSeriesFavNames = this.preferenceHelper.getSharedPreferenceSeriesFavNames();
        List<ResumeSeriesModel> sharedPreferenceRecentSeriesNames = this.preferenceHelper.getSharedPreferenceRecentSeriesNames();
        this.episodeModelHashMap = new HashMap<>();
        Iterator<EpisodeModel> it = list.iterator();
        while (it.hasNext()) {
            addEpisodeToSeries(it.next());
        }
        ArrayList arrayList = new ArrayList();
        this.episodeModelHashMap.keySet();
        Iterator it2 = new TreeSet(this.episodeModelHashMap.keySet()).iterator();
        while (true) {
            i = 0;
            if (!it2.hasNext()) {
                break;
            }
            String str = (String) it2.next();
            if (str != null && (list2 = this.episodeModelHashMap.get(str)) != null && list2.size() > 0) {
                SeriesModel seriesModel = new SeriesModel();
                seriesModel.setName(str);
                seriesModel.setCategory_name(list2.get(0).getCategory_name());
                seriesModel.setStream_icon(list2.get(0).getStream_icon());
                arrayList.add(seriesModel);
            }
        }
        this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda8(arrayList, 2));
        if (sharedPreferenceSeriesFavNames.size() > 0) {
            Iterator<String> it3 = sharedPreferenceSeriesFavNames.iterator();
            while (it3.hasNext()) {
                this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7(it3.next(), 13));
            }
        }
        if (sharedPreferenceRecentSeriesNames.size() > 0) {
            Iterator<ResumeSeriesModel> it4 = sharedPreferenceRecentSeriesNames.iterator();
            while (it4.hasNext()) {
                this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda6(it4.next(), i));
            }
        }
        getSeriesCategoryModels(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSeriesStreams() {
        if (this.realm.where(SeriesModel.class).findAll().size() != 0) {
            if (System.currentTimeMillis() / 1000 <= (((long) this.preferenceHelper.getSharedPreferenceUpdatePeriod()) * Constants.date_mils) + this.preferenceHelper.getSharedPreferenceLastPlaylistDate()) {
                if (this.is_stop) {
                    return;
                }
                doNextTask(true);
                return;
            }
        }
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_series(this.user, this.password).enqueue(new AnonymousClass9(this.preferenceHelper.getSharedPreferenceSeriesFavNames(), this.preferenceHelper.getSharedPreferenceRecentSeriesNames()));
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            doNextTask(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getVodCategory() {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_vod_categories(this.user, this.password).enqueue(new Callback<List<CategoryModel>>() { // from class: com.ouropro.player.apps.BaseActivity.3
                public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable th) {
                    BaseActivity baseActivity = BaseActivity.this;
                    if (baseActivity.is_stop) {
                        return;
                    }
                    int i = baseActivity.error_account;
                    if (i >= 2) {
                        baseActivity.getSeriesCategory();
                    } else {
                        baseActivity.error_account = i + 1;
                        baseActivity.getVodCategory();
                    }
                }

                public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                    BaseActivity.this.error_account = 0;
                    List<CategoryModel> listBody = response.body();
                    if (listBody == null) {
                        listBody = new ArrayList<>();
                    }
                    listBody.add(0, new CategoryModel(Constants.resume_id, BaseActivity.this.wordModels.getResume_to_watch()));
                    listBody.add(1, new CategoryModel(Constants.all_id, BaseActivity.this.wordModels.getAll()));
                    listBody.add(2, new CategoryModel(Constants.fav_id, BaseActivity.this.wordModels.getFavorite()));
                    BaseActivity.this.preferenceHelper.setSharedPreferenceVodCategory(listBody);
                    for (CategoryModel categoryModel : listBody) {
                        String lowerCase = categoryModel.getName().toLowerCase();
                        if (lowerCase.contains("adult") || lowerCase.contains("xxx") || lowerCase.contains("porn")) {
                            Constants.xxx_vod_categories.add(categoryModel.getId());
                        }
                    }
                    BaseActivity baseActivity = BaseActivity.this;
                    if (baseActivity.is_stop) {
                        return;
                    }
                    baseActivity.getSeriesCategory();
                }
            });
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            getSeriesCategory();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getVodStreams() {
        if (this.realm.where(MovieModel.class).findAll().size() != 0) {
            if (System.currentTimeMillis() / 1000 <= (((long) this.preferenceHelper.getSharedPreferenceUpdatePeriod()) * Constants.date_mils) + this.preferenceHelper.getSharedPreferenceLastPlaylistDate()) {
                if (this.is_stop) {
                    return;
                }
                getSeriesStreams();
                return;
            }
        }
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_vod_streams(this.user, this.password).enqueue(new AnonymousClass7(this.preferenceHelper.getSharedPreferenceVodFavNames(), this.preferenceHelper.getSharedPreferenceResumeModel()));
        } catch (Exception unused) {
            if (this.is_stop) {
                return;
            }
            getSeriesStreams();
        }
    }

    public static synchronized boolean isBusy() {
        return busy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchM3UItems$3(List list) {
        if (list.size() == 0) {
            doNextTask(false);
            Toast.makeText(getApplicationContext(), this.wordModels.getUser_incorrect(), 0).show();
        } else if (!this.is_stop) {
            prepareData(list);
            getChannelModels();
        }
        setBusy(false);
    }

    private void publishM3UVisibleCatalog() throws Exception {
        Realm backgroundRealm = Realm.getDefaultInstance();
        try {
            List<EPGChannel> channels = new LoadChannelsCommand().execute();
            backgroundRealm.executeTransaction(realm -> {
                realm.where(EPGChannel.class).findAll().deleteAllFromRealm();
                if (channels != null && !channels.isEmpty()) {
                    realm.insertOrUpdate(channels);
                }
            });
            saveM3UVisibleCategories(channels, null, null);

            List<MovieModel> movies = new LoadVideosCommand().execute();
            backgroundRealm.executeTransaction(realm -> realm.where(MovieModel.class).findAll().deleteAllFromRealm());
            if (movies != null && !movies.isEmpty()) {
                MovieCatalogDeduplicator.upsert(backgroundRealm, movies);
            }
            saveM3UVisibleCategories(channels, movies, null);
            this.preferenceHelper.setSharedPreferenceLastPlaylistDate(System.currentTimeMillis() / 1000);

            // Séries são mantidas em segundo plano para não atrasar canais e filmes.
            List<EpisodeModel> episodes = new LoadEpisodeCommand().execute();
            if (episodes != null && !episodes.isEmpty()) {
                backgroundRealm.executeTransaction(realm -> {
                    realm.where(EpisodeModel.class).findAll().deleteAllFromRealm();
                    realm.insertOrUpdate(episodes);
                });
                ArrayList<SeriesModel> series = buildSeriesFromEpisodes(episodes);
                if (!series.isEmpty()) {
                    SeriesCatalogDeduplicator.upsert(backgroundRealm, series);
                    saveM3UVisibleCategories(channels, movies, series);
                    getSharedPreferences(M3U_MIGRATION_PREFS, MODE_PRIVATE)
                            .edit().putInt("series_schema", M3U_SERIES_SCHEMA_VERSION).apply();
                }
            }
            // Só agora a Home fica navegável: nenhum catálogo abre vazio.
            runOnUiThread(() -> {
                if (!m3uVisibleNavigationSent && !is_stop) {
                    m3uVisibleNavigationSent = true;
                    setBusy(false);
                    doNextTask(true);
                }
            });
        } finally {
            backgroundRealm.close();
        }
    }

    private ArrayList<SeriesModel> buildSeriesFromEpisodes(List<EpisodeModel> episodes) {
        HashMap<String, List<EpisodeModel>> grouped = new HashMap<>();
        for (EpisodeModel episode : episodes) {
            if (episode == null || episode.getSeries_name() == null || episode.getSeries_name().trim().isEmpty()) {
                continue;
            }
            String categoryName = episode.getCategory_name();
            if (categoryName == null || categoryName.trim().isEmpty() || categoryName.equalsIgnoreCase("null")) {
                categoryName = "";
            }
            String seriesName = episode.getSeries_name().trim();
            String groupKey = categoryName.trim() + "|" + seriesName;
            List<EpisodeModel> current = grouped.get(groupKey);
            if (current == null) {
                current = new ArrayList<>();
                grouped.put(groupKey, current);
            }
            current.add(episode);
        }
        ArrayList<SeriesModel> seriesModels = new ArrayList<>();
        for (String groupKey : new TreeSet<>(grouped.keySet())) {
            List<EpisodeModel> group = grouped.get(groupKey);
            if (group == null || group.isEmpty()) {
                continue;
            }
            EpisodeModel firstEpisode = group.get(0);
            String displayName = firstEpisode.getSeries_name();
            if (displayName == null || displayName.trim().isEmpty() || displayName.equalsIgnoreCase("null")) {
                continue;
            }
            SeriesModel series = new SeriesModel();
            series.setName(displayName);
            series.setCategory_name(firstEpisode.getCategory_name());
            for (EpisodeModel episode : group) {
                String extractedSeriesId = extractM3USeriesId(episode == null ? "" : episode.getUrl());
                if (extractedSeriesId != null && !extractedSeriesId.isEmpty()) {
                    series.setSeries_id(extractedSeriesId);
                    break;
                }
            }
            String originalPoster = "";
            for (EpisodeModel episode : group) {
                if (episode != null && episode.getStream_icon() != null
                        && !episode.getStream_icon().trim().isEmpty()
                        && !"null".equalsIgnoreCase(episode.getStream_icon().trim())) {
                    originalPoster = episode.getStream_icon().trim();
                    break;
                }
            }
            series.setStream_icon(originalPoster);
            seriesModels.add(series);
        }
        return seriesModels;
    }

    private String extractM3USeriesId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        String lower = url.toLowerCase(Locale.ROOT);
        int marker = lower.indexOf("/series/");
        if (marker < 0) {
            return "";
        }
        String remainder = url.substring(marker + "/series/".length());
        String[] parts = remainder.split("[/?#]");
        return parts.length >= 3 ? parts[2].trim() : "";
    }

    private void sanitizeM3UCatalog(Realm realm) {
        RealmResults<EPGChannel> channels = realm.where(EPGChannel.class).findAll();
        for (int i = channels.size() - 1; i >= 0; i--) {
            EPGChannel channel = channels.get(i);
            if (channel != null && isMovieOrSeriesEntry(channel.getUrl(), channel.getName())) {
                channel.deleteFromRealm();
            }
        }
        RealmResults<MovieModel> movies = realm.where(MovieModel.class).findAll();
        for (int i = movies.size() - 1; i >= 0; i--) {
            MovieModel movie = movies.get(i);
            if (movie != null && isSeriesEntry(movie.getUrl(), movie.getName())) {
                movie.deleteFromRealm();
            }
        }
    }

    private boolean isMovieOrSeriesEntry(String url, String title) {
        return isMovieEntry(url) || isSeriesEntry(url, title);
    }

    private boolean isMovieEntry(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase(Locale.ROOT);
        return lower.contains("movie/") || lower.contains("=movie") || lower.contains("==movie")
                || lower.contains("movies/") || lower.contains("vod/") || lower.contains("video/");
    }

    private boolean isSeriesEntry(String url, String title) {
        String lower = url == null ? "" : url.toLowerCase(Locale.ROOT);
        return lower.contains("/series/") || lower.contains("/series?") || lower.contains("=series")
                || M3USeriesNaming.hasEpisodeMarker(title);
    }

    private void saveM3UVisibleCategoryNames(TreeSet<String> liveNames, TreeSet<String> movieNames, TreeSet<String> seriesNames) {
        if (liveNames != null) {
            this.preferenceHelper.setSharedPreferenceLiveCategory(buildM3UCategoryList(
                    wordModels.getRecently_viewed(), wordModels.getAll(), wordModels.getFavorite(), new ArrayList<>(liveNames)));
        }
        if (movieNames != null) {
            this.preferenceHelper.setSharedPreferenceVodCategory(buildM3UCategoryList(
                    wordModels.getResume_to_watch(), wordModels.getAll(), wordModels.getFavorite(), new ArrayList<>(movieNames)));
        }
        if (seriesNames != null) {
            this.preferenceHelper.setSharedPreferenceSeriesCategory(buildM3UCategoryList(
                    wordModels.getRecently_viewed(), wordModels.getAll(), wordModels.getFavorite(), new ArrayList<>(seriesNames)));
        }
    }

    private void saveM3UVisibleCategories(List<EPGChannel> channels, List<MovieModel> movies, List<SeriesModel> series) {
        ArrayList<String> liveNames = new ArrayList<>();
        ArrayList<String> movieNames = new ArrayList<>();
        ArrayList<String> seriesNames = new ArrayList<>();
        if (channels != null) {
            for (EPGChannel channel : channels) {
                if (channel != null) liveNames.add(channel.getCategory_name());
            }
        }
        if (movies != null) {
            for (MovieModel movie : movies) {
                if (movie != null) movieNames.add(movie.getCategory_name());
            }
        }
        if (series != null) {
            for (SeriesModel item : series) {
                if (item != null) seriesNames.add(item.getCategory_name());
            }
        }
        if (channels != null) {
            this.preferenceHelper.setSharedPreferenceLiveCategory(buildM3UCategoryList(
                    wordModels.getRecently_viewed(), wordModels.getAll(), wordModels.getFavorite(), liveNames));
        }
        if (movies != null) {
            this.preferenceHelper.setSharedPreferenceVodCategory(buildM3UCategoryList(
                    wordModels.getResume_to_watch(), wordModels.getAll(), wordModels.getFavorite(), movieNames));
        }
        if (series != null) {
            this.preferenceHelper.setSharedPreferenceSeriesCategory(buildM3UCategoryList(
                    wordModels.getRecently_viewed(), wordModels.getAll(), wordModels.getFavorite(), seriesNames));
        }
    }

    private ArrayList<CategoryModel> buildM3UCategoryList(String resumeName, String allName, String favoriteName, List<String> names) {
        ArrayList<CategoryModel> result = new ArrayList<>();
        result.add(new CategoryModel(Constants.resume_id, resumeName));
        result.add(new CategoryModel(Constants.all_id, allName));
        result.add(new CategoryModel(Constants.fav_id, favoriteName));
        TreeSet<String> distinct = new TreeSet<>();
        if (names != null) {
            for (String name : names) {
                if (name != null && !name.trim().isEmpty() && !"null".equalsIgnoreCase(name.trim())) {
                    distinct.add(name.trim());
                }
            }
        }
        int id = 0;
        for (String name : distinct) {
            result.add(new CategoryModel(String.valueOf(id++), name));
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchM3UItems$4(Exception exc) {
        if (!this.is_stop) {
            doNextTask(false);
            Toast.makeText(getApplicationContext(), this.wordModels.getUser_incorrect(), 0).show();
        }
        setBusy(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchM3UItems$5(NetworkErrorException networkErrorException) {
        if (!this.is_stop) {
            doNextTask(false);
            Toast.makeText(getApplicationContext(), this.wordModels.getUser_incorrect(), 0).show();
        }
        setBusy(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getChannelModels$6(List list, Realm realm) {
        realm.where(EPGChannel.class).findAll().deleteAllFromRealm();
        realm.insertOrUpdate(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getChannelModels$7(String str, Realm realm) {
        EPGChannel ePGChannel = (EPGChannel) Insets$$ExternalSyntheticOutline0.m(realm, EPGChannel.class, "name", str);
        if (ePGChannel != null) {
            ePGChannel.setIs_favorite(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelModels$8(RealmResults realmResults, List list, List list2) {
        if (realmResults.size() != list2.size()) {
            this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda8(list2, 1));
            if (list.size() > 0) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 0));
                }
            }
        }
        if (this.is_stop) {
            return;
        }
        getLiveCategoryModels(list2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelModels$9(Exception exc) {
        getMovieModels();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getEpisodeModels$15(List list, Realm realm) {
        realm.where(EpisodeModel.class).findAll().deleteAllFromRealm();
        realm.where(SeriesModel.class).findAll().deleteAllFromRealm();
        realm.insertOrUpdate(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getEpisodeModels$16(RealmResults realmResults, List list) {
        if (realmResults.size() != list.size()) {
            this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda8(list, 0));
        }
        if (this.is_stop) {
            return;
        }
        getSeriesFromEpisodes(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getEpisodeModels$17(Exception exc) {
        setBusy(false);
        if (this.is_stop) {
            return;
        }
        doNextTask(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getMovieModels$10(List list, Realm realm) {
        realm.where(MovieModel.class).findAll().deleteAllFromRealm();
        realm.insertOrUpdate(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getMovieModels$11(String str, Realm realm) {
        MovieModel movieModel = (MovieModel) Insets$$ExternalSyntheticOutline0.m(realm, MovieModel.class, "name", str);
        if (movieModel != null) {
            movieModel.setIs_favorite(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getMovieModels$12(ResumeModel resumeModel, Realm realm) {
        MovieModel movieModel = (MovieModel) realm.where(MovieModel.class).equalTo("name", resumeModel.getName()).findFirst();
        if (movieModel != null) {
            movieModel.setIs_recent(true);
            movieModel.setPro(resumeModel.getPro());
            movieModel.setTime(resumeModel.getLast_position());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMovieModels$13(RealmResults realmResults, List list, List list2, List list3) {
        if (realmResults.size() != list3.size()) {
            this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda8(list3, 3));
            if (list.size() > 0) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 14));
                }
            }
            if (list2.size() > 0) {
                Iterator it2 = list2.iterator();
                while (it2.hasNext()) {
                    this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda5((ResumeModel) it2.next(), 0));
                }
            }
        }
        if (this.is_stop) {
            return;
        }
        getMovieCategoryModels(list3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMovieModels$14(Exception exc) {
        getEpisodeModels();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getSeriesFromEpisodes$18(List list, Realm realm) {
        realm.where(SeriesModel.class).findAll().deleteAllFromRealm();
        realm.insertOrUpdate(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getSeriesFromEpisodes$19(String str, Realm realm) {
        SeriesModel seriesModel = (SeriesModel) Insets$$ExternalSyntheticOutline0.m(realm, SeriesModel.class, "name", str);
        if (seriesModel != null) {
            seriesModel.setIs_favorite(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getSeriesFromEpisodes$20(ResumeSeriesModel resumeSeriesModel, Realm realm) {
        SeriesModel seriesModel = (SeriesModel) realm.where(SeriesModel.class).equalTo("name", resumeSeriesModel.getName()).findFirst();
        if (seriesModel != null) {
            seriesModel.setIs_recent(true);
            seriesModel.setSeason_pos(resumeSeriesModel.getSeason_pos());
            seriesModel.setEpisode_pos(resumeSeriesModel.getEpisode_pos());
        }
    }

    private void prepareData(List<M3UItem> list) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (M3UItem m3UItem : list) {
            int mediaType = getMediaType(m3UItem);
            if (mediaType != -1) {
                if (mediaType == 0) {
                    arrayList.add(m3UItem);
                } else if (mediaType == 1) {
                    arrayList2.add(m3UItem);
                } else {
                    arrayList3.add(m3UItem);
                }
            }
        }
        this.model.setM3UChannelsItems(arrayList);
        this.model.setM3UVideosItems(arrayList2);
        this.model.setM3USeriesItems(arrayList3);
    }

    public static synchronized void setBusy(boolean z) {
        busy = z;
    }

    private boolean needsM3USeriesRebuild() {
        return this.preferenceHelper.getSharedPreferenceISM3U()
                && (this.realm.where(SeriesModel.class).count() < 100
                || getSharedPreferences(M3U_MIGRATION_PREFS, MODE_PRIVATE).getInt("series_schema", 0) < M3U_SERIES_SCHEMA_VERSION);
    }

    public void refreshM3USeriesInBackground() {
        try {
            if (!needsM3USeriesRebuild()
                    || this.model.getM3USeriesItems() == null
                    || this.model.getM3USeriesItems().isEmpty()) {
                return;
            }
            getEpisodeModels();
        } catch (Exception unused) {
        }
    }

    public void doNextTask(boolean z) {
    }

    public void goToLogin(String str, WordModels wordModels) {
        this.preferenceHelper = new PreferenceHelper(this);
        if (this.is_stop) {
            return;
        }
        try {
            this.wordModels = wordModels;
            String strReplaceAll = str.replaceAll(" ", "");
            URL url = new URL(strReplaceAll.trim());
            this.preferenceHelper.setSharedPreferenceServerUrl(url.getProtocol() + "://" + url.getAuthority());
            String[] strArrSplit = url.getQuery().split("&");
            this.user = strArrSplit[0].split("=")[1];
            this.password = strArrSplit[1].split("=")[1];
            this.user_id = Utils.getUserId(strReplaceAll);
            if (!this.preferenceHelper.getSharedPreferenceUserId().equalsIgnoreCase(this.user_id)) {
                this.realm.executeTransaction(BaseActivity$$ExternalSyntheticLambda0.INSTANCE$12);
                this.preferenceHelper.setSharedPreferenceUserId(this.user_id);
                this.preferenceHelper.setSharedPreferenceLiveCategory(GetSharedInfo.getDefaultLiveCategory());
                this.preferenceHelper.setSharedPreferenceVodCategory(GetSharedInfo.getDefaultVodCategory());
                this.preferenceHelper.setSharedPreferenceSeriesCategory(GetSharedInfo.getDefaultSeriesCategory());
            }
            authentication(strReplaceAll);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, wordModels.getAdd_correct_alert(), 0).show();
            doNextTask(false);
        }
    }

    public void goToXUILogin(String str, WordModels wordModels) {
        this.preferenceHelper = new PreferenceHelper(this);
        if (this.is_stop) {
            return;
        }
        try {
            this.wordModels = wordModels;
            String strReplaceAll = str.replaceAll(" ", "");
            URL url = new URL(strReplaceAll.trim());
            this.preferenceHelper.setSharedPreferenceServerUrl(url.getProtocol() + "://" + url.getAuthority());
            this.user = GetSharedInfo.getXUIUsername(strReplaceAll);
            this.password = GetSharedInfo.getXUIPassword(strReplaceAll);
            if (!this.preferenceHelper.getSharedPreferenceUserId().equalsIgnoreCase(this.user_id)) {
                this.realm.executeTransaction(BaseActivity$$ExternalSyntheticLambda0.INSTANCE);
                this.preferenceHelper.setSharedPreferenceUserId(this.user_id);
                this.preferenceHelper.setSharedPreferenceLiveCategory(GetSharedInfo.getDefaultLiveCategory());
                this.preferenceHelper.setSharedPreferenceVodCategory(GetSharedInfo.getDefaultVodCategory());
                this.preferenceHelper.setSharedPreferenceSeriesCategory(GetSharedInfo.getDefaultSeriesCategory());
            }
            authentication(strReplaceAll);
        } catch (Exception unused) {
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Realm.init(this);
        RealmConfiguration realmConfigurationBuild = new RealmConfiguration.Builder().name("MTV.realm").schemaVersion(1L).deleteRealmIfMigrationNeeded().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(realmConfigurationBuild);
        this.realm = Realm.getInstance(realmConfigurationBuild);
        try {
            IntentFilter failoverFilter = new IntentFilter();
            failoverFilter.addAction(PlaylistFailoverManager.ACTION_PLAYLIST_FAILOVER_SYNC);
            failoverFilter.addAction(PlaylistFailoverManager.ACTION_EXPIRATION_NOTICE);
            ContextCompat.registerReceiver(this, this.failoverReceiver, failoverFilter, ContextCompat.RECEIVER_NOT_EXPORTED);
            this.failoverReceiverRegistered = true;
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onDestroy() {
        if (this.failoverReceiverRegistered) {
            try {
                unregisterReceiver(this.failoverReceiver);
            } catch (Exception ignored) {
            }
            this.failoverReceiverRegistered = false;
        }
        super.onDestroy();
    }

    private void fetchM3UAccountMetadata(String playlistUrl) {
        final com.ouropro.player.improvements.M3UAccountEndpoint.Credentials credentials = com.ouropro.player.improvements.M3UAccountEndpoint.fromPlaylistUrl(playlistUrl);
        if (credentials == null) {
            return;
        }
        try {
            RetroClass.getAPIService(credentials.getBaseUrl(), true).authentication(credentials.getUsername(), credentials.getPassword()).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable throwable) {
                    // Uma M3U pode não expor metadados de conta; o catálogo continua válido.
                }

                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.body() == null || response.body().getUser_info() == null) {
                        return;
                    }
                    preferenceHelper.setSharedPreferenceServerUrl(credentials.getBaseUrl());
                    preferenceHelper.setSharedPreferenceLoginModel(response.body().getUser_info());
                    preferenceHelper.setSharedPreferenceUsername(credentials.getUsername());
                    preferenceHelper.setSharedPreferencePassword(credentials.getPassword());
                }
            });
        } catch (Exception ignored) {
            // Não bloquear o carregamento M3U se o endpoint de conta não estiver disponível.
        }
    }

    public void reloadM3UData(String str, WordModels wordModels) {
        this.wordModels = wordModels;
        this.preferenceHelper = new PreferenceHelper(this);
        if (isBusy()) {
            return;
        }
        setBusy(true);
        String strTrim = str.replaceAll(" ", "").trim();
        this.user_id = Utils.getUserId(strTrim);
        M3UAccountEndpoint.Credentials m3uCredentials = M3UAccountEndpoint.fromPlaylistUrl(strTrim);
        if (m3uCredentials != null) {
            this.preferenceHelper.setSharedPreferenceServerUrl(m3uCredentials.getBaseUrl());
            this.preferenceHelper.setSharedPreferenceUsername(m3uCredentials.getUsername());
            this.preferenceHelper.setSharedPreferencePassword(m3uCredentials.getPassword());
        } else {
            this.preferenceHelper.setSharedPreferenceServerUrl(strTrim);
        }
        if (!this.preferenceHelper.getSharedPreferenceUserId().equalsIgnoreCase(this.user_id)) {
            this.realm.executeTransaction(BaseActivity$$ExternalSyntheticLambda0.INSTANCE$11);
            this.preferenceHelper.setSharedPreferenceUserId(this.user_id);
        }
        if (this.is_stop) {
            return;
        }
        fetchM3UAccountMetadata(strTrim);
        fetchM3UItems(strTrim);
    }

    public void setStop(boolean z) {
        this.is_stop = z;
    }
}

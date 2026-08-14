package com.ouropro.player.apps;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.ouropro.player.improvements.StreamingM3UImporter;
import com.ouropro.player.improvements.SeriesCatalogDeduplicator;
import com.ouropro.player.improvements.NullTextGuard;
import com.ouropro.player.net.FetchChannelsTask;
import com.ouropro.player.net.FetchEpisodeTask;
import com.ouropro.player.net.FetchM3uItemsTask;
import com.ouropro.player.net.FetchVideosTask;
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
import java.util.TreeSet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class BaseTVActivity extends FragmentActivity {
    @Override
    protected void onResume() {
        super.onResume();
        NullTextGuard.sanitize(this);
    }

    public static boolean busy;
    private HashMap<String, String> categoryHashMap;
    private HashMap<String, List<EpisodeModel>> episodeModelHashMap;
    private NetworkTask<Void, Void, List<EPGChannel>> fetchChannelsTask;
    private NetworkTask<Void, Void, List<EpisodeModel>> fetchEpisodesTask;
    private NetworkTask<Void, Void, List<M3UItem>> fetchM3uItemsTask;
    private NetworkTask<Void, Void, List<MovieModel>> fetchVideosTask;
    public String password;
    public PreferenceHelper preferenceHelper;
    public Realm realm;
    public String user;
    public String user_id;
    public WordModels wordModels = new WordModels();
    public boolean is_stop = false;
    public int error_account = 0;
    private final LTVApp model = LTVApp.getInstance();

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseTVActivity$10, reason: invalid class name */
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
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            int i = baseTVActivity.error_account;
            if (i >= 2) {
                baseTVActivity.doNextTask(true);
            } else {
                baseTVActivity.error_account = i + 1;
                baseTVActivity.getSecondSeriesStreams();
            }
        }

        public void onResponse(@NonNull Call<List<SeriesModel>> call, @NonNull Response<List<SeriesModel>> response) {
            if (response.body() != null) {
                BaseTVActivity.this.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 6));
                if (this.val$series_favorites.size() > 0) {
                    Iterator it = this.val$series_favorites.iterator();
                    while (it.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 7));
                    }
                }
                if (this.val$series_recent.size() > 0) {
                    Iterator it2 = this.val$series_recent.iterator();
                    while (it2.hasNext()) {
                        BaseTVActivity.this.realm.executeTransactionAsync(new BaseActivity$$ExternalSyntheticLambda6((ResumeSeriesModel) it2.next(), 3));
                    }
                }
            }
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            baseTVActivity.doNextTask(true);
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseTVActivity$5, reason: invalid class name */
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
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            int i = baseTVActivity.error_account;
            if (i >= 2) {
                baseTVActivity.getSecondLiveStreams();
            } else {
                baseTVActivity.error_account = i + 1;
                baseTVActivity.getLiveStreams();
            }
        }

        public void onResponse(@NonNull Call<List<EPGChannel>> call, @NonNull Response<List<EPGChannel>> response) {
            if (response.body() == null) {
                BaseTVActivity baseTVActivity = BaseTVActivity.this;
                if (baseTVActivity.is_stop) {
                    return;
                }
                baseTVActivity.getSecondLiveStreams();
                return;
            }
            BaseTVActivity.this.error_account = 0;
            if (response.body().size() <= 0) {
                BaseTVActivity.this.getSecondLiveStreams();
                return;
            }
            BaseTVActivity.this.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 7));
            if (this.val$live_favorites.size() > 0) {
                Iterator it = this.val$live_favorites.iterator();
                while (it.hasNext()) {
                    BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 8));
                }
            }
            BaseTVActivity baseTVActivity2 = BaseTVActivity.this;
            if (baseTVActivity2.is_stop) {
                return;
            }
            baseTVActivity2.getVodStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseTVActivity$6, reason: invalid class name */
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
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            int i = baseTVActivity.error_account;
            if (i >= 2) {
                baseTVActivity.getSecondVodStreams();
            } else {
                baseTVActivity.error_account = i + 1;
                baseTVActivity.getSecondLiveStreams();
            }
        }

        public void onResponse(@NonNull Call<List<EPGChannel>> call, @NonNull Response<List<EPGChannel>> response) {
            if (response.body() != null) {
                BaseTVActivity baseTVActivity = BaseTVActivity.this;
                baseTVActivity.error_account = 0;
                baseTVActivity.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 8));
                if (this.val$live_favorites.size() > 0) {
                    Iterator it = this.val$live_favorites.iterator();
                    while (it.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 9));
                    }
                }
            }
            BaseTVActivity baseTVActivity2 = BaseTVActivity.this;
            if (baseTVActivity2.is_stop) {
                return;
            }
            baseTVActivity2.getSecondVodStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseTVActivity$7, reason: invalid class name */
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
            }
        }

        public void onFailure(@NonNull Call<List<MovieModel>> call, @NonNull Throwable th) {
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            int i = baseTVActivity.error_account;
            if (i >= 2) {
                baseTVActivity.getSeriesStreams();
            } else {
                baseTVActivity.error_account = i + 1;
                baseTVActivity.getVodStreams();
            }
        }

        public void onResponse(@NonNull Call<List<MovieModel>> call, @NonNull Response<List<MovieModel>> response) {
            if (response.body() != null) {
                BaseTVActivity baseTVActivity = BaseTVActivity.this;
                baseTVActivity.error_account = 0;
                baseTVActivity.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 9));
                if (this.val$vod_favorites.size() > 0) {
                    Iterator it = this.val$vod_favorites.iterator();
                    while (it.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 10));
                    }
                }
                if (this.val$resumeModels.size() > 0) {
                    Iterator it2 = this.val$resumeModels.iterator();
                    while (it2.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda5((ResumeModel) it2.next(), 3));
                    }
                }
            }
            BaseTVActivity baseTVActivity2 = BaseTVActivity.this;
            if (baseTVActivity2.is_stop) {
                return;
            }
            baseTVActivity2.getSeriesStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseTVActivity$8, reason: invalid class name */
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
            }
        }

        public void onFailure(@NonNull Call<List<MovieModel>> call, @NonNull Throwable th) {
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            int i = baseTVActivity.error_account;
            if (i >= 2) {
                baseTVActivity.getSecondSeriesStreams();
            } else {
                baseTVActivity.error_account = i + 1;
                baseTVActivity.getSecondVodStreams();
            }
        }

        public void onResponse(@NonNull Call<List<MovieModel>> call, @NonNull Response<List<MovieModel>> response) {
            if (response.body() != null) {
                BaseTVActivity baseTVActivity = BaseTVActivity.this;
                baseTVActivity.error_account = 0;
                baseTVActivity.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 10));
                if (this.val$vod_favorites.size() > 0) {
                    Iterator it = this.val$vod_favorites.iterator();
                    while (it.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 11));
                    }
                }
                if (this.val$resumeModels.size() > 0) {
                    Iterator it2 = this.val$resumeModels.iterator();
                    while (it2.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda5((ResumeModel) it2.next(), 4));
                    }
                }
            }
            BaseTVActivity baseTVActivity2 = BaseTVActivity.this;
            if (baseTVActivity2.is_stop) {
                return;
            }
            baseTVActivity2.getSecondSeriesStreams();
        }
    }

    /* JADX INFO: renamed from: com.ouropro.player.apps.BaseTVActivity$9, reason: invalid class name */
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
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            int i = baseTVActivity.error_account;
            if (i >= 2) {
                baseTVActivity.doNextTask(true);
            } else {
                baseTVActivity.error_account = i + 1;
                baseTVActivity.getSeriesStreams();
            }
        }

        public void onResponse(@NonNull Call<List<SeriesModel>> call, @NonNull Response<List<SeriesModel>> response) {
            if (response.body() != null) {
                BaseTVActivity.this.realm.executeTransaction(new BaseActivity$5$$ExternalSyntheticLambda0(response, 11));
                if (this.val$series_favorites.size() > 0) {
                    Iterator it = this.val$series_favorites.iterator();
                    while (it.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 12));
                    }
                }
                if (this.val$series_recent.size() > 0) {
                    Iterator it2 = this.val$series_recent.iterator();
                    while (it2.hasNext()) {
                        BaseTVActivity.this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda6((ResumeSeriesModel) it2.next(), 4));
                    }
                }
            }
            BaseTVActivity baseTVActivity = BaseTVActivity.this;
            if (baseTVActivity.is_stop) {
                return;
            }
            baseTVActivity.doNextTask(true);
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
        if (episodeModel == null) {
            return;
        }
        String seriesName = episodeModel.getSeries_name();
        if (seriesName == null || seriesName.trim().isEmpty() || seriesName.equalsIgnoreCase("null")) {
            seriesName = "All";
        }
        String categoryName = episodeModel.getCategory_name();
        if (categoryName == null || categoryName.trim().isEmpty() || categoryName.equalsIgnoreCase("null")) {
            categoryName = "";
        }
        String groupKey = categoryName.trim() + "|" + seriesName.trim();
        List<EpisodeModel> arrayList = this.episodeModelHashMap.get(groupKey);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(episodeModel);
        this.episodeModelHashMap.put(groupKey, arrayList);
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
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).authentication(this.user, this.password).enqueue(new Callback<LoginResponse>() { // from class: com.ouropro.player.apps.BaseTVActivity.1
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable th) {
                    BaseTVActivity baseTVActivity = BaseTVActivity.this;
                    if (baseTVActivity.is_stop) {
                        return;
                    }
                    int i = baseTVActivity.error_account;
                    if (i < 2) {
                        baseTVActivity.error_account = i + 1;
                        baseTVActivity.authentication(str);
                    } else {
                        baseTVActivity.preferenceHelper.setSharedPreferenceISM3U(true);
                        BaseTVActivity baseTVActivity2 = BaseTVActivity.this;
                        baseTVActivity2.reloadM3UData(str, baseTVActivity2.wordModels);
                    }
                }

                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.body() == null || response.body().getUser_info() == null || response.body().getUser_info().getStatus() == null) {
                        BaseTVActivity.this.preferenceHelper.setSharedPreferenceISM3U(true);
                        BaseTVActivity baseTVActivity = BaseTVActivity.this;
                        baseTVActivity.reloadM3UData(str, baseTVActivity.wordModels);
                        return;
                    }
                    BaseTVActivity.this.error_account = 0;
                    if (!response.body().getUser_info().getStatus().equalsIgnoreCase("Active")) {
                        Toast.makeText(BaseTVActivity.this.getApplicationContext(), BaseTVActivity.this.wordModels.getAccount_expired(), 1).show();
                        BaseTVActivity.this.doNextTask(false);
                        return;
                    }
                    BaseTVActivity.this.preferenceHelper.setSharedPreferenceLoginModel(response.body().getUser_info());
                    BaseTVActivity baseTVActivity2 = BaseTVActivity.this;
                    baseTVActivity2.preferenceHelper.setSharedPreferenceUsername(baseTVActivity2.user);
                    BaseTVActivity baseTVActivity3 = BaseTVActivity.this;
                    baseTVActivity3.preferenceHelper.setSharedPreferencePassword(baseTVActivity3.password);
                    Constants.setServerTimeOffset(response.body().getServerModel().getTimestamp_now(), response.body().getServerModel().getTime_now());
                    BaseTVActivity baseTVActivity4 = BaseTVActivity.this;
                    if (baseTVActivity4.is_stop) {
                        return;
                    }
                    baseTVActivity4.getLiveCategory();
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
        fetchM3uItemsTask.setOnCompleteListener(new BaseTVActivity$$ExternalSyntheticLambda0(this, 0));
        this.fetchM3uItemsTask.setOnGenericExceptionListener(new BaseTVActivity$$ExternalSyntheticLambda0(this, 1));
        this.fetchM3uItemsTask.setOnNetworkUnavailableListener(new BaseTVActivity$$ExternalSyntheticLambda0(this, 2));
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
        fetchChannelsTask.setOnCompleteListener(new BaseActivity$$ExternalSyntheticLambda2(this, realmResultsFindAll, sharedPreferenceLiveFavChannels, 3));
        this.fetchChannelsTask.setOnGenericExceptionListener(new BaseTVActivity$$ExternalSyntheticLambda0(this, 5));
        this.fetchChannelsTask.execute();
    }

    private void getEpisodeModels() {
        RealmResults realmResultsFindAll = this.realm.where(EpisodeModel.class).findAll();
        if (realmResultsFindAll.size() != 0) {
            boolean m3uNeedsRebuild = this.preferenceHelper.getSharedPreferenceISM3U()
                    && this.realm.where(SeriesModel.class).count() < 100;
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
        fetchEpisodeTask.setOnCompleteListener(new BaseActivity$$ExternalSyntheticLambda1(this, realmResultsFindAll, 2));
        this.fetchEpisodesTask.setOnGenericExceptionListener(new BaseTVActivity$$ExternalSyntheticLambda0(this, 4));
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
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_live_categories(this.user, this.password).enqueue(new Callback<List<CategoryModel>>() { // from class: com.ouropro.player.apps.BaseTVActivity.2
                public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable th) {
                    BaseTVActivity baseTVActivity = BaseTVActivity.this;
                    if (baseTVActivity.is_stop) {
                        return;
                    }
                    int i = baseTVActivity.error_account;
                    if (i >= 2) {
                        baseTVActivity.getVodCategory();
                    } else {
                        baseTVActivity.error_account = i + 1;
                        baseTVActivity.getLiveCategory();
                    }
                }

                public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                    BaseTVActivity.this.error_account = 0;
                    List<CategoryModel> listBody = response.body();
                    if (listBody == null) {
                        listBody = new ArrayList<>();
                    }
                    listBody.add(0, new CategoryModel(Constants.resume_id, BaseTVActivity.this.wordModels.getRecently_viewed()));
                    listBody.add(1, new CategoryModel(Constants.all_id, BaseTVActivity.this.wordModels.getAll()));
                    listBody.add(2, new CategoryModel(Constants.fav_id, BaseTVActivity.this.wordModels.getFavorite()));
                    for (CategoryModel categoryModel : listBody) {
                        String lowerCase = categoryModel.getName().toLowerCase();
                        if (lowerCase.contains("adult") || lowerCase.contains("xxx") || lowerCase.contains("porn")) {
                            Constants.xxx_live_categories.add(categoryModel.getId());
                        }
                    }
                    BaseTVActivity.this.preferenceHelper.setSharedPreferenceLiveCategory(listBody);
                    BaseTVActivity baseTVActivity = BaseTVActivity.this;
                    if (baseTVActivity.is_stop) {
                        return;
                    }
                    baseTVActivity.getVodCategory();
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
        String streamURL = m3UItem.getStreamURL();
        if (streamURL == null || streamURL.length() <= 0) {
            return -1;
        }
        if (streamURL.contains("movie/") || streamURL.contains("=movie") || streamURL.contains("==movie") || streamURL.contains("vod/") || streamURL.contains("video/")) {
            return 1;
        }
        return streamURL.contains("series/") ? 2 : 0;
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
        fetchVideosTask.setOnCompleteListener(new BaseActivity$$ExternalSyntheticLambda3(this, realmResultsFindAll, sharedPreferenceVodFavNames, sharedPreferenceResumeModel, 1));
        this.fetchVideosTask.setOnGenericExceptionListener(new BaseTVActivity$$ExternalSyntheticLambda0(this, 3));
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
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_series_categories(this.user, this.password).enqueue(new Callback<List<CategoryModel>>() { // from class: com.ouropro.player.apps.BaseTVActivity.4
                public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable th) {
                    BaseTVActivity baseTVActivity = BaseTVActivity.this;
                    if (baseTVActivity.is_stop) {
                        return;
                    }
                    int i = baseTVActivity.error_account;
                    if (i >= 2) {
                        baseTVActivity.getLiveStreams();
                    } else {
                        baseTVActivity.error_account = i + 1;
                        baseTVActivity.getSeriesCategory();
                    }
                }

                public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                    BaseTVActivity.this.error_account = 0;
                    List<CategoryModel> listBody = response.body();
                    if (listBody == null) {
                        listBody = new ArrayList<>();
                    }
                    listBody.add(0, new CategoryModel(Constants.resume_id, BaseTVActivity.this.wordModels.getRecently_viewed()));
                    listBody.add(1, new CategoryModel(Constants.all_id, BaseTVActivity.this.wordModels.getAll()));
                    listBody.add(2, new CategoryModel(Constants.fav_id, BaseTVActivity.this.wordModels.getFavorite()));
                    BaseTVActivity.this.preferenceHelper.setSharedPreferenceSeriesCategory(listBody);
                    BaseTVActivity baseTVActivity = BaseTVActivity.this;
                    if (baseTVActivity.is_stop) {
                        return;
                    }
                    baseTVActivity.getLiveStreams();
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
        if (list == null || list.isEmpty()) {
            if (!this.is_stop) {
                doNextTask(true);
            }
            return;
        }
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
        for (String str : (TreeSet<String>) (TreeSet) new TreeSet(this.episodeModelHashMap.keySet())) {
            if (str != null && (list2 = this.episodeModelHashMap.get(str)) != null && list2.size() > 0) {
                EpisodeModel firstEpisode = list2.get(0);
                String displayName = firstEpisode.getSeries_name();
                if (displayName == null || displayName.trim().isEmpty() || displayName.equalsIgnoreCase("null")) {
                    displayName = str;
                }
                SeriesModel seriesModel = new SeriesModel();
                seriesModel.setName(displayName);
                seriesModel.setCategory_name(firstEpisode.getCategory_name());
                String originalPoster = "";
                for (EpisodeModel episode : list2) {
                    if (episode != null && episode.getStream_icon() != null
                            && !episode.getStream_icon().trim().isEmpty()
                            && !"null".equalsIgnoreCase(episode.getStream_icon().trim())) {
                        originalPoster = episode.getStream_icon().trim();
                        break;
                    }
                }
                seriesModel.setStream_icon(originalPoster);
                for (EpisodeModel episode : list2) {
                    String extractedId = extractM3USeriesId(episode == null ? "" : episode.getUrl());
                    if (!extractedId.isEmpty()) {
                        seriesModel.setSeries_id(extractedId);
                        break;
                    }
                }
                arrayList.add(seriesModel);
            }
        }
        int i = 5;
        SeriesCatalogDeduplicator.upsert(this.realm, arrayList);
        if (sharedPreferenceSeriesFavNames.size() > 0) {
            Iterator<String> it2 = sharedPreferenceSeriesFavNames.iterator();
            while (it2.hasNext()) {
                this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7(it2.next(), 15));
            }
        }
        if (sharedPreferenceRecentSeriesNames.size() > 0) {
            Iterator<ResumeSeriesModel> it3 = sharedPreferenceRecentSeriesNames.iterator();
            while (it3.hasNext()) {
                this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda6(it3.next(), i));
            }
        }
        getSeriesCategoryModels(arrayList);
    }

    private String extractM3USeriesId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        String lower = url.toLowerCase(java.util.Locale.ROOT);
        int marker = lower.indexOf("/series/");
        if (marker < 0) {
            return "";
        }
        String remainder = url.substring(marker + "/series/".length());
        String[] parts = remainder.split("[/?#]");
        return parts.length >= 3 ? parts[2].trim() : "";
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
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl()).get_vod_categories(this.user, this.password).enqueue(new Callback<List<CategoryModel>>() { // from class: com.ouropro.player.apps.BaseTVActivity.3
                public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable th) {
                    BaseTVActivity baseTVActivity = BaseTVActivity.this;
                    if (baseTVActivity.is_stop) {
                        return;
                    }
                    int i = baseTVActivity.error_account;
                    if (i >= 2) {
                        baseTVActivity.getSeriesCategory();
                    } else {
                        baseTVActivity.error_account = i + 1;
                        baseTVActivity.getVodCategory();
                    }
                }

                public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                    BaseTVActivity.this.error_account = 0;
                    List<CategoryModel> listBody = response.body();
                    if (listBody == null) {
                        listBody = new ArrayList<>();
                    }
                    listBody.add(0, new CategoryModel(Constants.resume_id, BaseTVActivity.this.wordModels.getResume_to_watch()));
                    listBody.add(1, new CategoryModel(Constants.all_id, BaseTVActivity.this.wordModels.getAll()));
                    listBody.add(2, new CategoryModel(Constants.fav_id, BaseTVActivity.this.wordModels.getFavorite()));
                    BaseTVActivity.this.preferenceHelper.setSharedPreferenceVodCategory(listBody);
                    for (CategoryModel categoryModel : listBody) {
                        String lowerCase = categoryModel.getName().toLowerCase();
                        if (lowerCase.contains("adult") || lowerCase.contains("xxx") || lowerCase.contains("porn")) {
                            Constants.xxx_vod_categories.add(categoryModel.getId());
                        }
                    }
                    BaseTVActivity baseTVActivity = BaseTVActivity.this;
                    if (baseTVActivity.is_stop) {
                        return;
                    }
                    baseTVActivity.getSeriesCategory();
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
            this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda8(list2, 6));
            if (list.size() > 0) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 16));
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
        if (list == null || list.isEmpty()) {
            return;
        }
        realm.where(EpisodeModel.class).findAll().deleteAllFromRealm();
        realm.insertOrUpdate(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getEpisodeModels$16(RealmResults realmResults, List list) {
        if (realmResults.size() != list.size()) {
            this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda8(list, 4));
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
            this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda8(list3, 7));
            if (list.size() > 0) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda7((String) it.next(), 17));
                }
            }
            if (list2.size() > 0) {
                Iterator it2 = list2.iterator();
                while (it2.hasNext()) {
                    this.realm.executeTransaction(new BaseActivity$$ExternalSyntheticLambda5((ResumeModel) it2.next(), 5));
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
        if (list == null || list.isEmpty()) {
            return;
        }
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
                this.realm.executeTransaction(BaseActivity$$ExternalSyntheticLambda0.INSTANCE$15);
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
                this.realm.executeTransaction(BaseActivity$$ExternalSyntheticLambda0.INSTANCE$14);
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
        SeriesCatalogDeduplicator.deduplicate(this.realm);
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
                }

                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.body() == null || response.body().getUser_info() == null) {
                        return;
                    }
                    preferenceHelper.setSharedPreferenceLoginModel(response.body().getUser_info());
                    preferenceHelper.setSharedPreferenceUsername(credentials.getUsername());
                    preferenceHelper.setSharedPreferencePassword(credentials.getPassword());
                }
            });
        } catch (Exception ignored) {
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
        this.preferenceHelper.setSharedPreferenceServerUrl(strTrim);
        if (!this.preferenceHelper.getSharedPreferenceUserId().equalsIgnoreCase(this.user_id)) {
            this.realm.executeTransaction(BaseActivity$$ExternalSyntheticLambda0.INSTANCE$13);
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

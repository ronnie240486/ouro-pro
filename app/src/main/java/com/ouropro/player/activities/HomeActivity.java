package com.ouropro.player.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.bumptech.glide.Glide;
import com.ouropro.player.R;
import com.ouropro.player.activities.mobile.LiveMobileActivity;
import com.ouropro.player.apps.BaseActivity;
import com.ouropro.player.apps.HomeType;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.dlgfragment.AccountDlgFragment;
import com.ouropro.player.dlgfragment.ExitDlgFragment;
import com.ouropro.player.dlgfragment.NoConnectionDlgFragment;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.improvements.VoiceButtonFactory;
import com.ouropro.player.improvements.VoiceChannelMatcher;
import com.ouropro.player.improvements.VoiceCommand;
import com.ouropro.player.improvements.VoiceCommandController;
import com.ouropro.player.improvements.VoiceMediaMatcher;
import com.ouropro.player.improvements.SeriesCatalogLoader;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SeriesModel;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.LoginModel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.remote.RetroClass;
import com.ouropro.player.utils.Utils;
import com.rtx.Themes.dashtheme;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class HomeActivity extends BaseActivity implements View.OnClickListener {
    public AccountDlgFragment accountDlgFragment;
    public ExitDlgFragment exitDlgFragment;
    public ImageView image_account;
    public ImageView image_change;
    public ImageView image_exit;
    public ImageView image_live;
    public ImageView image_movie;
    public ImageView image_reload;
    public ImageView image_series;
    public ImageView image_setting;
    public ConstraintLayout ly_account;
    public ConstraintLayout ly_change;
    public ConstraintLayout ly_exit;
    public ConstraintLayout ly_live;
    public ConstraintLayout ly_movie;
    public ConstraintLayout ly_reload;
    public ConstraintLayout ly_series;
    public ConstraintLayout ly_setting;
    public NoConnectionDlgFragment noConnectionDlgFragment;
    public PreferenceHelper preferenceHelper;
    public GifImageView progressBar;
    public TextView txt_account;
    public TextView txt_change;
    public TextView txt_exit;
    public TextView txt_live;
    public TextView txt_movie;
    public TextView txt_reload;
    public TextView txt_series;
    public TextView txt_setting;
    public TextView txt_time;
    public TextView txt_version;
    public WordModels wordModels = new WordModels();
    private ImageButton microphoneButton;
    private VoiceCommandController voiceCommandController;
    private static final int VOICE_PERMISSION_REQUEST = 906;
    public ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new HomeActivity$$ExternalSyntheticLambda0(this));

    private List<CategoryModel> seriesCategoriesFrom(List<SeriesModel> models) {
        ArrayList<CategoryModel> categories = new ArrayList<>();
        categories.add(new CategoryModel(com.ouropro.player.apps.Constants.resume_id, this.wordModels.getRecently_viewed()));
        categories.add(new CategoryModel(com.ouropro.player.apps.Constants.all_id, this.wordModels.getAll()));
        categories.add(new CategoryModel(com.ouropro.player.apps.Constants.fav_id, this.wordModels.getFavorite()));
        Set<String> seen = new HashSet<>();
        if (models != null) {
            for (SeriesModel model : models) {
                if (model == null || model.getCategory_name() == null || model.getCategory_name().trim().isEmpty()) {
                    continue;
                }
                String id = model.getCategory_id();
                String key = id == null || id.isEmpty() ? model.getCategory_name() : id;
                if (seen.add(key)) {
                    categories.add(new CategoryModel(key, model.getCategory_name()));
                }
            }
        }
        return categories;
    }

    private void saveSeriesBackground(List<SeriesModel> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        final ArrayList<SeriesModel> copy = new ArrayList<>(models);
        final List<CategoryModel> categories = seriesCategoriesFrom(copy);
        new Thread(() -> {
            io.realm.Realm backgroundRealm = io.realm.Realm.getDefaultInstance();
            try {
                backgroundRealm.executeTransaction(realm -> realm.insertOrUpdate(copy));
            } finally {
                backgroundRealm.close();
            }
            runOnUiThread(() -> this.preferenceHelper.setSharedPreferenceSeriesCategory(categories));
        }, "ouro-series-persist").start();
    }

    private void refreshSeriesInBackground() {
        if (RealmController.with().realm.where(SeriesModel.class).count() >= 100) {
            return;
        }
        final String server = this.preferenceHelper.getSharedPreferenceServerUrl();
        final String username = this.preferenceHelper.getSharedPreferenceUsername();
        final String password = this.preferenceHelper.getSharedPreferencePassword();
        if (server == null || server.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            return;
        }
        SeriesCatalogLoader.load(RetroClass.getAPIService(server), username, password, new SeriesCatalogLoader.Listener() {
            @Override
            public void onComplete(List<SeriesModel> models, List<CategoryModel> categories) {
                saveSeriesBackground(models);
                if (models.size() >= 100) {
                    Toast.makeText(HomeActivity.this, "Catálogo de séries atualizado: " + models.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String message) {
                // A Home não deve bloquear nem substituir o catálogo local por uma resposta parcial.
            }
        });
    }

    private void changeStringsInApp() {
        WordModels wordModel = GetSharedInfo.getWordModel(this);
        this.wordModels = wordModel;
        this.txt_live.setText(wordModel.getLive_tv());
        this.txt_movie.setText(this.wordModels.getMovies());
        this.txt_series.setText(this.wordModels.getSeries());
        this.txt_account.setText(this.wordModels.getAccount());
        this.txt_change.setText(this.wordModels.getChange_playlist());
        this.txt_setting.setText(this.wordModels.getSettings());
        this.txt_reload.setText(this.wordModels.getReload_portal());
        this.txt_exit.setText(this.wordModels.getExit());
        try {
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/live_tv").error(R.drawable.icon_live).into(this.image_live);
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/movies").error(R.drawable.movie_icon).into(this.image_movie);
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/series").error(R.drawable.icon_series).into(this.image_series);
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/account").error(R.drawable.account_icon).into(this.image_account);
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/change_playlist").error(R.drawable.change_m3u_icon).into(this.image_change);
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/settings").error(R.drawable.ic_setting).into(this.image_setting);
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/reload").error(R.drawable.reload_icon).into(this.image_reload);
            Glide.with((FragmentActivity) this).load("https://renciaapp.manus.space/api/v4/icon/exit").error(R.drawable.exit_icon).into(this.image_exit);
        } catch (Exception unused) {
        }
    }

    private String getCurrentPlaylistExpiredDate() {
        LoginModel sharedPreferenceLoginModel;
        return (this.preferenceHelper.getSharedPreferenceISM3U() || (sharedPreferenceLoginModel = this.preferenceHelper.getSharedPreferenceLoginModel()) == null) ? "Undefined." : Utils.getDate(sharedPreferenceLoginModel.getExp_date());
    }

    private void initView() {
        this.txt_time = (TextView) findViewById(R.id.txt_time);
        this.ly_live = (ConstraintLayout) findViewById(R.id.ly_live);
        this.ly_movie = (ConstraintLayout) findViewById(R.id.ly_movie);
        this.ly_series = (ConstraintLayout) findViewById(R.id.ly_series);
        this.ly_account = (ConstraintLayout) findViewById(R.id.ly_account);
        this.ly_change = (ConstraintLayout) findViewById(R.id.ly_change);
        this.ly_setting = (ConstraintLayout) findViewById(R.id.ly_setting);
        this.ly_reload = (ConstraintLayout) findViewById(R.id.ly_reload);
        this.ly_exit = (ConstraintLayout) findViewById(R.id.ly_exit);
        this.txt_live = (TextView) findViewById(R.id.txt_live);
        this.txt_movie = (TextView) findViewById(R.id.txt_movie);
        this.txt_series = (TextView) findViewById(R.id.txt_series);
        this.txt_account = (TextView) findViewById(R.id.txt_account);
        this.txt_change = (TextView) findViewById(R.id.txt_change);
        this.txt_setting = (TextView) findViewById(R.id.txt_setting);
        this.txt_reload = (TextView) findViewById(R.id.txt_reload);
        this.txt_exit = (TextView) findViewById(R.id.txt_exit);
        this.txt_version = (TextView) findViewById(R.id.txt_version);
        this.progressBar = (GifImageView) findViewById(R.id.progress_bar);
        this.image_live = (ImageView) findViewById(R.id.image_live);
        this.image_movie = (ImageView) findViewById(R.id.image_movie);
        this.image_series = (ImageView) findViewById(R.id.image_series);
        this.image_account = (ImageView) findViewById(R.id.image_account);
        this.image_change = (ImageView) findViewById(R.id.image_change);
        this.image_setting = (ImageView) findViewById(R.id.image_setting);
        this.image_reload = (ImageView) findViewById(R.id.image_reload);
        this.image_exit = (ImageView) findViewById(R.id.image_exit);
        this.ly_live.setOnClickListener(this);
        this.ly_movie.setOnClickListener(this);
        this.ly_series.setOnClickListener(this);
        this.ly_account.setOnClickListener(this);
        this.ly_change.setOnClickListener(this);
        this.ly_setting.setOnClickListener(this);
        this.ly_reload.setOnClickListener(this);
        this.ly_exit.setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        changeStringsInApp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showAccountDlgFragment$1() {
        this.accountDlgFragment.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showNoConnectionDlgFragment$2() {
        Intent intent = new Intent(this, (Class<?>) ChangePlaylistActivity.class);
        intent.putExtra("is_home", false);
        startActivity(intent);
        finish();
    }

    private void reloadPortal() {
        this.progressBar.setVisibility(0);
        int playlistPosition = GetSharedInfo.getPlaylistPosition(this);
        AppInfoModel sharedPreferenceAppInfo = this.preferenceHelper.getSharedPreferenceAppInfo();
        if (sharedPreferenceAppInfo.getResult().size() > 0) {
            AppInfoModel.UrlModel urlModel = sharedPreferenceAppInfo.getResult().get(playlistPosition);
            this.preferenceHelper.setSharedPreferenceLastPlaylistDate(0L);
            if (urlModel.getUrl().contains("username")) {
                this.preferenceHelper.setSharedPreferenceISM3U(false);
                goToLogin(urlModel.getUrl(), this.wordModels);
            } else if (GetSharedInfo.checkXUILink(urlModel.getUrl())) {
                this.preferenceHelper.setSharedPreferenceISM3U(false);
                goToXUILogin(urlModel.getUrl(), this.wordModels);
            } else {
                this.preferenceHelper.setSharedPreferenceISM3U(true);
                reloadM3UData(urlModel.getUrl(), this.wordModels);
            }
        }
    }

    private void showAccountDlgFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_account");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        AccountDlgFragment accountDlgFragmentNewInstance = AccountDlgFragment.newInstance(this);
        this.accountDlgFragment = accountDlgFragmentNewInstance;
        accountDlgFragmentNewInstance.setOnPayButtonClickListener(new HomeActivity$$ExternalSyntheticLambda0(this));
        this.accountDlgFragment.show(supportFragmentManager, "fragment_account");
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
        exitDlgFragmentNewInstance.setOkButtonClickListener(new ExitDlgFragment.OkButtonClickListener() { // from class: com.ouropro.player.activities.HomeActivity.1
            public void onCancelClick() {
            }

            public void onOkClick() {
                HomeActivity.this.finishAffinity();
                System.exit(0);
            }
        });
        this.exitDlgFragment.show(supportFragmentManager, "fragment_exit");
    }

    private void showNoConnectionDlgFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_no_connection");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        NoConnectionDlgFragment noConnectionDlgFragmentNewInstance = NoConnectionDlgFragment.newInstance(this, this.wordModels.getPlaylist_is_not_working());
        this.noConnectionDlgFragment = noConnectionDlgFragmentNewInstance;
        noConnectionDlgFragmentNewInstance.setOnRetryClickListener(new HomeActivity$$ExternalSyntheticLambda0(this));
        this.noConnectionDlgFragment.show(supportFragmentManager, "fragment_no_connection");
    }

    private void showWaitToast() {
        Toast.makeText(this, this.wordModels.getPlaylist_is_loading(), 0).show();
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 4) {
            return super.dispatchKeyEvent(keyEvent);
        }
        showExitDlgFragment();
        return true;
    }

    public final void doNextTask(boolean z) {
        if (!z) {
            this.progressBar.setVisibility(8);
            showNoConnectionDlgFragment();
        } else {
            this.preferenceHelper.setSharedPreferenceLastPlaylistDate(System.currentTimeMillis() / 1000);
            this.progressBar.setVisibility(8);
            Toast.makeText(this, this.wordModels.getPortal_loaded_successfully(), 0).show();
        }
    }

    private void setupMicrophoneButton() {
        FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
        if (content == null) {
            return;
        }
        this.microphoneButton = VoiceButtonFactory.create(this, "Microfone: abrir canal, filme ou série", view -> requestVoicePermissionAndStart());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END);
        params.setMargins(0, 0, 24, 24);
        content.addView(this.microphoneButton, params);
        this.microphoneButton.bringToFront();
        if (!VoiceCommandController.isAvailable(this)) {
            this.microphoneButton.setVisibility(View.GONE);
            return;
        }
        this.voiceCommandController = new VoiceCommandController(this, new VoiceCommandController.Listener() {
            public void onVoiceCommand(VoiceCommand command) {
                handleHomeVoiceCommand(command);
            }

            public void onVoiceState(String state) {
                Toast.makeText(HomeActivity.this, state, Toast.LENGTH_SHORT).show();
            }

            public void onVoiceError(String message) {
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestVoicePermissionAndStart() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, VOICE_PERMISSION_REQUEST);
            return;
        }
        if (this.voiceCommandController != null) {
            this.voiceCommandController.start();
        }
    }

    private void handleHomeVoiceCommand(VoiceCommand command) {
        if (command == null) {
            return;
        }
        switch (command.getAction()) {
            case OPEN_MOVIES:
                startActivity(new Intent(this, MovieActivity.class));
                return;
            case OPEN_SERIES:
                startActivity(new Intent(this, SeriesActivity.class));
                return;
            case OPEN_LIVE:
                startActivity(new Intent(this, GetSharedInfo.isTVDevice(this) ? LiveActivity.class : LiveMobileActivity.class));
                return;
            case OPEN_SETTINGS:
                startActivity(new Intent(this, SettingActivity.class));
                return;
            case OPEN_MOVIE_ITEM:
            case SEARCH_MOVIE:
                openGlobalVoiceTitle(command.getQuery(), "movie");
                return;
            case OPEN_SERIES_ITEM:
            case SEARCH_SERIES:
                openGlobalVoiceTitle(command.getQuery(), "series");
                return;
            case OPEN_CHANNEL:
            case SEARCH_CHANNEL:
                openGlobalVoiceTitle(command.getQuery(), "channel");
                return;
            case OPEN_TITLE:
                openGlobalVoiceTitle(command.getQuery(), null);
                return;
            default:
                Toast.makeText(this, "Diga o nome do canal, filme ou série", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGlobalVoiceTitle(String query, String preferredType) {
        MovieModel movie = null;
        SeriesModel series = null;
        EPGChannel channel = null;
        if (preferredType == null || "movie".equals(preferredType)) {
            movie = VoiceMediaMatcher.findUniqueMovie(RealmController.with().getMoviesByKey(query, this.preferenceHelper.getSharedPreferenceISM3U()), query);
        }
        if (preferredType == null || "series".equals(preferredType)) {
            series = VoiceMediaMatcher.findUniqueSeries(RealmController.with().getSeriesByKey(query), query);
        }
        if (preferredType == null || "channel".equals(preferredType)) {
            channel = VoiceChannelMatcher.findUniqueMatch(RealmController.with().getLiveChannelsByKey(query, true), query);
        }
        int matches = (movie == null ? 0 : 1) + (series == null ? 0 : 1) + (channel == null ? 0 : 1);
        if (matches != 1) {
            Toast.makeText(this, "Não encontrei um único resultado para: " + query, Toast.LENGTH_SHORT).show();
            return;
        }
        if (movie != null) {
            Intent intent = new Intent(this, MovieInfoActivity.class);
            intent.putExtra("name", movie.getName());
            intent.putExtra("stream_id", movie.getStream_id());
            intent.putExtra("category_name", movie.getCategory_name());
            startActivity(intent);
        } else if (series != null) {
            Intent intent = new Intent(this, SeriesInfoActivity.class);
            intent.putExtra("series_id", series.getSeries_id());
            intent.putExtra("name", series.getName());
            intent.putExtra("category_name", series.getCategory_name());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, GetSharedInfo.isTVDevice(this) ? LiveActivity.class : LiveMobileActivity.class);
            intent.putExtra("voice_query", query);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VOICE_PERMISSION_REQUEST && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && this.voiceCommandController != null) {
            this.voiceCommandController.start();
        }
    }

    @Override
    protected void onPause() {
        if (this.voiceCommandController != null) {
            this.voiceCommandController.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (this.voiceCommandController != null) {
            this.voiceCommandController.destroy();
        }
        super.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_account /* 2131427885 */:
                showAccountDlgFragment();
                break;
            case R.id.ly_change /* 2131427893 */:
                setStop(true);
                Intent intent = new Intent(this, (Class<?>) ChangePlaylistActivity.class);
                intent.addFlags(67108864);
                intent.putExtra("is_home", true);
                startActivity(intent);
                finish();
                break;
            case R.id.ly_exit /* 2131427898 */:
                showExitDlgFragment();
                break;
            case R.id.ly_live /* 2131427903 */:
                if (this.progressBar.getVisibility() == 0) {
                    showWaitToast();
                } else if (this.preferenceHelper.getSharedPreferenceIsGrid()) {
                    LTVApp.homeType = HomeType.live;
                    startActivity(new Intent(this, (Class<?>) CategoryActivity.class));
                } else if (!GetSharedInfo.isTVDevice(this)) {
                    startActivity(new Intent(this, (Class<?>) LiveMobileActivity.class));
                } else {
                    startActivity(new Intent(this, (Class<?>) LiveActivity.class));
                }
                break;
            case R.id.ly_movie /* 2131427906 */:
                if (this.progressBar.getVisibility() == 0) {
                    showWaitToast();
                } else if (!this.preferenceHelper.getSharedPreferenceIsGrid()) {
                    startActivity(new Intent(this, (Class<?>) MovieActivity.class));
                } else {
                    LTVApp.homeType = HomeType.movies;
                    startActivity(new Intent(this, (Class<?>) CategoryActivity.class));
                }
                break;
            case R.id.ly_reload /* 2131427909 */:
                if (this.progressBar.getVisibility() != 0) {
                    reloadPortal();
                } else {
                    showWaitToast();
                }
                break;
            case R.id.ly_series /* 2131427913 */:
                if (this.progressBar.getVisibility() == 0) {
                    showWaitToast();
                } else if (!this.preferenceHelper.getSharedPreferenceIsGrid()) {
                    startActivity(new Intent(this, (Class<?>) SeriesActivity.class));
                } else {
                    LTVApp.homeType = HomeType.series;
                    startActivity(new Intent(this, (Class<?>) CategoryActivity.class));
                }
                break;
            case R.id.ly_setting /* 2131427914 */:
                if (this.progressBar.getVisibility() != 0) {
                    this.someActivityResultLauncher.launch(new Intent(this, (Class<?>) SettingActivity.class));
                } else {
                    showWaitToast();
                }
                break;
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(dashtheme.mNewDashtheme());
        Utils.FullScreenCall(this);
        this.preferenceHelper = new PreferenceHelper(this);
        initView();
        setupMicrophoneButton();
        changeStringsInApp();
        this.txt_time.setText(this.wordModels.getCurrent_expired() + " " + getCurrentPlaylistExpiredDate());
        LTVApp.instance.versionCheck();
        LTVApp.instance.loadVersion();
        TextView textView = this.txt_version;
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("v");
        sbM.append(LTVApp.version_name);
        textView.setText(sbM.toString());
        this.ly_live.requestFocus();
        refreshSeriesInBackground();
        refreshM3USeriesInBackground();
    }
}

package com.ouropro.player.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.gson.Gson;
import com.ouropro.player.R;
import com.ouropro.player.activities.mobile.LiveMobileActivity;
import com.ouropro.player.activities.mobile.MovieMobilePlayer;
import com.ouropro.player.activities.SettingActivity;
import com.ouropro.player.adapter.RecyclerVodCategoryAdapter;
import com.ouropro.player.adapter.SortSpinnerAdapter;
import com.ouropro.player.adapter.VodRecyclerAdapter;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.dlgfragment.LockDlgFragment;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.improvements.VoiceCommand;
import com.ouropro.player.improvements.VoiceCommandController;
import com.ouropro.player.improvements.VoiceMediaMatcher;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SubTitleUserModel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.remote.GetSubtitleLoginRequest;
import com.ouropro.player.utils.Security;
import com.ouropro.player.utils.Utils;
import com.ouropro.player.view.CustomSpinner;
import com.ouropro.player.view.LiveVerticalGridView;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kotlin.Unit;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MovieActivity extends AppCompatActivity implements View.OnClickListener {
    public List<CategoryModel> categoryModels;
    public EditText et_search;
    public LockDlgFragment lockDlgFragment;
    public LinearLayout ly_back;
    public LinearLayout ly_search;
    public RealmResults<MovieModel> movieModels;
    public PreferenceHelper preferenceHelper;
    public LiveVerticalGridView recycler_category;
    public LiveVerticalGridView recycler_movie;
    public CustomSpinner sort_spinner;
    public TextView txt_back;
    public TextView txt_category;
    public TextView txt_home;
    public TextView txt_live;
    public TextView txt_movie;
    public TextView txt_search;
    public TextView txt_series;
    public VodRecyclerAdapter vodAdapter;
    private Button voiceButton;
    private VoiceCommandController voiceCommandController;
    private static final int VOICE_PERMISSION_REQUEST = 910;
    public WordModels wordModels;
    public List<String> sortLists = new ArrayList();
    public int category_pos = 0;
    public int sort_pos = 0;
    public int pre_category_pos = 0;
    public int pre_movie_pos = 0;

    /* JADX INFO: renamed from: com.ouropro.player.activities.MovieActivity$1, reason: invalid class name */
    public class AnonymousClass1 implements VodRecyclerAdapter.ItemClickListener {
        public AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFavClick$0(int i) {
            MovieActivity.this.vodAdapter.notifyItemChanged(i);
            MovieActivity.this.preferenceHelper.setSharedPreferenceVodFavNames(RealmController.with().getFavMovieNames());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUnFavClick$1(int i) {
            MovieActivity.this.vodAdapter.notifyItemChanged(i);
            MovieActivity.this.preferenceHelper.setSharedPreferenceVodFavNames(RealmController.with().getFavMovieNames());
        }

        public void onFavClick(MovieModel movieModel, int i) {
            List<String> list = Constants.xxx_vod_categories;
            MovieActivity movieActivity = MovieActivity.this;
            if (list.contains(movieActivity.categoryModels.get(movieActivity.category_pos).getId())) {
                return;
            }
            RealmController.with().addToFavMovie(movieModel.getName(), true, new MovieActivity$1$$ExternalSyntheticLambda0(this, i, 1));
        }

        public void onFocusPosition(int i) {
            MovieActivity.this.pre_movie_pos = i;
        }

        public void onItemClick(MovieModel movieModel, int i) {
            MovieActivity movieActivity = MovieActivity.this;
            if (movieActivity.category_pos <= 1 && movieActivity.checkAdultMovie(movieModel.getCategory_name().toLowerCase(), movieModel.getCategory_id())) {
                MovieActivity.this.showMovieLockDlgFragment(movieModel, i);
                return;
            }
            Objects.requireNonNull(MovieActivity.this);
            MovieActivity movieActivity2 = MovieActivity.this;
            if (movieActivity2.categoryModels.get(movieActivity2.category_pos).getId().equalsIgnoreCase(Constants.resume_id)) {
                MovieActivity.this.playRecentMovie(movieModel);
                return;
            }
            Intent intent = new Intent(MovieActivity.this, (Class<?>) MovieInfoActivity.class);
            intent.putExtra("name", movieModel.getName());
            intent.putExtra("stream_id", movieModel.getStream_id());
            if (MovieActivity.this.preferenceHelper.getSharedPreferenceISM3U()) {
                intent.putExtra("category_name", movieModel.getCategory_name());
            } else {
                intent.putExtra("category_name", MovieActivity.this.getMovieCategoryName(movieModel.getCategory_id()));
            }
            MovieActivity.this.startActivity(intent);
        }

        public void onUnFavClick(MovieModel movieModel, int i) {
            RealmController.with().addToFavMovie(movieModel.getName(), false, new MovieActivity$1$$ExternalSyntheticLambda0(this, i, 0));
        }
    }

    private void GetLoginFromSubtitle() {
        GetSubtitleLoginRequest getSubtitleLoginRequest = new GetSubtitleLoginRequest(this, 1000);
        getSubtitleLoginRequest.getResponse(Security.getUserObject(Constants.USERNAME, Constants.PASSWORD), Constants.SUBTITLE_LOGIN, Constants.API_KEY);
        getSubtitleLoginRequest.setOnGetLinkModelListener(new MovieActivity$$ExternalSyntheticLambda2(this, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkAdultMovie(String str, String str2) {
        if (this.preferenceHelper.getSharedPreferenceISM3U()) {
            return str.contains("xxx") || str.contains("porn") || str.contains("adult");
        }
        return Constants.xxx_vod_categories.contains(str2);
    }

    private void externalMXplayer(String str, String str2, String str3) {
        try {
            Uri uri = Uri.parse(str3);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setPackage(str);
            intent.setClassName(str, str2);
            intent.setDataAndType(uri, MimeTypes.APPLICATION_M3U8);
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
        }
    }

    private void externalvlcplayer(String str, String str2) {
        Uri uri = Uri.parse(str);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setPackage("org.videolan.vlc");
        intent.setDataAndTypeAndNormalize(uri, "video/*");
        intent.putExtra("title", str2);
        intent.putExtra("from_start", true);
        intent.putExtra("position", 90000L);
        intent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
        startActivity(intent);
    }

    private int getAvailableCategoryPosition() {
        for (int i = 0; i < this.categoryModels.size(); i++) {
            if (RealmController.with().getMovieModelsByCategory(this.categoryModels.get(i), "", this.preferenceHelper.getSharedPreferenceISM3U(), 0).size() > 0) {
                return i;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getMovieCategoryName(String str) {
        for (CategoryModel categoryModel : LTVApp.vod_categories_filter) {
            if (str.equalsIgnoreCase(categoryModel.getId())) {
                return categoryModel.getName();
            }
        }
        return "UnNamed Category";
    }

    private void initView() {
        this.ly_back = (LinearLayout) findViewById(R.id.ly_back);
        this.ly_search = (LinearLayout) findViewById(R.id.ly_search);
        this.txt_home = (TextView) findViewById(R.id.txt_home);
        this.txt_live = (TextView) findViewById(R.id.txt_live);
        this.txt_movie = (TextView) findViewById(R.id.txt_movie);
        this.txt_series = (TextView) findViewById(R.id.txt_series);
        this.txt_search = (TextView) findViewById(R.id.txt_search);
        this.txt_back = (TextView) findViewById(R.id.txt_back);
        this.txt_category = (TextView) findViewById(R.id.txt_category);
        this.et_search = (EditText) findViewById(R.id.et_search);
        this.recycler_movie = (LiveVerticalGridView) findViewById(R.id.recycler_movie);
        this.recycler_category = (LiveVerticalGridView) findViewById(R.id.recycler_category);
        this.sort_spinner = (CustomSpinner) findViewById(R.id.sort_spinner);
        if (GetSharedInfo.isTVDevice(this)) {
            this.recycler_category.setNumColumns(1);
            this.recycler_category.setLoop(false);
            this.recycler_category.setPreserveFocusAfterLayout(true);
            final View[] viewArr = {null};
            this.recycler_category.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.MovieActivity.4
                public void onChildViewHolderSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
                    super.onChildViewHolderSelected(recyclerView, viewHolder, i, i2);
                    View[] viewArr2 = viewArr;
                    if (viewArr2[0] != null) {
                        viewArr2[0].setSelected(false);
                        View[] viewArr3 = viewArr;
                        viewArr3[0] = viewHolder.itemView;
                        viewArr3[0].setSelected(true);
                    }
                }
            });
        } else {
            this.recycler_category.setLayoutManager(new LinearLayoutManager(this));
            this.recycler_category.setHasFixedSize(true);
        }
        if (GetSharedInfo.isTVDevice(this)) {
            this.recycler_movie.setNumColumns(5);
            this.recycler_movie.setLoop(false);
            this.recycler_movie.setPreserveFocusAfterLayout(true);
            final View[] viewArr2 = {null};
            this.recycler_movie.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.MovieActivity.5
                public void onChildViewHolderSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
                    super.onChildViewHolderSelected(recyclerView, viewHolder, i, i2);
                    View[] viewArr3 = viewArr2;
                    if (viewArr3[0] != null) {
                        viewArr3[0].setSelected(false);
                        View[] viewArr4 = viewArr2;
                        viewArr4[0] = viewHolder.itemView;
                        viewArr4[0].setSelected(true);
                    }
                }
            });
        } else {
            this.recycler_movie.setLayoutManager(new GridLayoutManager(this, 5));
            this.recycler_movie.setHasFixedSize(true);
        }
        this.txt_home.setText(this.wordModels.getHome());
        this.txt_live.setText(this.wordModels.getLive_tv());
        this.txt_movie.setText(this.wordModels.getMovies());
        this.txt_series.setText(this.wordModels.getSeries());
        this.txt_back.setText(this.wordModels.getBack());
        this.txt_search.setText(this.wordModels.getSearch());
        this.ly_back.setOnClickListener(this);
        this.ly_search.setOnClickListener(this);
        this.txt_home.setOnClickListener(this);
        this.txt_live.setOnClickListener(this);
        this.txt_series.setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$GetLoginFromSubtitle$1(JSONObject jSONObject, int i) {
        if (jSONObject != null) {
            this.preferenceHelper.setSharedPreferenceSubtitleLoginModel((SubTitleUserModel) new Gson().fromJson(jSONObject.toString(), SubTitleUserModel.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreate$0(CategoryModel categoryModel, Integer num, Boolean bool) {
        this.pre_category_pos = num.intValue();
        if (!bool.booleanValue() || this.category_pos == num.intValue()) {
            return null;
        }
        if (Constants.xxx_vod_categories.contains(categoryModel.getId())) {
            showLockDlgFragment(num.intValue());
            return null;
        }
        this.category_pos = num.intValue();
        this.et_search.setText("");
        this.movieModels = RealmController.with().getMovieModelsByCategory(categoryModel, "", this.preferenceHelper.getSharedPreferenceISM3U(), this.sort_pos);
        this.txt_category.setText(categoryModel.getName() + "(" + this.movieModels.size() + ")");
        this.vodAdapter.updateData(this.movieModels);
        this.vodAdapter.setSelectedPosition(-1);
        this.recycler_movie.setSelectedPosition(0);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showExternalPlayerDialog$2(int i, DialogInterface dialogInterface, int i2) {
        Intent data;
        if (i != 1) {
            data = i != 2 ? null : new Intent("android.intent.action.VIEW").setData(Uri.parse("https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad"));
        } else {
            data = new Intent("android.intent.action.VIEW").setData(Uri.parse("https://play.google.com/store/apps/details?id=org.videolan.vlc&hl=en_US"));
        }
        startActivity(data);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playRecentMovie(MovieModel movieModel) {
        int sharedPreferenceExternalPlayer = this.preferenceHelper.getSharedPreferenceExternalPlayer();
        String url = this.preferenceHelper.getSharedPreferenceISM3U() ? movieModel.getUrl() : GetSharedInfo.getMovieUrl(this.preferenceHelper.getSharedPreferenceServerUrl(), this.preferenceHelper.getSharedPreferenceUsername(), this.preferenceHelper.getSharedPreferencePassword(), movieModel.getStream_id(), movieModel.getExtension());
        if (sharedPreferenceExternalPlayer == 0) {
            Intent intent = GetSharedInfo.isTVDevice(this) ? new Intent(this, (Class<?>) MoviePlayerActivity.class) : new Intent(this, (Class<?>) MovieMobilePlayer.class);
            intent.putExtra("name", movieModel.getName());
            intent.putExtra("stream_id", movieModel.getStream_id());
            intent.putExtra("description", "");
            intent.putExtra("category_name", this.preferenceHelper.getSharedPreferenceISM3U() ? movieModel.getCategory_name() : getMovieCategoryName(movieModel.getCategory_id()));
            startActivity(intent);
            return;
        }
        if (sharedPreferenceExternalPlayer == 1) {
            if (Utils.getVlcPackageInfo(this) != null) {
                externalvlcplayer(url, movieModel.getName());
                return;
            } else {
                showExternalPlayerDialog(1);
                return;
            }
        }
        if (sharedPreferenceExternalPlayer != 2) {
            return;
        }
        Utils.MXPackageInfo mXPackageInfo = Utils.getMXPackageInfo(this);
        if (mXPackageInfo != null) {
            externalMXplayer(mXPackageInfo.packageName, mXPackageInfo.activityName, url);
        } else {
            showExternalPlayerDialog(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchModels(String str) {
        RealmResults<MovieModel> movieModelsByCategory = RealmController.with().getMovieModelsByCategory(this.categoryModels.get(this.category_pos), str, this.preferenceHelper.getSharedPreferenceISM3U(), this.sort_pos);
        this.movieModels = movieModelsByCategory;
        this.vodAdapter.updateData(movieModelsByCategory);
        this.recycler_movie.setSelectedPosition(0);
    }

    private void setFocusTopView(boolean z) {
        this.txt_home.setFocusable(z);
        this.txt_live.setFocusable(z);
        this.txt_movie.setFocusable(z);
        this.txt_series.setFocusable(z);
        this.et_search.setFocusable(z);
        this.ly_back.setFocusable(z);
        this.ly_search.setFocusable(z);
    }

    private void showExternalPlayerDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.wordModels.getInstall_external_player());
        builder.setMessage(this.wordModels.getWant_external_player()).setCancelable(false).setPositiveButton(this.wordModels.getOk(), new MovieActivity$$ExternalSyntheticLambda0(this, i, 0)).setNegativeButton(this.wordModels.getCancel(), MovieActivity$$ExternalSyntheticLambda1.INSTANCE);
        builder.create().show();
    }

    private void showLockDlgFragment(final int i) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_lock");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        LockDlgFragment lockDlgFragmentNewInstance = LockDlgFragment.newInstance(this.preferenceHelper.getSharedPreferenceParentPassword());
        this.lockDlgFragment = lockDlgFragmentNewInstance;
        lockDlgFragmentNewInstance.setOnPinEventListener(new LockDlgFragment.OnPinEventListener() { // from class: com.ouropro.player.activities.MovieActivity.6
            public void OnPinCorrect() {
                MovieActivity movieActivity = MovieActivity.this;
                movieActivity.category_pos = i;
                movieActivity.et_search.setText("");
                MovieActivity.this.movieModels = RealmController.with().getMovieModelsByCategory(MovieActivity.this.categoryModels.get(i), "", MovieActivity.this.preferenceHelper.getSharedPreferenceISM3U(), MovieActivity.this.sort_pos);
                MovieActivity.this.txt_category.setText(MovieActivity.this.categoryModels.get(i).getName() + "(" + MovieActivity.this.movieModels.size() + ")");
                MovieActivity movieActivity2 = MovieActivity.this;
                movieActivity2.vodAdapter.updateData(movieActivity2.movieModels);
                MovieActivity.this.vodAdapter.setSelectedPosition(-1);
                MovieActivity.this.recycler_movie.setSelectedPosition(0);
            }

            public void OnPinIncorrect() {
                MovieActivity movieActivity = MovieActivity.this;
                Toast.makeText(movieActivity, movieActivity.wordModels.getPin_incorrect(), 0).show();
            }

            public void OnPutPinCode() {
                MovieActivity movieActivity = MovieActivity.this;
                Toast.makeText(movieActivity, movieActivity.wordModels.getPut_pin_code(), 0).show();
            }
        });
        this.lockDlgFragment.show(supportFragmentManager, "fragment_lock");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMovieLockDlgFragment(MovieModel movieModel, int i) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_lock");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        LockDlgFragment lockDlgFragmentNewInstance = LockDlgFragment.newInstance(this.preferenceHelper.getSharedPreferenceParentPassword());
        this.lockDlgFragment = lockDlgFragmentNewInstance;
        lockDlgFragmentNewInstance.setOnPinEventListener(new LockDlgFragment.OnPinEventListener() { // from class: com.ouropro.player.activities.MovieActivity.7
            public final /* synthetic */ MovieModel movieModelValue;

            {
                this.movieModelValue = movieModel;
            }

            public void OnPinCorrect() {
                Objects.requireNonNull(MovieActivity.this);
                Intent intent = new Intent(MovieActivity.this, (Class<?>) MovieInfoActivity.class);
                intent.putExtra("name", this.movieModelValue.getName());
                intent.putExtra("stream_id", this.movieModelValue.getStream_id());
                if (MovieActivity.this.preferenceHelper.getSharedPreferenceISM3U()) {
                    intent.putExtra("category_name", this.movieModelValue.getCategory_name());
                } else {
                    intent.putExtra("category_name", MovieActivity.this.getMovieCategoryName(this.movieModelValue.getCategory_id()));
                }
                MovieActivity.this.startActivity(intent);
            }

            public void OnPinIncorrect() {
                MovieActivity movieActivity = MovieActivity.this;
                Toast.makeText(movieActivity, movieActivity.wordModels.getPin_incorrect(), 0).show();
            }

            public void OnPutPinCode() {
                MovieActivity movieActivity = MovieActivity.this;
                Toast.makeText(movieActivity, movieActivity.wordModels.getPut_pin_code(), 0).show();
            }
        });
        this.lockDlgFragment.show(supportFragmentManager, "fragment_lock");
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int i;
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 4) {
                finish();
            } else if (keyCode != 89) {
                if (keyCode != 90) {
                    switch (keyCode) {
                        case 19:
                            if (this.recycler_category.hasFocus() && this.pre_category_pos == 0) {
                                setFocusTopView(true);
                                this.ly_back.requestFocus();
                                return true;
                            }
                            if (this.ly_back.hasFocus()) {
                                this.txt_home.requestFocus();
                                return true;
                            }
                            if (this.ly_search.hasFocus()) {
                                this.txt_live.requestFocus();
                                return true;
                            }
                            if (this.recycler_movie.hasFocus() && this.pre_movie_pos < 5) {
                                this.sort_spinner.requestFocus();
                                return true;
                            }
                            if (this.sort_spinner.hasFocus()) {
                                setFocusTopView(true);
                                this.txt_series.requestFocus();
                                return true;
                            }
                            break;
                        case 20:
                            if (this.txt_home.hasFocus()) {
                                this.ly_back.requestFocus();
                                return true;
                            }
                            if (this.txt_live.hasFocus() || this.txt_movie.hasFocus()) {
                                this.ly_search.requestFocus();
                                return true;
                            }
                            if (this.txt_series.hasFocus() || this.et_search.hasFocus()) {
                                setFocusTopView(false);
                                this.sort_spinner.requestFocus();
                                return true;
                            }
                            if (this.ly_back.hasFocus() || this.ly_search.hasFocus()) {
                                setFocusTopView(false);
                                this.recycler_category.requestFocus();
                                return true;
                            }
                            if (this.sort_spinner.hasFocus()) {
                                this.recycler_movie.requestFocus();
                                return true;
                            }
                            break;
                        case 21:
                            if (this.sort_spinner.hasFocus()) {
                                setFocusTopView(true);
                                this.ly_search.requestFocus();
                                return true;
                            }
                            if (this.txt_home.hasFocus() || this.recycler_category.hasFocus() || this.ly_back.hasFocus()) {
                                return true;
                            }
                            break;
                        case 22:
                            if (this.recycler_category.hasFocus()) {
                                this.recycler_movie.requestFocus();
                                return true;
                            }
                            if (this.ly_search.hasFocus()) {
                                setFocusTopView(false);
                                this.recycler_movie.requestFocus();
                                return true;
                            }
                            if (this.sort_spinner.hasFocus() || this.et_search.hasFocus()) {
                                return true;
                            }
                            break;
                    }
                } else if (this.recycler_category.hasFocus()) {
                    if (this.pre_category_pos < this.categoryModels.size() - 11) {
                        int i2 = this.pre_category_pos + 10;
                        this.pre_category_pos = i2;
                        this.recycler_category.setSelectedPosition(i2);
                    }
                } else if (this.recycler_movie.hasFocus() && this.pre_movie_pos < this.movieModels.size() - 11) {
                    int i3 = this.pre_movie_pos + 10;
                    this.pre_movie_pos = i3;
                    this.recycler_movie.setSelectedPosition(i3);
                }
            } else if (this.recycler_category.hasFocus()) {
                int i4 = this.pre_category_pos;
                if (i4 > 10) {
                    int i5 = i4 - 10;
                    this.pre_category_pos = i5;
                    this.recycler_category.setSelectedPosition(i5);
                }
            } else if (this.recycler_movie.hasFocus() && (i = this.pre_movie_pos) > 10) {
                int i6 = i - 10;
                this.pre_movie_pos = i6;
                this.recycler_movie.setSelectedPosition(i6);
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    private void setupVoiceButton() {
        FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
        if (content == null) {
            return;
        }
        this.voiceButton = new Button(this);
        this.voiceButton.setText("Voz");
        this.voiceButton.setAllCaps(false);
        this.voiceButton.setContentDescription("Comando de voz para filmes");
        this.voiceButton.setOnClickListener(view -> requestVoicePermissionAndStart());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END);
        params.setMargins(0, 0, 24, 24);
        content.addView(this.voiceButton, params);
        if (!VoiceCommandController.isAvailable(this)) {
            this.voiceButton.setVisibility(View.GONE);
            return;
        }
        this.voiceCommandController = new VoiceCommandController(this, new VoiceCommandController.Listener() {
            public void onVoiceCommand(VoiceCommand command) {
                handleVoiceCommand(command);
            }

            public void onVoiceState(String state) {
                if (voiceButton != null) {
                    voiceButton.setText(state.startsWith("Ouvindo") ? "Ouvindo..." : "Voz");
                }
            }

            public void onVoiceError(String message) {
                if (voiceButton != null) {
                    voiceButton.setText("Voz");
                }
                Toast.makeText(MovieActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestVoicePermissionAndStart() {
        if (android.os.Build.VERSION.SDK_INT >= 23
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, VOICE_PERMISSION_REQUEST);
            return;
        }
        if (this.voiceCommandController != null) {
            this.voiceCommandController.start();
        }
    }

    private void handleVoiceCommand(VoiceCommand command) {
        switch (command.getAction()) {
            case OPEN_MOVIE_ITEM:
            case SEARCH_MOVIE:
                openMovieByVoice(command.getQuery());
                break;
            case OPEN_SERIES:
                startActivity(new Intent(this, SeriesActivity.class));
                finish();
                break;
            case OPEN_LIVE:
                startActivity(new Intent(this, GetSharedInfo.isTVDevice(this) ? LiveActivity.class : LiveMobileActivity.class));
                finish();
                break;
            case OPEN_SETTINGS:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            default:
                Toast.makeText(this, "Diga: abrir filme seguido do título", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openMovieByVoice(String query) {
        MovieModel movie = VoiceMediaMatcher.findUniqueMovie(this.movieModels, query);
        if (movie == null) {
            Toast.makeText(this, "Filme não encontrado ou nome ambíguo", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = 0;
        for (int i = 0; i < this.movieModels.size(); i++) {
            if (this.movieModels.get(i) == movie) {
                index = i;
                break;
            }
        }
        new AnonymousClass1().onItemClick(movie, index);
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
            case R.id.ly_back /* 2131427888 */:
            case R.id.txt_home /* 2131428282 */:
                finish();
                break;
            case R.id.ly_search /* 2131427911 */:
                Intent intent = new Intent(this, (Class<?>) SearchActivity.class);
                intent.putExtra("is_live", false);
                startActivity(intent);
                break;
            case R.id.txt_live /* 2131428285 */:
                if (GetSharedInfo.isTVDevice(this)) {
                    startActivity(new Intent(this, (Class<?>) LiveActivity.class));
                } else {
                    startActivity(new Intent(this, (Class<?>) LiveMobileActivity.class));
                }
                finish();
                break;
            case R.id.txt_series /* 2131428309 */:
                startActivity(new Intent(this, (Class<?>) SeriesActivity.class));
                finish();
                break;
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_movie);
        Utils.FullScreenCall(this);
        this.preferenceHelper = new PreferenceHelper(this);
        this.wordModels = GetSharedInfo.getWordModel(this);
        initView();
        this.sortLists = GetSharedInfo.getVodSortLists(this.wordModels);
        Constants.getVodGroupModels(this.preferenceHelper.getSharedPreferenceInvisibleVodCategories(), this);
        this.categoryModels = LTVApp.vod_categories_filter;
        this.category_pos = getAvailableCategoryPosition();
        this.sort_pos = this.preferenceHelper.getSharedPreferenceVodOrder();
        this.movieModels = RealmController.with().getMovieModelsByCategory(this.categoryModels.get(this.category_pos), "", this.preferenceHelper.getSharedPreferenceISM3U(), this.sort_pos);
        this.txt_category.setText(this.categoryModels.get(this.category_pos).getName() + "(" + this.movieModels.size() + ")");
        this.recycler_category.setAdapter(new RecyclerVodCategoryAdapter(this, this.categoryModels, this.category_pos, false, this.preferenceHelper.getSharedPreferenceISM3U(), true, new LiveActivity$$ExternalSyntheticLambda4(this, 2)));
        this.recycler_category.setSelectedPosition(this.category_pos);
        VodRecyclerAdapter vodRecyclerAdapter = new VodRecyclerAdapter(this, this.movieModels, false);
        this.vodAdapter = vodRecyclerAdapter;
        vodRecyclerAdapter.setItemClickListener(new AnonymousClass1());
        this.recycler_movie.setAdapter(this.vodAdapter);
        this.sort_spinner.setAdapter((SpinnerAdapter) new SortSpinnerAdapter(this, this.sortLists));
        this.sort_spinner.setSelection(this.sort_pos);
        this.sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.ouropro.player.activities.MovieActivity.2
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                MovieActivity movieActivity = MovieActivity.this;
                if (movieActivity.sort_pos != i) {
                    movieActivity.sort_pos = i;
                    movieActivity.preferenceHelper.setSharedPreferenceVodOrder(i);
                    VodRecyclerAdapter vodRecyclerAdapter2 = MovieActivity.this.vodAdapter;
                    RealmController realmControllerWith = RealmController.with();
                    MovieActivity movieActivity2 = MovieActivity.this;
                    vodRecyclerAdapter2.updateData(realmControllerWith.getMovieModelsByCategory(movieActivity2.categoryModels.get(movieActivity2.category_pos), "", MovieActivity.this.preferenceHelper.getSharedPreferenceISM3U(), MovieActivity.this.sort_pos));
                    MovieActivity.this.recycler_movie.scrollToPosition(0);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.et_search.addTextChangedListener(new TextWatcher() { // from class: com.ouropro.player.activities.MovieActivity.3
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    return;
                }
                MovieActivity.this.searchModels(editable.toString());
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        if (GetSharedInfo.isTVDevice(this)) {
            setFocusTopView(false);
        }
        this.recycler_category.requestFocus();
        GetLoginFromSubtitle();
        setupVoiceButton();
    }
}

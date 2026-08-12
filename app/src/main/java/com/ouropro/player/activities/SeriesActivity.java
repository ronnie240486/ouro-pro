package com.ouropro.player.activities;

import android.Manifest;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.ouropro.player.R;
import com.ouropro.player.activities.mobile.LiveMobileActivity;
import com.ouropro.player.adapter.RecyclerVodCategoryAdapter;
import com.ouropro.player.adapter.SeriesRecyclerAdapter;
import com.ouropro.player.adapter.SortSpinnerAdapter;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.improvements.VoiceCommand;
import com.ouropro.player.improvements.VoiceCommandController;
import com.ouropro.player.improvements.VoiceMediaMatcher;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.SeriesModel;
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
public class SeriesActivity extends AppCompatActivity implements View.OnClickListener {
    public List<CategoryModel> categoryModels;
    public EditText et_search;
    public LinearLayout ly_back;
    public LinearLayout ly_search;
    public PreferenceHelper preferenceHelper;
    public LiveVerticalGridView recycler_category;
    public LiveVerticalGridView recycler_series;
    public SeriesRecyclerAdapter seriesAdapter;
    public RealmResults<SeriesModel> seriesModels;
    public CustomSpinner sort_spinner;
    public TextView txt_back;
    public TextView txt_category;
    public TextView txt_home;
    public TextView txt_live;
    public TextView txt_movie;
    public TextView txt_search;
    public TextView txt_series;
    public WordModels wordModels;
    private Button voiceButton;
    private VoiceCommandController voiceCommandController;
    private static final int VOICE_PERMISSION_REQUEST = 911;
    public List<String> sortLists = new ArrayList();
    public int category_pos = 0;
    public int sort_pos = 0;
    public int pre_category_pos = 0;
    public int pre_series_pos = 0;

    /* JADX INFO: renamed from: com.ouropro.player.activities.SeriesActivity$1, reason: invalid class name */
    public class AnonymousClass1 implements SeriesRecyclerAdapter.ItemClickListener {
        public AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFavClick$0(int i) {
            SeriesActivity.this.seriesAdapter.notifyItemChanged(i);
            SeriesActivity.this.preferenceHelper.setSharedPreferenceSeriesFavNames(RealmController.with().getFavSeriesNames());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUnFavClick$1(int i) {
            SeriesActivity.this.seriesAdapter.notifyItemChanged(i);
            SeriesActivity.this.preferenceHelper.setSharedPreferenceSeriesFavNames(RealmController.with().getFavSeriesNames());
        }

        public void onFavClick(SeriesModel seriesModel, int i) {
            RealmController.with().addToFavSeries(seriesModel.getName(), true, new SeriesActivity$1$$ExternalSyntheticLambda0(this, i, 0));
        }

        public void onFocusPosition(int i) {
            SeriesActivity.this.pre_series_pos = i;
        }

        public void onItemClick(SeriesModel seriesModel, int i) {
            Objects.requireNonNull(SeriesActivity.this);
            SeriesActivity seriesActivity = SeriesActivity.this;
            if (seriesActivity.categoryModels.get(seriesActivity.category_pos).getId().equalsIgnoreCase(Constants.resume_id)) {
                Intent intent = new Intent(SeriesActivity.this, (Class<?>) SeasonActivity.class);
                intent.putExtra("series_name", seriesModel.getName());
                intent.putExtra("series_id", seriesModel.getSeries_id());
                SeriesActivity.this.startActivity(intent);
                return;
            }
            Intent intent2 = new Intent(SeriesActivity.this, (Class<?>) SeriesInfoActivity.class);
            intent2.putExtra("series_id", seriesModel.getSeries_id());
            intent2.putExtra("name", seriesModel.getName());
            if (SeriesActivity.this.preferenceHelper.getSharedPreferenceISM3U()) {
                intent2.putExtra("category_name", seriesModel.getCategory_name());
            } else {
                intent2.putExtra("category_name", SeriesActivity.this.getSeriesCategoryName(seriesModel.getCategory_id()));
            }
            SeriesActivity.this.startActivity(intent2);
        }

        public void onUnFavClick(SeriesModel seriesModel, int i) {
            RealmController.with().addToFavSeries(seriesModel.getName(), false, new SeriesActivity$1$$ExternalSyntheticLambda0(this, i, 1));
        }
    }

    private void GetLoginFromSubtitle() {
        GetSubtitleLoginRequest getSubtitleLoginRequest = new GetSubtitleLoginRequest(this, 1000);
        getSubtitleLoginRequest.getResponse(Security.getUserObject(Constants.USERNAME, Constants.PASSWORD), Constants.SUBTITLE_LOGIN, Constants.API_KEY);
        getSubtitleLoginRequest.setOnGetLinkModelListener(new MovieActivity$$ExternalSyntheticLambda2(this, 4));
    }

    private int getAvailableCategoryPosition() {
        for (int i = 0; i < this.categoryModels.size(); i++) {
            if (RealmController.with().getSeriesModelsByCategory(this.categoryModels.get(i), "", this.preferenceHelper.getSharedPreferenceISM3U(), 0).size() > 0) {
                return i;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getSeriesCategoryName(String str) {
        for (CategoryModel categoryModel : LTVApp.series_categories_filter) {
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
        this.txt_category = (TextView) findViewById(R.id.txt_category);
        this.txt_search = (TextView) findViewById(R.id.txt_search);
        this.txt_back = (TextView) findViewById(R.id.txt_back);
        this.et_search = (EditText) findViewById(R.id.et_search);
        this.recycler_series = (LiveVerticalGridView) findViewById(R.id.recycler_series);
        this.recycler_category = (LiveVerticalGridView) findViewById(R.id.recycler_category);
        this.sort_spinner = (CustomSpinner) findViewById(R.id.sort_spinner);
        if (GetSharedInfo.isTVDevice(this)) {
            this.recycler_category.setNumColumns(1);
            this.recycler_category.setLoop(false);
            this.recycler_category.setPreserveFocusAfterLayout(true);
            final View[] viewArr = {null};
            this.recycler_category.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.SeriesActivity.4
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
            this.recycler_series.setNumColumns(5);
            this.recycler_series.setLoop(false);
            this.recycler_series.setPreserveFocusAfterLayout(true);
            final View[] viewArr2 = {null};
            this.recycler_series.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.SeriesActivity.5
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
            this.recycler_series.setLayoutManager(new GridLayoutManager(this, 5));
            this.recycler_series.setHasFixedSize(true);
        }
        this.txt_home.setText(this.wordModels.getHome());
        this.txt_search.setText(this.wordModels.getSearch());
        this.txt_back.setText(this.wordModels.getBack());
        this.txt_live.setText(this.wordModels.getLive_tv());
        this.txt_movie.setText(this.wordModels.getMovies());
        this.txt_series.setText(this.wordModels.getSeries());
        this.ly_back.setOnClickListener(this);
        this.ly_search.setOnClickListener(this);
        this.txt_home.setOnClickListener(this);
        this.txt_live.setOnClickListener(this);
        this.txt_movie.setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$GetLoginFromSubtitle$1(JSONObject jSONObject, int i) {
        if (jSONObject != null) {
            this.preferenceHelper.setSharedPreferenceSubtitleLoginModel((SubTitleUserModel) new Gson().fromJson(jSONObject.toString(), SubTitleUserModel.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreate$0(CategoryModel categoryModel, Integer num, Boolean bool) {
        if (!bool.booleanValue()) {
            this.pre_category_pos = num.intValue();
            return null;
        }
        if (this.category_pos == num.intValue()) {
            return null;
        }
        this.et_search.setText("");
        this.category_pos = num.intValue();
        this.seriesModels = RealmController.with().getSeriesModelsByCategory(categoryModel, "", this.preferenceHelper.getSharedPreferenceISM3U(), this.sort_pos);
        this.txt_category.setText(categoryModel.getName() + "(" + this.seriesModels.size() + ")");
        this.seriesAdapter.updateData(this.seriesModels);
        this.recycler_series.setSelectedPosition(0);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchModels(String str) {
        RealmResults<SeriesModel> seriesModelsByCategory = RealmController.with().getSeriesModelsByCategory(this.categoryModels.get(this.category_pos), str, this.preferenceHelper.getSharedPreferenceISM3U(), this.sort_pos);
        this.seriesModels = seriesModelsByCategory;
        this.seriesAdapter.updateData(seriesModelsByCategory);
        this.recycler_series.setSelectedPosition(0);
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
                            if (this.recycler_series.hasFocus() && this.pre_series_pos < 5) {
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
                                this.recycler_series.requestFocus();
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
                                this.recycler_series.requestFocus();
                                return true;
                            }
                            if (this.ly_search.hasFocus()) {
                                setFocusTopView(false);
                                this.recycler_series.requestFocus();
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
                } else if (this.recycler_series.hasFocus() && this.pre_series_pos < this.seriesModels.size() - 11) {
                    int i3 = this.pre_series_pos + 10;
                    this.pre_series_pos = i3;
                    this.recycler_series.setSelectedPosition(i3);
                }
            } else if (this.recycler_category.hasFocus()) {
                int i4 = this.pre_category_pos;
                if (i4 > 10) {
                    int i5 = i4 - 10;
                    this.pre_category_pos = i5;
                    this.recycler_category.setSelectedPosition(i5);
                }
            } else if (this.recycler_series.hasFocus() && (i = this.pre_series_pos) > 10) {
                int i6 = i - 10;
                this.pre_series_pos = i6;
                this.recycler_series.setSelectedPosition(i6);
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
        this.voiceButton.setContentDescription("Comando de voz para séries");
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
                Toast.makeText(SeriesActivity.this, message, Toast.LENGTH_SHORT).show();
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
            case OPEN_SERIES_ITEM:
            case SEARCH_SERIES:
                openSeriesByVoice(command.getQuery());
                break;
            case OPEN_MOVIES:
                startActivity(new Intent(this, MovieActivity.class));
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
                Toast.makeText(this, "Diga: abrir série seguido do título", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openSeriesByVoice(String query) {
        SeriesModel series = VoiceMediaMatcher.findUniqueSeries(this.seriesModels, query);
        if (series == null) {
            Toast.makeText(this, "Série não encontrada ou nome ambíguo", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = 0;
        for (int i = 0; i < this.seriesModels.size(); i++) {
            if (this.seriesModels.get(i) == series) {
                index = i;
                break;
            }
        }
        new AnonymousClass1().onItemClick(series, index);
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
            case R.id.txt_movie /* 2131428290 */:
                startActivity(new Intent(this, (Class<?>) MovieActivity.class));
                finish();
                break;
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_series);
        Utils.FullScreenCall(this);
        this.preferenceHelper = new PreferenceHelper(this);
        this.wordModels = GetSharedInfo.getWordModel(this);
        initView();
        this.sortLists = GetSharedInfo.getVodSortLists(this.wordModels);
        Constants.getSeriesGroupModels(this.preferenceHelper.getSharedPreferenceInvisibleSeriesCategories(), this);
        this.categoryModels = LTVApp.series_categories_filter;
        this.category_pos = getAvailableCategoryPosition();
        this.sort_pos = this.preferenceHelper.getSharedPreferenceSeriesOrder();
        this.seriesModels = RealmController.with().getSeriesModelsByCategory(this.categoryModels.get(this.category_pos), "", this.preferenceHelper.getSharedPreferenceISM3U(), this.sort_pos);
        this.txt_category.setText(this.categoryModels.get(this.category_pos).getName() + "(" + this.seriesModels.size() + ")");
        this.recycler_category.setAdapter(new RecyclerVodCategoryAdapter(this, this.categoryModels, this.category_pos, false, this.preferenceHelper.getSharedPreferenceISM3U(), false, new LiveActivity$$ExternalSyntheticLambda4(this, 4)));
        this.recycler_category.setSelectedPosition(this.category_pos);
        SeriesRecyclerAdapter seriesRecyclerAdapter = new SeriesRecyclerAdapter(this, this.seriesModels, false);
        this.seriesAdapter = seriesRecyclerAdapter;
        seriesRecyclerAdapter.setItemClickListener(new AnonymousClass1());
        this.recycler_series.setAdapter(this.seriesAdapter);
        this.sort_spinner.setAdapter((SpinnerAdapter) new SortSpinnerAdapter(this, this.sortLists));
        this.sort_spinner.setSelection(this.sort_pos);
        this.sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.ouropro.player.activities.SeriesActivity.2
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SeriesActivity seriesActivity = SeriesActivity.this;
                if (seriesActivity.sort_pos != i) {
                    seriesActivity.sort_pos = i;
                    seriesActivity.preferenceHelper.setSharedPreferenceSeriesOrder(i);
                    SeriesActivity seriesActivity2 = SeriesActivity.this;
                    RealmController realmControllerWith = RealmController.with();
                    SeriesActivity seriesActivity3 = SeriesActivity.this;
                    seriesActivity2.seriesModels = realmControllerWith.getSeriesModelsByCategory(seriesActivity3.categoryModels.get(seriesActivity3.category_pos), "", SeriesActivity.this.preferenceHelper.getSharedPreferenceISM3U(), SeriesActivity.this.sort_pos);
                    SeriesActivity seriesActivity4 = SeriesActivity.this;
                    seriesActivity4.seriesAdapter.updateData(seriesActivity4.seriesModels);
                    SeriesActivity.this.recycler_series.setSelectedPosition(0);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.et_search.addTextChangedListener(new TextWatcher() { // from class: com.ouropro.player.activities.SeriesActivity.3
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    return;
                }
                SeriesActivity.this.searchModels(editable.toString());
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

package com.ouropro.player.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ouropro.player.R;
import com.ouropro.player.adapter.DateRecyclerAdapter;
import com.ouropro.player.adapter.ProgramRecyclerAdapter;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.improvements.EpgReminderStore;
import com.ouropro.player.improvements.XmlTvEpgLoader;
import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.models.CatchUpEpgResponse;
import com.ouropro.player.models.CatchupModel;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.remote.RetroClass;
import com.ouropro.player.utils.Utils;
import com.ouropro.player.view.LiveVerticalGridView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class CatchUpActivity extends AppCompatActivity {
    public List<CatchUpEpg> catchUpEpgList;
    public List<CatchupModel> catchupModels;
    public ImageView channel_image;
    public TextView channel_name;
    public List<CatchUpEpg> currentEventList;
    public DateRecyclerAdapter dateAdapter;
    public LiveVerticalGridView date_list;
    public LiveVerticalGridView epg_list;
    public ImageButton image_back;
    public PreferenceHelper preferenceHelper;
    public ProgramRecyclerAdapter programAdapter;
    public ProgressBar progressBar;
    public EPGChannel selectedChannel;
    public TextView txt_catch;
    public int date_pos = -1;
    public int program_pos = -1;
    public int epg_focus_pos = -1;
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d");
    public SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");
    public WordModels wordModels = new WordModels();
    private final Handler reminderHandler = new Handler(Looper.getMainLooper());
    private AlertDialog reminderDialog;
    private CountDownTimer reminderTimer;
    private CatchUpEpg activeReminder;

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v15, types: [java.util.ArrayList, java.util.List<com.ouropro.player.models.CatchupModel>] */
    /* JADX WARN: Type inference failed for: r13v2, types: [java.util.ArrayList, java.util.List<com.ouropro.player.models.CatchupModel>] */
    /* JADX WARN: Type inference failed for: r13v9, types: [java.util.ArrayList, java.util.List<com.ouropro.player.models.CatchupModel>] */
    /* JADX WARN: Type inference failed for: r7v3, types: [java.util.ArrayList, java.util.List<com.ouropro.player.models.CatchupModel>] */
    public void getCatchupModels(List<CatchUpEpg> list) {
        this.catchupModels = new ArrayList();
        ArrayList arrayList = new ArrayList();
        String str = null;
        String str2 = null;
        int i = 0;
        for (CatchUpEpg catchUpEpg : list) {
            i++;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(catchUpEpg.getStart_timestamp() * 1000);
            String str3 = this.simpleDateFormat.format(calendar.getTime());
            if (str == null) {
                str2 = this.weekFormat.format(calendar.getTime());
                arrayList = new ArrayList();
                str = str3;
            }
            if (!str3.equals(str)) {
                CatchupModel catchupModel = new CatchupModel();
                catchupModel.setName(str);
                catchupModel.setDayofweek(str2);
                catchupModel.setEpgEvents(arrayList);
                this.catchupModels.add(catchupModel);
                str2 = this.weekFormat.format(calendar.getTime());
                arrayList = new ArrayList();
                str = str3;
            }
            arrayList.add(catchUpEpg);
            if (i == list.size()) {
                CatchupModel catchupModel2 = new CatchupModel();
                catchupModel2.setName(str);
                String str4 = this.weekFormat.format(calendar.getTime());
                catchupModel2.setDayofweek(str4);
                catchupModel2.setEpgEvents(arrayList);
                this.catchupModels.add(catchupModel2);
                str2 = str4;
            }
        }
        this.progressBar.setVisibility(8);
        if (this.catchupModels.size() <= 0) {
            Toast.makeText(this, this.wordModels.getNo_epg_avaliable(), 0).show();
            return;
        }
        int currentDatePosition = getCurrentDatePosition(this.catchupModels);
        this.date_pos = currentDatePosition;
        this.dateAdapter.setCatchupModels(this.catchupModels, currentDatePosition);
        this.currentEventList = ((CatchupModel) this.catchupModels.get(this.date_pos)).getEpgEvents();
        int i2 = -1;
        for (int i3 = 0; i3 < this.currentEventList.size(); i3++) {
            if (this.currentEventList.get(i3).getNow_playing() == 1) {
                i2 = i3;
            }
        }
        if (i2 == -1) {
            this.program_pos = 0;
        } else {
            this.program_pos = i2;
        }
        this.programAdapter.setProgramList(this.currentEventList);
        this.epg_list.setSelectedPosition(this.program_pos);
        this.programAdapter.setFocusDisable(this.program_pos, false);
        this.programAdapter.setCurrentProgramPosition(i2);
        this.date_list.requestFocus();
        this.date_list.setSelectedPosition(this.date_pos);
    }

    private int getCurrentDatePosition(List<CatchupModel> list) {
        for (int i = 0; i < list.size(); i++) {
            if (this.simpleDateFormat.format(new Date()).equalsIgnoreCase(list.get(i).getName())) {
                return i;
            }
        }
        return 0;
    }

    private void getEpg() {
        this.progressBar.setVisibility(0);
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl(), this.preferenceHelper.getSharedPreferenceISM3U()).get_short_epg(this.preferenceHelper.getSharedPreferenceUsername(), this.preferenceHelper.getSharedPreferencePassword(), this.selectedChannel.getStream_id()).enqueue(new Callback<CatchUpEpgResponse>() { // from class: com.ouropro.player.activities.CatchUpActivity.1
                public void onFailure(@NonNull Call<CatchUpEpgResponse> call, @NonNull Throwable th) {
                    CatchUpActivity.this.loadXmlTvEpg();
                }

                public void onResponse(@NonNull Call<CatchUpEpgResponse> call, @NonNull Response<CatchUpEpgResponse> response) {
                    if (response.body() != null && response.body().getEpg_listings().size() != 0) {
                        CatchUpActivity.this.catchUpEpgList = response.body().getEpg_listings();
                        CatchUpActivity catchUpActivity = CatchUpActivity.this;
                        catchUpActivity.getCatchupModels(catchUpActivity.catchUpEpgList);
                        return;
                    }
                    CatchUpActivity.this.loadXmlTvEpg();
                }
            });
        } catch (Exception unused) {
            loadXmlTvEpg();
        }
    }

    private void loadXmlTvEpg() {
        XmlTvEpgLoader.load(
                this.preferenceHelper.getSharedPreferenceServerUrl(),
                this.preferenceHelper.getSharedPreferenceISM3U(),
                this.preferenceHelper.getSharedPreferenceUsername(),
                this.preferenceHelper.getSharedPreferencePassword(),
                this.preferenceHelper.getSharedPreferenceM3UEpgUrl(),
                this.selectedChannel == null ? "" : this.selectedChannel.getId() + "|" + this.selectedChannel.getStream_id(),
                this.selectedChannel == null ? "" : this.selectedChannel.getName(),
                new XmlTvEpgLoader.Listener() {
                    @Override
                    public void onLoaded(List<CatchUpEpg> programs) {
                        runOnUiThread(() -> {
                            if (programs == null || programs.isEmpty()) {
                                progressBar.setVisibility(8);
                                Toast.makeText(CatchUpActivity.this, wordModels.getNo_epg_avaliable(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            catchUpEpgList = programs;
                            getCatchupModels(programs);
                        });
                    }

                    @Override
                    public void onError(Throwable error) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(8);
                            Toast.makeText(CatchUpActivity.this, wordModels.getNo_epg_avaliable(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void goToLivePage() {
        goToLivePage(false);
    }

    private void goToLivePage(boolean selectChannel) {
        Intent intent = new Intent();
        if (selectChannel) {
            saveTargetChannelPosition();
            intent.putExtra("is_changed", "from_search");
            intent.putExtra("go_to_channel", true);
            intent.putExtra("target_stream_id", this.selectedChannel == null ? "" : this.selectedChannel.getStream_id());
        } else {
            intent.putExtra("is_changed", "");
        }
        setResult(-1, intent);
        finish();
    }

    private void saveTargetChannelPosition() {
        if (this.selectedChannel == null || this.preferenceHelper == null) {
            return;
        }
        List<com.ouropro.player.models.CategoryModel> categories = this.preferenceHelper.getSharedLiveCategoryModels();
        if (categories == null || categories.isEmpty()) {
            return;
        }
        boolean isM3u = this.preferenceHelper.getSharedPreferenceISM3U();
        String targetCategory = isM3u ? this.selectedChannel.getCategory_name() : this.selectedChannel.getCategory_id();
        if (targetCategory == null) {
            targetCategory = "";
        }
        if (isM3u && targetCategory.contains("!@#%")) {
            targetCategory = targetCategory.split("!@#%", 2)[0];
        }
        for (int categoryIndex = 0; categoryIndex < categories.size(); categoryIndex++) {
            com.ouropro.player.models.CategoryModel category = categories.get(categoryIndex);
            if (category == null) {
                continue;
            }
            String categoryKey = isM3u ? category.getName() : category.getId();
            if (isM3u && categoryKey != null && categoryKey.contains("!@#%")) {
                categoryKey = categoryKey.split("!@#%", 2)[0];
            }
            if (categoryKey == null || !categoryKey.equalsIgnoreCase(targetCategory)) {
                continue;
            }
            io.realm.RealmResults<com.ouropro.player.models.EPGChannel> channels = RealmController.with().getLiveChannelsByCategory(category, "", isM3u, this.preferenceHelper.getSharedPreferenceLiveOrder());
            for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++) {
                com.ouropro.player.models.EPGChannel channel = channels.get(channelIndex);
                if (channel != null && this.selectedChannel.getStream_id() != null && this.selectedChannel.getStream_id().equalsIgnoreCase(channel.getStream_id())) {
                    this.preferenceHelper.setSharedPreferenceCategoryPos(categoryIndex);
                    this.preferenceHelper.setSharedPreferenceChannelPos(channelIndex);
                    return;
                }
            }
        }
    }

    private boolean isReminderScheduled(CatchUpEpg program) {
        return this.selectedChannel != null && EpgReminderStore.isScheduled(this, this.selectedChannel.getStream_id(), program);
    }

    private void toggleReminder(CatchUpEpg program) {
        if (this.selectedChannel == null || program == null) {
            return;
        }
        boolean scheduled = isReminderScheduled(program);
        EpgReminderStore.setScheduled(this, this.selectedChannel.getStream_id(), program, !scheduled);
        int index = this.currentEventList == null ? -1 : this.currentEventList.indexOf(program);
        if (index >= 0 && this.programAdapter != null) {
            this.programAdapter.notifyItemChanged(index);
        }
        if (scheduled) {
            Toast.makeText(this, "Aviso removido", Toast.LENGTH_SHORT).show();
            return;
        }
        long startMillis = program.getStart_timestamp() * 1000L;
        long delay = startMillis - System.currentTimeMillis() - 10000L;
        if (delay < 0L) {
            delay = 0L;
        }
        this.reminderHandler.postDelayed(() -> showReminderDialog(program), delay);
        Toast.makeText(this, "Aviso ativado para este programa", Toast.LENGTH_SHORT).show();
    }

    private void showReminderDialog(CatchUpEpg program) {
        if (program == null || this.selectedChannel == null || !isReminderScheduled(program)
                || isFinishing() || (android.os.Build.VERSION.SDK_INT >= 17 && isDestroyed())) {
            return;
        }
        if (this.reminderDialog != null && this.reminderDialog.isShowing()) {
            return;
        }
        this.activeReminder = program;
        final String title = Utils.decode64String(program.getTitle());
        final int gold = Color.rgb(255, 208, 0);
        final int dark = Color.rgb(28, 22, 36);
        int density = (int) (getResources().getDisplayMetrics().density + 0.5f);
        int padding = 24 * density;

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(padding, padding, padding, padding / 2);
        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setColor(Color.argb(228, 28, 22, 36));
        cardBackground.setCornerRadius(22 * density);
        cardBackground.setStroke(2 * density, gold);
        content.setBackground(cardBackground);
        FrameLayout modalRoot = new FrameLayout(this);
        ImageView backdrop = new ImageView(this);
        backdrop.setImageResource(R.drawable.home_logo);
        backdrop.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        backdrop.setAlpha(0.12f);
        modalRoot.addView(backdrop, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        modalRoot.addView(content, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView clockIcon = new ImageView(this);
        clockIcon.setImageResource(R.drawable.ic_clock_black_24dp);
        clockIcon.setColorFilter(gold, android.graphics.PorterDuff.Mode.SRC_IN);
        clockIcon.setAlpha(0.96f);
        int iconSize = 52 * density;
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.gravity = Gravity.CENTER_HORIZONTAL;
        content.addView(clockIcon, iconParams);

        TextView header = new TextView(this);
        header.setText("LEMBRETE DO EPG");
        header.setTextColor(gold);
        header.setTextSize(13.0f);
        header.setGravity(Gravity.CENTER);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(header, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setTextSize(17.0f);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, padding / 2, 0, padding / 3);
        status.setText("O programa começa em 10 segundos");
        content.addView(status, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView titleView = new TextView(this);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(22.0f);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setSingleLine(true);
        titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        titleView.setText(title);
        content.addView(titleView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView countdown = new TextView(this);
        countdown.setTextColor(gold);
        countdown.setTextSize(34.0f);
        countdown.setGravity(Gravity.CENTER);
        countdown.setTypeface(null, android.graphics.Typeface.BOLD);
        countdown.setText("10");
        GradientDrawable clockBackground = new GradientDrawable();
        clockBackground.setShape(GradientDrawable.OVAL);
        clockBackground.setColor(Color.rgb(48, 36, 58));
        clockBackground.setStroke(4 * density, gold);
        countdown.setBackground(clockBackground);
        int clockSize = 96 * density;
        LinearLayout.LayoutParams clockParams = new LinearLayout.LayoutParams(clockSize, clockSize);
        clockParams.gravity = Gravity.CENTER_HORIZONTAL;
        clockParams.topMargin = padding / 2;
        clockParams.bottomMargin = padding / 2;
        content.addView(countdown, clockParams);

        ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(10);
        progress.setProgress(10);
        content.addView(progress, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 8 * density));

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);
        buttons.setPadding(0, padding / 2, 0, 0);
        TextView discard = new TextView(this);
        TextView goNow = new TextView(this);
        for (TextView button : new TextView[]{discard, goNow}) {
            button.setTextColor(Color.WHITE);
            button.setTextSize(14.0f);
            button.setGravity(Gravity.CENTER);
            button.setTypeface(null, android.graphics.Typeface.BOLD);
            button.setPadding(20 * density, 12 * density, 20 * density, 12 * density);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            GradientDrawable buttonBackground = new GradientDrawable();
            buttonBackground.setColor(Color.rgb(102, 72, 150));
            buttonBackground.setCornerRadius(10 * density);
            button.setBackground(buttonBackground);
        }
        discard.setText("DESCARTAR");
        goNow.setText("IR AGORA");
        GradientDrawable goNowBackground = new GradientDrawable();
        goNowBackground.setColor(gold);
        goNowBackground.setCornerRadius(10 * density);
        goNow.setBackground(goNowBackground);
        goNow.setTextColor(dark);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        buttonParams.setMargins(6 * density, 0, 6 * density, 0);
        buttons.addView(discard, new LinearLayout.LayoutParams(buttonParams));
        buttons.addView(goNow, new LinearLayout.LayoutParams(buttonParams));
        content.addView(buttons, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(modalRoot).setOnCancelListener(d -> clearActiveReminder(program)).create();
        this.reminderDialog = dialog;
        discard.setOnClickListener(v -> clearActiveReminder(program));
        goNow.setOnClickListener(v -> {
            clearActiveReminder(program);
            goToLivePage(true);
        });
        dialog.setOnShowListener(d -> {
            Window window = dialog.getWindow();
            if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setDimAmount(0.78f);
                android.view.WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.82f);
                window.setAttributes(attributes);
            }
            goNow.requestFocus();
            this.reminderTimer = new CountDownTimer(10000L, 1000L) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) Math.ceil(millisUntilFinished / 1000.0d);
                    status.setText("O programa começa em " + seconds + " segundos");
                    countdown.setText(String.valueOf(seconds));
                    progress.setProgress(seconds);
                }

                @Override
                public void onFinish() {
                    status.setText("O programa está começando agora");
                    countdown.setText("0");
                    progress.setProgress(0);
                }
            }.start();
        });
        dialog.setOnDismissListener(d -> {
            if (this.reminderTimer != null) {
                this.reminderTimer.cancel();
                this.reminderTimer = null;
            }
            this.reminderDialog = null;
        });
        dialog.show();
    }

    private void clearActiveReminder(CatchUpEpg program) {
        if (this.selectedChannel != null && program != null) {
            EpgReminderStore.setScheduled(this, this.selectedChannel.getStream_id(), program, false);
        }
        if (this.reminderTimer != null) {
            this.reminderTimer.cancel();
            this.reminderTimer = null;
        }
        if (this.reminderDialog != null && this.reminderDialog.isShowing()) {
            this.reminderDialog.dismiss();
        }
        this.activeReminder = null;
    }

    private void initView() {
        this.channel_image = (ImageView) findViewById(R.id.channel_image);
        this.channel_name = (TextView) findViewById(R.id.channel_name);
        this.date_list = (LiveVerticalGridView) findViewById(R.id.date_list);
        this.epg_list = (LiveVerticalGridView) findViewById(R.id.epg_list);
        this.txt_catch = (TextView) findViewById(R.id.txt_catch);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.progressBar = progressBar;
        progressBar.setVisibility(8);
        ImageButton imageButton = (ImageButton) findViewById(R.id.image_back);
        this.image_back = imageButton;
        imageButton.setFocusable(false);
        this.image_back.setOnClickListener(new SearchActivity$$ExternalSyntheticLambda0(this, 1));
        this.date_list.setNumColumns(1);
        this.date_list.setLoop(false);
        this.date_list.setPreserveFocusAfterLayout(true);
        final View[] viewArr = {null};
        this.date_list.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.CatchUpActivity.2
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
        this.epg_list.setNumColumns(1);
        this.epg_list.setPreserveFocusAfterLayout(true);
        this.epg_list.setLoop(false);
        final View[] viewArr2 = {null};
        this.epg_list.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.CatchUpActivity.3
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
        this.txt_catch.setText(this.wordModels.getCatch_up());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initView$2(View view) {
        goToLivePage();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreate$0(CatchupModel catchupModel, Integer num, Boolean bool) {
        if (bool.booleanValue()) {
            this.currentEventList = catchupModel.getEpgEvents();
            int i = -1;
            for (int i2 = 0; i2 < this.currentEventList.size(); i2++) {
                if (this.currentEventList.get(i2).getNow_playing() == 1) {
                    i = i2;
                }
            }
            if (i == -1) {
                this.program_pos = 0;
            } else {
                this.program_pos = i;
            }
            this.programAdapter.setProgramList(this.currentEventList);
            this.epg_list.setSelectedPosition(this.program_pos);
            this.programAdapter.setFocusDisable(this.program_pos, false);
            this.programAdapter.setCurrentProgramPosition(i);
        }
        this.date_pos = num.intValue();
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreate$1(CatchUpEpg catchUpEpg, Integer num, Boolean bool) {
        if (bool.booleanValue()) {
            if (catchUpEpg.getHas_archive() == 1) {
                Intent intent = new Intent(this, (Class<?>) CatchUpPlayerActivity.class);
                intent.putExtra("position", num);
                intent.putExtra("stream_id", this.selectedChannel.getStream_id());
                intent.putExtra("epg_model", new Gson().toJson(this.currentEventList));
                startActivity(intent);
            } else {
                Toast.makeText(this, "This program can not be play", 0).show();
            }
        }
        this.epg_focus_pos = num.intValue();
        return null;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        List<CatchUpEpg> list;
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 4) {
                goToLivePage();
                return true;
            }
            if (keyCode != 22) {
                if (keyCode != 19) {
                    if (keyCode == 20 && this.image_back.hasFocus()) {
                        this.image_back.setFocusable(false);
                        this.date_list.requestFocus();
                        return true;
                    }
                } else {
                    if (this.date_list.hasFocus() && this.date_pos == 0) {
                        this.image_back.setFocusable(true);
                        this.image_back.requestFocus();
                        return true;
                    }
                    if (this.epg_list.hasFocus() && this.epg_focus_pos == 0) {
                        this.image_back.setFocusable(true);
                        this.image_back.requestFocus();
                        return true;
                    }
                }
            } else if (this.image_back.hasFocus() && (list = this.currentEventList) != null && list.size() > 0) {
                this.image_back.setFocusable(false);
                this.epg_list.requestFocus();
                return true;
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_catch_up);
        Utils.FullScreenCall(this);
        this.preferenceHelper = new PreferenceHelper(this);
        this.wordModels = GetSharedInfo.getWordModel(this);
        initView();
        String requestedStreamId = getIntent().getStringExtra("catchup_stream_id");
        EPGChannel channelByStreamId = RealmController.with().getEpgChannelByStreamId(requestedStreamId);
        this.selectedChannel = channelByStreamId != null ? channelByStreamId : RealmController.with().getEpgChannelByName(LTVApp.channelName);
        if (this.selectedChannel == null) {
            Toast.makeText(this, this.wordModels.getNo_epg_avaliable(), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        TextView textView = this.channel_name;
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("");
        sbM.append(this.selectedChannel.getNum());
        sbM.append(" ");
        sbM.append(this.selectedChannel.getName());
        textView.setText(sbM.toString());
        try {
            Glide.with((FragmentActivity) this).load(this.selectedChannel.getStream_icon()).error(R.drawable.home_logo).into(this.channel_image);
        } catch (Exception unused) {
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.home_logo)).error(R.drawable.home_logo).into(this.channel_image);
        }
        final int i = 0;
        DateRecyclerAdapter dateRecyclerAdapter = new DateRecyclerAdapter(this, new ArrayList(), new Function3() { // from class: com.ouropro.player.activities.CatchUpActivity$$ExternalSyntheticLambda0
            public final /* synthetic */ CatchUpActivity f$0;

            {
                this.f$0 = CatchUpActivity.this;
            }

            public final Object invoke(Object obj, Object obj2, Object obj3) {
                switch (i) {
                    case 0:
                        return this.f$0.lambda$onCreate$0((CatchupModel) obj, (Integer) obj2, (Boolean) obj3);
                    default:
                        return this.f$0.lambda$onCreate$1((CatchUpEpg) obj, (Integer) obj2, (Boolean) obj3);
                }
            }
        });
        this.dateAdapter = dateRecyclerAdapter;
        this.date_list.setAdapter(dateRecyclerAdapter);
        final int i2 = 1;
        ProgramRecyclerAdapter programRecyclerAdapter = new ProgramRecyclerAdapter(this, new ArrayList(), new Function3() { // from class: com.ouropro.player.activities.CatchUpActivity$$ExternalSyntheticLambda0
            public final /* synthetic */ CatchUpActivity f$0;

            {
                this.f$0 = CatchUpActivity.this;
            }

            public final Object invoke(Object obj, Object obj2, Object obj3) {
                switch (i2) {
                    case 0:
                        return this.f$0.lambda$onCreate$0((CatchupModel) obj, (Integer) obj2, (Boolean) obj3);
                    default:
                        return this.f$0.lambda$onCreate$1((CatchUpEpg) obj, (Integer) obj2, (Boolean) obj3);
                }
            }
        });
        this.programAdapter = programRecyclerAdapter;
        programRecyclerAdapter.setBellClickListener(new ProgramRecyclerAdapter.BellClickListener() {
            @Override
            public boolean isScheduled(CatchUpEpg program) {
                return isReminderScheduled(program);
            }

            @Override
            public void onBellClick(CatchUpEpg program) {
                toggleReminder(program);
            }
        });
        this.epg_list.setAdapter(programRecyclerAdapter);
        getEpg();
    }

    @Override
    protected void onDestroy() {
        this.reminderHandler.removeCallbacksAndMessages(null);
        if (this.reminderTimer != null) {
            this.reminderTimer.cancel();
            this.reminderTimer = null;
        }
        if (this.reminderDialog != null && this.reminderDialog.isShowing()) {
            this.reminderDialog.dismiss();
        }
        super.onDestroy();
    }
}

package com.ouropro.player.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DeviceInfo;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider;
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoSize;
import com.ouropro.player.R;
import com.ouropro.player.adapter.EpgRecyclerAdapter;
import com.ouropro.player.adapter.RecyclerLiveCategoryAdapter;
import com.ouropro.player.adapter.RecyclerLiveChannelAdapter;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.apps.FocusStatus;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.dlgfragment.LockDlgFragment;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.HeartbeatPeriodicHelper;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.improvements.XmlTvEpgLoader;
import com.ouropro.player.improvements.EpgReminderBinder;
import com.ouropro.player.improvements.EpgReminderStore;
import com.ouropro.player.improvements.NullTextGuard;
import com.ouropro.player.improvements.VoiceChannelMatcher;
import com.ouropro.player.improvements.VoiceCommand;
import com.ouropro.player.improvements.VoiceButtonFactory;
import com.ouropro.player.improvements.VoiceCommandController;
import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.models.CatchUpEpgResponse;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.remote.RetroClass;
import com.ouropro.player.utils.DemoUtil;
import com.ouropro.player.utils.Utils;
import com.ouropro.player.view.LiveVerticalGridView;
import io.realm.RealmResults;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* JADX INFO: loaded from: classes.dex */
public class LiveActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {
    public Button btn_catch_up;
    public Button btn_fav;
    public Button btn_search;
    public RecyclerLiveCategoryAdapter categoryAdapter;
    public List<CategoryModel> categoryModels;
    public RecyclerLiveChannelAdapter channelAdapter;
    public ImageView channel_image;
    public String channel_name;
    public String content_url;
    public DataSource.Factory dataSourceFactory;
    public EpgRecyclerAdapter epgAdapter;
    public RealmResults<EPGChannel> epgChannels;
    public List<CatchUpEpg> epgEventList;
    public LiveActivity$$ExternalSyntheticLambda3 epgTicker;
    public int epgTime;
    public EditText et_search;
    public HeartbeatPeriodicHelper heartbeatHelper;
    public LiveActivity$$ExternalSyntheticLambda2 hideInfoTicker;
    public int hide_time;
    public ImageButton image_audio;
    public ImageView image_def;
    public ImageButton image_epg;
    public ImageButton image_fav;
    public ImageButton image_search;
    public ImageButton image_series;
    public ImageButton image_subtitle;
    public ImageButton image_vod;
    public EPGChannel keySelChannel;
    public LockDlgFragment lockDlgFragment;
    public ConstraintLayout ly_actions;
    public ConstraintLayout ly_buttons;
    public ConstraintLayout ly_control;
    public ConstraintLayout ly_surface;
    public View epg_summary_visible;
    public ConstraintLayout main_lay;
    public LiveActivity$$ExternalSyntheticLambda2 moveTicker;
    public int move_time;
    public ExoPlayer player;
    public StyledPlayerView playerView;
    public PreferenceHelper preferenceHelper;
    public LiveVerticalGridView recycler_category;
    public LiveVerticalGridView recycler_channel;
    public RecyclerView recycler_epg;
    public RecyclerView visibleEpgPanel;
    public SeekBar seekBar;
    public EPGChannel selectedChannel;
    public TrackSelectionParameters trackSelectionParameters;
    public DefaultTrackSelector trackSelector;
    public TextView txt_audio;
    public TextView txt_bottom_series;
    public TextView txt_channel_name;
    public TextView txt_current_program;
    public TextView txt_current_time;
    public TextView txt_epg_now_visible;
    public TextView txt_epg_next_visible;
    public TextView txt_epg;
    public TextView txt_fav;
    public TextView txt_group;
    public TextView txt_home;
    public TextView txt_left;
    public TextView txt_live;
    public TextView txt_movie;
    public TextView txt_name;
    public TextView txt_next_program;
    public TextView txt_next_time;
    public TextView txt_num;
    public TextView txt_resolution;
    public TextView txt_right;
    public TextView txt_search;
    public TextView txt_series;
    public String epgNowDisplay = "carregando EPG...";
    public String epgNextDisplay = "aguardando programação...";
    public TextView txt_subtitle;
    public TextView txt_vod;
    public WordModels wordModels;
    private ImageButton voiceButton;
    private VoiceCommandController voiceCommandController;
    private static final int VOICE_PERMISSION_REQUEST = 904;
    public int category_pos = 0;
    public int channel_pos = 0;
    public int pre_category_pos = 0;
    public int pre_channel_pos = 0;
    public int move_pos = 0;
    public int error_count = 0;
    public String stream_id = "";
    private Handler tvReminderHandler = new Handler();
    private Runnable tvReminderRunnable;
    private String scheduledTvReminderKey = "";
    private CountDownTimer tvReminderCountdown;
    public String key = "";
    public boolean is_full = false;
    public Handler handler = new Handler();
    public String categoryName = "";
    public FocusStatus focusStatus = FocusStatus.second;
    public ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new LiveActivity$$ExternalSyntheticLambda1(this));

    /* JADX INFO: renamed from: com.ouropro.player.activities.LiveActivity$8, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass8 {
        public static final /* synthetic */ int[] $SwitchMap$com$flextv$livestore$apps$FocusStatus;

        static {
            int[] iArr = new int[FocusStatus.values().length];
            $SwitchMap$com$flextv$livestore$apps$FocusStatus = iArr;
            try {
                iArr[FocusStatus.first.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$flextv$livestore$apps$FocusStatus[FocusStatus.second.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$flextv$livestore$apps$FocusStatus[FocusStatus.third.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public class PlayerEventListener implements Player.Listener {
        private PlayerEventListener() {
        }

        public final /* synthetic */ void onAudioAttributesChanged(AudioAttributes audioAttributes) {
        }

        public final /* synthetic */ void onAudioSessionIdChanged(int i) {
        }

        public final /* synthetic */ void onAvailableCommandsChanged(Player.Commands commands) {
        }

        public final /* synthetic */ void onCues(CueGroup cueGroup) {
        }

        public final /* synthetic */ void onCues(List list) {
        }

        public final /* synthetic */ void onDeviceInfoChanged(DeviceInfo deviceInfo) {
        }

        public final /* synthetic */ void onDeviceVolumeChanged(int i, boolean z) {
        }

        public final /* synthetic */ void onEvents(Player player, Player.Events events) {
        }

        public final /* synthetic */ void onIsLoadingChanged(boolean z) {
        }

        public final /* synthetic */ void onIsPlayingChanged(boolean z) {
        }

        public final /* synthetic */ void onLoadingChanged(boolean z) {
        }

        public final /* synthetic */ void onMaxSeekToPreviousPositionChanged(long j) {
        }

        public final /* synthetic */ void onMediaItemTransition(MediaItem mediaItem, int i) {
        }

        public final /* synthetic */ void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
        }

        public final /* synthetic */ void onMetadata(Metadata metadata) {
        }

        public final /* synthetic */ void onPlayWhenReadyChanged(boolean z, int i) {
        }

        public final /* synthetic */ void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }

        public void onPlaybackStateChanged(int i) {
            if (i == 4) {
                LiveActivity.this.releaseMediaPlayer();
                LiveActivity liveActivity = LiveActivity.this;
                liveActivity.playVideo(liveActivity.content_url);
            } else if (i == 3) {
                LiveActivity.this.error_count = 0;
            }
        }

        public final /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
        }

        public void onPlayerError(PlaybackException playbackException) {
            if (playbackException.errorCode == 1002) {
                LiveActivity.this.releaseMediaPlayer();
                LiveActivity liveActivity = LiveActivity.this;
                liveActivity.playVideo(liveActivity.content_url);
                return;
            }
            LiveActivity liveActivity2 = LiveActivity.this;
            int i = liveActivity2.error_count;
            if (i > 3) {
                liveActivity2.releaseMediaPlayer();
                LiveActivity.this.image_def.setVisibility(0);
            } else {
                liveActivity2.error_count = i + 1;
                liveActivity2.releaseMediaPlayer();
                LiveActivity liveActivity3 = LiveActivity.this;
                liveActivity3.playVideo(liveActivity3.content_url);
            }
        }

        public final /* synthetic */ void onPlayerErrorChanged(PlaybackException playbackException) {
        }

        public final /* synthetic */ void onPlayerStateChanged(boolean z, int i) {
        }

        public final /* synthetic */ void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
        }

        public final /* synthetic */ void onPositionDiscontinuity(int i) {
        }

        public final /* synthetic */ void onPositionDiscontinuity(Player.PositionInfo positionInfo, Player.PositionInfo positionInfo2, int i) {
        }

        public final /* synthetic */ void onRenderedFirstFrame() {
        }

        public final /* synthetic */ void onRepeatModeChanged(int i) {
        }

        public final /* synthetic */ void onSeekBackIncrementChanged(long j) {
        }

        public final /* synthetic */ void onSeekForwardIncrementChanged(long j) {
        }

        public final /* synthetic */ void onSeekProcessed() {
        }

        public final /* synthetic */ void onShuffleModeEnabledChanged(boolean z) {
        }

        public final /* synthetic */ void onSkipSilenceEnabledChanged(boolean z) {
        }

        public final /* synthetic */ void onSurfaceSizeChanged(int i, int i2) {
        }

        public final /* synthetic */ void onTimelineChanged(Timeline timeline, int i) {
        }

        public final /* synthetic */ void onTrackSelectionParametersChanged(TrackSelectionParameters trackSelectionParameters) {
        }

        public final /* synthetic */ void onTracksChanged(Tracks tracks) {
        }

        public final /* synthetic */ void onVideoSizeChanged(VideoSize videoSize) {
        }

        public final /* synthetic */ void onVolumeChanged(float f) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeChannelInfo(int i) {
        this.txt_group.setText(this.categoryName);
        this.txt_channel_name.setText(((EPGChannel) this.epgChannels.get(i)).getNum() + " " + ((EPGChannel) this.epgChannels.get(i)).getName());
        String stream_icon = ((EPGChannel) this.epgChannels.get(i)).getStream_icon();
        if (stream_icon == null || stream_icon.isEmpty()) {
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.home_logo)).error(R.drawable.home_logo).into(this.channel_image);
        } else {
            Glide.with((FragmentActivity) this).load(stream_icon).error(R.drawable.home_logo).into(this.channel_image);
        }
    }

    private void controlFav(EPGChannel ePGChannel, int i) {
        if (ePGChannel == null || Constants.xxx_live_categories.contains(this.categoryModels.get(this.category_pos).getId())) {
            return;
        }
        RealmController.with().addToFavChannels(ePGChannel.getName(), !ePGChannel.is_favorite(), new LiveActivity$$ExternalSyntheticLambda0(this, i, 0));
    }

    private MediaSource.Factory createMediaSourceFactory() {
        DefaultDrmSessionManagerProvider defaultDrmSessionManagerProvider = new DefaultDrmSessionManagerProvider();
        defaultDrmSessionManagerProvider.setDrmHttpDataSourceFactory(DemoUtil.getHttpDataSourceFactory(this));
        return new DefaultMediaSourceFactory(this).setDataSourceFactory(this.dataSourceFactory).setDrmSessionManagerProvider((DrmSessionManagerProvider) defaultDrmSessionManagerProvider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void epgTimer(String str) {
        this.epgTime = 1;
        LiveActivity$$ExternalSyntheticLambda3 liveActivity$$ExternalSyntheticLambda3 = new LiveActivity$$ExternalSyntheticLambda3(this, str, 0);
        this.epgTicker = liveActivity$$ExternalSyntheticLambda3;
        liveActivity$$ExternalSyntheticLambda3.run();
    }

    private void findAndShowChannel() {
        if (this.move_pos <= RealmController.with().getAllEpgChannelSize() - 1) {
            this.txt_num.setText(this.key);
            this.handler.removeCallbacks(this.moveTicker);
            moveTimer();
        } else {
            this.txt_num.setText("");
            this.key = "";
            this.move_pos = 0;
            this.handler.removeCallbacks(this.moveTicker);
        }
    }

    private void findChannelInfo() {
        for (int i = 0; i < this.epgChannels.size(); i++) {
            if (Integer.parseInt(((EPGChannel) this.epgChannels.get(i)).getNum()) == this.move_pos) {
                this.keySelChannel = (EPGChannel) this.epgChannels.get(i);
                this.channel_pos = i;
            }
        }
        if (this.keySelChannel == null) {
            this.key = "";
            this.txt_num.setText("");
            this.txt_num.setVisibility(8);
            Toast.makeText(this, this.wordModels.getNo_channels(), 0).show();
            return;
        }
        this.key = "";
        this.txt_num.setText("");
        this.txt_num.setVisibility(8);
        if (isAdultChannel(this.keySelChannel.getCategory_id(), this.keySelChannel.getCategory_name())) {
            showChannelLockDlgFragment(this.keySelChannel, this.channel_pos, 2);
            return;
        }
        playSelectedChannel(this.keySelChannel);
        this.recycler_channel.setSelectedPosition(this.channel_pos);
        this.handler.removeCallbacks(this.epgTicker);
        getShortEpg(this.stream_id);
        changeChannelInfo(this.channel_pos);
        if (this.ly_control.getVisibility() == 8 && this.is_full) {
            this.ly_control.setVisibility(0);
        }
        this.handler.removeCallbacks(this.hideInfoTicker);
        mInfoHideTimer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getShortEpg(String str) {
        try {
            RetroClass.getAPIService(this.preferenceHelper.getSharedPreferenceServerUrl(), this.preferenceHelper.getSharedPreferenceISM3U()).get_short_epg(this.preferenceHelper.getSharedPreferenceUsername(), this.preferenceHelper.getSharedPreferencePassword(), str).enqueue(new Callback<CatchUpEpgResponse>() { // from class: com.ouropro.player.activities.LiveActivity.4
                public void onFailure(@NonNull Call<CatchUpEpgResponse> call, @NonNull Throwable th) {
                    LiveActivity.this.loadXmlTvEpg(str);
                }

                public void onResponse(@NonNull Call<CatchUpEpgResponse> call, @NonNull Response<CatchUpEpgResponse> response) {
                    if (response.body() == null || response.body().getEpg_listings() == null || response.body().getEpg_listings().size() <= 0) {
                        LiveActivity.this.loadXmlTvEpg(str);
                        return;
                    }
                    LiveActivity.this.showEpgInfo(response.body().getEpg_listings());
                    LiveActivity.this.epgEventList = response.body().getEpg_listings();
                }
            });
                } catch (Exception unused) {
            loadXmlTvEpg(str);
        }
    }

    private void loadXmlTvEpg(String streamId) {
        XmlTvEpgLoader.load(
                this.preferenceHelper.getSharedPreferenceServerUrl(),
                this.preferenceHelper.getSharedPreferenceISM3U(),
                this.preferenceHelper.getSharedPreferenceUsername(),
                this.preferenceHelper.getSharedPreferencePassword(),
                this.preferenceHelper.getSharedPreferenceM3UEpgUrl(),
                this.selectedChannel == null ? "" : this.selectedChannel.getId() + "|" + this.selectedChannel.getStream_id(),
                this.selectedChannel == null ? this.channel_name : this.selectedChannel.getName(),
                new XmlTvEpgLoader.Listener() {
                    @Override
                    public void onLoaded(List<CatchUpEpg> programs) {
                        runOnUiThread(() -> {
                            showEpgInfo(programs);
                            epgEventList = programs;
                        });
                    }

                    @Override
                    public void onError(Throwable error) {
                        runOnUiThread(() -> showEpgInfo(null));
                    }
                });
    }

    private void goToCatchupActivity() {
        if (this.selectedChannel != null) {
            releaseMediaPlayer();
            LTVApp.channelName = this.selectedChannel.getName();
            Intent intent = new Intent(this, (Class<?>) CatchUpActivity.class);
            intent.putExtra("catchup_stream_id", this.selectedChannel.getStream_id());
            intent.putExtra("catchup_channel_id", this.selectedChannel.getId());
            intent.putExtra("catchup_channel_name", this.selectedChannel.getName());
            this.someActivityResultLauncher.launch(intent);
        }
    }

    private void goToSearchActivity() {
        releaseMediaPlayer();
        Intent intent = new Intent(this, (Class<?>) SearchActivity.class);
        intent.putExtra("is_live", true);
        this.someActivityResultLauncher.launch(intent);
    }

    private void goToSeriesActivity() {
        releaseMediaPlayer();
        startActivity(new Intent(this, (Class<?>) SeriesActivity.class));
        if (this.categoryModels.get(this.category_pos).getId().equalsIgnoreCase(Constants.resume_id) && this.selectedChannel != null) {
            saveCategoryAndChannelPosition();
        }
        finish();
    }

    private void goToSettingActivity() {
        releaseMediaPlayer();
        this.someActivityResultLauncher.launch(new Intent(this, (Class<?>) SettingActivity.class));
    }

    private void goToVodActivity() {
        releaseMediaPlayer();
        startActivity(new Intent(this, (Class<?>) MovieActivity.class));
        if (this.categoryModels.get(this.category_pos).getId().equalsIgnoreCase(Constants.resume_id) && this.selectedChannel != null) {
            saveCategoryAndChannelPosition();
        }
        finish();
    }

    private void initView() {
        this.main_lay = (ConstraintLayout) findViewById(R.id.fullContainer);
        setupVoiceButton();
        StyledPlayerView styledPlayerView = (StyledPlayerView) findViewById(R.id.player_view);
        this.playerView = styledPlayerView;
        styledPlayerView.setResizeMode(3);
        this.playerView.getSubtitleView().setApplyEmbeddedStyles(false);
        CaptionStyleCompat captionStyleCompat = new CaptionStyleCompat(Color.parseColor(this.preferenceHelper.getSharedPreferenceSubtitleColor()), Color.parseColor(this.preferenceHelper.getSharedPreferenceSubtitleBgColor()), 0, 0, 0, null);
        this.playerView.getSubtitleView().setFixedTextSize(3, this.preferenceHelper.getSharedPreferenceSubtitleSize());
        this.playerView.getSubtitleView().setStyle(captionStyleCompat);
        this.txt_num = (TextView) findViewById(R.id.txt_num);
        this.txt_home = (TextView) findViewById(R.id.txt_home);
        this.txt_live = (TextView) findViewById(R.id.txt_live);
        this.txt_movie = (TextView) findViewById(R.id.txt_movie);
        this.txt_series = (TextView) findViewById(R.id.txt_series);
        this.txt_name = (TextView) findViewById(R.id.txt_name);
        this.epg_summary_visible = findViewById(R.id.epg_summary_visible);
        this.txt_epg_now_visible = (TextView) findViewById(R.id.txt_epg_now_visible);
        this.txt_epg_next_visible = (TextView) findViewById(R.id.txt_epg_next_visible);
        this.et_search = (EditText) findViewById(R.id.et_search);
        this.recycler_category = (LiveVerticalGridView) findViewById(R.id.recycler_category);
        this.recycler_channel = (LiveVerticalGridView) findViewById(R.id.recycler_channel);
        this.ly_surface = (ConstraintLayout) findViewById(R.id.ly_surface);
        this.recycler_epg = (RecyclerView) findViewById(R.id.recycler_epg);
        this.btn_fav = (Button) findViewById(R.id.btn_fav);
        this.btn_catch_up = (Button) findViewById(R.id.btn_catch_up);
        this.btn_search = (Button) findViewById(R.id.btn_search);
        this.txt_left = (TextView) findViewById(R.id.txt_left);
        this.txt_right = (TextView) findViewById(R.id.txt_right);
        this.txt_audio = (TextView) findViewById(R.id.txt_audio);
        this.txt_epg = (TextView) findViewById(R.id.txt_epg);
        this.txt_vod = (TextView) findViewById(R.id.txt_vod);
        this.txt_search = (TextView) findViewById(R.id.txt_search);
        this.txt_bottom_series = (TextView) findViewById(R.id.txt_bottom_series);
        this.txt_fav = (TextView) findViewById(R.id.txt_fav);
        this.txt_subtitle = (TextView) findViewById(R.id.txt_subtitle);
        this.txt_left.setText(this.wordModels.getPrevious_channel());
        this.txt_right.setText(this.wordModels.getNext_channel());
        this.txt_home.setText(this.wordModels.getHome());
        this.txt_live.setText(this.wordModels.getLive_tv());
        this.txt_movie.setText(this.wordModels.getMovies());
        this.txt_series.setText(this.wordModels.getSeries());
        this.txt_audio.setText(this.wordModels.getAudio_track());
        this.btn_catch_up.setText(this.wordModels.getCatch_up());
        this.btn_search.setText(this.wordModels.getSearch());
        this.btn_fav.setText(this.wordModels.getAdd_to_favorite());
        this.txt_epg.setText(this.wordModels.getEpg());
        this.txt_vod.setText(this.wordModels.getMovies());
        this.txt_search.setText(this.wordModels.getSearch());
        this.txt_bottom_series.setText(this.wordModels.getSeries());
        this.txt_fav.setText(this.wordModels.getFavorite());
        this.txt_subtitle.setText(this.wordModels.getSubtitle());
        this.recycler_category.setNumColumns(1);
        this.recycler_category.setLoop(false);
        this.recycler_category.setPreserveFocusAfterLayout(true);
        final View[] viewArr = {null};
        this.recycler_category.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.LiveActivity.5
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
        this.recycler_channel.setNumColumns(1);
        this.recycler_channel.setLoop(false);
        this.recycler_channel.setPreserveFocusAfterLayout(true);
        final View[] viewArr2 = {null};
        this.recycler_channel.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() { // from class: com.ouropro.player.activities.LiveActivity.6
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
        this.ly_control = (ConstraintLayout) findViewById(R.id.ly_control);
        this.ly_buttons = (ConstraintLayout) findViewById(R.id.ly_buttons);
        this.ly_actions = (ConstraintLayout) findViewById(R.id.ly_actions);
        this.image_epg = (ImageButton) findViewById(R.id.image_epg);
        this.image_vod = (ImageButton) findViewById(R.id.image_vod);
        this.image_series = (ImageButton) findViewById(R.id.image_series);
        this.image_subtitle = (ImageButton) findViewById(R.id.image_subtitle);
        this.image_audio = (ImageButton) findViewById(R.id.image_audio);
        this.image_fav = (ImageButton) findViewById(R.id.image_fav);
        this.image_search = (ImageButton) findViewById(R.id.image_search);
        this.seekBar = (SeekBar) findViewById(R.id.seekBar);
        this.txt_current_time = (TextView) findViewById(R.id.txt_current_time);
        if (this.epg_summary_visible != null) {
            this.epg_summary_visible.setVisibility(View.GONE);
        }
        this.txt_epg_now_visible.setText("Agora: carregando EPG...");
        this.txt_epg_next_visible.setText("Próximo: aguardando programação...");
        updateChannelEpgText("carregando EPG...", "aguardando programação...");
        this.txt_current_program = (TextView) findViewById(R.id.txt_current_program);
        this.txt_next_time = (TextView) findViewById(R.id.txt_next_time);
        this.txt_next_program = (TextView) findViewById(R.id.txt_next_program);
        this.txt_group = (TextView) findViewById(R.id.txt_group);
        this.txt_channel_name = (TextView) findViewById(R.id.txt_channel_name);
        this.txt_resolution = (TextView) findViewById(R.id.txt_resolution);
        this.channel_image = (ImageView) findViewById(R.id.channel_image);
        this.image_def = (ImageView) findViewById(R.id.image_def);
        this.image_epg.setOnFocusChangeListener(this);
        this.image_vod.setOnFocusChangeListener(this);
        this.image_series.setOnFocusChangeListener(this);
        this.image_fav.setOnFocusChangeListener(this);
        this.image_search.setOnFocusChangeListener(this);
        this.image_subtitle.setOnFocusChangeListener(this);
        this.image_audio.setOnFocusChangeListener(this);
        this.image_epg.setOnClickListener(this);
        this.image_vod.setOnClickListener(this);
        this.image_series.setOnClickListener(this);
        this.image_fav.setOnClickListener(this);
        this.image_search.setOnClickListener(this);
        this.image_subtitle.setOnClickListener(this);
        this.image_audio.setOnClickListener(this);
        this.txt_live.setOnClickListener(this);
        this.txt_home.setOnClickListener(this);
        this.txt_series.setOnClickListener(this);
        this.txt_movie.setOnClickListener(this);
        this.btn_fav.setOnClickListener(this);
        this.btn_catch_up.setOnClickListener(view -> goToCatchupActivity());
        this.image_epg.setOnClickListener(view -> goToCatchupActivity());
        this.image_subtitle.setOnClickListener(view -> showSubtitleTrack());
        this.btn_search.setOnClickListener(this);
        this.et_search.addTextChangedListener(new TextWatcher() { // from class: com.ouropro.player.activities.LiveActivity.7
            public void afterTextChanged(Editable editable) {
                LiveActivity.this.searchChannelsInCategory(editable.toString());
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
    }

    private boolean isAdultChannel(String str, String str2) {
        String value = ((str == null ? "" : str) + " " + (str2 == null ? "" : str2)).toLowerCase(java.util.Locale.US);
        return value.contains("adult") || value.contains("xxx") || value.contains("porn") || value.contains("18+") || value.contains("18 ") || value.contains("sex") || value.contains("sexy") || value.contains("erotic") || value.contains("erotico") || value.contains("playboy") || value.contains("venus") || value.contains("hot ") || value.contains("redtube") || Constants.xxx_live_categories.contains(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$controlFav$4(int i) {
        if (this.categoryModels.get(this.category_pos).getId().equalsIgnoreCase(Constants.fav_id)) {
            RealmResults<EPGChannel> liveChannelsByCategory = RealmController.with().getLiveChannelsByCategory(this.categoryModels.get(this.category_pos), "", this.preferenceHelper.getSharedPreferenceISM3U(), this.preferenceHelper.getSharedPreferenceLiveOrder());
            this.epgChannels = liveChannelsByCategory;
            this.channelAdapter.updateData(liveChannelsByCategory, 0);
        } else {
            this.channelAdapter.notifyItemChanged(i);
        }
        this.preferenceHelper.setSharedPreferenceLiveFavChannels(RealmController.with().getFavChannelNames());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$epgTimer$3(String str) {
        if (this.epgTime == 0) {
            getShortEpg(str);
        } else {
            runNextEpgTicker();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$mInfoHideTimer$5() {
        if (this.hide_time == 0 && this.ly_control.getVisibility() == 0) {
            this.ly_control.setVisibility(8);
            this.focusStatus = FocusStatus.second;
            showAndHideActionsButtons(false);
        }
        moveNexHideTicker();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$moveTimer$6() {
        this.handler.removeCallbacks(this.moveTicker);
        if (this.move_time == 0) {
            findChannelInfo();
        }
        moveNextTicker();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(ActivityResult activityResult) {
        Intent data;
        if (activityResult.getResultCode() != -1 || (data = activityResult.getData()) == null) {
            return;
        }
        if (data.getStringExtra("is_changed").equalsIgnoreCase("from_search")) {
            this.is_full = true;
            setFull();
            this.category_pos = this.preferenceHelper.getSharedPreferenceCategoryPos();
            this.channel_pos = this.preferenceHelper.getSharedPreferenceChannelPos();
            RealmResults<EPGChannel> liveChannelsByCategory = RealmController.with().getLiveChannelsByCategory(this.categoryModels.get(this.category_pos), "", this.preferenceHelper.getSharedPreferenceISM3U(), this.preferenceHelper.getSharedPreferenceLiveOrder());
            this.epgChannels = liveChannelsByCategory;
            if (this.channel_pos > liveChannelsByCategory.size() - 1) {
                this.channel_pos = 0;
            }
            this.channelAdapter.updateData(this.epgChannels, this.channel_pos);
            this.selectedChannel = (EPGChannel) this.epgChannels.get(this.channel_pos);
            this.recycler_category.setSelectedPosition(this.category_pos);
            this.recycler_category.scrollToPosition(this.category_pos);
            this.categoryAdapter.setCategoryPosition(this.category_pos);
            this.handler.removeCallbacks(this.epgTicker);
            epgTimer(this.selectedChannel.getStream_id());
            String name = this.selectedChannel.getName();
            this.channel_name = name;
            updateChannelEpgText("carregando EPG...", "aguardando programação...");
            showFavImageIcon(this.selectedChannel.is_favorite());
            changeChannelInfo(this.channel_pos);
        }
        playSelectedChannel(this.selectedChannel);
        setFocusButtons(false);
        this.recycler_channel.requestFocus();
        this.recycler_channel.setSelectedPosition(this.channel_pos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreate$0(CategoryModel categoryModel, Integer num, Boolean bool) {
        this.pre_category_pos = num.intValue();
        if (!bool.booleanValue() || this.category_pos == num.intValue()) {
            return null;
        }
        this.et_search.setText("");
        if (Constants.xxx_live_categories.contains(categoryModel.getId())) {
            showLockDlgFragment(num.intValue());
            return null;
        }
        this.category_pos = num.intValue();
        this.categoryName = (this.category_pos + 1) + " • Group : " + this.categoryModels.get(this.category_pos).getName();
        RealmResults<EPGChannel> liveChannelsByCategory = RealmController.with().getLiveChannelsByCategory(categoryModel, "", this.preferenceHelper.getSharedPreferenceISM3U(), this.preferenceHelper.getSharedPreferenceLiveOrder());
        this.epgChannels = liveChannelsByCategory;
        this.channelAdapter.updateData(liveChannelsByCategory, -1);
        this.recycler_channel.setSelectedPosition(0);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Unit lambda$onCreate$1(EPGChannel ePGChannel, Integer num, Boolean bool, Boolean bool2) {
        this.pre_channel_pos = num.intValue();
        if (bool.booleanValue()) {
            if (!this.is_full) {
                if (this.stream_id.equalsIgnoreCase(ePGChannel.getStream_id())) {
                    if (this.categoryModels.get(this.category_pos).getId().equalsIgnoreCase(Constants.resume_id)) {
                        this.channel_pos = 0;
                    }
                    this.is_full = true;
                    setFull();
                } else if (isAdultChannel(ePGChannel.getCategory_id(), ePGChannel.getCategory_name())) {
                    showChannelLockDlgFragment(ePGChannel, num.intValue(), 0);
                } else {
                    this.channel_pos = num.intValue();
                    playSelectedChannel(ePGChannel);
                }
            }
        } else if (bool2.booleanValue()) {
            controlFav(ePGChannel, num.intValue());
            showFavImageIcon(ePGChannel.is_favorite());
        } else if (!this.is_full) {
            this.handler.removeCallbacks(this.epgTicker);
            epgTimer(ePGChannel.getStream_id());
            String name = ePGChannel.getName();
            this.channel_name = name;
            updateChannelEpgText("carregando EPG...", "aguardando programação...");
            showFavImageIcon(ePGChannel.is_favorite());
            changeChannelInfo(this.pre_channel_pos);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playSelectedChannel$2() {
        if (!this.categoryModels.get(this.category_pos).getId().equalsIgnoreCase(Constants.resume_id)) {
            this.categoryAdapter.notifyItemChanged(0);
            return;
        }
        this.channelAdapter.notifyDataSetChanged();
        this.channelAdapter.setSelectedPosition(0);
        this.recycler_channel.scrollToPosition(0);
        this.recycler_channel.setSelectedPosition(0);
        this.channel_pos = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mInfoHideTimer() {
        this.hide_time = 10;
        LiveActivity$$ExternalSyntheticLambda2 liveActivity$$ExternalSyntheticLambda2 = new LiveActivity$$ExternalSyntheticLambda2(this, 1);
        this.hideInfoTicker = liveActivity$$ExternalSyntheticLambda2;
        liveActivity$$ExternalSyntheticLambda2.run();
    }

    private void moveNexHideTicker() {
        this.hide_time--;
        this.handler.postAtTime(this.hideInfoTicker, SystemClock.uptimeMillis() + 1000);
    }

    private void moveNextTicker() {
        this.move_time--;
        this.handler.postAtTime(this.moveTicker, SystemClock.uptimeMillis() + 1000);
    }

    private void moveTimer() {
        this.move_time = 2;
        LiveActivity$$ExternalSyntheticLambda2 liveActivity$$ExternalSyntheticLambda2 = new LiveActivity$$ExternalSyntheticLambda2(this, 0);
        this.moveTicker = liveActivity$$ExternalSyntheticLambda2;
        liveActivity$$ExternalSyntheticLambda2.run();
    }

    private void playNextChannel() {
        if (this.channel_pos < this.epgChannels.size() - 1) {
            this.channel_pos++;
        } else {
            this.channel_pos = 0;
        }
        if (isAdultChannel(((EPGChannel) this.epgChannels.get(this.channel_pos)).getCategory_id(), ((EPGChannel) this.epgChannels.get(this.channel_pos)).getCategory_name())) {
            showChannelLockDlgFragment((EPGChannel) this.epgChannels.get(this.channel_pos), this.channel_pos, 1);
            return;
        }
        playSelectedChannel((EPGChannel) this.epgChannels.get(this.channel_pos));
        this.handler.removeCallbacks(this.epgTicker);
        epgTimer(this.stream_id);
        changeChannelInfo(this.channel_pos);
        if (this.ly_control.getVisibility() == 8) {
            this.ly_control.setVisibility(0);
        }
        this.handler.removeCallbacks(this.hideInfoTicker);
        mInfoHideTimer();
        updateChannelEpgText("carregando EPG...", "aguardando programação...");
        this.recycler_channel.setSelectedPosition(this.channel_pos);
    }

    private void playPreviousChannel() {
        int i = this.channel_pos;
        if (i > 0) {
            this.channel_pos = i - 1;
        } else {
            this.channel_pos = this.epgChannels.size() - 1;
        }
        if (isAdultChannel(((EPGChannel) this.epgChannels.get(this.channel_pos)).getCategory_id(), ((EPGChannel) this.epgChannels.get(this.channel_pos)).getCategory_name())) {
            showChannelLockDlgFragment((EPGChannel) this.epgChannels.get(this.channel_pos), this.channel_pos, 1);
            return;
        }
        playSelectedChannel((EPGChannel) this.epgChannels.get(this.channel_pos));
        this.handler.removeCallbacks(this.epgTicker);
        epgTimer(this.stream_id);
        changeChannelInfo(this.channel_pos);
        updateChannelEpgText("carregando EPG...", "aguardando programação...");
        if (this.ly_control.getVisibility() == 8) {
            this.ly_control.setVisibility(0);
        }
        this.handler.removeCallbacks(this.hideInfoTicker);
        mInfoHideTimer();
        this.recycler_channel.setSelectedPosition(this.channel_pos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playSelectedChannel(EPGChannel ePGChannel) {
        if (ePGChannel != null) {
            this.preferenceHelper.setSharedPreferenceCategoryPos(this.category_pos);
            this.preferenceHelper.setSharedPreferenceChannelPos(this.channel_pos);
            this.selectedChannel = ePGChannel;
            this.stream_id = ePGChannel.getStream_id();
            this.channel_name = this.selectedChannel.getName();
            String sharedPreferenceMacAddress = this.preferenceHelper.getSharedPreferenceMacAddress();
            String str = this.channel_name;
            HeartbeatPeriodicHelper heartbeatPeriodicHelper = this.heartbeatHelper;
            if (heartbeatPeriodicHelper != null) {
                heartbeatPeriodicHelper.stop();
            }
            HeartbeatPeriodicHelper heartbeatPeriodicHelper2 = new HeartbeatPeriodicHelper();
            this.heartbeatHelper = heartbeatPeriodicHelper2;
            heartbeatPeriodicHelper2.start(sharedPreferenceMacAddress, str, "https://renciaapp.manus.space/api/v4/heartbeat.php");
            showFavImageIcon(this.selectedChannel.is_favorite());
            if (this.preferenceHelper.getSharedPreferenceISM3U()) {
                this.content_url = this.selectedChannel.getUrl();
            } else {
                this.content_url = GetSharedInfo.getLiveChannelUrl(this.preferenceHelper.getSharedPreferenceServerUrl(), this.preferenceHelper.getSharedPreferenceUsername(), this.preferenceHelper.getSharedPreferencePassword(), this.stream_id, this.preferenceHelper.getSharedPreferenceLiveStreamFormat());
            }
            if (!Constants.xxx_live_categories.contains(this.categoryModels.get(this.category_pos).getId()) && !isAdultChannel(this.selectedChannel.getCategory_id(), this.selectedChannel.getCategory_name())) {
                RealmController.with().addToRecentChannels(this.selectedChannel.getName(), new LiveActivity$$ExternalSyntheticLambda1(this));
            }
            releaseMediaPlayer();
            playVideo(this.content_url);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playVideo(String str) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        if (this.image_def.getVisibility() == 0) {
            this.image_def.setVisibility(8);
        }
        String adaptiveMimeTypeForContentType = Util.getAdaptiveMimeTypeForContentType(Util.inferContentType(Uri.parse(str), ""));
        MediaItem.Builder builder = new MediaItem.Builder();
        builder.setUri(Uri.parse(str)).setMediaMetadata(new MediaMetadata.Builder().setTitle("title").build()).setMimeType(adaptiveMimeTypeForContentType);
        MediaItem mediaItemBuild = builder.build();
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(this);
        this.trackSelector = defaultTrackSelector;
        defaultTrackSelector.setParameters(this.trackSelectionParameters);
        ExoPlayer.Builder trackSelector = new ExoPlayer.Builder(this).setMediaSourceFactory(createMediaSourceFactory()).setTrackSelector(this.trackSelector);
        setRenderersFactory(trackSelector, true);
        ExoPlayer exoPlayerBuild = trackSelector.build();
        this.player = exoPlayerBuild;
        exoPlayerBuild.setTrackSelectionParameters(this.trackSelectionParameters);
        this.player.addAnalyticsListener(new AnalyticsListener() { // from class: com.ouropro.player.activities.LiveActivity.3
            public final /* synthetic */ void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
            }

            public final /* synthetic */ void onAudioCodecError(AnalyticsListener.EventTime eventTime, Exception exc) {
            }

            public final /* synthetic */ void onAudioDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j) {
            }

            public final /* synthetic */ void onAudioDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j, long j2) {
            }

            public final /* synthetic */ void onAudioDecoderReleased(AnalyticsListener.EventTime eventTime, String str2) {
            }

            public final /* synthetic */ void onAudioDisabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onAudioEnabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format) {
            }

            public final /* synthetic */ void onAudioInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
            }

            public final /* synthetic */ void onAudioPositionAdvancing(AnalyticsListener.EventTime eventTime, long j) {
            }

            public final /* synthetic */ void onAudioSessionIdChanged(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onAudioSinkError(AnalyticsListener.EventTime eventTime, Exception exc) {
            }

            public final /* synthetic */ void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
            }

            public final /* synthetic */ void onAvailableCommandsChanged(AnalyticsListener.EventTime eventTime, Player.Commands commands) {
            }

            public final /* synthetic */ void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
            }

            public final /* synthetic */ void onCues(AnalyticsListener.EventTime eventTime, CueGroup cueGroup) {
            }

            public final /* synthetic */ void onCues(AnalyticsListener.EventTime eventTime, List list) {
            }

            public final /* synthetic */ void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int i, String str2, long j) {
            }

            public final /* synthetic */ void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int i, Format format) {
            }

            public final /* synthetic */ void onDeviceInfoChanged(AnalyticsListener.EventTime eventTime, DeviceInfo deviceInfo) {
            }

            public final /* synthetic */ void onDeviceVolumeChanged(AnalyticsListener.EventTime eventTime, int i, boolean z) {
            }

            public final /* synthetic */ void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaLoadData mediaLoadData) {
            }

            public final /* synthetic */ void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception exc) {
            }

            public final /* synthetic */ void onDrmSessionReleased(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j) {
            }

            public final /* synthetic */ void onEvents(Player player, AnalyticsListener.Events events) {
            }

            public final /* synthetic */ void onIsLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
            }

            public final /* synthetic */ void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
            }

            public final /* synthetic */ void onLoadCanceled(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            }

            public final /* synthetic */ void onLoadCompleted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            }

            public final /* synthetic */ void onLoadError(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException iOException, boolean z) {
            }

            public final /* synthetic */ void onLoadStarted(AnalyticsListener.EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            }

            public final /* synthetic */ void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
            }

            public final /* synthetic */ void onMaxSeekToPreviousPositionChanged(AnalyticsListener.EventTime eventTime, long j) {
            }

            public final /* synthetic */ void onMediaItemTransition(AnalyticsListener.EventTime eventTime, MediaItem mediaItem, int i) {
            }

            public final /* synthetic */ void onMediaMetadataChanged(AnalyticsListener.EventTime eventTime, MediaMetadata mediaMetadata) {
            }

            public final /* synthetic */ void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
            }

            public final /* synthetic */ void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
            }

            public final /* synthetic */ void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
            }

            public final /* synthetic */ void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onPlaybackSuppressionReasonChanged(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException playbackException) {
            }

            public final /* synthetic */ void onPlayerErrorChanged(AnalyticsListener.EventTime eventTime, PlaybackException playbackException) {
            }

            public final /* synthetic */ void onPlayerReleased(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
            }

            public final /* synthetic */ void onPlaylistMetadataChanged(AnalyticsListener.EventTime eventTime, MediaMetadata mediaMetadata) {
            }

            public final /* synthetic */ void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, Player.PositionInfo positionInfo, Player.PositionInfo positionInfo2, int i) {
            }

            public final /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object obj, long j) {
            }

            public final /* synthetic */ void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onSeekBackIncrementChanged(AnalyticsListener.EventTime eventTime, long j) {
            }

            public final /* synthetic */ void onSeekForwardIncrementChanged(AnalyticsListener.EventTime eventTime, long j) {
            }

            public final /* synthetic */ void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            }

            public final /* synthetic */ void onShuffleModeChanged(AnalyticsListener.EventTime eventTime, boolean z) {
            }

            public final /* synthetic */ void onSkipSilenceEnabledChanged(AnalyticsListener.EventTime eventTime, boolean z) {
            }

            public final /* synthetic */ void onSurfaceSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2) {
            }

            public final /* synthetic */ void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onTrackSelectionParametersChanged(AnalyticsListener.EventTime eventTime, TrackSelectionParameters trackSelectionParameters) {
            }

            public final /* synthetic */ void onTracksChanged(AnalyticsListener.EventTime eventTime, Tracks tracks) {
            }

            public final /* synthetic */ void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaLoadData mediaLoadData) {
            }

            public final /* synthetic */ void onVideoCodecError(AnalyticsListener.EventTime eventTime, Exception exc) {
            }

            public final /* synthetic */ void onVideoDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j) {
            }

            public final /* synthetic */ void onVideoDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j, long j2) {
            }

            public final /* synthetic */ void onVideoDecoderReleased(AnalyticsListener.EventTime eventTime, String str2) {
            }

            public final /* synthetic */ void onVideoDisabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onVideoEnabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onVideoFrameProcessingOffset(AnalyticsListener.EventTime eventTime, long j, int i) {
            }

            public final /* synthetic */ void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format) {
            }

            public final /* synthetic */ void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
            }

            public final /* synthetic */ void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2, int i3, float f) {
            }

            public void onVideoSizeChanged(@NonNull AnalyticsListener.EventTime eventTime, @NonNull VideoSize videoSize) {
                LiveActivity.this.txt_resolution.setText(videoSize.width + "x" + videoSize.height);
            }

            public final /* synthetic */ void onVolumeChanged(AnalyticsListener.EventTime eventTime, float f) {
            }
        });
        this.player.addListener(new PlayerEventListener());
        this.player.addAnalyticsListener(new EventLogger());
        this.player.setAudioAttributes(AudioAttributes.DEFAULT, true);
        this.player.setPlayWhenReady(true);
        this.playerView.setPlayer(this.player);
        this.player.setMediaItem(mediaItemBuild);
        this.player.prepare();
        this.player.play();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseMediaPlayer() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return;
        }
        exoPlayer.stop();
        this.player.release();
        this.player = null;
        this.playerView.setPlayer(null);
    }

    private void runNextEpgTicker() {
        this.epgTime--;
        this.handler.postAtTime(this.epgTicker, SystemClock.uptimeMillis() + 500);
    }

    private void saveCategoryAndChannelPosition() {
        int i;
        int i2 = 0;
        if (this.preferenceHelper.getSharedPreferenceISM3U()) {
            String category_name = this.selectedChannel.getCategory_name();
            i = 0;
            while (true) {
                if (i >= this.categoryModels.size()) {
                    i = 0;
                    break;
                } else if (this.categoryModels.get(i).getName().equalsIgnoreCase(category_name)) {
                    break;
                } else {
                    i++;
                }
            }
        } else {
            String category_id = this.selectedChannel.getCategory_id();
            i = 0;
            for (int i3 = 0; i3 < this.categoryModels.size(); i3++) {
                if (this.categoryModels.get(i3).getId().equalsIgnoreCase(category_id)) {
                    i = i3;
                }
            }
        }
        RealmResults<EPGChannel> liveChannelsByCategory = RealmController.with().getLiveChannelsByCategory(this.categoryModels.get(i), "", this.preferenceHelper.getSharedPreferenceISM3U(), this.preferenceHelper.getSharedPreferenceLiveOrder());
        for (int i4 = 0; i4 < liveChannelsByCategory.size(); i4++) {
            if (this.selectedChannel.getName().equalsIgnoreCase(((EPGChannel) liveChannelsByCategory.get(i4)).getName())) {
                i2 = i4;
                break;
            }
        }
        this.preferenceHelper.setSharedPreferenceCategoryPos(i);
        this.preferenceHelper.setSharedPreferenceChannelPos(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchChannelsInCategory(String str) {
        RealmResults<EPGChannel> liveChannelsByCategory = RealmController.with().getLiveChannelsByCategory(this.categoryModels.get(this.category_pos), str, this.preferenceHelper.getSharedPreferenceISM3U(), this.preferenceHelper.getSharedPreferenceLiveOrder());
        this.epgChannels = liveChannelsByCategory;
        this.channelAdapter.updateData(liveChannelsByCategory, -1);
        this.recycler_channel.setSelectedPosition(0);
        this.recycler_channel.scrollToPosition(0);
    }

    private void ensureVisibleEpgPanel() {
        if (this.main_lay == null || this.epgAdapter == null) {
            return;
        }
        if (this.visibleEpgPanel == null) {
            RecyclerView panel = new RecyclerView(this);
            panel.setId(View.generateViewId());
            panel.setFocusable(true);
            panel.setFocusableInTouchMode(true);
            panel.setClickable(true);
            panel.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            panel.setNextFocusUpId(R.id.recycler_channel);
            panel.setNextFocusLeftId(R.id.recycler_channel);
            panel.setBackgroundColor(Color.TRANSPARENT);
            panel.setLayoutManager(new LinearLayoutManager(this));
            panel.setAdapter(this.epgAdapter);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0, 0);
            params.startToStart = R.id.vertical_line2;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topToBottom = R.id.txt_name;
            params.bottomToTop = R.id.btn_catch_up;
            int margin = getResources().getDimensionPixelSize(R.dimen._5sdp);
            params.setMargins(margin, margin, margin, margin);
            this.main_lay.addView(panel, params);
            this.visibleEpgPanel = panel;
        }
        this.visibleEpgPanel.setVisibility(View.VISIBLE);
        if (this.recycler_channel != null) {
            this.recycler_channel.setNextFocusDownId(this.visibleEpgPanel.getId());
        }
        this.visibleEpgPanel.setNextFocusUpId(this.recycler_channel == null ? View.NO_ID : this.recycler_channel.getId());
        this.visibleEpgPanel.bringToFront();
        this.visibleEpgPanel.requestLayout();
        this.visibleEpgPanel.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus && this.epgAdapter != null && this.epgAdapter.getItemCount() > 0) {
                view.post(() -> {
                    RecyclerView.ViewHolder holder = this.visibleEpgPanel.findViewHolderForAdapterPosition(0);
                    if (holder != null && holder.itemView.findViewById(R.id.epg_bell) != null) {
                        holder.itemView.findViewById(R.id.epg_bell).requestFocus();
                    }
                });
            }
        });
    }

    private void scheduleTvReminder(List<CatchUpEpg> list) {
        if (this.tvReminderHandler == null) {
            return;
        }
        String currentStream = this.selectedChannel == null ? this.stream_id : this.selectedChannel.getStream_id();
        if (currentStream == null || currentStream.trim().isEmpty() || list == null) {
            clearTvReminderSchedule();
            return;
        }
        long now = System.currentTimeMillis();
        CatchUpEpg nextReminder = null;
        for (CatchUpEpg program : list) {
            if (program == null || !EpgReminderStore.isScheduled(this, currentStream, program)) {
                continue;
            }
            long startMillis = program.getStart_timestamp() * 1000L;
            long stopMillis = program.getStop_timestamp() * 1000L;
            if (stopMillis <= now || startMillis <= 0L) {
                continue;
            }
            if (nextReminder == null || startMillis < nextReminder.getStart_timestamp() * 1000L) {
                nextReminder = program;
            }
        }
        if (nextReminder == null) {
            clearTvReminderSchedule();
            return;
        }
        String reminderKey = currentStream + "|" + nextReminder.getStart_timestamp();
        if (reminderKey.equals(this.scheduledTvReminderKey) && this.tvReminderRunnable != null) {
            return;
        }
        clearTvReminderSchedule();
        this.scheduledTvReminderKey = reminderKey;
        long delay = Math.max(0L, nextReminder.getStart_timestamp() * 1000L - now - 10000L);
        CatchUpEpg reminderProgram = nextReminder;
        this.tvReminderRunnable = () -> showTvReminder(reminderProgram, currentStream);
        this.tvReminderHandler.postDelayed(this.tvReminderRunnable, delay);
    }

    private void clearTvReminderSchedule() {
        if (this.tvReminderHandler != null && this.tvReminderRunnable != null) {
            this.tvReminderHandler.removeCallbacks(this.tvReminderRunnable);
        }
        this.tvReminderRunnable = null;
        this.scheduledTvReminderKey = "";
        if (this.tvReminderCountdown != null) {
            this.tvReminderCountdown.cancel();
            this.tvReminderCountdown = null;
        }
    }

    private void showTvReminder(CatchUpEpg program, String streamId) {
        if (isFinishing() || program == null || !EpgReminderStore.isScheduled(this, streamId, program)) {
            return;
        }
        TextView message = new TextView(this);
        message.setTextColor(Color.WHITE);
        message.setTextSize(18.0f);
        message.setPadding(32, 24, 32, 8);
        message.setText("O programa começa em 10 segundos");
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Lembrete EPG").setView(message).setPositiveButton("Ir agora", (d, which) -> EpgReminderStore.setScheduled(this, streamId, program, false)).setNegativeButton("Descartar", (d, which) -> EpgReminderStore.setScheduled(this, streamId, program, false)).create();
        dialog.setOnDismissListener(d -> {
            EpgReminderStore.setScheduled(this, streamId, program, false);
            clearTvReminderSchedule();
        });
        dialog.setOnShowListener(d -> {
            this.tvReminderCountdown = new CountDownTimer(10000L, 1000L) {
                @Override
                public void onTick(long millisUntilFinished) {
                    message.setText("O programa começa em " + Math.max(1, (int) Math.ceil(millisUntilFinished / 1000.0d)) + " segundos");
                }

                @Override
                public void onFinish() {
                    message.setText("Começando agora");
                }
            }.start();
        });
        dialog.show();
    }

    private void updateChannelEpgText(String nowText, String nextText) {
        if (this.txt_name == null) {
            return;
        }
        String channelTitle = this.channel_name == null || this.channel_name.trim().isEmpty() ? "Canal" : this.channel_name;
        this.txt_name.setMaxLines(3);
        this.txt_name.setEllipsize(null);
        this.txt_name.setTextColor(Color.WHITE);
        this.epgNowDisplay = nowText == null ? "carregando EPG..." : nowText;
        this.epgNextDisplay = nextText == null ? "aguardando programação..." : nextText;
        this.txt_name.setText(channelTitle);
    }

    private void setCurrentEpgEvent(List<CatchUpEpg> list) {
        if (this.epg_summary_visible != null) {
            this.epg_summary_visible.setVisibility(View.GONE);
        }
        if (list == null || list.size() <= 0) {
            this.txt_current_time.setText(this.wordModels.getNo_information());
            this.txt_current_program.setText("");
            this.seekBar.setProgress(0);
            this.txt_next_time.setText("");
            this.txt_next_program.setText(this.wordModels.getNo_information());
            if (this.txt_epg_now_visible != null) this.txt_epg_now_visible.setText("Agora: EPG não disponível");
            if (this.txt_epg_next_visible != null) this.txt_epg_next_visible.setText("Próximo: aguardando programação...");
            updateChannelEpgText("EPG não disponível", "aguardando programação...");
            return;
        }
        CatchUpEpg current = list.get(0);
        String currentTitle = Utils.decode64String(current.getTitle());
        this.txt_current_program.setText(currentTitle);
        this.txt_current_time.setText(Utils.getDateFromMillisecond(GetSharedInfo.getCurrentTimeFormat(this), Utils.getDateFromString("yyyy-MM-dd HH:mm:ss", current.getStart()).getTime() + LTVApp.SEVER_OFFSET));
        this.seekBar.setProgress(Math.max(0, Math.min(100, current.getProgress())));
        if (this.txt_epg_now_visible != null) this.txt_epg_now_visible.setText("Agora: " + currentTitle);
        String nextTitle = this.wordModels.getNo_information();
        if (list.size() > 1) {
            CatchUpEpg next = list.get(1);
            nextTitle = Utils.decode64String(next.getTitle());
            this.txt_next_program.setText(nextTitle);
            this.txt_next_time.setText(Utils.getDateFromMillisecond(GetSharedInfo.getCurrentTimeFormat(this), Utils.getDateFromString("yyyy-MM-dd HH:mm:ss", next.getStart()).getTime() + LTVApp.SEVER_OFFSET));
            if (this.txt_epg_next_visible != null) this.txt_epg_next_visible.setText("Próximo: " + nextTitle);
        } else {
            this.txt_next_time.setText("");
            this.txt_next_program.setText(nextTitle);
            if (this.txt_epg_next_visible != null) this.txt_epg_next_visible.setText("Próximo: " + nextTitle);
        }
        updateChannelEpgText(currentTitle, nextTitle);
    }

    private void setFocusButtons(boolean z) {
        this.btn_catch_up.setFocusable(z);
        this.btn_fav.setFocusable(z);
        this.btn_search.setFocusable(z);
    }

    private void setFocusTopView(boolean z) {
        this.txt_home.setFocusable(z);
        this.txt_live.setFocusable(z);
        this.txt_movie.setFocusable(z);
        this.txt_series.setFocusable(z);
        this.et_search.setFocusable(z);
    }

    private void setFull() {
        if (this.is_full) {
            this.btn_catch_up.setVisibility(8);
            this.btn_fav.setVisibility(8);
            this.btn_search.setVisibility(8);
            this.recycler_channel.setVisibility(8);
            this.recycler_category.setVisibility(8);
            setMargins(this.ly_surface, 0, 0, 0, 0);
            if (this.ly_control.getVisibility() == 8) {
                this.ly_control.setVisibility(0);
            }
            this.handler.removeCallbacks(this.hideInfoTicker);
            mInfoHideTimer();
        } else {
            Utils.dp2px(this, getResources().getDimensionPixelSize(R.dimen._3sdp));
            setMargins(this.ly_surface, 0, 0, 0, 0);
            this.btn_catch_up.setVisibility(0);
            this.btn_fav.setVisibility(0);
            this.btn_search.setVisibility(0);
            this.handler.removeCallbacks(this.hideInfoTicker);
            this.ly_control.setVisibility(8);
            showAndHideActionsButtons(false);
            this.focusStatus = FocusStatus.second;
            this.recycler_channel.setVisibility(0);
            this.recycler_category.setVisibility(0);
            this.channelAdapter.setSelectedPosition(this.channel_pos);
            this.recycler_channel.requestFocus();
        }
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.main_lay);
        if (this.is_full) {
            constraintSet.setGuidelinePercent(R.id.vertical_line1, -0.23f);
            constraintSet.setGuidelinePercent(R.id.vertical_line2, 0.0f);
            constraintSet.setGuidelinePercent(R.id.horizontal_line1, 0.0f);
            constraintSet.setGuidelinePercent(R.id.horizontal_line2, 1.0f);
        } else {
            constraintSet.setGuidelinePercent(R.id.vertical_line1, 0.23f);
            constraintSet.setGuidelinePercent(R.id.vertical_line2, 0.5f);
            constraintSet.setGuidelinePercent(R.id.horizontal_line1, 0.1f);
            constraintSet.setGuidelinePercent(R.id.horizontal_line2, 0.6f);
        }
        constraintSet.applyTo(this.main_lay);
    }

    private void setMargins(View view, int i, int i2, int i3, int i4) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(i, i2, i3, i4);
            view.requestLayout();
        }
    }

    private void setRenderersFactory(ExoPlayer.Builder builder, boolean z) {
        builder.setRenderersFactory(DemoUtil.buildRenderersFactory(this, z));
    }

    private void showAndHideActionsButtons(boolean z) {
        if (!z) {
            this.ly_buttons.setVisibility(8);
            this.ly_actions.setVisibility(0);
        } else {
            this.ly_buttons.setVisibility(0);
            this.ly_actions.setVisibility(8);
            this.image_epg.requestFocus();
        }
    }

    private void showAudioTrack() {
        DefaultTrackSelector defaultTrackSelector = this.trackSelector;
        if (defaultTrackSelector == null) {
            return;
        }
        MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = defaultTrackSelector.getCurrentMappedTrackInfo();
        if (currentMappedTrackInfo == null) {
            Toast.makeText(getApplicationContext(), this.wordModels.getNo_audio(), 1).show();
            return;
        }
        String audio_track = this.wordModels.getAudio_track();
        int rendererType = currentMappedTrackInfo.getRendererType(1);
        boolean z = rendererType == 2 || (rendererType == 1 && currentMappedTrackInfo.getTypeSupport(2) == 0);
        TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(this, audio_track, this.player, 1);
        trackSelectionDialogBuilder.setAllowAdaptiveSelections(z);
        trackSelectionDialogBuilder.setShowDisableOption(false);
        trackSelectionDialogBuilder.build().show();
    }

    private void showChannelLockDlgFragment(final EPGChannel ePGChannel, final int i, final int i2) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_channel_lock");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        LockDlgFragment lockDlgFragmentNewInstance = LockDlgFragment.newInstance(this.preferenceHelper.getSharedPreferenceParentPassword());
        this.lockDlgFragment = lockDlgFragmentNewInstance;
        lockDlgFragmentNewInstance.setOnPinEventListener(new LockDlgFragment.OnPinEventListener() { // from class: com.ouropro.player.activities.LiveActivity.2
            public void OnPinCorrect() {
                int i3 = i2;
                if (i3 == 0) {
                    LiveActivity liveActivity = LiveActivity.this;
                    liveActivity.channel_pos = i;
                    liveActivity.playSelectedChannel(ePGChannel);
                    return;
                }
                if (i3 != 1) {
                    if (i3 != 2) {
                        return;
                    }
                    LiveActivity.this.playSelectedChannel(ePGChannel);
                    LiveActivity liveActivity2 = LiveActivity.this;
                    liveActivity2.recycler_channel.setSelectedPosition(liveActivity2.channel_pos);
                    liveActivity2.recycler_channel.requestFocus();
                    LiveActivity liveActivity3 = LiveActivity.this;
                    liveActivity3.handler.removeCallbacks(liveActivity3.epgTicker);
                    LiveActivity liveActivity4 = LiveActivity.this;
                    liveActivity4.getShortEpg(liveActivity4.stream_id);
                    LiveActivity liveActivity5 = LiveActivity.this;
                    liveActivity5.changeChannelInfo(liveActivity5.channel_pos);
                    if (LiveActivity.this.ly_control.getVisibility() == 8) {
                        LiveActivity liveActivity6 = LiveActivity.this;
                        if (liveActivity6.is_full) {
                            liveActivity6.ly_control.setVisibility(0);
                        }
                    }
                    LiveActivity liveActivity7 = LiveActivity.this;
                    liveActivity7.handler.removeCallbacks(liveActivity7.hideInfoTicker);
                    LiveActivity.this.mInfoHideTimer();
                    return;
                }
                LiveActivity.this.playSelectedChannel(ePGChannel);
                LiveActivity liveActivity8 = LiveActivity.this;
                liveActivity8.handler.removeCallbacks(liveActivity8.epgTicker);
                LiveActivity liveActivity9 = LiveActivity.this;
                liveActivity9.epgTimer(liveActivity9.stream_id);
                LiveActivity liveActivity10 = LiveActivity.this;
                liveActivity10.changeChannelInfo(liveActivity10.channel_pos);
                if (LiveActivity.this.ly_control.getVisibility() == 8) {
                    LiveActivity.this.ly_control.setVisibility(0);
                }
                LiveActivity liveActivity11 = LiveActivity.this;
                liveActivity11.handler.removeCallbacks(liveActivity11.hideInfoTicker);
                LiveActivity.this.mInfoHideTimer();
                LiveActivity liveActivity12 = LiveActivity.this;
                liveActivity12.updateChannelEpgText("carregando EPG...", "aguardando programação...");
                LiveActivity liveActivity13 = LiveActivity.this;
                liveActivity13.recycler_channel.setSelectedPosition(liveActivity13.channel_pos);
                liveActivity13.recycler_channel.requestFocus();
            }

            public void OnPinIncorrect() {
                LiveActivity liveActivity = LiveActivity.this;
                Toast.makeText(liveActivity, liveActivity.wordModels.getPin_incorrect(), 0).show();
            }

            public void OnPutPinCode() {
                LiveActivity liveActivity = LiveActivity.this;
                Toast.makeText(liveActivity, liveActivity.wordModels.getPut_pin_code(), 0).show();
            }
        });
        this.lockDlgFragment.show(supportFragmentManager, "fragment_channel_lock");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showEpgInfo(List<CatchUpEpg> list) {
        ensureVisibleEpgPanel();
        if (this.recycler_epg != null) {
            this.recycler_epg.setVisibility(View.GONE);
        }
        if (list == null || list.size() == 0) {
            this.epgAdapter.setEpgList(new ArrayList());
            setCurrentEpgEvent(new ArrayList());
        } else {
            this.epgAdapter.setEpgList(list);
            setCurrentEpgEvent(list);
        }
        scheduleTvReminder(list);
    }

    private void showFavImageIcon(boolean z) {
        if (z) {
            this.btn_fav.setText(this.wordModels.getRemove_favorites());
        } else {
            this.btn_fav.setText(this.wordModels.getAdd_to_favorite());
        }
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
        lockDlgFragmentNewInstance.setOnPinEventListener(new LockDlgFragment.OnPinEventListener() { // from class: com.ouropro.player.activities.LiveActivity.1
            public void OnPinCorrect() {
                LiveActivity liveActivity = LiveActivity.this;
                liveActivity.category_pos = i;
                StringBuilder sb = new StringBuilder();
                sb.append(LiveActivity.this.category_pos + 1);
                sb.append(" • Group : ");
                LiveActivity liveActivity2 = LiveActivity.this;
                sb.append(liveActivity2.categoryModels.get(liveActivity2.category_pos).getName());
                liveActivity.categoryName = sb.toString();
                LiveActivity.this.epgChannels = RealmController.with().getLiveChannelsByCategory(LiveActivity.this.categoryModels.get(i), "", LiveActivity.this.preferenceHelper.getSharedPreferenceISM3U(), LiveActivity.this.preferenceHelper.getSharedPreferenceLiveOrder());
                LiveActivity liveActivity3 = LiveActivity.this;
                liveActivity3.channelAdapter.updateData(liveActivity3.epgChannels, -1);
                LiveActivity.this.recycler_channel.setSelectedPosition(0);
            }

            public void OnPinIncorrect() {
                LiveActivity liveActivity = LiveActivity.this;
                Toast.makeText(liveActivity, liveActivity.wordModels.getPin_incorrect(), 0).show();
            }

            public void OnPutPinCode() {
                LiveActivity liveActivity = LiveActivity.this;
                Toast.makeText(liveActivity, liveActivity.wordModels.getPut_pin_code(), 0).show();
            }
        });
        this.lockDlgFragment.show(supportFragmentManager, "fragment_lock");
    }

    private void showSubtitleTrack() {
        DefaultTrackSelector defaultTrackSelector = this.trackSelector;
        if (defaultTrackSelector == null) {
            return;
        }
        MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = defaultTrackSelector.getCurrentMappedTrackInfo();
        boolean z = true;
        if (currentMappedTrackInfo == null) {
            Toast.makeText(getApplicationContext(), this.wordModels.getNo_subtitle(), 1).show();
            return;
        }
        String subtitle = this.wordModels.getSubtitle();
        int rendererType = currentMappedTrackInfo.getRendererType(3);
        if (rendererType != 2 && (rendererType != 3 || currentMappedTrackInfo.getTypeSupport(2) != 0)) {
            z = false;
        }
        TrackSelectionDialogBuilder trackSelectionDialogBuilder = new TrackSelectionDialogBuilder(this, subtitle, this.player, 3);
        trackSelectionDialogBuilder.setAllowAdaptiveSelections(z);
        trackSelectionDialogBuilder.build().show();
    }

    /* JADX WARN: Code duplicated, block: B:162:0x0326  */
    /* JADX WARN: Code duplicated, block: B:163:0x032b  */
    /* JADX WARN: Code duplicated, block: B:165:0x032f  */
    /* JADX WARN: Code duplicated, block: B:167:0x0333  */
    /* JADX WARN: Code duplicated, block: B:169:0x034a  */
    /* JADX WARN: Code duplicated, block: B:170:0x0359  */
    /* JADX WARN: Code duplicated, block: B:171:0x0368  */
    /* JADX WARN: Code duplicated, block: B:173:0x0374  */
    /* JADX WARN: Code duplicated, block: B:175:0x0387  */
    /* JADX WARN: Code duplicated, block: B:176:0x0396  */
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int i;
        EPGChannel ePGChannel;
        EPGChannel ePGChannel2;
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 4) {
                if (this.ly_control.getVisibility() == 0) {
                    this.ly_control.setVisibility(8);
                    return true;
                }
                if (this.is_full) {
                    this.is_full = false;
                    setFull();
                    return true;
                }
                releaseMediaPlayer();
                if (this.selectedChannel != null) {
                    saveCategoryAndChannelPosition();
                }
                finish();
            } else if (keyCode != 89) {
                if (keyCode != 90) {
                    if (keyCode == 135) {
                        if (!this.is_full) {
                            ePGChannel2 = this.selectedChannel;
                            if (ePGChannel2 != null) {
                                controlFav(ePGChannel2, this.channel_pos);
                                showFavImageIcon(!this.selectedChannel.is_favorite());
                                if (!this.selectedChannel.is_favorite()) {
                                    Toast.makeText(this, this.wordModels.getChannel_added_to_fav(), 0).show();
                                } else {
                                    Toast.makeText(this, this.wordModels.getChannel_removed_from_fav(), 0).show();
                                }
                            }
                        } else {
                            ePGChannel = (EPGChannel) this.epgChannels.get(this.pre_channel_pos);
                            if (ePGChannel != null) {
                                controlFav(ePGChannel, this.channel_pos);
                                showFavImageIcon(!ePGChannel.is_favorite());
                                if (!ePGChannel.is_favorite()) {
                                    Toast.makeText(this, this.wordModels.getChannel_added_to_fav(), 0).show();
                                } else {
                                    Toast.makeText(this, this.wordModels.getChannel_removed_from_fav(), 0).show();
                                }
                            }
                        }
                    } else if (keyCode == 136) {
                        goToSearchActivity();
                    } else if (keyCode != 166) {
                        if (keyCode != 167) {
                            switch (keyCode) {
                                case 7:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    if (!this.key.isEmpty()) {
                                        String strM = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, "0");
                                        this.key = strM;
                                        this.move_pos = Integer.parseInt(strM);
                                        findAndShowChannel();
                                    }
                                    break;
                                case 8:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM2 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
                                    this.key = strM2;
                                    this.move_pos = Integer.parseInt(strM2);
                                    findAndShowChannel();
                                    break;
                                case 9:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM3 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, ExifInterface.GPS_MEASUREMENT_2D);
                                    this.key = strM3;
                                    this.move_pos = Integer.parseInt(strM3);
                                    findAndShowChannel();
                                    break;
                                case 10:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM4 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, ExifInterface.GPS_MEASUREMENT_3D);
                                    this.key = strM4;
                                    this.move_pos = Integer.parseInt(strM4);
                                    findAndShowChannel();
                                    break;
                                case 11:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM5 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, "4");
                                    this.key = strM5;
                                    this.move_pos = Integer.parseInt(strM5);
                                    findAndShowChannel();
                                    break;
                                case 12:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM6 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, "5");
                                    this.key = strM6;
                                    this.move_pos = Integer.parseInt(strM6);
                                    findAndShowChannel();
                                    break;
                                case 13:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM7 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, "6");
                                    this.key = strM7;
                                    this.move_pos = Integer.parseInt(strM7);
                                    findAndShowChannel();
                                    break;
                                case 14:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM8 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, "7");
                                    this.key = strM8;
                                    this.move_pos = Integer.parseInt(strM8);
                                    findAndShowChannel();
                                    break;
                                case 15:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM9 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, "8");
                                    this.key = strM9;
                                    this.move_pos = Integer.parseInt(strM9);
                                    findAndShowChannel();
                                    break;
                                case 16:
                                    if (this.txt_num.getVisibility() == 8) {
                                        this.txt_num.setVisibility(0);
                                    }
                                    String strM10 = Insets$$ExternalSyntheticOutline0.m(new StringBuilder(), this.key, "9");
                                    this.key = strM10;
                                    this.move_pos = Integer.parseInt(strM10);
                                    findAndShowChannel();
                                    break;
                                default:
                                    switch (keyCode) {
                                        case 19:
                                            if (this.is_full) {
                                                int i2 = AnonymousClass8.$SwitchMap$com$flextv$livestore$apps$FocusStatus[this.focusStatus.ordinal()];
                                                if (i2 == 1 || i2 == 2) {
                                                    this.ly_control.setVisibility(8);
                                                    return true;
                                                }
                                                if (i2 == 3) {
                                                    this.focusStatus = FocusStatus.second;
                                                    showAndHideActionsButtons(false);
                                                }
                                            } else {
                                                if (this.recycler_category.hasFocus() && this.pre_category_pos == 0) {
                                                    setFocusTopView(true);
                                                    this.txt_home.requestFocus();
                                                    return true;
                                                }
                                                if (this.recycler_channel.hasFocus() && this.pre_channel_pos == 0) {
                                                    setFocusTopView(true);
                                                    this.txt_series.requestFocus();
                                                    return true;
                                                }
                                                if (this.btn_catch_up.hasFocus() || this.btn_fav.hasFocus() || this.btn_search.hasFocus()) {
                                                    return true;
                                                }
                                            }
                                            break;
                                        case 20:
                                            if (!this.is_full) {
                                                if (this.txt_home.hasFocus() || this.txt_live.hasFocus() || this.txt_movie.hasFocus()) {
                                                    setFocusTopView(false);
                                                    this.recycler_category.requestFocus();
                                                    return true;
                                                }
                                                if (this.txt_series.hasFocus() || this.et_search.hasFocus()) {
                                                    setFocusTopView(false);
                                                    this.recycler_channel.requestFocus();
                                                    return true;
                                                }
                                            } else if (this.ly_control.getVisibility() == 8) {
                                                this.ly_control.setVisibility(0);
                                                this.handler.removeCallbacks(this.hideInfoTicker);
                                                mInfoHideTimer();
                                            } else {
                                                int i3 = AnonymousClass8.$SwitchMap$com$flextv$livestore$apps$FocusStatus[this.focusStatus.ordinal()];
                                                if (i3 == 1) {
                                                    this.focusStatus = FocusStatus.second;
                                                    return true;
                                                }
                                                if (i3 == 2) {
                                                    this.focusStatus = FocusStatus.third;
                                                    showAndHideActionsButtons(true);
                                                    return true;
                                                }
                                                if (i3 == 3) {
                                                    return true;
                                                }
                                            }
                                            break;
                                        case 21:
                                            if (this.is_full) {
                                                if (this.focusStatus == FocusStatus.second) {
                                                    playPreviousChannel();
                                                } else if (this.image_epg.hasFocus()) {
                                                    return true;
                                                }
                                            } else if (this.btn_catch_up.hasFocus()) {
                                                setFocusButtons(false);
                                                this.recycler_channel.requestFocus();
                                                return true;
                                            }
                                            break;
                                        case 22:
                                            if (this.is_full) {
                                                if (this.focusStatus == FocusStatus.second) {
                                                    playNextChannel();
                                                } else if (this.image_audio.hasFocus()) {
                                                    return true;
                                                }
                                            } else if (this.recycler_channel.hasFocus()) {
                                                setFocusButtons(true);
                                                this.btn_catch_up.requestFocus();
                                                return true;
                                            }
                                            break;
                                        case 23:
                                            if (this.is_full && this.ly_control.getVisibility() == 8) {
                                                this.is_full = false;
                                                setFull();
                                                return true;
                                            }
                                            break;
                                        default:
                                            switch (keyCode) {
                                                case 183:
                                                    goToVodActivity();
                                                    break;
                                                case 184:
                                                    goToSeriesActivity();
                                                    break;
                                                case 185:
                                                    if (!this.is_full) {
                                                        ePGChannel = (EPGChannel) this.epgChannels.get(this.pre_channel_pos);
                                                        if (ePGChannel != null) {
                                                            controlFav(ePGChannel, this.channel_pos);
                                                            showFavImageIcon(!ePGChannel.is_favorite());
                                                            if (!ePGChannel.is_favorite()) {
                                                                Toast.makeText(this, this.wordModels.getChannel_removed_from_fav(), 0).show();
                                                            } else {
                                                                Toast.makeText(this, this.wordModels.getChannel_added_to_fav(), 0).show();
                                                            }
                                                        }
                                                    } else {
                                                        ePGChannel2 = this.selectedChannel;
                                                        if (ePGChannel2 != null) {
                                                            controlFav(ePGChannel2, this.channel_pos);
                                                            showFavImageIcon(!this.selectedChannel.is_favorite());
                                                            if (!this.selectedChannel.is_favorite()) {
                                                                Toast.makeText(this, this.wordModels.getChannel_removed_from_fav(), 0).show();
                                                            } else {
                                                                Toast.makeText(this, this.wordModels.getChannel_added_to_fav(), 0).show();
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case 186:
                                                    goToSearchActivity();
                                                    break;
                                            }
                                            break;
                                    }
                                    break;
                            }
                        } else if (this.is_full) {
                            playPreviousChannel();
                        }
                    } else if (this.is_full) {
                        playNextChannel();
                    }
                } else if (!this.is_full) {
                    if (this.recycler_category.hasFocus()) {
                        if (this.pre_category_pos < this.categoryModels.size() - 11) {
                            int i4 = this.pre_category_pos + 10;
                            this.pre_category_pos = i4;
                            this.recycler_category.setSelectedPosition(i4);
                        }
                    } else if (this.recycler_channel.hasFocus() && this.pre_channel_pos < this.epgChannels.size() - 11) {
                        int i5 = this.pre_channel_pos + 10;
                        this.pre_channel_pos = i5;
                        this.recycler_channel.setSelectedPosition(i5);
                    }
                }
            } else if (!this.is_full) {
                if (this.recycler_category.hasFocus()) {
                    int i6 = this.pre_category_pos;
                    if (i6 > 10) {
                        int i7 = i6 - 10;
                        this.pre_category_pos = i7;
                        this.recycler_category.setSelectedPosition(i7);
                    }
                } else if (this.recycler_channel.hasFocus() && (i = this.pre_channel_pos) > 10) {
                    int i8 = i - 10;
                    this.pre_channel_pos = i8;
                    this.recycler_channel.setSelectedPosition(i8);
                }
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public void onClick(View view) {
        EPGChannel ePGChannel;
        switch (view.getId()) {
            case R.id.btn_catch_up /* 2131427465 */:
            case R.id.image_epg /* 2131427796 */:
                goToCatchupActivity();
                break;
            case R.id.btn_fav /* 2131427468 */:
                if (this.epgChannels.size() > 0 && (ePGChannel = (EPGChannel) this.epgChannels.get(this.pre_channel_pos)) != null) {
                    controlFav(ePGChannel, this.pre_channel_pos);
                    showFavImageIcon(!ePGChannel.is_favorite());
                    if (!ePGChannel.is_favorite()) {
                        Toast.makeText(this, this.wordModels.getChannel_added_to_fav(), 0).show();
                    } else {
                        Toast.makeText(this, this.wordModels.getChannel_removed_from_fav(), 0).show();
                    }
                    break;
                }
                break;
            case R.id.btn_search /* 2131427485 */:
            case R.id.image_search /* 2131427814 */:
                goToSearchActivity();
                break;
            case R.id.image_audio /* 2131427782 */:
                showAudioTrack();
                break;
            case R.id.image_fav /* 2131427799 */:
                EPGChannel ePGChannel2 = this.selectedChannel;
                if (ePGChannel2 != null) {
                    controlFav(ePGChannel2, this.channel_pos);
                    showFavImageIcon(!this.selectedChannel.is_favorite());
                    if (!this.selectedChannel.is_favorite()) {
                        Toast.makeText(this, this.wordModels.getChannel_added_to_fav(), 0).show();
                    } else {
                        Toast.makeText(this, this.wordModels.getChannel_removed_from_fav(), 0).show();
                    }
                }
                break;
            case R.id.image_series /* 2131427815 */:
            case R.id.txt_series /* 2131428309 */:
                goToSeriesActivity();
                break;
            case R.id.image_subtitle /* 2131427817 */:
                showSubtitleTrack();
                break;
            case R.id.image_vod /* 2131427823 */:
            case R.id.txt_movie /* 2131428290 */:
                goToVodActivity();
                break;
            case R.id.txt_home /* 2131428282 */:
                releaseMediaPlayer();
                if (this.categoryModels.get(this.category_pos).getId().equalsIgnoreCase(Constants.resume_id) && this.selectedChannel != null) {
                    saveCategoryAndChannelPosition();
                }
                finish();
                break;
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_live);
        Utils.FullScreenCall(this);
        this.preferenceHelper = new PreferenceHelper(this);
        this.wordModels = GetSharedInfo.getWordModel(this);
        initView();
        this.dataSourceFactory = DemoUtil.getDataSourceFactory(this);
        this.trackSelectionParameters = new TrackSelectionParameters.Builder(this).setTrackTypeDisabled(3, !this.preferenceHelper.getSharedPreferenceSubtitleEnable()).build();
        int i = 0;
        this.is_full = getIntent().getBooleanExtra("is_full", false);
        Constants.getLiveGroupModels(this.preferenceHelper.getSharedPreferenceInvisibleLiveCategories(), this);
        this.categoryModels = LTVApp.live_categories_filter;
        if (this.categoryModels == null || this.categoryModels.isEmpty()) {
            this.categoryModels = new ArrayList<>();
            this.categoryModels.add(new CategoryModel(Constants.all_id, "All"));
        }
        int sharedPreferenceCategoryPos = this.preferenceHelper.getSharedPreferenceCategoryPos();
        this.category_pos = sharedPreferenceCategoryPos;
        if (sharedPreferenceCategoryPos > this.categoryModels.size() - 1) {
            this.category_pos = 0;
        }
        if (isAdultChannel(this.categoryModels.get(this.category_pos).getId(), this.categoryModels.get(this.category_pos).getId())) {
            this.category_pos = 0;
        }
        RecyclerLiveCategoryAdapter recyclerLiveCategoryAdapter = new RecyclerLiveCategoryAdapter(this, this.categoryModels, this.preferenceHelper.getSharedPreferenceISM3U(), this.preferenceHelper.getSharedPreferenceIsGrid(), this.category_pos, new LiveActivity$$ExternalSyntheticLambda4(this, i));
        this.categoryAdapter = recyclerLiveCategoryAdapter;
        this.recycler_category.setAdapter(recyclerLiveCategoryAdapter);
        this.recycler_category.setSelectedPosition(this.category_pos);
        this.epgChannels = RealmController.with().getLiveChannelsByCategory(this.categoryModels.get(this.category_pos), "", this.preferenceHelper.getSharedPreferenceISM3U(), this.preferenceHelper.getSharedPreferenceLiveOrder());
        this.channel_pos = this.preferenceHelper.getSharedPreferenceChannelPos();
        if (this.epgChannels.size() <= 0) {
            this.channel_pos = -1;
        } else if (this.channel_pos > this.epgChannels.size() - 1) {
            this.channel_pos = 0;
        }
        RecyclerLiveChannelAdapter recyclerLiveChannelAdapter = new RecyclerLiveChannelAdapter(this, this.epgChannels, this.channel_pos, new LiveActivity$$ExternalSyntheticLambda5(this, i));
        this.channelAdapter = recyclerLiveChannelAdapter;
        this.recycler_channel.setAdapter(recyclerLiveChannelAdapter);
        setFocusTopView(false);
        setFocusButtons(false);
        this.epgAdapter = new EpgRecyclerAdapter(this, new ArrayList());
        EpgReminderBinder.bind(this, this.epgAdapter, () -> this.selectedChannel == null ? this.stream_id : this.selectedChannel.getStream_id());
        this.recycler_epg.setLayoutManager(new LinearLayoutManager(this));
        this.recycler_epg.setAdapter(this.epgAdapter);
        this.recycler_epg.setFocusable(false);
        this.recycler_epg.setVisibility(View.GONE);
        ensureVisibleEpgPanel();
        if (this.epgChannels.size() <= 0) {
            this.recycler_category.requestFocus();
            return;
        }
        setFull();
        EPGChannel initialChannel = (EPGChannel) this.epgChannels.get(this.channel_pos);
        if (isAdultChannel(initialChannel.getCategory_id(), initialChannel.getCategory_name())) {
            showChannelLockDlgFragment(initialChannel, this.channel_pos, 2);
            return;
        }
        playSelectedChannel(initialChannel);
        this.stream_id = ((EPGChannel) this.epgChannels.get(this.channel_pos)).getStream_id();
        this.handler.removeCallbacks(this.epgTicker);
        epgTimer(this.stream_id);
        String name = ((EPGChannel) this.epgChannels.get(this.channel_pos)).getName();
        this.channel_name = name;
        this.txt_name.setText(name);
        showFavImageIcon(((EPGChannel) this.epgChannels.get(this.channel_pos)).is_favorite());
        changeChannelInfo(this.channel_pos);
        this.recycler_channel.requestFocus();
        this.recycler_channel.setSelectedPosition(this.channel_pos);
        this.recycler_channel.scrollToPosition(this.channel_pos);
        String voiceQuery = getIntent().getStringExtra("voice_query");
        if (voiceQuery != null && !voiceQuery.trim().isEmpty()) {
            openVoiceChannel(voiceQuery);
        }
        NullTextGuard.sanitize(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NullTextGuard.sanitize(this);
    }

    private void applyVoiceChannelSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        int allPosition = 0;
        for (int i = 0; this.categoryModels != null && i < this.categoryModels.size(); i++) {
            if (Constants.all_id.equalsIgnoreCase(this.categoryModels.get(i).getId())) {
                allPosition = i;
                break;
            }
        }
        this.category_pos = allPosition;
        if (this.categoryAdapter != null) {
            this.categoryAdapter.setCategoryPosition(allPosition);
        }
        this.et_search.setText(query);
        Toast.makeText(this, "Canais encontrados para: " + query, Toast.LENGTH_SHORT).show();
    }

    private int voiceDp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setupVoiceButton() {
        if (this.main_lay == null) {
            return;
        }
        this.voiceButton = VoiceButtonFactory.create(this, "Microfone: comando de voz", view -> requestVoicePermissionAndStart());
        this.voiceButton.setId(View.generateViewId());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(voiceDp(116), voiceDp(52));
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMarginEnd(voiceDp(24));
        params.bottomMargin = voiceDp(24);
        this.main_lay.addView(this.voiceButton, params);
        this.voiceButton.bringToFront();
        if (!VoiceCommandController.isAvailable(this)) {
            this.voiceButton.setVisibility(View.GONE);
            return;
        }
        this.voiceCommandController = new VoiceCommandController(this, new VoiceCommandController.Listener() {
            public void onVoiceCommand(VoiceCommand command) {
                handleVoiceCommand(command);
            }

            public void onVoiceState(String state) {
                Toast.makeText(LiveActivity.this, state, Toast.LENGTH_SHORT).show();
            }

            public void onVoiceError(String message) {
                Toast.makeText(LiveActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestVoicePermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, VOICE_PERMISSION_REQUEST);
            return;
        }
        if (this.voiceCommandController != null) {
            this.voiceCommandController.start();
        }
    }

    private void handleVoiceCommand(VoiceCommand command) {
        if (command == null) {
            return;
        }
        switch (command.getAction()) {
            case OPEN_LIVE:
                Toast.makeText(this, "Você já está nos canais ao vivo", Toast.LENGTH_SHORT).show();
                return;
            case OPEN_MOVIES:
                goToVodActivity();
                return;
            case OPEN_SERIES:
                goToSeriesActivity();
                return;
            case OPEN_SETTINGS:
                goToSettingActivity();
                return;
            case NEXT_CHANNEL:
                playNextChannel();
                return;
            case PREVIOUS_CHANNEL:
                playPreviousChannel();
                return;
            case PLAY:
                if (this.player != null) {
                    this.player.setPlayWhenReady(true);
                }
                return;
            case PAUSE:
                if (this.player != null) {
                    this.player.setPlayWhenReady(false);
                }
                return;
            case SEARCH_CHANNEL:
                applyVoiceChannelSearch(command.getQuery());
                return;
            case OPEN_CHANNEL:
            case OPEN_TITLE:
                openVoiceChannel(command.getQuery());
                return;
            default:
                Toast.makeText(this, "Diga: abrir canal seguido do nome", Toast.LENGTH_SHORT).show();
        }
    }

    private void openVoiceChannel(String query) {
        if (this.et_search != null && this.et_search.length() > 0) {
            this.et_search.setText("");
        }
        RealmResults<EPGChannel> globalMatches = RealmController.with().getLiveChannelsByKey(query, true);
        EPGChannel channel = VoiceChannelMatcher.findExactMatch(globalMatches, query);
        if (channel == null) {
            applyVoiceChannelSearch(query);
            return;
        }
        int index = -1;
        for (int i = 0; i < this.epgChannels.size(); i++) {
            EPGChannel item = this.epgChannels.get(i);
            if (item != null && item.getStream_id() != null && item.getStream_id().equals(channel.getStream_id())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            this.epgChannels = globalMatches;
            if (this.channelAdapter != null) {
                this.channelAdapter.updateData(this.epgChannels, 0);
            }
            for (int i = 0; i < this.epgChannels.size(); i++) {
                EPGChannel item = this.epgChannels.get(i);
                if (item != null && item.getStream_id() != null && item.getStream_id().equals(channel.getStream_id())) {
                    index = i;
                    break;
                }
            }
        }
        if (index < 0) {
            applyVoiceChannelSearch(query);
            return;
        }
        if (isAdultChannel(channel.getCategory_id(), channel.getCategory_name())) {
            showChannelLockDlgFragment(channel, index, 0);
            return;
        }
        this.channel_pos = index;
        this.pre_channel_pos = index;
        this.is_full = true;
        setFull();
        playSelectedChannel(channel);
        this.handler.removeCallbacks(this.epgTicker);
        epgTimer(channel.getStream_id());
        this.channel_name = channel.getName();
        updateChannelEpgText("carregando EPG...", "aguardando programação...");
        changeChannelInfo(index);
        this.recycler_channel.setSelectedPosition(index);
        this.recycler_channel.scrollToPosition(index);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VOICE_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestVoicePermissionAndStart();
        } else if (requestCode == VOICE_PERMISSION_REQUEST) {
            Toast.makeText(this, "O comando de voz precisa da permissão de microfone", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onDestroy() {
        if (this.voiceCommandController != null) {
            this.voiceCommandController.destroy();
        }
        super.onDestroy();
    }

    public void onFocusChange(View view, boolean z) {
        if (z) {
            this.handler.removeCallbacks(this.hideInfoTicker);
            mInfoHideTimer();
        }
    }

    public void onPause() {
        if (this.voiceCommandController != null) {
            this.voiceCommandController.stop();
        }
        super.onPause();
        if (Util.SDK_INT <= 23) {
            StyledPlayerView styledPlayerView = this.playerView;
            if (styledPlayerView != null) {
                styledPlayerView.onPause();
            }
            releaseMediaPlayer();
        }
    }

    public void onStop() {
        super.onStop();
        HeartbeatPeriodicHelper heartbeatPeriodicHelper = this.heartbeatHelper;
        if (heartbeatPeriodicHelper != null) {
            heartbeatPeriodicHelper.stop();
            this.heartbeatHelper = null;
        }
        if (Util.SDK_INT > 23) {
            StyledPlayerView styledPlayerView = this.playerView;
            if (styledPlayerView != null) {
                styledPlayerView.onPause();
            }
            releaseMediaPlayer();
        }
    }
}

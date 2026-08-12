package com.ouropro.player.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
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
import com.ouropro.player.dlgfragment.ExitDlgFragment;
import com.ouropro.player.dlgfragment.MovieInfoDlgFragment;
import com.ouropro.player.dlgfragment.PlayErrorDlgFragment;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.WordModels;
import com.ouropro.player.utils.DemoUtil;
import com.ouropro.player.utils.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class TrailerActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, SeekBar.OnSeekBarChangeListener {
    public ImageButton btn_audio;
    public ImageButton btn_back;
    public ImageButton btn_forward;
    public ImageButton btn_info;
    public ImageButton btn_play;
    public ImageButton btn_resolution;
    public ImageButton btn_rewind;
    public ImageButton btn_sub;
    public DataSource.Factory dataSourceFactory;
    public String description;
    public PlayErrorDlgFragment errorDlgFragment;
    public ExitDlgFragment exitDlgFragment;
    public TrailerActivity$$ExternalSyntheticLambda0 hideInfoTicker;
    public TrailerActivity$$ExternalSyntheticLambda0 hideResolutionTicker;
    public String id;
    public ImageView image_forward;
    public ImageView image_rewind;
    public MovieInfoDlgFragment infoDlgFragment;
    public Tracks lastSeenTracks;
    public ConstraintLayout ly_control;
    public int maxTime;
    public ExoPlayer player;
    public StyledPlayerView playerView;
    public PreferenceHelper preferenceHelper;
    public int resolutionTime;
    public SeekBar seekBar;
    public TrackSelectionParameters trackSelectionParameters;
    public DefaultTrackSelector trackSelector;
    public TextView txt_end_time;
    public TextView txt_name;
    public TextView txt_resolution;
    public TextView txt_start_time;
    public String cont_url = "";
    public String movie_name = "";
    public String image_url = "";
    public int error_count = 0;
    public int duration = 0;
    public Handler handler = new Handler();
    public String resolution = "1920x1080";
    public WordModels wordModels = new WordModels();
    private final Runnable mUpdateTimeTask = new Runnable() { // from class: com.ouropro.player.activities.TrailerActivity.3
        public void run() {
            try {
                ExoPlayer exoPlayer = TrailerActivity.this.player;
                if (exoPlayer != null) {
                    long duration = exoPlayer.getDuration();
                    long currentPosition = TrailerActivity.this.player.getCurrentPosition();
                    TrailerActivity.this.txt_start_time.setText("" + Utils.milliSecondsToTimer(currentPosition));
                    TrailerActivity.this.txt_end_time.setText("" + Utils.milliSecondsToTimer(duration));
                    int progressPercentage = Utils.getProgressPercentage(currentPosition, duration);
                    TrailerActivity.this.seekBar.setProgress(progressPercentage);
                    if (progressPercentage > 98) {
                        TrailerActivity trailerActivity = TrailerActivity.this;
                        trailerActivity.handler.removeCallbacks(trailerActivity.mUpdateTimeTask);
                        return;
                    }
                }
            } catch (Exception unused) {
                TrailerActivity.this.seekBar.setProgress(0);
            }
            TrailerActivity.this.handler.postDelayed(this, 1000L);
        }
    };

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
                TrailerActivity.this.releaseMediaPlayer();
                TrailerActivity trailerActivity = TrailerActivity.this;
                trailerActivity.playVideo(trailerActivity.cont_url, 0L);
            } else {
                if (i == 3) {
                    TrailerActivity trailerActivity2 = TrailerActivity.this;
                    trailerActivity2.error_count = 0;
                    trailerActivity2.image_forward.setVisibility(8);
                    TrailerActivity.this.image_rewind.setVisibility(8);
                    return;
                }
                if (i == 2 && TrailerActivity.this.ly_control.getVisibility() == 0) {
                    TrailerActivity trailerActivity3 = TrailerActivity.this;
                    trailerActivity3.handler.removeCallbacks(trailerActivity3.hideInfoTicker);
                    TrailerActivity.this.listTimer();
                }
            }
        }

        public final /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
        }

        public void onPlayerError(PlaybackException playbackException) {
            if (playbackException.errorCode == 1002) {
                TrailerActivity.this.image_forward.setVisibility(8);
                TrailerActivity.this.image_rewind.setVisibility(8);
                TrailerActivity.this.releaseMediaPlayer();
                TrailerActivity trailerActivity = TrailerActivity.this;
                trailerActivity.playVideo(trailerActivity.cont_url, 0L);
                return;
            }
            TrailerActivity trailerActivity2 = TrailerActivity.this;
            int i = trailerActivity2.error_count;
            if (i > 3) {
                trailerActivity2.releaseMediaPlayer();
                TrailerActivity.this.showPlayErrorDlgFragment();
            } else {
                trailerActivity2.error_count = i + 1;
                trailerActivity2.releaseMediaPlayer();
                TrailerActivity trailerActivity3 = TrailerActivity.this;
                trailerActivity3.playVideo(trailerActivity3.cont_url, 0L);
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

        public void onTracksChanged(@NonNull Tracks tracks) {
            if (tracks == TrailerActivity.this.lastSeenTracks) {
                return;
            }
            if (tracks.containsType(2) && !tracks.isTypeSupported(2, true)) {
                Toast.makeText(TrailerActivity.this, "Unsupported Video", 0).show();
            }
            if (tracks.containsType(1) && !tracks.isTypeSupported(1, true)) {
                Toast.makeText(TrailerActivity.this, "Unsupported Audio", 0).show();
            }
            TrailerActivity.this.lastSeenTracks = tracks;
        }

        public final /* synthetic */ void onVideoSizeChanged(VideoSize videoSize) {
        }

        public final /* synthetic */ void onVolumeChanged(float f) {
        }
    }

    private MediaSource.Factory createMediaSourceFactory() {
        DefaultDrmSessionManagerProvider defaultDrmSessionManagerProvider = new DefaultDrmSessionManagerProvider();
        defaultDrmSessionManagerProvider.setDrmHttpDataSourceFactory(DemoUtil.getHttpDataSourceFactory(this));
        return new DefaultMediaSourceFactory(this).setDataSourceFactory(this.dataSourceFactory).setDrmSessionManagerProvider((DrmSessionManagerProvider) defaultDrmSessionManagerProvider);
    }

    private boolean getControlButtonFocus() {
        return this.btn_rewind.hasFocus() || this.btn_play.hasFocus() || this.btn_forward.hasFocus();
    }

    private boolean getFeatureButtonFocus() {
        return this.btn_info.hasFocus() || this.btn_audio.hasFocus() || this.btn_sub.hasFocus() || this.btn_resolution.hasFocus();
    }

    private void initView() {
        StyledPlayerView styledPlayerView = (StyledPlayerView) findViewById(R.id.player_view);
        this.playerView = styledPlayerView;
        styledPlayerView.getSubtitleView().setApplyEmbeddedStyles(false);
        CaptionStyleCompat captionStyleCompat = new CaptionStyleCompat(-1, 0, 0, 0, 0, null);
        this.playerView.getSubtitleView().setFixedTextSize(3, this.preferenceHelper.getSharedPreferenceSubtitleSize());
        this.playerView.getSubtitleView().setStyle(captionStyleCompat);
        this.ly_control = (ConstraintLayout) findViewById(R.id.ly_control);
        this.btn_back = (ImageButton) findViewById(R.id.btn_back);
        this.btn_rewind = (ImageButton) findViewById(R.id.btn_rewind);
        this.btn_play = (ImageButton) findViewById(R.id.btn_play);
        this.btn_forward = (ImageButton) findViewById(R.id.btn_forward);
        this.btn_info = (ImageButton) findViewById(R.id.btn_info);
        this.btn_sub = (ImageButton) findViewById(R.id.btn_sub);
        this.btn_audio = (ImageButton) findViewById(R.id.btn_audio);
        this.btn_resolution = (ImageButton) findViewById(R.id.btn_resolution);
        this.txt_start_time = (TextView) findViewById(R.id.txt_start_time);
        this.txt_end_time = (TextView) findViewById(R.id.txt_end_time);
        this.image_forward = (ImageView) findViewById(R.id.image_forward);
        this.image_rewind = (ImageView) findViewById(R.id.image_rewind);
        this.txt_name = (TextView) findViewById(R.id.txt_name);
        this.txt_resolution = (TextView) findViewById(R.id.txt_resolution);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        this.seekBar = seekBar;
        seekBar.setMax(100);
        this.seekBar.setOnSeekBarChangeListener(this);
        this.btn_rewind.setOnClickListener(this);
        this.btn_play.setOnClickListener(this);
        this.btn_forward.setOnClickListener(this);
        this.btn_info.setOnClickListener(this);
        this.btn_sub.setOnClickListener(this);
        this.btn_audio.setOnClickListener(this);
        this.btn_back.setOnClickListener(this);
        this.btn_resolution.setOnClickListener(this);
        this.btn_rewind.setOnFocusChangeListener(this);
        this.btn_play.setOnFocusChangeListener(this);
        this.btn_forward.setOnFocusChangeListener(this);
        this.btn_info.setOnFocusChangeListener(this);
        this.btn_sub.setOnFocusChangeListener(this);
        this.btn_audio.setOnFocusChangeListener(this);
        this.btn_resolution.setOnFocusChangeListener(this);
        this.btn_back.setOnFocusChangeListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$listTimer$0() {
        if (this.maxTime < 1) {
            this.ly_control.setVisibility(8);
        } else {
            runNextTicker();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resolutionTimer$1() {
        if (this.resolutionTime < 1) {
            this.txt_resolution.setVisibility(8);
        } else {
            runNextResolutionTicker();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void listTimer() {
        this.maxTime = 10;
        TrailerActivity$$ExternalSyntheticLambda0 trailerActivity$$ExternalSyntheticLambda0 = new TrailerActivity$$ExternalSyntheticLambda0(this, 0);
        this.hideInfoTicker = trailerActivity$$ExternalSyntheticLambda0;
        trailerActivity$$ExternalSyntheticLambda0.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playVideo(String str, long j) {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.release();
        }
        String adaptiveMimeTypeForContentType = Util.getAdaptiveMimeTypeForContentType(Util.inferContentType(Uri.parse(str), ""));
        MediaItem.Builder builder = new MediaItem.Builder();
        builder.setUri(Uri.parse(str)).setMediaMetadata(new MediaMetadata.Builder().setTitle("title").build()).setMimeType(adaptiveMimeTypeForContentType);
        MediaItem mediaItemBuild = builder.build();
        this.lastSeenTracks = Tracks.EMPTY;
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(this);
        this.trackSelector = defaultTrackSelector;
        defaultTrackSelector.setParameters(this.trackSelectionParameters);
        ExoPlayer.Builder trackSelector = new ExoPlayer.Builder(this).setMediaSourceFactory(createMediaSourceFactory()).setTrackSelector(this.trackSelector);
        setRenderersFactory(trackSelector, true);
        ExoPlayer exoPlayerBuild = trackSelector.build();
        this.player = exoPlayerBuild;
        exoPlayerBuild.setTrackSelectionParameters(this.trackSelectionParameters);
        this.player.addListener(new PlayerEventListener());
        this.player.addAnalyticsListener(new EventLogger());
        this.player.addAnalyticsListener(new AnalyticsListener() { // from class: com.ouropro.player.activities.TrailerActivity.2
            public final /* synthetic */ void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
            }

            public final /* synthetic */ void onAudioCodecError(AnalyticsListener.EventTime eventTime, Exception exc) {
            }

            public final /* synthetic */ void onAudioDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j2) {
            }

            public final /* synthetic */ void onAudioDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j2, long j3) {
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

            public final /* synthetic */ void onAudioPositionAdvancing(AnalyticsListener.EventTime eventTime, long j2) {
            }

            public final /* synthetic */ void onAudioSessionIdChanged(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onAudioSinkError(AnalyticsListener.EventTime eventTime, Exception exc) {
            }

            public final /* synthetic */ void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int i, long j2, long j3) {
            }

            public final /* synthetic */ void onAvailableCommandsChanged(AnalyticsListener.EventTime eventTime, Player.Commands commands) {
            }

            public final /* synthetic */ void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int i, long j2, long j3) {
            }

            public final /* synthetic */ void onCues(AnalyticsListener.EventTime eventTime, CueGroup cueGroup) {
            }

            public final /* synthetic */ void onCues(AnalyticsListener.EventTime eventTime, List list) {
            }

            public final /* synthetic */ void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int i, String str2, long j2) {
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

            public final /* synthetic */ void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j2) {
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

            public final /* synthetic */ void onMaxSeekToPreviousPositionChanged(AnalyticsListener.EventTime eventTime, long j2) {
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

            public final /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object obj, long j2) {
            }

            public final /* synthetic */ void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int i) {
            }

            public final /* synthetic */ void onSeekBackIncrementChanged(AnalyticsListener.EventTime eventTime, long j2) {
            }

            public final /* synthetic */ void onSeekForwardIncrementChanged(AnalyticsListener.EventTime eventTime, long j2) {
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

            public final /* synthetic */ void onVideoDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j2) {
            }

            public final /* synthetic */ void onVideoDecoderInitialized(AnalyticsListener.EventTime eventTime, String str2, long j2, long j3) {
            }

            public final /* synthetic */ void onVideoDecoderReleased(AnalyticsListener.EventTime eventTime, String str2) {
            }

            public final /* synthetic */ void onVideoDisabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onVideoEnabled(AnalyticsListener.EventTime eventTime, DecoderCounters decoderCounters) {
            }

            public final /* synthetic */ void onVideoFrameProcessingOffset(AnalyticsListener.EventTime eventTime, long j2, int i) {
            }

            public final /* synthetic */ void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format) {
            }

            public final /* synthetic */ void onVideoInputFormatChanged(AnalyticsListener.EventTime eventTime, Format format, DecoderReuseEvaluation decoderReuseEvaluation) {
            }

            public final /* synthetic */ void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2, int i3, float f) {
            }

            public void onVideoSizeChanged(@NonNull AnalyticsListener.EventTime eventTime, @NonNull VideoSize videoSize) {
                TrailerActivity.this.resolution = videoSize.width + "x" + videoSize.height;
                Objects.requireNonNull(TrailerActivity.this);
                Objects.requireNonNull(TrailerActivity.this);
            }

            public final /* synthetic */ void onVolumeChanged(AnalyticsListener.EventTime eventTime, float f) {
            }
        });
        this.player.setAudioAttributes(AudioAttributes.DEFAULT, true);
        this.player.setPlayWhenReady(true);
        this.playerView.setPlayer(this.player);
        this.player.setMediaItem(mediaItemBuild);
        this.player.prepare();
        this.player.play();
        this.handler.removeCallbacks(this.mUpdateTimeTask);
        updateProgressBar();
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

    private void resolutionTimer() {
        this.resolutionTime = 2;
        TrailerActivity$$ExternalSyntheticLambda0 trailerActivity$$ExternalSyntheticLambda0 = new TrailerActivity$$ExternalSyntheticLambda0(this, 1);
        this.hideResolutionTicker = trailerActivity$$ExternalSyntheticLambda0;
        trailerActivity$$ExternalSyntheticLambda0.run();
    }

    private void runNextResolutionTicker() {
        this.resolutionTime--;
        this.handler.postAtTime(this.hideResolutionTicker, SystemClock.uptimeMillis() + 1000);
    }

    private void runNextTicker() {
        this.maxTime--;
        this.handler.postAtTime(this.hideInfoTicker, SystemClock.uptimeMillis() + 1000);
    }

    private void seekToForward() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            long currentPosition = exoPlayer.getCurrentPosition();
            long duration = this.player.getDuration();
            int i = this.duration + 30;
            this.duration = i;
            if (duration < ((long) i) * 1000) {
                this.player.seekTo(duration - 30);
            } else {
                this.player.seekTo((((long) i) * 1000) + currentPosition);
            }
            this.duration = 0;
            this.handler.removeCallbacks(this.mUpdateTimeTask);
            updateProgressBar();
        }
    }

    private void seekToRewind() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            long currentPosition = exoPlayer.getCurrentPosition();
            int i = this.duration + 30;
            this.duration = i;
            if (currentPosition < ((long) i) * 1000) {
                this.player.seekTo(1L);
            } else {
                this.player.seekTo(currentPosition - (((long) i) * 1000));
            }
            this.duration = 0;
            this.handler.removeCallbacks(this.mUpdateTimeTask);
            updateProgressBar();
        }
    }

    private void setRenderersFactory(ExoPlayer.Builder builder, boolean z) {
        builder.setRenderersFactory(DemoUtil.buildRenderersFactory(this, z));
    }

    private void showAudioTrackDlgFragment() {
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

    private void showExitDlgFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_exit");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        ExitDlgFragment exitDlgFragmentNewInstance = ExitDlgFragment.newInstance(this.wordModels.getStop_playback(), this.wordModels.getPlayback_description(), this.wordModels.getStr_yes(), this.wordModels.getNo());
        this.exitDlgFragment = exitDlgFragmentNewInstance;
        exitDlgFragmentNewInstance.setOkButtonClickListener(new ExitDlgFragment.OkButtonClickListener() { // from class: com.ouropro.player.activities.TrailerActivity.4
            public void onCancelClick() {
                TrailerActivity trailerActivity = TrailerActivity.this;
                trailerActivity.handler.removeCallbacks(trailerActivity.hideInfoTicker);
                TrailerActivity.this.ly_control.setVisibility(0);
                TrailerActivity.this.btn_play.requestFocus();
                TrailerActivity.this.listTimer();
            }

            public void onOkClick() {
                TrailerActivity.this.exitDlgFragment.dismiss();
                TrailerActivity.this.releaseMediaPlayer();
                TrailerActivity.this.finish();
            }
        });
        this.exitDlgFragment.show(supportFragmentManager, "fragment_exit");
    }

    private void showInfoDlgFragment(String str, String str2, String str3, String str4) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_info");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        MovieInfoDlgFragment movieInfoDlgFragmentNewInstance = MovieInfoDlgFragment.newInstance(str, str2, str3, str4);
        this.infoDlgFragment = movieInfoDlgFragmentNewInstance;
        movieInfoDlgFragmentNewInstance.show(supportFragmentManager, "fragment_info");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPlayErrorDlgFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag("fragment_error");
        if (fragmentFindFragmentByTag != null) {
            Insets$$ExternalSyntheticOutline0.m(fragmentTransactionBeginTransaction, fragmentFindFragmentByTag, (String) null);
            return;
        }
        PlayErrorDlgFragment playErrorDlgFragmentNewInstance = PlayErrorDlgFragment.newInstance(this.wordModels.getPlay_back_error(), this.wordModels.getPlay_back_error_description(), true);
        this.errorDlgFragment = playErrorDlgFragmentNewInstance;
        playErrorDlgFragmentNewInstance.setOkButtonClickListener(new PlayErrorDlgFragment.OkButtonClickListener() { // from class: com.ouropro.player.activities.TrailerActivity.5
            public void onCancelClick() {
            }

            public void onOkClick() {
                TrailerActivity.this.finish();
            }
        });
        this.errorDlgFragment.show(supportFragmentManager, "fragment_error");
    }

    private void showSubTitleTrackDlgFragment() {
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

    @SuppressLint({"StaticFieldLeak"})
    private void trailerClick(String str) {
        this.ly_control.setVisibility(0);
        new YouTubeExtractor(this) { // from class: com.ouropro.player.activities.TrailerActivity.1
            public void onExtractionComplete(SparseArray<YtFile> sparseArray, VideoMeta videoMeta) {
                try {
                    if (sparseArray != null) {
                        TrailerActivity.this.cont_url = sparseArray.get(22).getUrl();
                        TrailerActivity trailerActivity = TrailerActivity.this;
                        trailerActivity.playVideo(trailerActivity.cont_url, 0L);
                        TrailerActivity.this.listTimer();
                    } else {
                        Toast.makeText(TrailerActivity.this, "Can't play this trailer.", 0).show();
                        TrailerActivity.this.finish();
                    }
                } catch (Exception unused) {
                    Toast.makeText(TrailerActivity.this, "Can't play this trailer.", 0).show();
                    TrailerActivity.this.finish();
                }
            }
        }.extract("https://www.youtube.com/watch?v=" + str);
    }

    /* JADX WARN: Code duplicated, block: B:46:0x00aa  */
    /* JADX WARN: Code duplicated, block: B:48:0x00b2  */
    /* JADX WARN: Code duplicated, block: B:49:0x00bb  */
    /* JADX WARN: Code duplicated, block: B:51:0x00c3  */
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 4) {
                MovieInfoDlgFragment movieInfoDlgFragment = this.infoDlgFragment;
                if (movieInfoDlgFragment != null && movieInfoDlgFragment.isAdded()) {
                    this.infoDlgFragment.dismiss();
                    return true;
                }
                if (this.ly_control.getVisibility() == 0) {
                    this.ly_control.setVisibility(8);
                    return true;
                }
                showExitDlgFragment();
                return false;
            }
            if (keyCode == 85) {
                ExoPlayer exoPlayer = this.player;
                if (exoPlayer != null) {
                    if (exoPlayer.getPlayWhenReady()) {
                        this.player.setPlayWhenReady(false);
                        this.btn_play.setImageResource(R.drawable.ic_play);
                    } else {
                        this.player.setPlayWhenReady(true);
                        this.btn_play.setImageResource(R.drawable.ic_pause);
                    }
                }
            } else if (keyCode == 89) {
                if (this.ly_control.getVisibility() == 8) {
                    this.image_rewind.setVisibility(0);
                    seekToRewind();
                }
            } else if (keyCode != 90) {
                switch (keyCode) {
                    case 19:
                        if (getFeatureButtonFocus()) {
                            this.btn_play.requestFocus();
                            return true;
                        }
                        if (getControlButtonFocus()) {
                            this.seekBar.requestFocus();
                            return true;
                        }
                        if (this.seekBar.hasFocus()) {
                            this.btn_back.requestFocus();
                            return true;
                        }
                        break;
                    case 20:
                        if (this.ly_control.getVisibility() == 8) {
                            this.ly_control.setVisibility(0);
                            this.btn_play.requestFocus();
                            this.handler.removeCallbacks(this.hideInfoTicker);
                            listTimer();
                            return true;
                        }
                        if (this.btn_back.hasFocus()) {
                            this.seekBar.requestFocus();
                            return true;
                        }
                        if (this.seekBar.hasFocus()) {
                            this.btn_play.requestFocus();
                            return true;
                        }
                        if (getControlButtonFocus()) {
                            this.btn_info.requestFocus();
                            return true;
                        }
                        break;
                    case 21:
                        if (this.ly_control.getVisibility() == 8) {
                            this.image_rewind.setVisibility(0);
                            seekToRewind();
                        }
                        break;
                    case 22:
                        if (this.ly_control.getVisibility() == 8) {
                            this.image_forward.setVisibility(0);
                            seekToForward();
                        }
                        break;
                    case 23:
                        if (this.ly_control.getVisibility() == 8) {
                            this.ly_control.setVisibility(0);
                            this.btn_play.requestFocus();
                            this.handler.removeCallbacks(this.hideInfoTicker);
                            listTimer();
                            return true;
                        }
                        break;
                }
            } else if (this.ly_control.getVisibility() == 8) {
                this.image_forward.setVisibility(0);
                seekToForward();
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_audio /* 2131427462 */:
                showAudioTrackDlgFragment();
                break;
            case R.id.btn_back /* 2131427463 */:
                showExitDlgFragment();
                break;
            case R.id.btn_forward /* 2131427469 */:
                this.handler.removeCallbacks(this.hideInfoTicker);
                listTimer();
                seekToForward();
                break;
            case R.id.btn_info /* 2131427473 */:
                MovieInfoDlgFragment movieInfoDlgFragment = this.infoDlgFragment;
                if (movieInfoDlgFragment != null && movieInfoDlgFragment.isAdded()) {
                    this.infoDlgFragment.dismiss();
                } else {
                    showInfoDlgFragment(this.movie_name, this.description, this.image_url, this.resolution);
                }
                break;
            case R.id.btn_play /* 2131427478 */:
                ExoPlayer exoPlayer = this.player;
                if (exoPlayer != null) {
                    if (!exoPlayer.getPlayWhenReady()) {
                        this.player.setPlayWhenReady(true);
                        this.btn_play.setImageResource(R.drawable.ic_pause);
                    } else {
                        this.player.setPlayWhenReady(false);
                        this.btn_play.setImageResource(R.drawable.ic_play);
                    }
                }
                break;
            case R.id.btn_resolution /* 2131427482 */:
                ExoPlayer exoPlayer2 = this.player;
                if (exoPlayer2 != null && exoPlayer2.getPlayWhenReady()) {
                    if (this.playerView.getResizeMode() == 3) {
                        this.playerView.setResizeMode(0);
                        this.txt_resolution.setText(this.wordModels.getFit_screen());
                    } else {
                        this.playerView.setResizeMode(3);
                        this.txt_resolution.setText(this.wordModels.getFill_screen());
                    }
                    this.txt_resolution.setVisibility(0);
                    this.handler.removeCallbacks(this.hideResolutionTicker);
                    resolutionTimer();
                    break;
                }
                break;
            case R.id.btn_rewind /* 2131427484 */:
                this.handler.removeCallbacks(this.hideInfoTicker);
                listTimer();
                seekToRewind();
                break;
            case R.id.btn_sub /* 2131427487 */:
                showSubTitleTrackDlgFragment();
                break;
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_trailer);
        Utils.FullScreenCall(this);
        this.preferenceHelper = new PreferenceHelper(this);
        this.wordModels = GetSharedInfo.getWordModel(this);
        initView();
        this.description = getIntent().getStringExtra("description");
        this.movie_name = getIntent().getStringExtra("name");
        this.image_url = getIntent().getStringExtra("image_url");
        this.txt_name.setText(this.movie_name);
        this.id = getIntent().getStringExtra("youtube_id");
        this.dataSourceFactory = DemoUtil.getDataSourceFactory(this);
        this.trackSelectionParameters = new TrackSelectionParameters.Builder(this).build();
        String str = this.id;
        if (str != null) {
            trailerClick(str);
        } else {
            Toast.makeText(this, "Invalid youtube Id.", 1).show();
            finish();
        }
    }

    public void onFocusChange(View view, boolean z) {
        if (z) {
            this.handler.removeCallbacks(this.hideInfoTicker);
            listTimer();
        }
    }

    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= 23) {
            StyledPlayerView styledPlayerView = this.playerView;
            if (styledPlayerView != null) {
                styledPlayerView.onPause();
            }
            releaseMediaPlayer();
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (this.player == null || !z) {
            return;
        }
        this.handler.removeCallbacks(this.mUpdateTimeTask);
        seekBar.setProgress(i);
        long duration = (int) ((this.player.getDuration() * ((long) i)) / 100);
        this.player.seekTo(duration);
        TextView textView = this.txt_start_time;
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("");
        sbM.append(Utils.milliSecondsToTimer(duration));
        textView.setText(sbM.toString());
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > 23) {
            StyledPlayerView styledPlayerView = this.playerView;
            if (styledPlayerView != null) {
                styledPlayerView.onPause();
            }
            releaseMediaPlayer();
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (this.player == null || seekBar.getId() != R.id.seekBar) {
            return;
        }
        this.handler.removeCallbacks(this.mUpdateTimeTask);
        long jProgressToTimer = Utils.progressToTimer(seekBar.getProgress(), this.player.getDuration());
        this.player.seekTo(jProgressToTimer);
        TextView textView = this.txt_start_time;
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("");
        sbM.append(Utils.milliSecondsToTimer(jProgressToTimer));
        textView.setText(sbM.toString());
    }

    public void updateProgressBar() {
        this.handler.postDelayed(this.mUpdateTimeTask, 100L);
    }
}

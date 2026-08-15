package com.ouropro.player.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.ouropro.player.R;

public class RadioPlaybackService extends Service {
    public static final String ACTION_PLAY = "com.ouropro.player.radio.PLAY";
    public static final String ACTION_STOP = "com.ouropro.player.radio.STOP";
    public static final String EXTRA_URL = "radio_url";
    public static final String EXTRA_TITLE = "radio_title";
    private static final String CHANNEL_ID = "ouropro_radio_playback";
    private static final int NOTIFICATION_ID = 2406;

    private ExoPlayer player;
    private MediaSessionCompat mediaSession;
    private String currentUrl;
    private String currentTitle;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        this.player = new ExoPlayer.Builder(this).build();
        this.mediaSession = new MediaSessionCompat(this, "OuroProRadio");
        this.mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if (player != null) {
                    player.play();
                    updateNotification();
                }
            }

            @Override
            public void onPause() {
                if (player != null) {
                    player.pause();
                    updateNotification();
                }
            }

            @Override
            public void onStop() {
                stopSelf();
            }
        });
        this.mediaSession.setActive(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (ACTION_PLAY.equals(action)) {
            String url = intent.getStringExtra(EXTRA_URL);
            String title = intent.getStringExtra(EXTRA_TITLE);
            if (url != null && !url.trim().isEmpty()) {
                play(url, title == null || title.trim().isEmpty() ? "Rádio" : title);
            }
        }
        return START_STICKY;
    }

    private void play(String url, String title) {
        this.currentUrl = url;
        this.currentTitle = title;
        this.player.setMediaItem(MediaItem.fromUri(url));
        this.player.prepare();
        this.player.play();
        updateMediaMetadata();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    private void updateMediaMetadata() {
        if (this.mediaSession != null) {
            this.mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, this.currentTitle)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "OuroPro Rádio")
                    .build());
            this.mediaSession.setPlaybackState(new android.support.v4.media.session.PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP)
                    .build());
        }
    }

    private void updateNotification() {
        if (this.currentTitle != null) {
            startForeground(NOTIFICATION_ID, buildNotification());
        }
    }

    private Notification buildNotification() {
        Intent openIntent = new Intent(this, RadioActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 2406, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0));
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_radio)
                .setContentTitle(this.currentTitle == null ? "Rádio" : this.currentTitle)
                .setContentText("OuroPro Rádio")
                .setContentIntent(contentIntent)
                .setOngoing(this.player != null && this.player.isPlaying())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new MediaStyle().setMediaSession(this.mediaSession.getSessionToken()).setShowActionsInCompactView(0, 1))
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pausar", null))
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Reproduzir", null))
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Rádio OuroPro", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Reprodução de rádio em segundo plano");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        if (this.mediaSession != null) {
            this.mediaSession.setActive(false);
            this.mediaSession.release();
            this.mediaSession = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

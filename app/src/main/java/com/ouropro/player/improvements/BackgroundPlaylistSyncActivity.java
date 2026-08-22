package com.ouropro.player.improvements;

import android.content.Intent;
import android.os.Bundle;

import com.ouropro.player.apps.BaseActivity;
import com.ouropro.player.apps.BaseTVActivity;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.WordModels;

/**
 * Runs the existing M3U pipeline without presenting ChangePlaylistActivity.
 * The hosting live screen receives the result and restores the active stream.
 */
public final class BackgroundPlaylistSyncActivity extends BaseActivity {
    public static final String EXTRA_PLAYLIST_URL = "playlist_url";
    public static final String EXTRA_TARGET_STREAM_ID = "target_stream_id";
    public static final String EXTRA_BACKGROUND_SYNC = "background_sync";
    public static final String EXTRA_GO_TO_CHANNEL = "go_to_channel";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.setBusy(false);
        BaseTVActivity.setBusy(false);

        String playlistUrl = getIntent().getStringExtra(EXTRA_PLAYLIST_URL);
        if (playlistUrl == null || playlistUrl.trim().isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        PreferenceHelper helper = new PreferenceHelper(this);
        helper.setSharedPreferenceISM3U(true);
        reloadM3UData(playlistUrl.trim(), new WordModels());
    }

    @Override
    public void doNextTask(boolean success) {
        BaseActivity.setBusy(false);
        BaseTVActivity.setBusy(false);
        if (!success) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_BACKGROUND_SYNC, true);
        result.putExtra(EXTRA_GO_TO_CHANNEL, true);
        result.putExtra(EXTRA_TARGET_STREAM_ID, getIntent().getStringExtra(EXTRA_TARGET_STREAM_ID));
        result.putExtra(PlaylistFailoverManager.EXTRA_MESSAGE, "background_sync");
        setResult(RESULT_OK, result);
        finish();
    }
}

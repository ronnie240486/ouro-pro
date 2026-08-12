package com.ouropro.player.activities;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.FlacStreamMetadata;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.video.VideoFrameReleaseHelper;
import com.ouropro.player.dlgfragment.AddPlaylistDlgFragment;
import com.ouropro.player.dlgfragment.NoConnectionDlgFragment;
import com.ouropro.player.dlgfragment.SelectColorDlgFragment;
import com.ouropro.player.dlgfragment.SubtitleSettingDlgFragment;
import com.ouropro.player.remote.GetSubtitleLoginRequest;
import org.json.JSONObject;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class MovieActivity$$ExternalSyntheticLambda2 implements InputConnectionCompat.OnCommitContentListener, ActivityResultCallback, NoConnectionDlgFragment.OnRetryClickListener, GetSubtitleLoginRequest.OnGetLinkModelListener, SelectColorDlgFragment.ChangeColorListener, BinarySearchSeeker.SeekTimestampConverter, TrackSelectionUtil.AdaptiveTrackSelectionFactory, VideoFrameReleaseHelper.DisplayHelper.Listener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ Object f$0;

    public /* synthetic */ MovieActivity$$ExternalSyntheticLambda2(Object obj, int i) {
        this.$r8$classId = i;
        this.f$0 = obj;
    }

    @Override // com.ouropro.player.remote.GetSubtitleLoginRequest.OnGetLinkModelListener
    public final void OnGetLinkModelResult(JSONObject jSONObject, int i) {
        switch (this.$r8$classId) {
            case 0:
                ((MovieActivity) this.f$0).lambda$GetLoginFromSubtitle$1(jSONObject, i);
                break;
            default:
                ((SeriesActivity) this.f$0).lambda$GetLoginFromSubtitle$1(jSONObject, i);
                break;
        }
    }

    @Override // com.google.android.exoplayer2.trackselection.TrackSelectionUtil.AdaptiveTrackSelectionFactory
    public final ExoTrackSelection createAdaptiveTrackSelection(ExoTrackSelection.Definition definition) {
        return ((RandomTrackSelection.Factory) this.f$0).lambda$createTrackSelections$0(definition);
    }

    @Override // androidx.activity.result.ActivityResultCallback
    public final void onActivityResult(Object obj) {
        ((CategoryActivity) this.f$0).lambda$new$3((ActivityResult) obj);
    }

    @Override // com.ouropro.player.dlgfragment.SelectColorDlgFragment.ChangeColorListener
    public final void onChangeColor() {
        ((SubtitleSettingDlgFragment) this.f$0).lambda$showSelectColorDlgFragment$0();
    }

    @Override // androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener
    public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return InputConnectionCompat.lambda$createOnCommitContentListenerUsingPerformReceiveContent$0((View) this.f$0, inputContentInfoCompat, i, bundle);
    }

    @Override // com.google.android.exoplayer2.video.VideoFrameReleaseHelper.DisplayHelper.Listener
    public final void onDefaultDisplayChanged(Display display) {
        ((VideoFrameReleaseHelper) this.f$0).updateDefaultDisplayRefreshRateParams(display);
    }

    @Override // com.ouropro.player.dlgfragment.NoConnectionDlgFragment.OnRetryClickListener
    public final void onRetryClick() {
        switch (this.$r8$classId) {
            case 3:
                ((ChangePlaylistActivity) this.f$0).lambda$showNoConnectionDlgFragment$2();
                break;
            default:
                ((AddPlaylistDlgFragment) this.f$0).lambda$showNoConnectionDlgFragment$1();
                break;
        }
    }

    @Override // com.google.android.exoplayer2.extractor.BinarySearchSeeker.SeekTimestampConverter
    public final long timeUsToTargetTime(long j) {
        return ((FlacStreamMetadata) this.f$0).getSampleNumber(j);
    }
}

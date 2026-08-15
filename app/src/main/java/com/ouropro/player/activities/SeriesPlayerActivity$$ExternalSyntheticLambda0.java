package com.ouropro.player.activities;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.ouropro.player.dlgfragment.AudioTrackDlgFragment;
import com.ouropro.player.dlgfragment.SubtitleTrackDlgFragment;
import java.util.List;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class SeriesPlayerActivity$$ExternalSyntheticLambda0 implements AudioTrackDlgFragment.ItemPositionListener, SubtitleTrackDlgFragment.ItemPositionListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ SeriesPlayerActivity f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ TrackGroupArray f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ SeriesPlayerActivity$$ExternalSyntheticLambda0(SeriesPlayerActivity seriesPlayerActivity, List list, TrackGroupArray trackGroupArray, int i, int i2) {
        this.$r8$classId = i2;
        this.f$0 = seriesPlayerActivity;
        this.f$1 = list;
        this.f$2 = trackGroupArray;
        this.f$3 = i;
    }

    public final void onItemPosition(int i) {
        switch (this.$r8$classId) {
            case 0:
                this.f$0.lambda$showAudioTrackDlgFragment$3(this.f$1, this.f$2, this.f$3, i);
                break;
            default:
                this.f$0.lambda$showSubTitleTrackDlgFragment$4(this.f$1, this.f$2, this.f$3, i);
                break;
        }
    }
}

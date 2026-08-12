package com.ouropro.player.activities;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.util.ListenerSet;
import com.ouropro.player.dlgfragment.ConnectDlgFragment;
import com.ouropro.player.models.AppInfoModel;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class ChangePlaylistActivity$$ExternalSyntheticLambda0 implements ConnectDlgFragment.SelectList, ListenerSet.Event {
    public final /* synthetic */ Object f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Object f$2;

    public /* synthetic */ ChangePlaylistActivity$$ExternalSyntheticLambda0(AnalyticsListener.EventTime eventTime, MediaItem mediaItem, int i) {
        this.f$0 = eventTime;
        this.f$2 = mediaItem;
        this.f$1 = i;
    }

    public /* synthetic */ ChangePlaylistActivity$$ExternalSyntheticLambda0(ChangePlaylistActivity changePlaylistActivity, int i, AppInfoModel.UrlModel urlModel) {
        this.f$0 = changePlaylistActivity;
        this.f$1 = i;
        this.f$2 = urlModel;
    }

    public final void invoke(Object obj) {
        ((AnalyticsListener) obj).onMediaItemTransition((AnalyticsListener.EventTime) this.f$0, (MediaItem) this.f$2, this.f$1);
    }

    public final void onSelect(int i) {
        ((ChangePlaylistActivity) this.f$0).lambda$showConnectDlgFragment$1(this.f$1, (AppInfoModel.UrlModel) this.f$2, i);
    }
}

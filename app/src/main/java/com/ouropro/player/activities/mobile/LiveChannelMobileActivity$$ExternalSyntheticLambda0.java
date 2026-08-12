package com.ouropro.player.activities.mobile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import com.ouropro.player.helper.RealmChangeItemListener;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveChannelMobileActivity$$ExternalSyntheticLambda0 implements RealmChangeItemListener, ActivityResultCallback {
    public final /* synthetic */ LiveChannelMobileActivity f$0;

    public /* synthetic */ LiveChannelMobileActivity$$ExternalSyntheticLambda0(LiveChannelMobileActivity liveChannelMobileActivity) {
        this.f$0 = liveChannelMobileActivity;
    }

    public final void onActivityResult(Object obj) {
        this.f$0.lambda$new$5((ActivityResult) obj);
    }

    public final void onItemChanged() {
        this.f$0.lambda$playSelectedChannel$1();
    }
}

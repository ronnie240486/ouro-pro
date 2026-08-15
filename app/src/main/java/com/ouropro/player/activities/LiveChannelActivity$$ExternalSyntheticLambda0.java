package com.ouropro.player.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import com.ouropro.player.helper.RealmChangeItemListener;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveChannelActivity$$ExternalSyntheticLambda0 implements ActivityResultCallback, RealmChangeItemListener {
    public final /* synthetic */ LiveChannelActivity f$0;

    public /* synthetic */ LiveChannelActivity$$ExternalSyntheticLambda0(LiveChannelActivity liveChannelActivity) {
        this.f$0 = liveChannelActivity;
    }

    public final void onActivityResult(Object obj) {
        this.f$0.lambda$new$6((ActivityResult) obj);
    }

    public final void onItemChanged() {
        this.f$0.lambda$playSelectedChannel$1();
    }
}

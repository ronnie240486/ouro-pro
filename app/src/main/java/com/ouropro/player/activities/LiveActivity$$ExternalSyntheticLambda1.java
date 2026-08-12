package com.ouropro.player.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import com.ouropro.player.helper.RealmChangeItemListener;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveActivity$$ExternalSyntheticLambda1 implements RealmChangeItemListener, ActivityResultCallback {
    public final /* synthetic */ LiveActivity f$0;

    public /* synthetic */ LiveActivity$$ExternalSyntheticLambda1(LiveActivity liveActivity) {
        this.f$0 = liveActivity;
    }

    public final void onActivityResult(Object obj) {
        this.f$0.lambda$new$7((ActivityResult) obj);
    }

    public final void onItemChanged() {
        this.f$0.lambda$playSelectedChannel$2();
    }
}

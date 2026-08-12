package com.ouropro.player.activities.mobile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import com.ouropro.player.helper.RealmChangeItemListener;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveMobileActivity$$ExternalSyntheticLambda0 implements ActivityResultCallback, RealmChangeItemListener {
    public final /* synthetic */ LiveMobileActivity f$0;

    public /* synthetic */ LiveMobileActivity$$ExternalSyntheticLambda0(LiveMobileActivity liveMobileActivity) {
        this.f$0 = liveMobileActivity;
    }

    public final void onActivityResult(Object obj) {
        this.f$0.lambda$new$6((ActivityResult) obj);
    }

    public final void onItemChanged() {
        this.f$0.lambda$playSelectedChannel$2();
    }
}

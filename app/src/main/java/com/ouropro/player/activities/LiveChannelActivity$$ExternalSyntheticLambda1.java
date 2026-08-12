package com.ouropro.player.activities;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveChannelActivity$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ LiveChannelActivity f$0;

    public /* synthetic */ LiveChannelActivity$$ExternalSyntheticLambda1(LiveChannelActivity liveChannelActivity, int i) {
        this.$r8$classId = i;
        this.f$0 = liveChannelActivity;
    }

    public final void run() {
        switch (this.$r8$classId) {
            case 0:
                this.f$0.lambda$moveTimer$5();
                break;
            default:
                this.f$0.lambda$mInfoHideTimer$4();
                break;
        }
    }
}

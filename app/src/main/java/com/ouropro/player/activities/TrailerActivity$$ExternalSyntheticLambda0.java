package com.ouropro.player.activities;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class TrailerActivity$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ TrailerActivity f$0;

    public /* synthetic */ TrailerActivity$$ExternalSyntheticLambda0(TrailerActivity trailerActivity, int i) {
        this.$r8$classId = i;
        this.f$0 = trailerActivity;
    }

    public final void run() {
        switch (this.$r8$classId) {
            case 0:
                this.f$0.lambda$listTimer$0();
                break;
            default:
                this.f$0.lambda$resolutionTimer$1();
                break;
        }
    }
}

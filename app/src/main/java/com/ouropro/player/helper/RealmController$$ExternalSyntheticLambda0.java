package com.ouropro.player.helper;

import androidx.constraintlayout.core.state.Interpolator;
import androidx.constraintlayout.core.state.Transition;
import io.realm.Realm;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class RealmController$$ExternalSyntheticLambda0 implements Interpolator, Realm.Transaction {
    public final /* synthetic */ String f$0;

    public /* synthetic */ RealmController$$ExternalSyntheticLambda0(String str) {
        this.f$0 = str;
    }

    public final void execute(Realm realm) {
        RealmController.lambda$addToRecentChannels$1(this.f$0, realm);
    }

    public final float getInterpolation(float f) {
        return Transition.lambda$getInterpolator$0(this.f$0, f);
    }
}

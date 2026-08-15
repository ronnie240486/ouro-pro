package com.ouropro.player.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import com.ouropro.player.dlgfragment.AccountDlgFragment;
import com.ouropro.player.dlgfragment.NoConnectionDlgFragment;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class HomeActivity$$ExternalSyntheticLambda0 implements NoConnectionDlgFragment.OnRetryClickListener, ActivityResultCallback, AccountDlgFragment.PayButtonClickListener {
    public final /* synthetic */ HomeActivity f$0;

    public /* synthetic */ HomeActivity$$ExternalSyntheticLambda0(HomeActivity homeActivity) {
        this.f$0 = homeActivity;
    }

    public final void onActivityResult(Object obj) {
        this.f$0.lambda$new$0((ActivityResult) obj);
    }

    public final void onPayButtonClicked() {
        this.f$0.lambda$showAccountDlgFragment$1();
    }

    public final void onRetryClick() {
        this.f$0.lambda$showNoConnectionDlgFragment$2();
    }
}

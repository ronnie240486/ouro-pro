package com.ouropro.player.activities.mobile;

import androidx.appcompat.app.AppCompatActivity;
import com.ouropro.player.helper.RealmChangeItemListener;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveMobileActivity$$ExternalSyntheticLambda1 implements RealmChangeItemListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ AppCompatActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LiveMobileActivity$$ExternalSyntheticLambda1(AppCompatActivity appCompatActivity, int i, int i2) {
        this.$r8$classId = i2;
        this.f$0 = appCompatActivity;
        this.f$1 = i;
    }

    @Override // com.ouropro.player.helper.RealmChangeItemListener
    public final void onItemChanged() {
        switch (this.$r8$classId) {
            case 0:
                ((LiveMobileActivity) this.f$0).lambda$controlFav$4(this.f$1);
                break;
            default:
                ((LiveChannelMobileActivity) this.f$0).lambda$controlFav$3(this.f$1);
                break;
        }
    }
}

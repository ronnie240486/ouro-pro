package com.ouropro.player.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.ouropro.player.helper.RealmChangeItemListener;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveActivity$$ExternalSyntheticLambda0 implements RealmChangeItemListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ AppCompatActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LiveActivity$$ExternalSyntheticLambda0(AppCompatActivity appCompatActivity, int i, int i2) {
        this.$r8$classId = i2;
        this.f$0 = appCompatActivity;
        this.f$1 = i;
    }

    public final void onItemChanged() {
        switch (this.$r8$classId) {
            case 0:
                ((LiveActivity) this.f$0).lambda$controlFav$4(this.f$1);
                break;
            default:
                ((LiveChannelActivity) this.f$0).lambda$controlFav$3(this.f$1);
                break;
        }
    }
}

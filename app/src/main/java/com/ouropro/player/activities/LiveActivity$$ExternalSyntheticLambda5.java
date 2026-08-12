package com.ouropro.player.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.ouropro.player.activities.mobile.LiveChannelMobileActivity;
import com.ouropro.player.activities.mobile.LiveMobileActivity;
import com.ouropro.player.models.EPGChannel;
import kotlin.jvm.functions.Function4;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class LiveActivity$$ExternalSyntheticLambda5 implements Function4 {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ AppCompatActivity f$0;

    public /* synthetic */ LiveActivity$$ExternalSyntheticLambda5(AppCompatActivity appCompatActivity, int i) {
        this.$r8$classId = i;
        this.f$0 = appCompatActivity;
    }

    public final Object invoke(Object obj, Object obj2, Object obj3, Object obj4) {
        switch (this.$r8$classId) {
            case 0:
                return ((LiveActivity) this.f$0).lambda$onCreate$1((EPGChannel) obj, (Integer) obj2, (Boolean) obj3, (Boolean) obj4);
            case 1:
                return ((LiveChannelActivity) this.f$0).lambda$onCreate$0((EPGChannel) obj, (Integer) obj2, (Boolean) obj3, (Boolean) obj4);
            case 2:
                return ((LiveChannelMobileActivity) this.f$0).lambda$onCreate$0((EPGChannel) obj, (Integer) obj2, (Boolean) obj3, (Boolean) obj4);
            default:
                return ((LiveMobileActivity) this.f$0).lambda$onCreate$1((EPGChannel) obj, (Integer) obj2, (Boolean) obj3, (Boolean) obj4);
        }
    }
}

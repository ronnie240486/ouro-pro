package com.ouropro.player.apps;

import io.realm.Realm;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BaseActivity$$ExternalSyntheticLambda7 implements Realm.Transaction {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ String f$0;

    public /* synthetic */ BaseActivity$$ExternalSyntheticLambda7(String str, int i) {
        this.$r8$classId = i;
        this.f$0 = str;
    }

    public final void execute(Realm realm) {
        switch (this.$r8$classId) {
            case 0:
                BaseActivity.lambda$getChannelModels$7(this.f$0, realm);
                break;
            case 1:
                BaseActivity.AnonymousClass10.lambda$onResponse$1(this.f$0, realm);
                break;
            case 2:
                BaseActivity.AnonymousClass5.lambda$onResponse$1(this.f$0, realm);
                break;
            case 3:
                BaseActivity.AnonymousClass6.lambda$onResponse$1(this.f$0, realm);
                break;
            case 4:
                BaseActivity.AnonymousClass7.lambda$onResponse$1(this.f$0, realm);
                break;
            case 5:
                BaseActivity.AnonymousClass8.lambda$onResponse$1(this.f$0, realm);
                break;
            case 6:
                BaseActivity.AnonymousClass9.lambda$onResponse$1(this.f$0, realm);
                break;
            case 7:
                BaseTVActivity.AnonymousClass10.lambda$onResponse$1(this.f$0, realm);
                break;
            case 8:
                BaseTVActivity.AnonymousClass5.lambda$onResponse$1(this.f$0, realm);
                break;
            case 9:
                BaseTVActivity.AnonymousClass6.lambda$onResponse$1(this.f$0, realm);
                break;
            case 10:
                BaseTVActivity.AnonymousClass7.lambda$onResponse$1(this.f$0, realm);
                break;
            case 11:
                BaseTVActivity.AnonymousClass8.lambda$onResponse$1(this.f$0, realm);
                break;
            case 12:
                BaseTVActivity.AnonymousClass9.lambda$onResponse$1(this.f$0, realm);
                break;
            case 13:
                BaseActivity.lambda$getSeriesFromEpisodes$19(this.f$0, realm);
                break;
            case 14:
                BaseActivity.lambda$getMovieModels$11(this.f$0, realm);
                break;
            case 15:
                BaseTVActivity.lambda$getSeriesFromEpisodes$19(this.f$0, realm);
                break;
            case 16:
                BaseTVActivity.lambda$getChannelModels$7(this.f$0, realm);
                break;
            default:
                BaseTVActivity.lambda$getMovieModels$11(this.f$0, realm);
                break;
        }
    }
}

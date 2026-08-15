package com.ouropro.player.apps;

import io.realm.Realm;
import java.util.List;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BaseActivity$$ExternalSyntheticLambda8 implements Realm.Transaction {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ List f$0;

    public /* synthetic */ BaseActivity$$ExternalSyntheticLambda8(List list, int i) {
        this.$r8$classId = i;
        this.f$0 = list;
    }

    public final void execute(Realm realm) {
        switch (this.$r8$classId) {
            case 0:
                BaseActivity.lambda$getEpisodeModels$15(this.f$0, realm);
                break;
            case 1:
                BaseActivity.lambda$getChannelModels$6(this.f$0, realm);
                break;
            case 2:
                BaseActivity.lambda$getSeriesFromEpisodes$18(this.f$0, realm);
                break;
            case 3:
                BaseActivity.lambda$getMovieModels$10(this.f$0, realm);
                break;
            case 4:
                BaseTVActivity.lambda$getEpisodeModels$15(this.f$0, realm);
                break;
            case 5:
                BaseTVActivity.lambda$getSeriesFromEpisodes$18(this.f$0, realm);
                break;
            case 6:
                BaseTVActivity.lambda$getChannelModels$6(this.f$0, realm);
                break;
            default:
                BaseTVActivity.lambda$getMovieModels$10(this.f$0, realm);
                break;
        }
    }
}

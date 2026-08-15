package com.ouropro.player.apps;

import androidx.fragment.app.FragmentActivity;
import com.ouropro.player.net.NetworkTask;
import io.realm.RealmResults;
import java.util.List;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BaseActivity$$ExternalSyntheticLambda3 implements NetworkTask.OnCompleteListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ FragmentActivity f$0;
    public final /* synthetic */ RealmResults f$1;
    public final /* synthetic */ List f$2;
    public final /* synthetic */ List f$3;

    public /* synthetic */ BaseActivity$$ExternalSyntheticLambda3(FragmentActivity fragmentActivity, RealmResults realmResults, List list, List list2, int i) {
        this.$r8$classId = i;
        this.f$0 = fragmentActivity;
        this.f$1 = realmResults;
        this.f$2 = list;
        this.f$3 = list2;
    }

    public final void onComplete(Object obj) {
        switch (this.$r8$classId) {
            case 0:
                ((BaseActivity) this.f$0).lambda$getMovieModels$13(this.f$1, this.f$2, this.f$3, (List) obj);
                break;
            default:
                ((BaseTVActivity) this.f$0).lambda$getMovieModels$13(this.f$1, this.f$2, this.f$3, (List) obj);
                break;
        }
    }
}

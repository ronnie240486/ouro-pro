package com.ouropro.player.apps;

import android.accounts.NetworkErrorException;
import com.ouropro.player.net.NetworkTask;
import java.util.List;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BaseActivity$$ExternalSyntheticLambda4 implements NetworkTask.OnExceptionListener, NetworkTask.OnCompleteListener, NetworkTask.OnNetworkUnavailableListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ BaseActivity f$0;

    public /* synthetic */ BaseActivity$$ExternalSyntheticLambda4(BaseActivity baseActivity, int i) {
        this.$r8$classId = i;
        this.f$0 = baseActivity;
    }

    public final void onComplete(Object obj) {
        this.f$0.lambda$fetchM3UItems$3((List) obj);
    }

    public final void onException(Exception exc) {
        switch (this.$r8$classId) {
            case 0:
                this.f$0.lambda$getMovieModels$14(exc);
                break;
            case 1:
                this.f$0.lambda$getChannelModels$9(exc);
                break;
            case 2:
            default:
                this.f$0.lambda$getEpisodeModels$17(exc);
                break;
            case 3:
                this.f$0.lambda$fetchM3UItems$4(exc);
                break;
        }
    }

    public final void onNetworkException(NetworkErrorException networkErrorException) {
        this.f$0.lambda$fetchM3UItems$5(networkErrorException);
    }
}

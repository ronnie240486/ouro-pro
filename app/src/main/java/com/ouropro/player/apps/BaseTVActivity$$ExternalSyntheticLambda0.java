package com.ouropro.player.apps;

import android.accounts.NetworkErrorException;
import com.ouropro.player.net.NetworkTask;
import java.util.List;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BaseTVActivity$$ExternalSyntheticLambda0 implements NetworkTask.OnCompleteListener, NetworkTask.OnExceptionListener, NetworkTask.OnNetworkUnavailableListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ BaseTVActivity f$0;

    public /* synthetic */ BaseTVActivity$$ExternalSyntheticLambda0(BaseTVActivity baseTVActivity, int i) {
        this.$r8$classId = i;
        this.f$0 = baseTVActivity;
    }

    @Override // com.ouropro.player.net.NetworkTask.OnCompleteListener
    public final void onComplete(Object obj) {
        this.f$0.lambda$fetchM3UItems$3((List) obj);
    }

    @Override // com.ouropro.player.net.NetworkTask.OnExceptionListener
    public final void onException(Exception exc) {
        switch (this.$r8$classId) {
            case 1:
                this.f$0.lambda$fetchM3UItems$4(exc);
                break;
            case 2:
            default:
                this.f$0.lambda$getChannelModels$9(exc);
                break;
            case 3:
                this.f$0.lambda$getMovieModels$14(exc);
                break;
            case 4:
                this.f$0.lambda$getEpisodeModels$17(exc);
                break;
        }
    }

    @Override // com.ouropro.player.net.NetworkTask.OnNetworkUnavailableListener
    public final void onNetworkException(NetworkErrorException networkErrorException) {
        this.f$0.lambda$fetchM3UItems$5(networkErrorException);
    }
}

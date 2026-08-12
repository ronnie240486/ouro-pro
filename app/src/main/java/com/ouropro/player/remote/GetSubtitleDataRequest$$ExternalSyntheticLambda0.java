package com.ouropro.player.remote;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONObject;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class GetSubtitleDataRequest$$ExternalSyntheticLambda0 implements Response.Listener, Response.ErrorListener {
    public final /* synthetic */ GetSubtitleDataRequest f$0;

    public /* synthetic */ GetSubtitleDataRequest$$ExternalSyntheticLambda0(GetSubtitleDataRequest getSubtitleDataRequest) {
        this.f$0 = getSubtitleDataRequest;
    }

    public final void onErrorResponse(VolleyError volleyError) {
        this.f$0.lambda$getResponse$1(volleyError);
    }

    public final void onResponse(Object obj) {
        this.f$0.lambda$getResponse$0((JSONObject) obj);
    }
}

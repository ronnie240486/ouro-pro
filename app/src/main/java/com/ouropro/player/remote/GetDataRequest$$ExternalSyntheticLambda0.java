package com.ouropro.player.remote;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONObject;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class GetDataRequest$$ExternalSyntheticLambda0 implements Response.Listener, Response.ErrorListener {
    public final /* synthetic */ GetDataRequest f$0;

    public /* synthetic */ GetDataRequest$$ExternalSyntheticLambda0(GetDataRequest getDataRequest) {
        this.f$0 = getDataRequest;
    }

    public final void onErrorResponse(VolleyError volleyError) {
        this.f$0.lambda$getResponse$1(volleyError);
    }

    public final void onResponse(Object obj) {
        this.f$0.lambda$getResponse$0((JSONObject) obj);
    }
}

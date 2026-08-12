package com.ouropro.player.remote;

import android.content.Context;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import org.androidannotations.api.rest.MediaType;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class GetSubtitleLoginRequest {
    public Context context;
    public OnGetLinkModelListener listener;
    public int requestCode;

    public interface OnGetLinkModelListener {
        void OnGetLinkModelResult(JSONObject jSONObject, int i);
    }

    public GetSubtitleLoginRequest(Context context, int i) {
        this.context = context;
        this.requestCode = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getResponse$0(JSONObject jSONObject) {
        try {
            this.listener.OnGetLinkModelResult(jSONObject, this.requestCode);
        } catch (Exception e) {
            this.listener.OnGetLinkModelResult(null, this.requestCode);
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getResponse$1(VolleyError volleyError) {
        this.listener.OnGetLinkModelResult(null, this.requestCode);
    }

    public void getResponse(JSONObject jSONObject, String str, final String str2) {
        RequestQueue requestQueueNewRequestQueue = Volley.newRequestQueue(this.context);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(str, jSONObject, new GetSubtitleLoginRequest$$ExternalSyntheticLambda0(this), new GetSubtitleLoginRequest$$ExternalSyntheticLambda0(this)) { // from class: com.ouropro.player.remote.GetSubtitleLoginRequest.1
                public Map<String, String> getHeaders() {
                    HashMap map = new HashMap();
                    map.put("Api-Key", str2);
                    map.put("Content-Type", MediaType.APPLICATION_JSON);
                    return map;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1.0f));
            requestQueueNewRequestQueue.add(jsonObjectRequest);
        } catch (Exception unused) {
            this.listener.OnGetLinkModelResult(null, this.requestCode);
        }
    }

    public void setOnGetLinkModelListener(OnGetLinkModelListener onGetLinkModelListener) {
        this.listener = onGetLinkModelListener;
    }
}

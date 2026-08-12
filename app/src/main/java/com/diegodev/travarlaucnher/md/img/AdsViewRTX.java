package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class AdsViewRTX extends WebView {
    private static final String DEFAULT_URL = "https://renciaapp-ldyffp73.manus.space/api/v4/allads.php";
    private Context mContext;

    public AdsViewRTX(Context context) {
        super(context);
        this.mContext = context;
        init(context);
    }

    public AdsViewRTX(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context);
    }

    public AdsViewRTX(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        setLayerType(2, null);
        setWebViewClient(new WebViewClient() { // from class: com.diegodev.travarlaucnher.md.img.AdsViewRTX.1
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/offline/adindex.html");
            }
        });
        String deviceId = CryptoHelper.getDeviceIdentifier(context);
        Map<String, String> headers = new HashMap<>();
        headers.put("device-id", deviceId);
        loadUrl(DEFAULT_URL, headers);
    }
}

package com.rtx.ads;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.rtx.DNS.InternetConnectivityChecker;
import com.rtx.DNS.mConfig;

/* JADX INFO: loaded from: classes2.dex */
public class SportViewRTX extends WebView {
    private static final String DEFAULT_URL = mConfig.mAPI + "allads.php";
    private Context mContext;

    public SportViewRTX(Context context) {
        super(context);
        init(context);
        this.mContext = context;
    }

    public SportViewRTX(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.mContext = context;
    }

    public SportViewRTX(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.mContext = context;
    }

    private void init(Context context) {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        setWebViewClient(new MyWebViewClient());
        if (Build.VERSION.SDK_INT >= 19) {
            setLayerType(2, null);
        } else {
            setLayerType(1, null);
        }
        setWebViewClient(new WebViewClient() { // from class: com.rtx.ads.SportViewRTX.1
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                SportViewRTX.this.loadUrl("file:///android_asset/offline/adindex.html");
            }
        });
        if (InternetConnectivityChecker.isConnectedToInternet(context)) {
            loadUrl(DEFAULT_URL);
        } else {
            loadUrl("file:///android_asset/offline/adindex.html");
        }
    }

    private class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
}

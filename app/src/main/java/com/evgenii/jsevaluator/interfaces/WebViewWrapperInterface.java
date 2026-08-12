package com.evgenii.jsevaluator.interfaces;

import android.webkit.WebView;

/* JADX INFO: loaded from: classes.dex */
public interface WebViewWrapperInterface {
    void destroy();

    WebView getWebView();

    void loadJavaScript(String str);
}

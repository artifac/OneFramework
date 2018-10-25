package com.one.framework.app.web.jsbridge.functions;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONException;
import org.json.JSONObject;

public class FuncClearCache extends JavascriptBridge.Function {

    private Context mContext;

    private WebView mWebView;

    public FuncClearCache(Context context, WebView webView) {
        this.mContext = context;
        this.mWebView = webView;
    }

    @Override
    public JSONObject execute(JSONObject params) {
        CookieSyncManager.createInstance(mContext);  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync();
        mWebView.clearCache(true);
        mContext.deleteDatabase("WebView.db");
        mContext.deleteDatabase("WebViewCache.db");
        mWebView.loadUrl("javascript:window.localStorage.clear()");

        JSONObject result = new JSONObject();
        try {
            result.put("clear_result", "0");
        } catch (JSONException e) {
        }

        return result;
    }
}

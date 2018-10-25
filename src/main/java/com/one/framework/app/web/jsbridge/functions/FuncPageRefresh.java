package com.one.framework.app.web.jsbridge.functions;

import android.webkit.WebView;
import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

public class FuncPageRefresh extends JavascriptBridge.Function {

  private WeakReference<BaseWebView> webVieReference;
  public static final String TAG = "FuncPageRefresh";


  public FuncPageRefresh(WeakReference<BaseWebView> webVieReference) {
    this.webVieReference = webVieReference;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    WebView webView = webVieReference != null ? webVieReference.get() : null;
    if (webView != null) {
      webView.reload();
    }

    return new JSONObject();
  }
}

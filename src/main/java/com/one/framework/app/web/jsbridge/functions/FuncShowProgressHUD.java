package com.one.framework.app.web.jsbridge.functions;

import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

public class FuncShowProgressHUD extends JavascriptBridge.Function {

  private WeakReference<BaseWebView> webVieReference;

  public FuncShowProgressHUD(WeakReference<BaseWebView> webVieReference) {
    this.webVieReference = webVieReference;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    if (params == null) {
      return null;
    }
    String message = params.optString("title");
    BaseWebView webView = webVieReference != null ? webVieReference.get() : null;
    if (webView != null) {
      webView.showLoadingDialog();
    }
    return null;
  }
}

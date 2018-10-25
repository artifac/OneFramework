package com.one.framework.app.web.jsbridge.functions;

import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

public class FuncHideProgressHUD extends JavascriptBridge.Function {

  private WeakReference<BaseWebView> webVieReference;

  public FuncHideProgressHUD(WeakReference<BaseWebView> webVieReference) {
    this.webVieReference = webVieReference;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    BaseWebView webView = webVieReference != null ? webVieReference.get() : null;
    if (webView != null) {
      webView.hideLoadingDialog();
    }
    return null;
  }
}

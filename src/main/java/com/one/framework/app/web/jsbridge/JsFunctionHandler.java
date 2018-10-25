package com.one.framework.app.web.jsbridge;

import android.webkit.WebView;
import com.one.framework.app.web.BaseWebView;
import org.json.JSONObject;

@Deprecated
public class JsFunctionHandler {

  public static final void callHandler(WebView webView, String cmd, JSONObject params,
      JsCallback jsCallback) {

    if (isUrlForbidden(webView.getUrl())) {
      return;
    }

    if (!(webView instanceof BaseWebView)) {
      return;
    }

    JavascriptBridge jsBridge = null; //((BaseWebView) webView).getJavascriptBridge();
    if (jsBridge == null) {
      return;
    }

    JavascriptBridge.Function function = jsBridge.getFunction(cmd);
    if (function == null) {
      return;
    }

    function.setJsCallback(jsCallback);
    JSONObject result = function.execute(params);

    if (jsCallback != null && function.isAutoCallbackJs()) {
      try {
        jsCallback.apply(result);
      } catch (JsCallback.JsCallbackException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 检查 url 是否被禁
   *
   * @param url 调用 js 接口的 h5 地址
   * @return {@code true} 如果该 url 是被禁止的，{@code false} 反之
   */
  private static boolean isUrlForbidden(String url) {
    return false;
  }

}

package com.one.framework.app.web.jsbridge;

import android.webkit.WebView;
import java.lang.ref.WeakReference;
import org.json.JSONException;
import org.json.JSONObject;

public class JsCallback {

  private static final String JS_CALLBACK_FMT = "javascript:%s(%s);";
  private WeakReference<WebView> mWebViewRef;
  private String id;
  private String callback;

  public JsCallback(WebView webView, String id, String callback) {
    mWebViewRef = new WeakReference<>(webView);
    this.id = id;
    this.callback = callback;
  }

  /**
   * 回调 js
   *
   * @param data 回调参数
   */
  public void apply(JSONObject data) throws JsCallbackException {
    WebView webView = mWebViewRef.get();
    if (webView == null) {
      throw new JsCallbackException("the WebView related to the JsCallback has been recycled");
    }

    JSONObject callbackJson = new JSONObject();
    try {
      callbackJson.put("id", id);
      callbackJson.put("errno", 0);
      callbackJson.put("errmsg", "");
      callbackJson.put("result", data);
    } catch (JSONException e) {
    }

    String execJs = String.format(JS_CALLBACK_FMT, callback, callbackJson.toString());
    webView.loadUrl(execJs);
  }

  public static class JsCallbackException extends Exception {

    public JsCallbackException(String msg) {
      super(msg);
    }
  }
}

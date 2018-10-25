package com.one.framework.app.web.jsbridge;

import org.json.JSONObject;

/**
 * <p/>
 * 对 {@link com.one.framework.app.web.jsbridge.JsCallback} 与
 * {@link JsCallback} 的包装，为了兼容两者
 */
public class JsCallbackWraper {

  private JsCallback mJsCallback;

  public JsCallbackWraper(JsCallback jsCallback) {
    mJsCallback = jsCallback;
  }

  /**
   * 执行回调
   *
   * @param data 回调参数
   */
  public void apply(JSONObject data) {
    if (mJsCallback != null) {
      try {
        mJsCallback.apply(data);
      } catch (JsCallback.JsCallbackException e) {
        e.printStackTrace();
      }
      return;
    }

  }

}

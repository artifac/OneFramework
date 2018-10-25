package com.one.framework.app.web.jsbridge.functions;

import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONObject;

public class FuncWebPageClose extends JavascriptBridge.Function {

  public static final String TAG = "FuncWebPageClose";
  private WebActivity mWebActivity;

  public FuncWebPageClose(WebActivity webActivity) {
    mWebActivity = webActivity;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    mWebActivity.finish();
    return new JSONObject();
  }
}

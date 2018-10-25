package com.one.framework.app.web.jsbridge.functions;

import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONException;
import org.json.JSONObject;

public class FuncHideEntrance extends JavascriptBridge.Function {

  public static final String TAG = "FuncHideEntrance";
  private Callback callback;

  public FuncHideEntrance(Callback callback) {
    this.callback = callback;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    if (callback != null) {
      callback.hideEntrance();
    }

    try {
      JSONObject result = new JSONObject();
      result.put("result", new JSONObject());
      return result;
    } catch (JSONException e) {
    }
    return null;
  }

  public interface Callback {

    void hideEntrance();
  }
}

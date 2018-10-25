package com.one.framework.app.web.jsbridge.functions;

import android.text.TextUtils;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONObject;

public class FuncUpdateTitle extends JavascriptBridge.Function {

  public static final String TAG = "FuncUpdateTitle";

  private Callback callback;

  public FuncUpdateTitle(Callback callback) {
    this.callback = callback;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    if (callback != null) {
      try {
        String title =
            TextUtils.isEmpty(params.optString("navi_title", "")) ? params.optString("title", "")
                : params.optString("navi_title", "");
        callback.updateTitle(title);
      } catch (Exception e) {
      }
    }

    return new JSONObject();
  }

  public interface Callback {

    void updateTitle(String title);
  }
}

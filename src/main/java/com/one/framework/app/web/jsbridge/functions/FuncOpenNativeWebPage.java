package com.one.framework.app.web.jsbridge.functions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONObject;

public class FuncOpenNativeWebPage extends JavascriptBridge.Function {

  /**
   * 应用上下文
   */
  private Context mContext;

  public FuncOpenNativeWebPage(Context context) {
    mContext = context;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    if (params != null) {
      String url = params.optString("url");
      String title = params.optString("title");
      WebViewModel model = new WebViewModel();
      model.url = url;
      model.title = title;
      Intent intent = new Intent(mContext, WebActivity.class);
      if (!(mContext instanceof Activity)) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      }
      intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, model);
      mContext.startActivity(intent);
    }

    return null;
  }
}

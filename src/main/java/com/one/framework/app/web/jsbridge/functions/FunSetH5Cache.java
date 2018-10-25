package com.one.framework.app.web.jsbridge.functions;

import android.content.Context;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import com.one.framework.utils.PreferenceUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class FunSetH5Cache extends JavascriptBridge.Function {

  private Context mContext;

  public FunSetH5Cache(Context context) {
    mContext = context;
  }

  @Override
  public JSONObject execute(JSONObject jsonObject) {
    try {
      String native_key = jsonObject.getString("key");
      String native_valueStr = jsonObject.getString("value");
      PreferenceUtil.instance(mContext).putString(native_key, native_valueStr);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}

package com.one.framework.app.web.jsbridge.functions;

import android.content.Context;
import com.one.framework.app.web.jsbridge.JavascriptBridge.Function;
import com.one.framework.utils.PreferenceUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class FunGetH5Cache extends Function {

  private Context mContext;

  public FunGetH5Cache(Context context) {
    mContext = context;
  }

  @Override
  public JSONObject execute(JSONObject jsonObject) {
    String native_value = "[]";
    JSONObject object = new JSONObject();
    try {
      String native_key = jsonObject.getString("key");
      native_value = PreferenceUtil.instance(mContext).getString(native_key);
      object.put("datas", native_value);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return object;
  }
}

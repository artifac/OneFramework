package com.one.framework.app.web.jsbridge.functions;

import android.content.Context;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import com.one.framework.provider.Constant;
import org.json.JSONException;
import org.json.JSONObject;

public class FuncGetUserInfo extends JavascriptBridge.Function {

  private Context mContext;

  public FuncGetUserInfo(Context context) {
    mContext = context;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    JSONObject object = new JSONObject();
    try {
      object.put(Constant.PARAM_PHONE, UserProfile.getInstance(mContext).getMobile());
      object.put(Constant.PARAM_TOKEN, UserProfile.getInstance(mContext).getTokenValue());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return object;
  }
}

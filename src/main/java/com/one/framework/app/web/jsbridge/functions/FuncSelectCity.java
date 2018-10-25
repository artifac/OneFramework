package com.one.framework.app.web.jsbridge.functions;

import android.content.Intent;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.model.FusionBridgeModule;
import com.one.framework.provider.Constant;
import org.json.JSONObject;

public class FuncSelectCity extends FusionBridgeModule.Function {

  WebActivity mWebActivity; //AllBusinessActivity

  public static final String BIZ_STR_ID = "biz_str_id";

  public FuncSelectCity(WebActivity mWebActivity) {
    this.mWebActivity = mWebActivity;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    String bizStr = params.optString("businessId");
    Intent selectCityIntent = new Intent();
    selectCityIntent.putExtra(FuncSelectCity.BIZ_STR_ID, bizStr);
    mWebActivity.setResult(Constant.SELECT_CITY_RESULT, selectCityIntent);
    mWebActivity.finish();
    return null;
  }
}

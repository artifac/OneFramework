package com.one.framework.app.web.jsbridge.functions;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONObject;

/**
 * 优惠券选择页面接收H5回传的couponId
 */
public class FuncCoupon extends JavascriptBridge.Function {

  public static final String KEY_COUPON_ID = "couponID";

  private WebActivity mWebActivity;

  public FuncCoupon(WebActivity webActivity) {
    mWebActivity = webActivity;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    if (params == null) {
      return null;
    }
    String dId = params.optString(KEY_COUPON_ID);
    Intent intent = new Intent();
    intent.putExtra(KEY_COUPON_ID, dId);
    mWebActivity.setResultIntent(intent);
    if (!TextUtils.isEmpty(dId)) {
      mWebActivity.setResult(Activity.RESULT_OK, intent);
      mWebActivity.finish();
    }
    return null;
  }
}

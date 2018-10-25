package com.one.framework.pay.wx.wxapi;

import android.app.Activity;
import com.one.framework.log.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

  @Override
  public void onReq(BaseReq req) {

  }

  @Override
  public void onResp(BaseResp resp) {
//    0	成功	展示成功页面
//    -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//    -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
    Logger.d("Pay", "onPayFinish, errCode = " + resp.errCode);
    if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//      AlertDialog.Builder builder = new AlertDialog.Builder(this);
//      builder.setTitle(R.string.app_tip);
//      builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//      builder.show();
    }
  }
}

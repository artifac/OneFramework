package com.one.framework.pay;

import android.app.Activity;
import com.one.framework.pay.IPay.ISecretFreePay;
import com.one.framework.pay.wx.WxPay;
import java.lang.ref.WeakReference;

public class SecretFreePay implements ISecretFreePay {
  private WeakReference<Activity> mReference;

  public SecretFreePay(Activity activity) {
    mReference = new WeakReference<Activity>(activity);
  }

  @Override
  public void onWxSecretFree(String url) {
    if (mReference.get() != null) {
      WxPay wxPay = new WxPay(mReference.get());
      wxPay.wxSecretFree(url);
    }
  }
}

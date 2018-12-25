package com.one.framework.pay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import com.one.framework.log.Logger;
import com.one.framework.pay.IPay.ISecretFreePay;
import com.one.framework.pay.dialog.PayBottomDlg;
import com.one.framework.pay.dialog.PayBottomDlg.IPayCallback;
import com.one.pay.model.PayInfo;
import com.one.pay.model.PayModel;
import java.lang.ref.SoftReference;

/**
 * Created by ludexiang on 2018/6/21.
 */

public class Pay {

  private SoftReference<Activity> mReference;

  private Pay(Activity activity) {
    mReference = new SoftReference<Activity>(activity);
  }

  public static Pay getInstance(Activity activity) {
    return PayFactory.instance(activity);
  }

  private final static class PayFactory {

    private static Pay sPay;

    public static Pay instance(Activity activity) {
      if (sPay == null) {
        sPay = new Pay(activity);
      }
      return sPay;
    }
  }

  public void showPayBottom(PayModel model, IPayCallback listener) {
    if (mReference.get() == null) {
      return;
    }
    PayBottomDlg payBottomDlg = new PayBottomDlg(mReference.get(), listener, model);
    payBottomDlg.setOnDismissListener(dialog -> {
      // 支付成功 将mContext = null 否则 内存泄露
//        mContext = null;
//        mReference.clear();
    });
    payBottomDlg.show();
  }

  /**
   * 直接发起支付
   * @param info
   * @param listener
   */
  public void pay(PayInfo info, IPayCallback listener) {
    Logger.e("Pay", "Pay ..... " + mReference.get());
    if (mReference.get() == null) {
      return;
    }
    IPay pay = new PayBottomDlg(mReference.get(), listener, info);
    Logger.e("Pay", "Pay ..... " + info.getPayChannel() + " info >>>> " + info);
    switch (info.getPayChannel()) {
      case IPay.PAY_ZFB: {
        pay.onAliPay();
        break;
      }
      case IPay.PAY_WX: {
        pay.onWxPay();
        break;
      }
    }
  }

  /**
   * 免密支付开通
   */
  public void openWxSecretFree(String url) {
    if (mReference.get() == null) {
      return;
    }
    ISecretFreePay wxSecret = new SecretFreePay(mReference.get());
    wxSecret.onWxSecretFree(url);
  }

  public void updatePayBottomList(int position) {
//    payBottomDlg.updatePayList(position);
  }
}

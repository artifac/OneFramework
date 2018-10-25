package com.one.framework.pay;

import android.support.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ludexiang on 2018/6/24.
 */

public interface IPay {
  int PAY_ZFB = 1; // 支付宝
  int PAY_WX = 2;  // 微信
  int PAY_ZSBANK = 3; // 招商银行
  @IntDef({PAY_WX, PAY_ZFB, PAY_ZSBANK})
  @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.SOURCE)
  @interface PayType {}

  void onWxPay();

  void onAliPay();

  interface ISecretFreePay {
    void onWxSecretFree(String url);
  }

}

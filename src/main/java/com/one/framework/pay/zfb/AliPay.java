package com.one.framework.pay.zfb;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import com.alipay.sdk.app.PayTask;
import com.one.framework.pay.dialog.PayBottomDlg;
import com.one.framework.pay.dialog.PayBottomDlg.IPayCallback;
import com.one.framework.pay.util.OrderInfoUtil2_0;
import com.one.framework.pay.util.PayResult;
import com.one.framework.utils.ToastUtils;
import com.one.framework.utils.UIThreadHandler;
import java.lang.ref.SoftReference;
import java.util.Map;

public class AliPay {
  public static final String APP_ID = "2016091400506347";
  public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCnQYW7otbDJ1gLrg4v4hDtwKXZOtiJG6RsX1SOL7+aL6tEm+IF3T+SHQy9dDWIA+89/YT1ID/nRUJvaIqopdtleAxr4ErX5V+DgeT2MJscA8H5sQym6bfMxLV0gfBRlUgs5zBfA6BJjhxocZ3sQZYdUWKmrVzoMrhPqP0hgxIVucFGC+bqa6Qr3qhW3cJWCXOj20mX2pFZXyWpYKDs0jyJfkZXuTtkOa7BvbrdLo5wHyVhmfZPprkD5M3Bu271wKHEYoSQfERQshI2Nwbc5OsGIBTWKaqo8JgBCXX3TWx5Nnt/yjkrN42q1foYqNOZvlzAs4h7yUklIsI0L0t1H89hAgMBAAECggEAP3z6s5/7X7bWkBBw6DIq9OTUz6GrwlfQ0SZapWJwm6mOnIuDf3Bdiiwmwt+S1l+P9/r89kNYnoRG1J1dTOkwX9Ly/Bv2rP8vg27SJ7WJm1e90Pco807yavMcYe5U99Lvw8O6M8bfmCwukmLWbHD094oGJXjXNHjUR8DA3XCwc7gjC2gq619v1isnYxeSNTIEc4wp2AiPUVli8ir6mkYG5zZ2wBqjaXxCw0mGLjB0YVvHeqn6pfaRlwInZKqb16PEyxnJq3fDscNv379BHlHbvcvPkOXZaDvksXDmnf6lp7wE79sVKeYR9Ou7SHUTSTuzXS3e+pQ/wp5RSo9c6VZjhQKBgQDS0Imdk12Ga7c/Z/2nRHecEFEVPGvfKz6TkErUE9u007ebhKAOQVWA+5uR4uR37w0oLjGB9jdi8Jny4mSvxVTGNFDJsrkF/cD98llvSNVx7wQEBbkGfuZW66NdJeh7yXFX6zXWX3UaNJUAyCmjD2qiefVUMrq6pLhPAEXJNZjp+wKBgQDLGuh1c08+UxLIIRLTPhBmX6NMoXdeDiL/CgfeeQb7pvGHM/CAn3Z3xKtRrhnxRkyA1YBV+NWj1fE60FXI2XgxEac2yIyZOdoQEIt+re5rO3s4KyhtxGA2bemm2azv4UgZ1iF4YshO0Sy66P1wDQntaWY/pEAFLK4fQ2x2ZXxpUwKBgE7jElwQ5RKEBFKJy3VoPYn84RqdPZCJUiVN/XJW7ARCpZZgjrRG5oQZoyF4Uh4/KiE7B/Ol+xvfBPzKkvrjUNHKieCwLGR9jFZ69dSa4KR92HbY3V/85JzqGHumz9RikTNhJQhPODbSLl/YhxwpjLG4CYG0agOl2AJYC8U7dTk5AoGBAImu7QSprX93e1T1rrhK630quh25m5zEAo9XsmvrKYOw8SyRVikrcHjSdrzBRE7eNABmO0CMle8hmg0fq/ejDKpe6DboDv/USvH29UlkCSriQK6b+yUmKX7cVbdI+sYGtwTktv3QIS/k9uiiByRNgnxRpgHgi5G2bzc8UquUMpmHAoGBAIaZP+LtscZh4tDz0Ww9ydpkat34nXF4rOPE0+sU0o/NX+Dy0tQ5sXUPRGlAKjInRU3mN4QuTpsFEbB4pQE9RLZtgyeeaB847aDbOSvCIDAY7P0VSyfnFEaW8sAnTUDTKds+pI5IesuhLYJBtAA6EN/Xq6UQsXjPIfFvNCTOjPLc";
  private SoftReference<Activity> mReference;
  public AliPay(Activity activity) {
    mReference = new SoftReference<>(activity);
  }

  public void aliPay(PayBottomDlg dlg, final IPayCallback payListener) {
    if (mReference != null && mReference.get() != null) {
      boolean rsa2 = (RSA2_PRIVATE.length() > 0);
      Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APP_ID, rsa2);
      String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

      String privateKey = RSA2_PRIVATE;
      String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
      final String orderInfo = orderParam + "&" + sign;
      PayTask alipay = new PayTask(mReference.get());
      Map<String, String> result = alipay.payV2(orderInfo, true);
      Log.i("msp", result.toString());

      PayResult payResult = new PayResult(result);
      /**
       对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
       */
      String resultInfo = payResult.getResult();// 同步返回需要验证的信息
      String resultStatus = payResult.getResultStatus();
      // 判断resultStatus 为9000则代表支付成功
      if (TextUtils.equals(resultStatus, "9000")) {
        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
        ToastUtils.toast(mReference.get(), "支付成功");

        dlg.dismiss();
        UIThreadHandler.post(new Runnable() {
          @Override
          public void run() {
            payListener.onPaySuccess();
          }
        });
      } else {
        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
        ToastUtils.toast(mReference.get(), "支付失败");
        UIThreadHandler.post(new Runnable() {
          @Override
          public void run() {
            payListener.onPayFail();
          }
        });
      }
    }
  }
}

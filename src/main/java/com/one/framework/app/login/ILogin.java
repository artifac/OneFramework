package com.one.framework.app.login;

import android.support.annotation.IntDef;
import com.one.framework.net.model.UserInfo;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ludexiang on 2018/6/15.
 */

public interface ILogin {
  int DIALOG = 0;
  int PAGE = 1;

  boolean isLogin();

  void showLogin(@LoginType int type);

  @Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
  @Retention(RetentionPolicy.RUNTIME)
  @IntDef({PAGE, DIALOG})
  @interface LoginType {}

  /**
   * 登录接口
   */
  interface ILoginListener {
    void onLoginSuccess();
    void onLoginFail(String message);
  }

  /**
   * 验证码
   */
  interface ILoginVerifyCode {
    void onSuccess();
    void onFail();
  }

  /**
   * 倒计时
   */
  interface ILoginCountDownTimer {
    void onTick(long millisUntilFinished);
    void onFinish();
  }

  void setLoginListener(ILoginListener listener);
}

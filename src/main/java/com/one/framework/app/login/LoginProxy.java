package com.one.framework.app.login;

import android.content.Context;
import android.os.CountDownTimer;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.one.framework.app.login.ILogin.ILoginCountDownTimer;
import com.one.framework.app.login.ILogin.ILoginListener;
import com.one.framework.app.login.ILogin.ILoginVerifyCode;
import com.one.framework.app.login.UserProfile.User;
import com.one.framework.log.Logger;
import com.one.framework.net.Api;
import com.one.framework.net.model.IMLoginInfo;
import com.one.framework.net.model.LoginModel;
import com.one.framework.net.model.UserInfo;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.PreferenceUtil;
import com.one.framework.utils.ToastUtils;

public class LoginProxy {

  private Context mContext;
  private CountDownTimer countDown;

  public LoginProxy(Context context) {
    mContext = context;
  }

  public void doSms(String phone, ILoginVerifyCode listener) {
    Api.sendSms(phone, new IResponseListener<LoginModel>() {
      @Override
      public void onSuccess(LoginModel login) {
        if (listener != null) {
          listener.onSuccess(login.isNewUser());
        }
      }

      @Override
      public void onFail(int errCode, String message) {
        ToastUtils.toast(mContext, message);
        if (listener != null) {
          listener.onFail();
        }
      }

      @Override
      public void onFinish(LoginModel login) {

      }
    });
  }

  public void doLogin(String phone, String verificationCode, String inviteCode, ILoginListener loginListener) {
    Api.doLogin(phone, verificationCode, inviteCode, new IResponseListener<UserInfo>() {
      @Override
      public void onSuccess(UserInfo userInfo) {
        if (countDown != null) {
          countDown.cancel();
        }
        UserProfile profile = UserProfile.getInstance(mContext);
        User user = profile.new User(userInfo.getMobileNo(), userInfo.getUserId(),
            userInfo.getToken());
        profile.sync(user);

        Api.imLogin(new IResponseListener<IMLoginInfo>() {
          @Override
          public void onSuccess(IMLoginInfo imLoginInfo) {
            LoginInfo info = new LoginInfo(imLoginInfo.getUserName(), imLoginInfo.getPassword());
            Logger.e("ldx", "aaaaaaa  login " + imLoginInfo);
            RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
              @Override
              public void onSuccess(LoginInfo param) {
                Logger.e("IM", "IM login Success");
                PreferenceUtil.instance(mContext).putString("userName", param.getAccount());
                PreferenceUtil.instance(mContext).putString("password", param.getToken());

                NimUIKit.setAccount(param.getAccount());
              }

              @Override
              public void onFailed(int code) {
                Logger.e("IM", "IM login failed");
              }

              @Override
              public void onException(Throwable exception) {
                Logger.e("IM", "IM login exception");
              }
            };
            NIMClient.getService(AuthService.class).login(info).setCallback(callback);
          }

          @Override
          public void onFail(int errCod, String message) {

          }

          @Override
          public void onFinish(IMLoginInfo imLoginInfo) {

          }
        });

        if (loginListener != null) {
          loginListener.onLoginSuccess();
        }
      }

      @Override
      public void onFail(int errCode, String message) {
        if (loginListener != null) {
          loginListener.onLoginFail(message);
        }
      }

      @Override
      public void onFinish(UserInfo userInfo) {
      }
    });
  }

  public void countDown(final ILoginCountDownTimer countDownTimer) {
    countDown = new CountDownTimer(60000, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        countDownTimer.onTick(millisUntilFinished);
      }

      @Override
      public void onFinish() {
        countDownTimer.onFinish();
      }
    };
    countDown.start();
  }
}

package com.one.framework.app.login;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.one.framework.R;
import com.one.framework.app.login.ILogin.ILoginCountDownTimer;
import com.one.framework.app.login.UserProfile.User;
import com.one.framework.app.login.VerificationCodeView.OnCodeFinishListener;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.TripButton;
import com.one.framework.dialog.LoginDialog;
import com.one.framework.log.Logger;
import com.one.framework.net.Api;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.model.IMLoginInfo;
import com.one.framework.net.model.UserInfo;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.PreferenceUtil;
import com.one.framework.utils.ToastUtils;
import com.one.framework.utils.UIUtils;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class Login implements ILogin {


  private Context mContext;
  private String mobilePhone;

  private LoginProxy mLoginProxy;

  private ILoginListener mLoginListener;

  public Login(Context context) {
    mContext = context;
    mLoginProxy = new LoginProxy(context);
  }

  @Override
  public boolean isLogin() {
    return UserProfile.getInstance(mContext).isLogin();
  }

  @Override
  public void showLogin(int type) {
    if (type == DIALOG) {
      loginDialog();
    } else {
      loginPage();
    }
  }

  private void loginDialog() {
    View view = LayoutInflater.from(mContext).inflate(R.layout.one_login_dialog_layout, null);
    LoginDialog.Builder builder = new LoginDialog.Builder(mContext)
        .setContentView(view)
        .setOutsideHide(false);
    final LoginDialog loginDialog = builder.create();
    ImageView close = view.findViewById(R.id.one_login_dlg_close);
    final TextView title = view.findViewById(R.id.one_login_dlg_title);
    final EditText input = view.findViewById(R.id.one_login_input);
    final TextView phoneArea = view.findViewById(R.id.one_login_dlg_phone_area);
    final TripButton next = view.findViewById(R.id.one_login_next);
    final LoadingView loading = view.findViewById(R.id.one_login_dlg_loading);
    final LinearLayout verifiLayout = view.findViewById(R.id.one_login_verification_code_layout);
    final TextView verifiCode = view.findViewById(R.id.one_login_input_verification_code);
    final VerificationCodeView verificationCodeView = view
        .findViewById(R.id.one_login_verification_code);
    final RelativeLayout loginLoading = (RelativeLayout) view
        .findViewById(R.id.one_login_dlg_login_loading_layout);
    final LoadingView loginLoadingView = view.findViewById(R.id.one_login_loading_view);
    verificationCodeView.setOnCodeFinishListener(new OnCodeFinishListener() {
      @Override
      public void onComplete(String content) {
        loginLoading.setVisibility(View.VISIBLE);
        loginLoadingView.setRepeatCount(-1).setConfigWaitTime(5);

        mLoginProxy.doLogin(mobilePhone, content, new ILoginListener() {
          @Override
          public void onLoginSuccess() {
            loginDialog.dismiss();
            if (mLoginListener != null) {
              mLoginListener.onLoginSuccess();
            }
          }

          @Override
          public void onLoginFail(String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            loginLoadingView.release();
            loginLoading.setVisibility(View.GONE);
            if (mLoginListener != null) {
              mLoginListener.onLoginFail(message);
            }
          }
        });
      }
    });
    close.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loginDialog.dismiss();
      }
    });
    next.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        verificationCodeView.clear();
        next.setTripButtonText("");
        loading.setVisibility(View.VISIBLE);
        String phone = input.getText().toString();
        mobilePhone = phone.replace(" ", "");
        sendSms(title, verifiLayout, verifiCode, input, next, loading, phoneArea);
      }
    });
    loginDialog.show();
  }

  private void sendSms(final TextView title, final LinearLayout verifiLayout,
      final TextView verifyCode, final EditText input, final TripButton next,
      final LoadingView loading, final TextView phoneArea) {
    mLoginProxy.doSms(mobilePhone, new ILoginVerifyCode() {
      @Override
      public void onSuccess() {
        title.setText(R.string.one_login_input_verification_code);
        verifiLayout.setVisibility(View.VISIBLE);
        phoneArea.setVisibility(View.INVISIBLE);
        verifyCode.setText(UIUtils.highlight(
            String.format(mContext.getString(R.string.one_login_verification_confirm), mobilePhone),
            Color.parseColor("#f05b48")));
        input.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.GONE);
        next.setTripButtonText(String
            .format(mContext.getString(R.string.one_login_reobtain_verificode_count_down), 10));
        next.setEnabled(false);
        next.setRippleColor(Color.parseColor("#d3d3d3"), Color.WHITE);

        mLoginProxy.countDown(new ILoginCountDownTimer() {
          @Override
          public void onTick(long countDownTime) {
            String time = String.format(mContext.getString(R.string.one_login_reobtain_verificode_count_down), countDownTime / 1000);
            next.setTripButtonText(time);
          }

          @Override
          public void onFinish() {
            next.setEnabled(true);
            next.setTripButtonText(R.string.one_login_reobtain_verificode);
            next.setRippleColor(Color.parseColor("#343d4a"), Color.WHITE);
          }
        });
      }

      @Override
      public void onFail() {
//        try {
//          ToastUtils.toast(mContext, mContext.getString(R.string.one_login_verificode_fail));
//        } catch (Exception e) {
//        }
        Toast.makeText(mContext, mContext.getString(R.string.one_login_verificode_fail),
            Toast.LENGTH_SHORT).show();
        loading.setVisibility(View.GONE);
        next.setTripButtonText(R.string.one_login_next);
      }
    });
  }

  @Override
  public void setLoginListener(ILoginListener listener) {
    mLoginListener = listener;
  }

  private void loginPage() {
    // start Login Activity
  }

}

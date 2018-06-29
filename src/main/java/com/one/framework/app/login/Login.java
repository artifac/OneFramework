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
import com.one.framework.R;
import com.one.framework.app.login.UserProfile.User;
import com.one.framework.app.login.VerificationCodeView.OnCodeFinishListener;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.TripButton;
import com.one.framework.dialog.LoginDialog;
import com.one.framework.net.Api;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.model.UserInfo;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.utils.UIUtils;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class Login implements ILogin {

  private CountDownTimer countDown;
  private Context mContext;
  private String mobilePhone;
  private ILoginListener mLoginListener;

  public Login(Context context) {
    mContext = context;
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
    final TripButton next = view.findViewById(R.id.one_login_next);
    final LoadingView loading = view.findViewById(R.id.one_login_dlg_loading);
    final LinearLayout verifiLayout = view.findViewById(R.id.one_login_verification_code_layout);
    final TextView verifiCode = view.findViewById(R.id.one_login_input_verification_code);
    final VerificationCodeView verificationCodeView = view.findViewById(R.id.one_login_verification_code);
    final RelativeLayout loginLoading = (RelativeLayout) view.findViewById(R.id.one_login_dlg_login_loading_layout);
    final LoadingView loginLoadingView = view.findViewById(R.id.one_login_loading_view);
    verificationCodeView.setOnCodeFinishListener(new OnCodeFinishListener() {
      @Override
      public void onComplete(String content) {
        loginLoading.setVisibility(View.VISIBLE);
        loginLoadingView.setRepeatCount(-1).setConfigWaitTime(5);

        Api.doLogin(mobilePhone, content, new IResponseListener<UserInfo>() {
          @Override
          public void onSuccess(UserInfo userInfo) {
            loginDialog.dismiss();
            if (countDown != null) {
              countDown.cancel();
            }
            UserProfile profile = UserProfile.getInstance(mContext);
            User user = profile.new User(userInfo.getMobileNo(), userInfo.getUserId(),
                userInfo.getToken());
            profile.sync(user);

            if (mLoginListener != null) {
              mLoginListener.onLoginSuccess();
            }
          }

          @Override
          public void onFail(int errCode, UserInfo userInfo) {
            if (mLoginListener != null) {
              mLoginListener.onLoginFail();
            }
          }

          @Override
          public void onFinish(UserInfo userInfo) {
            loginLoadingView.release();
            loginLoading.setVisibility(View.GONE);
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
        next.setTripButtonText("");
        loading.setVisibility(View.VISIBLE);
        String phone = input.getText().toString();
        mobilePhone = phone.replace(" ", "");
        sendSms(title, verifiLayout, verifiCode, input, next, loading);
      }
    });
    loginDialog.show();
  }

  private void sendSms(final TextView title, final LinearLayout verifiLayout,
      final TextView verifiCode, final EditText input, final TripButton next,
      final LoadingView loading) {
    Api.sendSms(mobilePhone, new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
        title.setText(R.string.one_login_input_verification_code);
        verifiLayout.setVisibility(View.VISIBLE);
        verifiCode.setText(UIUtils.highlight(
            String.format(mContext.getString(R.string.one_login_verification_confirm), mobilePhone),
            Color.parseColor("#f05b48")));
        input.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.GONE);
        next.setTripButtonText(String
            .format(mContext.getString(R.string.one_login_reobtain_verificode_count_down), 10));
        next.setEnabled(false);
        next.setRippleColor(Color.parseColor("#d3d3d3"), Color.WHITE);
        countDown = new CountDownTimer(60000, 1000) {
          @Override
          public void onTick(long millisUntilFinished) {
            next.setTripButtonText(
                String.format(mContext.getString(R.string.one_login_reobtain_verificode_count_down),
                    millisUntilFinished / 1000));
          }

          @Override
          public void onFinish() {
            next.setEnabled(true);
            next.setTripButtonText(R.string.one_login_reobtain_verificode);
            next.setRippleColor(Color.parseColor("#343d4a"), Color.WHITE);
          }
        };
        countDown.start();
      }

      @Override
      public void onFail(int errCode, BaseObject baseObject) {
        title.setText(R.string.one_login_input_verification_code);
        verifiLayout.setVisibility(View.VISIBLE);
        verifiCode.setText(UIUtils.highlight(
            String.format(mContext.getString(R.string.one_login_verification_confirm), mobilePhone),
            Color.parseColor("#f05b48")));
        input.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.GONE);
        next.setTripButtonText(String
            .format(mContext.getString(R.string.one_login_reobtain_verificode_count_down), 10));
        next.setEnabled(false);
        next.setRippleColor(Color.parseColor("#d3d3d3"), Color.WHITE);
        countDown = new CountDownTimer(60000, 1000) {
          @Override
          public void onTick(long millisUntilFinished) {
            next.setTripButtonText(
                String.format(mContext.getString(R.string.one_login_reobtain_verificode_count_down),
                    millisUntilFinished / 1000));
          }

          @Override
          public void onFinish() {
            next.setEnabled(true);
            next.setTripButtonText(R.string.one_login_reobtain_verificode);
            next.setRippleColor(Color.parseColor("#343d4a"), Color.WHITE);
          }
        };
        countDown.start();
      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  @Override
  public void setLoginListener(ILoginListener listener) {
    mLoginListener = listener;
  }

  private void loginPage() {

  }

}

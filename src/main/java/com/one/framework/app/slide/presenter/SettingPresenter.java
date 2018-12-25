package com.one.framework.app.slide.presenter;

import android.content.Context;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.slide.ISettingView;
import com.one.framework.log.Logger;
import com.one.framework.net.Api;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.DBUtil;

public class SettingPresenter {
  private ISettingView mView;
  private Context mContext;
  public SettingPresenter(Context context, ISettingView view) {
    mContext = context;
    mView = view;
  }

  public void doLogout() {
    Api.doLogout(UserProfile.getInstance(mContext).getUserId(),
        new IResponseListener<BaseObject>() {
          @Override
          public void onSuccess(BaseObject baseObject) {
          }

          @Override
          public void onFail(int errCod, String errMsg) {
          }

          @Override
          public void onFinish(BaseObject baseObject) {
            UserProfile.getInstance(mContext).logout(); // clear data
            DBUtil.deleteTables(mContext); // clear table
            HomeDataProvider.getInstance().clearOrderDetails();
            NIMClient.getService(AuthService.class).logout();
            mView.logout();
          }
        });
  }
}

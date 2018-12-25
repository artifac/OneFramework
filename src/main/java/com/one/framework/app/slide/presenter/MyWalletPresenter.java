package com.one.framework.app.slide.presenter;

import android.app.Activity;
import com.one.framework.app.slide.IWalletView;
import com.one.framework.net.Api;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.model.MyWalletModel;
import com.one.framework.net.model.WxSecretFreeModel;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.pay.Pay;
import java.lang.ref.WeakReference;
import org.apache.lucene.portmobile.annotations.Weak;

public class MyWalletPresenter {
  private IWalletView mView;
  private WeakReference<Activity> mReference;

  public MyWalletPresenter(Activity activity, IWalletView view) {
    mReference = new WeakReference<Activity>(activity);
    mView = view;
  }

  public void loadData() {
    Api.myWallet(new IResponseListener<MyWalletModel>() {
      @Override
      public void onSuccess(MyWalletModel myWalletModel) {
        mView.updateMyWallet(myWalletModel);
      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(MyWalletModel myWalletModel) {

      }
    });
  }

  /**
   * 开通免密支付
   */
  public void openSecretFree() {
    Api.wxOpenSecretFree(new IResponseListener<WxSecretFreeModel>() {
      @Override
      public void onSuccess(WxSecretFreeModel wxSecretFreeModel) {
        if (mReference.get() != null) {
          Pay.getInstance(mReference.get()).openWxSecretFree(wxSecretFreeModel.getWxAuthUrl());
        }
      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(WxSecretFreeModel wxSecretFreeModel) {

      }
    });
  }

  public void closeSecretFree() {
    Api.wxCloseSecretFree(new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
        // 免密支付关闭

      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }
}

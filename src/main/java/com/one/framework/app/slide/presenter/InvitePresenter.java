package com.one.framework.app.slide.presenter;

import com.one.framework.app.slide.IInviteView;
import com.one.framework.model.InviteModel;
import com.one.framework.net.Api;
import com.one.framework.net.response.IResponseListener;

public class InvitePresenter {

  private IInviteView inviteView;

  public InvitePresenter(IInviteView view) {
    inviteView = view;
  }

  public void invite() {
    Api.inviteFriend(new IResponseListener<InviteModel>() {
      @Override
      public void onSuccess(InviteModel inviteModel) {
        inviteView.updateView(inviteModel);
      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(InviteModel inviteModel) {

      }
    });
  }
}

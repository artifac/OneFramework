package com.one.framework.app.slide.presenter;

import com.one.framework.app.slide.IEmergentView;
import com.one.framework.app.slide.IEmergentView.IContactView;
import com.one.framework.log.Logger;
import com.one.framework.model.AutoShareModel;
import com.one.framework.model.ContactLists;
import com.one.framework.model.ContactModel;
import com.one.framework.net.Api;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;

public class ContactPresenter {

  private IEmergentView mView;
  private IContactView mContactView;

  public ContactPresenter(IContactView contactView) {
    mContactView = contactView;
  }

  public ContactPresenter(IEmergentView contactView) {
    mView = contactView;
  }

  public void addContact(ContactModel model) {
    Api.addContact(model.getName(), model.getPhoneNumber(), new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
        mView.status(baseObject.code, baseObject.dataInt);
      }

      @Override
      public void onFail(int errCod, String message) {
        mView.onError(errCod, message);
      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  public void updateContact(ContactModel model) {
    Api.updateContact(model.getName(), model.getPhoneNumber(), model.getContactId(), new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
        Logger.e("ldx", "baseObject >>>>>>>>>>" + baseObject);
        mView.status(baseObject.code,  baseObject.dataInt);
      }

      @Override
      public void onFail(int errCod, String message) {
        mView.onError(errCod, message);
      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  public void deleteContact(long uid) {
    Api.deleteContact(uid, new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {
        mView.status(baseObject.code, 0);
      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  public void queryContact() {
    Api.queryContact(new IResponseListener<ContactLists>() {
      @Override
      public void onSuccess(ContactLists models) {
        if (models != null && models.getContactLists() != null && !models.getContactLists()
            .isEmpty()) {
          mContactView.fillContacts(models.getContactLists());
        }
      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(ContactLists model) {

      }
    });
  }

  public void autoShareEnable(String from, String to) {
    Api.autoShareEnable(from, to, new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {

      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  public void autoShareDisable() {
    Api.autoShareDisable(new IResponseListener<BaseObject>() {
      @Override
      public void onSuccess(BaseObject baseObject) {

      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(BaseObject baseObject) {

      }
    });
  }

  public void autoShareInfo() {
    Api.autoShareUserInfo(new IResponseListener<AutoShareModel>() {
      @Override
      public void onSuccess(AutoShareModel autoShareModel) {
        mContactView.updateAutoShareTime(autoShareModel.getTimeBegin(), autoShareModel.getTimeEnd());
      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(AutoShareModel autoShareModel) {

      }
    });
  }
}

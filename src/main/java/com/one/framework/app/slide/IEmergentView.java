package com.one.framework.app.slide;

import com.one.framework.model.ContactModel;
import java.util.List;

public interface IEmergentView {
  void status(int code, long contactId);
  void onError(int code, String message);

  interface IContactView {
    void updateAutoShareTime(String from, String to);
    void fillContacts(List<ContactModel> modelList);
    void onError(int code, String message);
  }
}



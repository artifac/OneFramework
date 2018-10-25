package com.one.framework.app.web.jsbridge.functions.image;

public interface ImageCallback {

  void onSuccess(String image);

  void onSuccess(String image, String imageType);

  void onFail();

  void onCancel();

  void onPermissionFail();
}

package com.one.framework.net.response;

import com.one.framework.net.base.BaseObject;

/**
 * Created by ludexiang on 2018/6/8.
 */

public interface IResponseListener<T extends BaseObject> {
  void onSuccess(T t);
  void onFail(T t);
  void onFinish(T t);
}

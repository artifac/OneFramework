package com.one.framework.app.base;


import android.app.Application;
import com.one.framework.app.model.BizInfo;
import com.one.framework.app.model.IBizInfo;

/**
 * Created by ludexiang on 2018/3/26.
 */
public abstract class ApplicationDelegate implements IBizInfo {
  protected Application mApplication;
  protected BizInfo mBizInfo;

  public void attachBaseContext(Application application) {
    mApplication = application;
  }

  public void onCreate(Application application) {
    mApplication = application;
  }

  @Override
  public void setBizInfo(BizInfo info) {
    mBizInfo = info;
  }

  @Override
  public BizInfo getBizInfo() {
    return mBizInfo;
  }
}

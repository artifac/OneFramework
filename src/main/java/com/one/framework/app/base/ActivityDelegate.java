package com.one.framework.app.base;

import android.app.Activity;
import com.one.framework.app.model.BizInfo;
import com.one.framework.app.model.IBizInfo;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class ActivityDelegate implements IBizInfo {

  private Activity mActivity;

  public void onCreate(Activity activity) {

  }

  public void onStart(Activity activity) {

  }

  public void onResume(Activity activity) {

  }

  public void onPause(Activity activity) {

  }

  public void onStop(Activity activity) {

  }

  public void onDestroy(Activity activity) {

  }

  @Override
  public void setBizInfo(BizInfo info) {

  }

  @Override
  public BizInfo getBizInfo() {
    return null;
  }
}

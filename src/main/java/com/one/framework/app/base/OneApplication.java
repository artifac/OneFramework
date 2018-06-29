package com.one.framework.app.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.one.framework.log.Logger;
import com.one.framework.utils.SystemUtils;
import com.one.map.location.LocationProvider;
import com.one.map.view.IMapView;
import java.lang.reflect.Method;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class OneApplication extends MultiDexApplication {

  private Class applicationDelegateClass;
  private Object applicationDelegate;

  private Application appContext;

  private void checkAppDelegate() {
    if (applicationDelegateClass != null) {
      return;
    }
    try {
      applicationDelegateClass = Class.forName("com.one.framework.app.base.OneApplicationDelegate");
      applicationDelegate = applicationDelegateClass.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
    try {
      checkAppDelegate();
      Method method = applicationDelegateClass.getDeclaredMethod("attachBaseContext", Application.class);
      method.setAccessible(true);
      method.invoke(applicationDelegate, this);

//      Api.getInstance(base).request(IRequestConstant.REQUEST_CONFIG, null, new ReqConfigResponse());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    try {
      SystemUtils.init(this);

      checkAppDelegate();
      Method method = applicationDelegateClass.getDeclaredMethod("onCreate", Application.class);
      method.setAccessible(true);
      method.invoke(applicationDelegate, this);
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e("ldx", "ApplicationDelegate onCreate exception");
    }
    LocationProvider.getInstance().buildLocation(this, IMapView.TENCENT);
  }
}

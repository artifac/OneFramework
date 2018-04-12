package com.one.framework.app.base;

import android.app.Application;
import com.one.framework.manager.ApplicationDelegateManager;
import java.lang.reflect.Field;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class OneApplicationDelegate extends ApplicationDelegate {

  private ApplicationDelegateManager appDelegateManager;
  private Application appContext;

  @Override
  public void attachBaseContext(Application application) {
    super.attachBaseContext(application);
    try {
      Class app = OneApplication.class;
      Field field = app.getDeclaredField("appContext");
      field.setAccessible(true);
      field.set(app, application);
    } catch (Exception e) {
      e.printStackTrace();
    }

    appContext = application;
  }

  @Override
  public void onCreate(Application application) {
    super.onCreate(application);
    try {
      Class.forName("com.one.framework.api.annotation.ServiceProvider");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    appDelegateManager = new ApplicationDelegateManager(appContext);
    appDelegateManager.notifyOnCreate();
  }
}

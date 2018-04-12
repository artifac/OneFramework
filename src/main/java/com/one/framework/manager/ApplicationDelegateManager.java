package com.one.framework.manager;


import android.app.Application;
import android.util.Log;
import com.one.framework.app.base.ApplicationDelegate;
import com.one.framework.app.model.BizInfo;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class ApplicationDelegateManager extends AbstractDelegateManager<ApplicationDelegate> {
  private Application application;
  private Set<ApplicationDelegate> appDelegates = new HashSet<>();

  public ApplicationDelegateManager(Application app) {
    application = app;

    loadDelegate(ApplicationDelegate.class, new IDelegateListener<ApplicationDelegate>() {
      @Override
      public void onDelegate(String id, ApplicationDelegate clazz) {
        BizInfo biz = new BizInfo();
        biz.business = id;
        clazz.setBizInfo(biz);

        appDelegates.add(clazz);
      }
    });
  }

  public void notifyOnCreate() {
    Log.e("ldx", "AppDelegates >>>> " + appDelegates);
    for (ApplicationDelegate delegate: appDelegates) {
      delegate.onCreate(application);
    }
  }
}

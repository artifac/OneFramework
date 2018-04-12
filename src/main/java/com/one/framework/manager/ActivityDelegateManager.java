package com.one.framework.manager;

import android.app.Activity;
import com.one.framework.app.base.ActivityDelegate;
import com.one.framework.app.model.BizInfo;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class ActivityDelegateManager extends AbstractDelegateManager<ActivityDelegate> {

  private Set<ActivityDelegate> mActivityDelegates = new HashSet<>();
  private Activity mActivity;

  public ActivityDelegateManager(Activity activity) {
    mActivity = activity;

    loadDelegate(ActivityDelegate.class, new IDelegateListener<ActivityDelegate>() {
      @Override
      public void onDelegate(String id, ActivityDelegate clazz) {
        BizInfo bizInfo = new BizInfo();
        bizInfo.business = id;
        clazz.setBizInfo(bizInfo);

        mActivityDelegates.add(clazz);
      }
    });
  }

  public void notifyOnCreate() {
    for (ActivityDelegate activity : mActivityDelegates) {
      activity.onCreate(mActivity);
    }
  }

  public void notifyOnStart() {
    for (ActivityDelegate activity : mActivityDelegates) {
      activity.onStart(mActivity);
    }
  }

  public void notifyOnResume() {
    for (ActivityDelegate activity : mActivityDelegates) {
      activity.onResume(mActivity);
    }
  }

  public void notifyOnPause() {
    for (ActivityDelegate activity : mActivityDelegates) {
      activity.onPause(mActivity);
    }
  }

  public void notifyOnStop() {
    for (ActivityDelegate activity : mActivityDelegates) {
      activity.onStop(mActivity);
    }
  }

  public void notifyOnDestroy() {
    for (ActivityDelegate activity : mActivityDelegates) {
      activity.onDestroy(mActivity);
    }
  }

}

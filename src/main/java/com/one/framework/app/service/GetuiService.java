package com.one.framework.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.igexin.sdk.GTServiceManager;
import com.one.framework.log.Logger;

/**
 * Created by ludexiang on 2018/7/3.
 */

public class GetuiService extends Service {
  public static final String TAG = GetuiService.class.getName();

  @Override
  public void onCreate() {
    // 该行日志在 noMore 版本去掉
    Logger.d(TAG, TAG + " call -> onCreate -------");

    super.onCreate();
    GTServiceManager.getInstance().onCreate(this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // 该行日志在 noMore 版本去掉
    Logger.d(TAG, TAG + " call -> onStartCommand -------");

    super.onStartCommand(intent, flags, startId);
    return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
  }

  @Override
  public IBinder onBind(Intent intent) {
    // 该行日志在 noMore 版本去掉
    Logger.d(TAG, "onBind -------");
    return GTServiceManager.getInstance().onBind(intent);
  }

  @Override
  public void onDestroy() {
    // 该行日志在 noMore 版本去掉
    Logger.d(TAG, "onDestroy -------");

    super.onDestroy();
    GTServiceManager.getInstance().onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    GTServiceManager.getInstance().onLowMemory();
  }
}

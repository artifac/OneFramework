package com.one.framework.app.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.one.framework.BuildConfig;
import com.one.framework.log.Logger;
import com.one.framework.utils.SystemUtils;
import com.one.map.location.LocationProvider;
import com.one.map.view.IMapView;
import com.one.share.ShareModel;
import com.one.share.ShareSDK;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.bugly.crashreport.CrashReport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class OneApplication extends MultiDexApplication {

  private Class applicationDelegateClass;
  private Object applicationDelegate;

  public static Application appContext;

  //hotfix init need attr
  public interface MsgDisplayListener {
    void handle(String msg);
  }

  public static MsgDisplayListener msgDisplayListener = null;
  public static StringBuilder cacheMsg = new StringBuilder();

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
    MultiDex.install(base);
    // 初始化热修复
    initSophix();
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
    appContext = this;
    SystemUtils.init(this);
//    BlurKit.init(this); // 去掉
    try {
      CrashReport.initCrashReport(getApplicationContext(), "69923719cb", BuildConfig.DEBUG);
      checkAppDelegate();
      Method method = applicationDelegateClass.getDeclaredMethod("onCreate", Application.class);
      method.setAccessible(true);
      method.invoke(applicationDelegate, this);
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e("ldx", "ApplicationDelegate onCreate exception");
    }

    initShareSDK();
    LocationProvider.getInstance().buildLocation(this, IMapView.TENCENT);
    SophixManager.getInstance().queryAndLoadNewPatch();
  }


  private void initSophix() {
    String appVersion = "0.0.0";
    try {
      appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
      ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
      String hotfixId = appInfo.metaData.getString("com.taobao.android.hotfix.IDSECRET");
      String hotfixSecret = appInfo.metaData.getString("com.taobao.android.hotfix.APPSECRET");
      String hotfixRSA = appInfo.metaData.getString("com.taobao.android.hotfix.RSASECRET");
      Logger.e("HOTFIX", "hotfix " + hotfixId + " " + hotfixRSA);
          final SophixManager instance = SophixManager.getInstance();
    instance.setContext(this)
        .setAppVersion(appVersion)
        .setSecretMetaData(hotfixId, hotfixSecret, hotfixRSA)
        .setEnableDebug(true)
        .setEnableFullLog()
        .setPatchLoadStatusStub(new PatchLoadStatusListener() {
          @Override
          public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
            if (code == PatchStatus.CODE_LOAD_SUCCESS) {
              Logger.i("HOTFIX", "sophix load patch success!");
            } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
              // 如果需要在后台重启，建议此处用SharePreference保存状态。
              Logger.i("HOTFIX", "sophix preload patch success. restart app to make effect.");
            }
          }
        }).initialize();
    } catch (Exception e) {
    }

  }

  private void initShareSDK() {
    ApplicationInfo applicationInfo = null;
    try {
      applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }

    String wechatId = applicationInfo.metaData.get("tencentAppID").toString();
    String wechatKey = applicationInfo.metaData.getString("tencentAppKey");
    ShareModel wechat = new ShareModel(ShareModel.WECHAT, wechatId, wechatKey, "");
    ShareModel wechatMoments = new ShareModel(ShareModel.WECHATMOMENTS, wechatId, wechatKey, "");
    List<ShareModel> models = new ArrayList<ShareModel>();
    models.add(wechat);
    models.add(wechatMoments);
    ShareSDK.init(this, models);
  }

}

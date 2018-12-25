package com.one.framework.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.one.framework.R;
import com.one.framework.download.UpdateService;
import com.one.framework.download.UpgradeDialog;
import com.one.framework.net.Api;
import com.one.framework.net.model.DownloadModel;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.Constant;

public class GlobalUtils {

  /**
   * 跳转到登录页面
   */
  public static void login(Activity activity, int requestCode) {
    ComponentName componentName = new ComponentName(activity.getPackageName(),
        "com.one.framework.app.login.LoginActivity");
    Intent intent = new Intent();
    intent.setComponent(componentName);
    if (activity != null) {
      activity.startActivityForResult(intent, requestCode);
    }
  }

  /**
   * 检验应用升级
   */
  public static void requestAppVersion(final Context context) {
    try {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
      Api.appUpgrade(packageInfo.versionName, new IResponseListener<DownloadModel>() {
        @Override
        public void onSuccess(DownloadModel downloadModel) {
          final UpgradeDialog upgrade = new UpgradeDialog(context);
          UpdateService.setUpdateProgressListener(upgrade);
          upgrade.updateUI("版本升级", downloadModel.getDescription())
              .forceUpgrade(false).upgradeListener(
              v -> {
                UpdateService.Builder.create(downloadModel.getDownloadUrl())
                    .setStoreDir(Constant.APP_DOWNLOAD_FILE)
                    .setUpdateToVersion(downloadModel.getVersion())
                    .setDownloadSuccessNotificationFlag(Notification.DEFAULT_ALL)
                    .setDownloadErrorNotificationFlag(Notification.DEFAULT_ALL)
                    .setIsSendBroadcast(true)
                    .setIcoResId(R.drawable.ic_launcher)
                    .build(context);

//                    ToastUtils.toast(context, "正在后台下载");
                //通过浏览器去下载APK
                //InstallUtils.installAPKWithBrower(mContext, url);
              });
          upgrade.show();
        }

        @Override
        public void onFail(int errCod, String message) {

        }

        @Override
        public void onFinish(DownloadModel baseObject) {

        }
      });
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
  }

}

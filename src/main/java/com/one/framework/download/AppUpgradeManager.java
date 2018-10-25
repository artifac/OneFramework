package com.one.framework.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;
import com.one.framework.net.model.AppConfig;
import java.io.File;

public class AppUpgradeManager {
  private volatile static AppUpgradeManager sAppUpgradeManager;
  private DownloadManager downloader;
  private Context appContext;
  private NotificationClickReceiver mNotificationClickReceiver;
  private DownloadReceiver mDownloaderReceiver;
  private String apkName = "APP";//AppConfig.APP_NAME;
  //apk下载文件的路径
  private String downloadApkPath;
  //   服务器返回的版本信息
  private Version latestVersion;

  public AppUpgradeManager(Context context, Version version) {
    appContext = context.getApplicationContext();
    latestVersion = version;
    mDownloaderReceiver = new DownloadReceiver();
    mNotificationClickReceiver = new NotificationClickReceiver();
  }

  public static AppUpgradeManager getInstance(Context context, Version version) {
    if (sAppUpgradeManager == null) {
      synchronized (AppUpgradeManager.class) {
        if (sAppUpgradeManager == null) {
          sAppUpgradeManager = new AppUpgradeManager(context, version);
        }
      }
    }
    return sAppUpgradeManager;
  }

  public void startDown() {
    //确定apk下载的绝对路径 todo
    String dirPath = "";//AppConfig.DEFAULT_SAVE_FILE_PATH_PUBLIC;
    //AppConfig.DEFAULT_SAVE_FILE_PATH_PUBLIC=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    dirPath = dirPath.endsWith(File.separator) ? dirPath : dirPath + File.separator;
    downloadApkPath = dirPath + apkName;

    //先检查本地是否已经有需要升级版本的安装包，如有就不需要再下载
    File targetApkFile = new File(downloadApkPath);
    if (targetApkFile.exists()) {
      PackageManager pm = appContext.getPackageManager();
      PackageInfo info = pm.getPackageArchiveInfo(downloadApkPath, PackageManager.GET_ACTIVITIES);
      if (info != null) {
        String versionCode = String.valueOf(info.versionCode);
        //比较已下载到本地的apk安装包，与服务器上apk安装包的版本号是否一致
        if (String.valueOf(latestVersion.getVersionCode()).equals(versionCode)) {
          installApk();
          return;
        }
      }
    }
    //要检查本地是否有安装包，有则删除重新下
    File apkFile = new File(downloadApkPath);
    if (apkFile.exists()) {
      apkFile.delete();
    }

    if (downloader == null) {
      downloader = (DownloadManager) appContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }
    //开始下载
    DownloadManager.Query query = new DownloadManager.Query();
    long downloadTaskId = 0;//TDevice.getDownloadTaskId(appContext);
    query.setFilterById(downloadTaskId);
    Cursor cur = downloader.query(query);
    // 检查下载任务是否已经存在
    if (cur.moveToFirst()) {
      int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
      int status = cur.getInt(columnIndex);
      if (DownloadManager.STATUS_PENDING == status || DownloadManager.STATUS_RUNNING == status || DownloadManager.STATUS_PAUSED == status) {
        cur.close();
        Toast.makeText(appContext, "更新任务已在后台进行中，无需重复更新", Toast.LENGTH_LONG).show();
        return;
      }
    }
    cur.close();
    DownloadManager.Request task = new DownloadManager.Request(Uri.parse(latestVersion.getDownloadUrl()));
    //定制Notification的样式
    String title = "最新版本:" + latestVersion.getVersionCode();
    task.setTitle(title);
    task.setDescription("本次更新:\n1.增强系统稳定性\n2.修复已知bug");
    task.setVisibleInDownloadsUi(true);
    //设置是否允许手机在漫游状态下下载
    //task.setAllowedOverRoaming(false);
    //限定在WiFi下进行下载
    //task.setAllowedNetworkTypes(Request.NETWORK_WIFI);
    task.setMimeType("application/vnd.android.package-archive");
    // 在通知栏通知下载中和下载完成
    // 下载完成后该Notification才会被显示
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      // 3.0(11)以后才有该方法
      //在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，直到用户点击该Notification或者消除该Notification
      task.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }
    // 可能无法创建Download文件夹，如无sdcard情况，系统会默认将路径设置为/data/data/com.android.providers.downloads/cache/xxx.apk
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      task.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);
    }
// 自定义文件路径
//task.setDestinationUri()
    downloadTaskId = downloader.enqueue(task);
    //TDevice SharedPreferences封装类
//    TDevice.saveDownloadTaskId(appContext, downloadTaskId);
    //注册下载完成广播
    appContext.registerReceiver(mDownloaderReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    appContext.registerReceiver(mNotificationClickReceiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));

  }

  private void installApk() {
    if (TextUtils.isEmpty(downloadApkPath)) {
      Toast.makeText(appContext, "APP安装文件不存在或已损坏", Toast.LENGTH_LONG).show();
      return;
    }
    File apkFile = new File(Uri.parse(downloadApkPath).getPath());
    if (!apkFile.exists()) {
      Toast.makeText(appContext, "APP安装文件不存在或已损坏", Toast.LENGTH_LONG).show();
      return;
    }

    Intent intent = new Intent(Intent.ACTION_VIEW);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      Uri contentUri = FileProvider.getUriForFile(appContext, "net.xxx.app.provider", apkFile);
      intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
    } else {
      intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    appContext.startActivity(intent);
  }

  /**
   * 下载完成的广播
   */
  class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (downloader == null) {
        return;
      }
      long completeId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
      long downloadTaskId = 0;//TDevice.getDownloadTaskId(context);
      if (completeId != downloadTaskId) {
        return;
      }

      DownloadManager.Query query = new DownloadManager.Query();
      query.setFilterById(downloadTaskId);
      Cursor cur = downloader.query(query);
      if (!cur.moveToFirst()) {
        return;
      }

      int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
      if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
        installApk();
      } else {
        Toast.makeText(appContext, "下载App最新版本失败!", Toast.LENGTH_LONG).show();
      }
      // 下载任务已经完成，清除
//      TDevice.removeDownloadTaskId(context);
      cur.close();
    }

  }

  /**
   * 点击通知栏下载项目，下载完成前点击都会进来，下载完成后点击不会进来。
   */
  public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      long[] completeIds = intent.getLongArrayExtra(
          DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
      //正在下载的任务ID
      long downloadTaskId = 0;//TDevice.getDownloadTaskId(context);
      if (completeIds == null || completeIds.length <= 0) {
        openDownloadsPage(appContext);
        return;
      }

      for (long completeId : completeIds) {
        if (completeId == downloadTaskId) {
          openDownloadsPage(appContext);
          break;
        }
      }
    }

    /**
     * Open the Activity which shows a list of all downloads.
     *
     * @param context 上下文
     */
    private void openDownloadsPage(Context context) {
      Intent pageView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
      pageView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(pageView);
    }
  }

  public class Version {
    int code;
    int getVersionCode() {
      return code;
    }
    String getDownloadUrl() {
      return "";
    }
  }
}

package com.one.framework.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.one.framework.R;
import com.one.framework.app.base.OneApplication;
import com.one.framework.log.Logger;
import com.one.framework.utils.ToastUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateService extends Service {

  public static final String TAG = "UpdateService";
  public static final String ACTION = "com.one.trip.UPDATE_APP";
  public static final String STATUS = "status";
  public static final String PROGRESS = "progress";
  public static boolean DEBUG = true;

  //下载大小通知频率
  public static final int UPDATE_NUMBER_SIZE = 1;
  public static final int DEFAULT_RES_ID = -1;

  public static final int UPDATE_PROGRESS_STATUS = 0;
  public static final int UPDATE_ERROR_STATUS = -1;
  public static final int UPDATE_SUCCESS_STATUS = 1;

  //params
  private static final String URL = "downloadUrl";
  private static final String ICO_RES_ID = "icoResId";
  private static final String ICO_SMALL_RES_ID = "icoSmallResId";
  private static final String UPDATE_PROGRESS = "updateProgress";
  private static final String STORE_DIR = "storeDir";
  private static final String DOWNLOAD_NOTIFICATION_FLAG = "downloadNotificationFlag";
  private static final String DOWNLOAD_SUCCESS_NOTIFICATION_FLAG = "downloadSuccessNotificationFlag";
  private static final String DOWNLOAD_ERROR_NOTIFICATION_FLAG = "downloadErrorNotificationFlag";
  private static final String IS_SEND_BROADCAST = "isSendBroadcast";
  private static final String UPDATE_TO_VERSION = "updateToVersion";


  private String downloadUrl;
  private int icoResId;             //default app ico
  private int icoSmallResId;
  private int updateProgress;   //update notification progress when it add number
  private String storeDir;          //default sdcard/Android/package/update
  private int downloadNotificationFlag;
  private int downloadSuccessNotificationFlag;
  private int downloadErrorNotificationFlag;
  private boolean isSendBroadcast;
  private String updateToVersion;

  private String updateChannelId = "updateChannel";
  private CharSequence updateName = "Update";

  private static UpdateProgressListener updateProgressListener;
  private LocalBinder localBinder = new LocalBinder();

  /**
   * Class used for the client Binder.
   */
  public class LocalBinder extends Binder {

    /**
     * set update progress call back
     */
    public void setUpdateProgressListener(UpdateProgressListener listener) {
      UpdateService.this.setUpdateProgressListener(listener);
    }
  }

  private boolean startDownload;//开始下载
  private int lastProgressNumber;
  private NotificationCompat.Builder builder;
  private NotificationManager manager;
  private int notifyId;
  private String appName;
  private LocalBroadcastManager localBroadcastManager;
  private Intent localIntent;
  private DownloadApk downloadApkTask;

  private PackageChangeBroadcast packageChangeBroadcast;

  /**
   * whether debug
   */
  public static void debug() {
    DEBUG = true;
  }

  private static Intent installIntent(String path) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      //特别特别 注意这里的包名,必须和你应用的包名保持一致,不然下载了也会安装不了,程序闪退!!!
      Uri contentUri = FileProvider
          .getUriForFile(OneApplication.appContext, "com.one.trip.FileProvider", new File(path));
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
    } else {
      Uri uri = Uri.fromFile(new File(path));
      intent.setDataAndType(uri, "application/vnd.android.package-archive");
    }
    return intent;
  }

  private static Intent webLauncher(String downloadUrl) {
    Uri download = Uri.parse(downloadUrl);
    Intent intent = new Intent(Intent.ACTION_VIEW, download);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }

  private static String getSaveFileName(UpdateService service) {
    if (service == null) {
      return "noName.apk";
    }
    String downloadUrl = service.downloadUrl;
    if (downloadUrl == null || TextUtils.isEmpty(downloadUrl)) {
      return "noName.apk";
    }
    if (downloadUrl.endsWith(".apk")) {
      return downloadUrl.substring(downloadUrl.lastIndexOf("/"));
    } else {
      StringBuilder builder = new StringBuilder();
      return builder.append("App_UPDATE_TO_").append(service.updateToVersion).append(".apk")
          .toString();
    }
  }

  /**
   * 文件下载的位置
   */
  private static File getDownloadDir(UpdateService service) {
    File downloadDir;
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      if (!TextUtils.isEmpty(service.storeDir)) {
        StringBuilder parentBuilder = new StringBuilder();
        parentBuilder.append(service.getPackageName()).append(File.separator)
            .append(service.storeDir);
        downloadDir = new File(Environment.getExternalStorageDirectory(), parentBuilder.toString());
      } else {
        downloadDir = new File(service.getExternalCacheDir(), "update");
      }
    } else {
      downloadDir = new File(service.getCacheDir(), "update");
    }
    if (!downloadDir.exists()) {
      downloadDir.mkdirs();
    }
    return downloadDir;
  }


  @Override
  public void onCreate() {
    super.onCreate();
    appName = getApplicationName();
    packageChangeBroadcast = new PackageChangeBroadcast();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_PACKAGE_ADDED);
    filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    filter.addDataScheme("package");
    registerReceiver(packageChangeBroadcast, filter);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (!startDownload && intent != null) {
      startDownload = true;
      downloadUrl = intent.getStringExtra(URL);
      icoResId = intent.getIntExtra(ICO_RES_ID, DEFAULT_RES_ID);
      icoSmallResId = intent.getIntExtra(ICO_SMALL_RES_ID, DEFAULT_RES_ID);
      storeDir = intent.getStringExtra(STORE_DIR);
      updateProgress = intent.getIntExtra(UPDATE_PROGRESS, UPDATE_NUMBER_SIZE);
      downloadNotificationFlag = intent.getIntExtra(DOWNLOAD_NOTIFICATION_FLAG, 0);
      downloadErrorNotificationFlag = intent.getIntExtra(DOWNLOAD_ERROR_NOTIFICATION_FLAG, 0);
      downloadSuccessNotificationFlag = intent.getIntExtra(DOWNLOAD_SUCCESS_NOTIFICATION_FLAG, 0);
      isSendBroadcast = intent.getBooleanExtra(IS_SEND_BROADCAST, false);
      updateToVersion = intent.getStringExtra(UPDATE_TO_VERSION);

      if (DEBUG) {
        Logger.d(TAG, "downloadUrl: " + downloadUrl);
        Logger.d(TAG, "icoResId: " + icoResId);
        Logger.d(TAG, "icoSmallResId: " + icoSmallResId);
        Logger.d(TAG, "storeDir: " + storeDir);
        Logger.d(TAG, "updateProgress: " + updateProgress);
        Logger.d(TAG, "downloadNotificationFlag: " + downloadNotificationFlag);
        Logger.d(TAG, "downloadErrorNotificationFlag: " + downloadErrorNotificationFlag);
        Logger.d(TAG, "downloadSuccessNotificationFlag: " + downloadSuccessNotificationFlag);
        Logger.d(TAG, "isSendBroadcast: " + isSendBroadcast);
      }

      notifyId = startId;
      buildNotification();
      buildBroadcast();
      downloadApkTask = new DownloadApk(this);
      downloadApkTask.execute(downloadUrl);
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return localBinder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return true;
  }

  public static void setUpdateProgressListener(UpdateProgressListener listener) {
    updateProgressListener = listener;
  }

  @Override
  public void onDestroy() {
    if (downloadApkTask != null) {
      downloadApkTask.cancel(true);
    }

    if (updateProgressListener != null) {
      updateProgressListener = null;
    }
    localIntent = null;
    builder = null;

    unregisterReceiver(packageChangeBroadcast);
    if (VERSION.SDK_INT >= VERSION_CODES.O && manager != null) {
      manager.deleteNotificationChannel(updateChannelId);
    }

    super.onDestroy();
  }

  public String getApplicationName() {
    PackageManager packageManager = null;
    ApplicationInfo applicationInfo;
    try {
      packageManager = getApplicationContext().getPackageManager();
      applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      applicationInfo = null;
    }
    String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
    return applicationName;
  }

  private void buildBroadcast() {
    if (!isSendBroadcast) {
      return;
    }
    localBroadcastManager = LocalBroadcastManager.getInstance(this);
    localIntent = new Intent(ACTION);
  }

  private void sendLocalBroadcast(int status, int progress) {
    if (!isSendBroadcast || localIntent == null) {
      return;
    }
    localIntent.putExtra(STATUS, status);
    localIntent.putExtra(PROGRESS, progress);
    localBroadcastManager.sendBroadcast(localIntent);
  }

  private void buildNotification() {
    manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    if (VERSION.SDK_INT > VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(updateChannelId, updateName,
          NotificationManager.IMPORTANCE_DEFAULT);
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.BLUE);
      notificationChannel.enableVibration(true);
      manager.createNotificationChannel(notificationChannel);
    }
    builder = new NotificationCompat.Builder(this, updateChannelId);
    builder.setContentTitle(getString(R.string.one_download_start))
        .setWhen(System.currentTimeMillis())
        .setProgress(100, 1, false)
        .setSmallIcon(icoSmallResId)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), icoResId))
        .setDefaults(downloadNotificationFlag);

    manager.notify(notifyId, builder.build());

  }

  private void start() {
    builder.setContentTitle(appName);
    builder.setContentText(getString(R.string.one_download_start));
    manager.notify(notifyId, builder.build());
    sendLocalBroadcast(UPDATE_PROGRESS_STATUS, 1);
    if (updateProgressListener != null) {
      updateProgressListener.start();
    }
  }

  /**
   * @param progress download percent , max 100
   */
  private void update(int progress) {
    if (progress - lastProgressNumber > updateProgress) {
      lastProgressNumber = progress;
      builder.setProgress(100, progress, false);
      builder.setContentText("" + progress + "%");
      manager.notify(notifyId, builder.build());
      sendLocalBroadcast(UPDATE_PROGRESS_STATUS, progress);
      if (updateProgressListener != null) {
        updateProgressListener.update(progress);
      }
    }
  }

  private void success(String path) {
    builder.setProgress(0, 0, false);
    builder.setContentText(getString(R.string.one_download_success));
    Intent i = installIntent(path);
    PendingIntent intent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(intent);
    builder.setDefaults(downloadSuccessNotificationFlag);
    Notification n = builder.build();
    n.contentIntent = intent;
    manager.notify(notifyId, n);
    sendLocalBroadcast(UPDATE_SUCCESS_STATUS, 100);
    if (updateProgressListener != null) {
      updateProgressListener.success();
    }
    startActivity(i);
    stopSelf();
  }

  private void error() {
    Intent i = webLauncher(downloadUrl);
    PendingIntent intent = PendingIntent.getActivity(this, 0, i,
        PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentText(getString(R.string.one_download_error));
    builder.setContentIntent(intent);
    builder.setProgress(0, 0, false);
    builder.setDefaults(downloadErrorNotificationFlag);
    Notification n = builder.build();
    n.contentIntent = intent;
    manager.notify(notifyId, n);
    sendLocalBroadcast(UPDATE_ERROR_STATUS, -1);
    if (updateProgressListener != null) {
      updateProgressListener.error();
    }
    stopSelf();
  }

  private static class DownloadApk extends AsyncTask<String, Integer, String> {

    private WeakReference<UpdateService> updateServiceWeakReference;

    public DownloadApk(UpdateService service) {
      updateServiceWeakReference = new WeakReference<>(service);
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      UpdateService service = updateServiceWeakReference.get();
      if (service != null) {
        service.start();
      }
    }

    @Override
    protected String doInBackground(String... params) {

      final String downloadUrl = params[0];

      final File file = new File(UpdateService.getDownloadDir(updateServiceWeakReference.get()),
          UpdateService.getSaveFileName(updateServiceWeakReference.get()));
      if (DEBUG) {
        Logger.d(TAG, "download url is " + downloadUrl);
        Logger.d(TAG, "download apk cache at " + file.getAbsolutePath());
      }
      File dir = file.getParentFile();
      Logger.e(TAG, "DIR isExist >>> " + dir.exists());
      if (!dir.exists()) {
        boolean createDIR = dir.mkdirs();
        Logger.e(TAG, "createDIR >>> " + createDIR);
      }

      HttpURLConnection httpConnection = null;
      InputStream is = null;
      FileOutputStream fos = null;
      int updateTotalSize;
      java.net.URL url;
      try {
        url = new URL(downloadUrl);
        httpConnection = (HttpURLConnection) url.openConnection();

        if (DEBUG) {
          Logger.d(TAG, "download status code: " + httpConnection.getResponseCode());
        }

        if (httpConnection.getResponseCode() == 302) {
          String location = httpConnection.getHeaderField("Location");
          if (DEBUG) {
            Logger.d(TAG, "download redirect: " + location);
          }
          url = new URL(location);
          httpConnection = (HttpURLConnection) url.openConnection();
        }

        if (httpConnection.getResponseCode() != 200) {
          return null;
        }

        if (DEBUG) {
          Logger.d(TAG, "download status code: " + httpConnection.getResponseCode());
        }

        httpConnection.setConnectTimeout(20000);
        httpConnection.setReadTimeout(20000);
        httpConnection.setRequestMethod("GET");
        httpConnection.setInstanceFollowRedirects(false);
        httpConnection.connect();

        updateTotalSize = httpConnection.getContentLength();
        if (DEBUG) {
          Logger.d(TAG, "download File size: " + updateTotalSize);
        }

        if (file.exists()) {
          if (updateTotalSize == file.length()) {
            // 下载完成
            return file.getAbsolutePath();
          } else {
            file.delete();
          }
        }
        file.createNewFile();
        is = httpConnection.getInputStream();
        fos = new FileOutputStream(file, false);
        byte buffer[] = new byte[4096];

        int readSize = 0;
        double currentSize = 0;

        while ((readSize = is.read(buffer)) > 0) {
          fos.write(buffer, 0, readSize);
          currentSize += readSize;
          publishProgress((int) (currentSize * 100 / updateTotalSize));
        }
        // download success
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      } finally {
        if (httpConnection != null) {
          httpConnection.disconnect();
        }
        if (is != null) {
          try {
            is.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      return file.getAbsolutePath();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
      super.onProgressUpdate(values);
      if (DEBUG) {
        Logger.d(TAG, "current progress is " + values[0]);
      }
      UpdateService service = updateServiceWeakReference.get();
      if (service != null) {
        service.update(values[0]);
      }
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
      UpdateService service = updateServiceWeakReference.get();
      if (service != null) {
        if (s != null) {
          service.success(s);
        } else {
          service.error();
        }
      }
    }
  }


  /**
   * a builder class helper use UpdateService
   */
  public static class Builder {

    private String downloadUrl;
    private int icoResId = DEFAULT_RES_ID;             //default app ico
    private int icoSmallResId = DEFAULT_RES_ID;
    private int updateProgress = UPDATE_NUMBER_SIZE;   //update notification progress when it add number
    private String storeDir;          //default sdcard/Android/package/update
    private int downloadNotificationFlag;
    private int downloadSuccessNotificationFlag;
    private int downloadErrorNotificationFlag;
    private boolean isSendBroadcast;
    private String updateToVersion;

    protected Builder(String downloadUrl) {
      this.downloadUrl = downloadUrl;
    }

    public static Builder create(String downloadUrl) {
      if (downloadUrl == null) {
        throw new NullPointerException("downloadUrl == null");
      }
      return new Builder(downloadUrl);
    }

    public String getDownloadUrl() {
      return downloadUrl;
    }

    public int getIcoResId() {
      return icoResId;
    }

    public Builder setIcoResId(int icoResId) {
      this.icoResId = icoResId;
      return this;
    }

    public Builder setUpdateToVersion(String newVersion) {
      updateToVersion = newVersion;
      return this;
    }

    public String getUpdateToVersion() {
      return updateToVersion;
    }

    public int getIcoSmallResId() {
      return icoSmallResId;
    }

    public Builder setIcoSmallResId(int icoSmallResId) {
      this.icoSmallResId = icoSmallResId;
      return this;
    }

    public int getUpdateProgress() {
      return updateProgress;
    }

    public Builder setUpdateProgress(int updateProgress) {
      if (updateProgress < 1) {
        throw new IllegalArgumentException("updateProgress < 1");
      }
      this.updateProgress = updateProgress;
      return this;
    }

    public String getStoreDir() {
      return storeDir;
    }

    public Builder setStoreDir(String storeDir) {
      this.storeDir = storeDir;
      return this;
    }

    public int getDownloadNotificationFlag() {
      return downloadNotificationFlag;
    }

    public Builder setDownloadNotificationFlag(int downloadNotificationFlag) {
      this.downloadNotificationFlag = downloadNotificationFlag;
      return this;
    }

    public int getDownloadSuccessNotificationFlag() {
      return downloadSuccessNotificationFlag;
    }

    public Builder setDownloadSuccessNotificationFlag(int downloadSuccessNotificationFlag) {//标记等于-1
      this.downloadSuccessNotificationFlag = downloadSuccessNotificationFlag;
      return this;
    }

    public int getDownloadErrorNotificationFlag() {
      return downloadErrorNotificationFlag;
    }

    public Builder setDownloadErrorNotificationFlag(int downloadErrorNotificationFlag) {//标记-1
      this.downloadErrorNotificationFlag = downloadErrorNotificationFlag;
      return this;
    }

    public boolean isSendBroadcast() {
      return isSendBroadcast;
    }

    public Builder setIsSendBroadcast(boolean isSendBroadcast) {
      this.isSendBroadcast = isSendBroadcast;
      return this;
    }

    public Builder build(Context context) {
      if (context == null) {
        throw new NullPointerException("context == null");
      }
      Intent intent = new Intent();//Intent { cmp=com.wcyq.gangrong/.utils.UpdateService }
      intent.setClass(context, UpdateService.class);
      intent.putExtra(URL, downloadUrl);

      if (icoResId == DEFAULT_RES_ID) {//进来了
        icoResId = getIcon(context);
      }

      if (icoSmallResId == DEFAULT_RES_ID) {//进来了
        icoSmallResId = icoResId;
      }
      intent.putExtra(ICO_RES_ID, icoResId);
      intent.putExtra(STORE_DIR, storeDir);
      intent.putExtra(ICO_SMALL_RES_ID, icoSmallResId);
      intent.putExtra(UPDATE_PROGRESS, updateProgress);
      intent.putExtra(DOWNLOAD_NOTIFICATION_FLAG, downloadNotificationFlag);
      intent.putExtra(DOWNLOAD_SUCCESS_NOTIFICATION_FLAG, downloadSuccessNotificationFlag);
      intent.putExtra(DOWNLOAD_ERROR_NOTIFICATION_FLAG, downloadErrorNotificationFlag);
      intent.putExtra(IS_SEND_BROADCAST, isSendBroadcast);
      intent.putExtra(UPDATE_TO_VERSION, updateToVersion);
      context.startService(intent);

      return this;
    }

    private int getIcon(Context context) {
      final PackageManager packageManager = context.getPackageManager();
      ApplicationInfo appInfo = null;
      try {
        appInfo = packageManager
            .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
      if (appInfo != null) {
        return appInfo.icon;
      }
      return 0;
    }
  }

  /**
   * apk 安装之后删除安装包
   */
  public class PackageChangeBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Logger.e(TAG, "packageBroadcast action >> " + intent.getAction());
      if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
        if (removeFile()) {
          ToastUtils.toast(context, getString(R.string.one_download_remove_apk_file));
        }
      }

      if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
        if (removeFile()) {
          ToastUtils.toast(context, getString(R.string.one_download_remove_apk_file));
        }
      }

      if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
        if (removeFile()) {
          ToastUtils.toast(context, getString(R.string.one_download_remove_apk_file));
        }
      }
    }
  }

  public boolean removeFile() {
    final File file = new File(storeDir, UpdateService.getSaveFileName(this));
    if (file != null && file.exists()) {
      return file.delete();
    }
    return false;
  }
}


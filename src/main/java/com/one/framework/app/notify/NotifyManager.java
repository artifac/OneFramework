package com.one.framework.app.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;
import com.one.framework.R;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.log.Logger;
import com.one.framework.push.PushMsgModel;
import com.trip.taxi.utils.H5Page;

/**
 * Created by ludexiang on 2018/7/3.
 */

public class NotifyManager {

  private static NotifyManager sManager;
  private Context mContext;
  private String CHANNELID = "CHANNELID";
  private String TAG = NotifyManager.class.getSimpleName();

  private NotifyManager(Context context) {
    mContext = context;
  }

  public static NotifyManager getManager(Context context) {
    synchronized (NotifyManager.class) {
      if (sManager == null) {
        sManager = new NotifyManager(context);
        return sManager;
      }
      return sManager;
    }
  }

  public Intent buildIntent(Context context, PushMsgModel model) {
    Intent intent;
    String deepLink = model.getDeepLink();
    if (URLUtil.isNetworkUrl(deepLink)) {
      WebViewModel webViewModel = new WebViewModel();
      webViewModel.title = model.getTitle();
      webViewModel.url = H5Page.INSTANCE.createDeeplink(context, deepLink);
      intent = new Intent(context, WebActivity.class);
      intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    } else if (!TextUtils.isEmpty(deepLink) && Uri.parse(deepLink).getScheme() == "oneTrip") {
      intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    } else {
      ComponentName componentName = new ComponentName(context, "com.one.trip.SplashActivity");
      intent = new Intent();
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      intent.setComponent(componentName);
    }
    return intent;
  }

  public void showNotification(String title, String message, int id, Intent intent) {
    Logger.e("ldx", "showNotification before >>>>>>>>>>>>");

    NotificationManager notificationManagerCompat = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNELID)
        // 清楚通知栏时的相应
//                .setDeleteIntent(getDeleteIntent(context))
        // 设置自定义通知栏布局，support兼容包25以下，找不到此方法
        // 需要使用setContent(remoteViews)
//                .setCustomContentView(remoteViews)
        // 设置通知时间，此事件用于通知栏排序
        .setWhen(System.currentTimeMillis())
        // 设置通知栏被点击时的事件
        .setContentIntent(getContentClickIntent(mContext, intent))
        // 设置优先级，低优先级可能被隐藏
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentTitle(title)
        .setContentText(message)
        // 设置通知栏能否被清楚，true不能被清除，false可以被清除
        .setOngoing(false)
        // 设置通知栏的小图标,必需设置，否则crash
        .setSmallIcon(R.drawable.push_small)
        .setVibrate(new long[]{100, 200, 100, 200});


    // 此处必须兼容android O设备，否则系统版本在O以上可能不展示通知栏
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(mContext.getPackageName(), TAG, NotificationManager.IMPORTANCE_DEFAULT);
      notificationManagerCompat.createNotificationChannel(channel);
    }

    // channelId非常重要，不设置通知栏不展示
    builder.setChannelId(mContext.getPackageName());

    // 创建通知栏
    Notification notify = builder.build();
    // 通知系统展示通知栏
    notificationManagerCompat.notify(TAG, id, notify);
    Logger.e("ldx", "showNotification after >>>>>>>>>>>>");
  }

  private PendingIntent getContentClickIntent(Context context, Intent intent) {
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    return pendingIntent;
  }
}

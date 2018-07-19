package com.one.framework.app.notify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.one.framework.R;
import java.util.Random;

/**
 * Created by ludexiang on 2018/7/3.
 */

public class NotifyManager {

  private static NotifyManager sManager;
  private Context mContext;

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

  public void buildNotification(String ticker, String title, String message, Intent intent) {
    int requestCode = new Random().nextInt(9999) + 1;
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "default");
    builder.setSmallIcon(R.drawable.ic_launcher)
        .setTicker(ticker)
        .setWhen(System.currentTimeMillis())
        .setContentTitle(title)
        .setContentText(message)
        .setOngoing(false)
        .setDefaults(Notification.DEFAULT_LIGHTS)
        .setAutoCancel(true)
        .setContentIntent(PendingIntent.getActivity(mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT));
    Notification notification = builder.build();
    NotificationManagerCompat.from(mContext).notify(requestCode, notification);
  }
}

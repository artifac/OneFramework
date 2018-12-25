package com.one.framework.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.notify.NotifyManager;
import com.one.framework.log.Logger;
import com.one.framework.push.PushMessageManager;
import com.one.framework.push.PushMsgModel;

/**
 * Created by ludexiang on 2018/7/3.
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */

public class GetuiIntentService extends GTIntentService {

  private static final String TAG = GetuiIntentService.class.getSimpleName();
  private static Handler target;

  private int notifyId = 0x999;

  public GetuiIntentService() {
  }

  public void setHandler(Handler handler) {
    target = handler;
  }

  @Override
  public void onReceiveServicePid(Context context, int pid) {
    Logger.d(TAG, "onReceiveServicePid -> " + pid);
  }

  @Override
  public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
    String appid = msg.getAppid();
    String taskid = msg.getTaskId();
    String messageid = msg.getMessageId();
    byte[] payload = msg.getPayload();
    String pkg = msg.getPkgName();
    String cid = msg.getClientId();

    // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
    boolean result = PushManager
        .getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
    Logger.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

    Logger.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
        + "\ncid = " + cid);

    if (payload == null) {
      Logger.e(TAG, "receiver payload = null");
    } else {
      String data = new String(payload);
      Logger.d(TAG, "receiver payload = " + data);
      PushMessageManager pushMessageManager = new PushMessageManager(context, data);
      PushMsgModel msgModel = pushMessageManager.getPushModel();
      pushMessageManager.handlePush(msgModel);
      sendMessage(msgModel);

      /** 通知栏展示 */
      Intent intent = NotifyManager.getManager(context).buildIntent(context, msgModel);
      NotifyManager.getManager(context).showNotification(msgModel.getTitle(), msgModel.getDescription(), msgModel.getMsgId(), intent);
    }
  }

  @Override
  public void onReceiveClientId(Context context, String clientId) {
    if (!TextUtils.isEmpty(clientId)) {
      UserProfile.getInstance(context).savePushToken(clientId);
    }
  }

  @Override
  public void onReceiveOnlineState(Context context, boolean online) {
  }

  @Override
  public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
//    int action = cmdMessage.getAction();
  }

  @Override
  public void onNotificationMessageArrived(Context context, GTNotificationMessage message) {
    Logger.d(TAG, "onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
        + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
        + message.getTitle() + "\ncontent = " + message.getContent());

    /** 通知栏展示 */
    NotifyManager.getManager(context).showNotification(message.getTitle(), message.getContent(), notifyId, new Intent());
  }

  @Override
  public void onNotificationMessageClicked(Context context, GTNotificationMessage message) {
    Logger.d(TAG, "onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
        + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
        + message.getTitle() + "\ncontent = " + message.getContent());
  }

  private void sendMessage(PushMsgModel model) {
    Message msg = target.obtainMessage();
    msg.what = model.getMsgId();
    msg.obj = model;
    msg.sendToTarget();
  }
}

package com.one.framework.push;

import android.content.Context;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.one.framework.app.login.UserProfile;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.DBUtil;

public class PushMessageManager {
  public static final int LOGOUT = 1011;

  private Context mContext;
  private String pushMessage;

  public PushMessageManager(Context context, String message) {
    mContext = context;
    pushMessage = message;
  }

  public PushMsgModel getPushModel() {
    PushMsgModel pushMsgModel = new Gson().fromJson(pushMessage, PushMsgModel.class);
    return pushMsgModel;
  }

  public void handlePush(PushMsgModel pushMsg) {
    switch (pushMsg.getMsgId()) {
      case LOGOUT: {
        // 被踢出
        UserProfile.getInstance(mContext).logout(); // clear data
        DBUtil.deleteTables(mContext); // clear table
        HomeDataProvider.getInstance().clearOrderDetails();
        NIMClient.getService(AuthService.class).logout();
        break;
      }
    }
  }
}

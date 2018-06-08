package com.one.framework.utils;

import android.content.Context;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ludexiang on 2018/6/8.
 */

public class ChannelUtil {

  private static final String CHANNEL_KEY = "channel";
  private static final String DEFAULT_CHANNEL_ID = "72980";
  private static String mChannel;

  private ChannelUtil() {
  }

  static String getChannel(Context context) {
    return getChannel(context, DEFAULT_CHANNEL_ID);
  }

  static String getChannel(Context context, String defaultChannel) {
    if (TextUtils.isEmpty(mChannel)) {
      String comment = ZipCommentFetcher.getInstance().getComment(context);
      try {
        JSONObject json = new JSONObject(comment);
        mChannel = json.optString(CHANNEL_KEY, defaultChannel);
      } catch (JSONException e) {
        mChannel = defaultChannel;
      }
    }

    return mChannel;
  }
}

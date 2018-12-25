package com.one.framework.app.web;

import android.net.Uri;
import android.text.TextUtils;
import com.one.framework.app.login.UserProfile;

public class StrategyManager {

  /**
   * 域名白名单
   */
  private static final String[] HOST_WHITE_LIST = {"taxi.mobike.com"};
  private volatile static StrategyManager sInstance;

  private StrategyManager() {

  }

  public static StrategyManager getInstance() {
    if (sInstance == null) {
      synchronized (StrategyManager.class) {
        if (sInstance == null) {
          sInstance = new StrategyManager();
        }
      }
    }

    return sInstance;
  }

  /**
   * 检查 host 是否在白名单里面
   */
  public boolean isHostInWhiteList(String host) {
    for (String h : HOST_WHITE_LIST) {
      if (h.equalsIgnoreCase(host)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 检查 url 是否在白名单里
   */
  public boolean isUrlInWhiteList(String url) {
    if (url == null) {
      return false;
    }

    Uri uri = Uri.parse(url);
    if (uri == null) {
      return false;
    }

    return isHostInWhiteList(uri.getHost());
  }

  /**
   * 添加 token 到 query string
   */
  public String appendToken(String url) {
    if (url == null) {
      return url;
    }

    String token = "";//UserProfile.getInstance().getToken();
    if (TextUtils.isEmpty(token)) {
      return url;
    }

    Uri uri = Uri.parse(url);
    url = uri.buildUpon().appendQueryParameter("token", token).toString();

    return url;
  }

}

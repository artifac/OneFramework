package com.one.framework.net;

import android.text.TextUtils;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.base.INetworkConfig;
import com.one.framework.net.request.RequestHelper;
import com.one.framework.net.response.IResponseListener;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/4/25.
 */

public class Api {
  public static String BASE_URL_HOST = "http://app.mobike.com/";
  public static String BASE_URL_HOSTS = "https://app.mobike.com/";
  private static String sApiUrl;
  private static INetworkConfig sConfig;

  public static void initNetworkConfig(INetworkConfig config) {
    sConfig = config;
  }

  public static void apiUrl(String apiHost) {
    if (TextUtils.isEmpty(apiHost)) {
      sApiUrl = BASE_URL_HOSTS;
      return;
    }
    sApiUrl = apiHost;
  }

  /**
   * 请求时返回请求 hashcode()
   * @param url
   * @param urlParams
   * @param <T> BaseObject 子类
   * @return
   */
  public static <T extends BaseObject> int request(String url, HashMap<String, Object> urlParams, IResponseListener<T> listener, Class<T> t) {
    StringBuilder builder = new StringBuilder(sApiUrl);
    return RequestHelper.getRequest(sConfig).request(builder.append(url).toString(), urlParams, listener, t);
  }

  public static void cancelRequest(int requestCode) {
    RequestHelper.cancelRequest(requestCode);
  }
}

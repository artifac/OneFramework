package com.one.framework.net;

import android.content.Context;
import com.one.framework.net.base.BaseResponse;
import com.one.framework.net.request.RequestHelper;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/4/25.
 */

public class Api {

  private Context mContext;

  private Api(Context context) {
    mContext = context.getApplicationContext();
  }

  public static Api getInstance(Context context) {
    return API.instance(context);
  }

  private static final class API {

    static Api sApi;

    static Api instance(Context context) {
      if (sApi == null) {
        sApi = new Api(context);
      }
      return sApi;
    }
  }

  /**
   * 请求时返回请求 hashcode()
   * @param url
   * @param urlParams
   * @param <T> BaseResponse 子类
   * @return
   */
  public <T extends BaseResponse> int request(String url, HashMap<String, Object> urlParams, T t) {
    CommonNameValueParams.addCommonParams(mContext, urlParams);
    return RequestHelper.request(url, urlParams, t);
  }

  public void cancelRequest(int requestCode) {
    RequestHelper.cancelRequest(requestCode);
  }
}

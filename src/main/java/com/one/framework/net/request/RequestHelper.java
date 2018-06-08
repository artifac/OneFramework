package com.one.framework.net.request;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.one.framework.net.NetConstant;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.base.INetworkConfig;
import com.one.framework.net.response.IResponseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ludexiang on 2018/4/25.
 */

public final class RequestHelper {

  private static RequestHelper sRequestHelper;
  private INetworkConfig mConfig;

  private static ConcurrentHashMap<Integer, Call> requestQueue = new ConcurrentHashMap<Integer, Call>();

  private RequestHelper(INetworkConfig config) {
    mConfig = config;
  }

  public static RequestHelper getRequest(INetworkConfig config) {
    if (sRequestHelper == null) {
      sRequestHelper = new RequestHelper(config);
    }
    return sRequestHelper;
  }

  private OkHttpClient beforeRequest(INetworkConfig config) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .sslSocketFactory(config.getSslSocketFactory(), config.getTrustManager())
        .hostnameVerifier(config.getHostnameVerifier());
    for (Interceptor interceptor : config.getInterceptors()) {
      builder.addInterceptor(interceptor);
    }
    return builder.build();
  }

  public <T extends BaseObject> int request(String url, HashMap<String, Object> params,
      final IResponseListener<T> listener, final Class<T> clazz) {
    OkHttpClient mHttpClient = beforeRequest(mConfig);
    Builder bodyBuilder = new FormBody.Builder();
    if (params != null) {
      Set<String> keys = params.keySet();
      for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
        String key = iterator.next();
        bodyBuilder.add(key, params.get(key).toString());
      }
    }
    Request request = new Request.Builder().url(url).post(bodyBuilder.build()).build();
    Call call = mHttpClient.newCall(request);
    final int requestCode = call.hashCode();
    requestQueue.put(requestCode, call);
    /**
     * 异步请求
     */
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        requestQueue.remove(requestCode);

        T t = null;
        try {
          t = clazz.newInstance();
        } catch (InstantiationException e1) {
          e1.printStackTrace();
        } catch (IllegalAccessException e1) {
          e1.printStackTrace();
        }
        if (t != null) {
          t.setErrorCode(NetConstant.MSG_ERROR);
//        t.setThrowable(t);

          if (listener == null) {
            return;
          }
          listener.onFail(t);
          listener.onFinish(t);
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        requestQueue.remove(requestCode);
        ResponseBody responseBody = response.body();
        String result = responseBody.string();
        Gson gson = new Gson();
        T t = gson.fromJson(result, clazz);
        t.parse(result);

        /* 自动校验结果 */
        if (listener == null) {
          return;
        }
        /* 非法数据 */
        if (!t.isAvailable()) {
          String error = t.getErrorMsg();
          t.setErrorMsg(TextUtils.isEmpty(error) ? "好像出错了请稍后再试!" : error);
          listener.onFail(t);
          listener.onFinish(t);
          return;
        }
        /* 合法数据 */
        listener.onSuccess(t);
        listener.onFinish(t);
      }
    });
    return requestCode;
  }

  /**
   * 取消请求
   */
  public static void cancelRequest(int requestCode) {
    Call call = requestQueue.get(requestCode);
    if (call != null && call.isExecuted()) {
      call.cancel();
    }
  }
}

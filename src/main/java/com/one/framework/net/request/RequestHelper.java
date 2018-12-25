package com.one.framework.net.request;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.one.framework.app.base.OneApplication;
import com.one.framework.app.login.UserProfile;
import com.one.framework.log.Logger;
import com.one.framework.net.NetConstant;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.base.INetworkConfig;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.DBUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
  private String errMsg = "";
  private int errCode = -1;
  private static ConcurrentHashMap<Integer, Call> requestQueue = new ConcurrentHashMap<Integer, Call>();
  private OkHttpClient mHttpClient;

  private RequestHelper(INetworkConfig config) {
    mConfig = config;
    mHttpClient = beforeRequest(mConfig);
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
        .connectTimeout(config.getTimeout(), TimeUnit.SECONDS)
        .hostnameVerifier(config.getHostnameVerifier());
    for (Interceptor interceptor : config.getInterceptors()) {
      builder.addInterceptor(interceptor);
    }
    return builder.build();
  }

  public <T extends BaseObject> int requestPost(String url, HashMap<String, Object> params,
      final IResponseListener<T> listener, final Class<T> clazz) {

    Builder bodyBuilder = new FormBody.Builder();
    if (params != null) {
      Set<String> keys = params.keySet();
      for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
        String key = iterator.next();
        if (params.get(key) != null) {
          bodyBuilder.add(key, params.get(key).toString());
        }
      }
    }
    Request request = new Request.Builder().url(url).post(bodyBuilder.build()).build();
    Call call = mHttpClient.newCall(request);
    final int requestCode = call.hashCode();
    requestQueue.put(requestCode, call);
    executeRequest(listener, clazz, call, requestCode);
    return requestCode;
  }

  public <T extends BaseObject> int requestGet(String url, HashMap<String, Object> urlParams,
      final IResponseListener<T> listener, final Class<T> clazz) {
    StringBuilder params = new StringBuilder();
    int pos = 0;
    for (String key : urlParams.keySet()) {
      if (pos > 0) {
        params.append("&");
      }
      params.append(String.format("%s=%s", key, urlParams.get(key)));
      pos++;
    }
    String requestUrl = String.format("%s?%s", url, params.toString());
    Request request = new Request.Builder().get().url(requestUrl).build();
    Call call = mHttpClient.newCall(request);
    final int requestCode = call.hashCode();
    requestQueue.put(requestCode, call);
    executeRequest(listener, clazz, call, requestCode);
    return requestCode;
  }

  private <T extends BaseObject> void executeRequest(final IResponseListener<T> listener,
      final Class<T> clazz,
      Call call, final int requestCode) {
    /**
     * 异步请求
     */
    try {
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
            t.setErrorMsg("error...");
          }
          if (listener == null) {
            return;
          }
          final UIHandler<T> uiHandler = new UIHandler<>(Looper.getMainLooper(), t);
          uiHandler.post(() -> {
            T t1 = uiHandler.t();
            /* 非法数据 */
            listener.onFail(t1 != null ? t1.code : NetConstant.NO_DATA, "服务开小差,请稍后再试!");
            listener.onFinish(t1);
          });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          requestQueue.remove(requestCode);
          boolean isSuccess = response.isSuccessful();

          T t = null;
          if (isSuccess) {
            ResponseBody responseBody = response.body();
            String result = responseBody.string();
            Gson gson = new Gson();
            BaseObject base = new BaseObject();
            base.parse(result);
            if (base != null && base.isAvailable()) {
              try {
                if (!TextUtils.isEmpty(base.data)) {
                  t = gson.fromJson(base.data, clazz);
                } else if (base.object != null) {
                  t = gson.fromJson(base.object, clazz);
                }
              } catch (Exception e) {

              }
              if (base.data == null && clazz == BaseObject.class) {
                // 后端有可能返回 data: null 无返回值但是请求成功
                t = (T) base;
              }
            } else {
              errCode = base.code;
              errMsg = base.message;
            }
          } else {
            try {
              t = clazz.newInstance();
            } catch (InstantiationException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
          }

          /* 自动校验结果 */
          if (listener == null) {
            return;
          }

          final UIHandler<T> uiHandler = new UIHandler<T>(Looper.getMainLooper(), t);
          uiHandler.post(new Runnable() {
            @Override
            public void run() {
              T t = uiHandler.t();
//              if (t != null) {
//                Logger.e("ldx", " t >> " + t.code + " " + t);
//              }
              /* 非法数据 */
              if (t != null && t.isAvailable()) {
                /* 合法数据 */
                listener.onSuccess(t);
                listener.onFinish(t);
                return;
              }
              if (t != null && t.getErrorCode() == NetConstant.OUT) {
                UserProfile.getInstance(OneApplication.appContext).logout(); // clear data
                DBUtil.deleteTables(OneApplication.appContext); // clear table
                HomeDataProvider.getInstance().clearOrderDetails();
                NIMClient.getService(AuthService.class).logout();
              }
              if (errCode != -1 && !TextUtils.isEmpty(errMsg)) {
                listener.onFail(errCode, errMsg);
              } else {
                listener.onFail(t != null ? t.code : NetConstant.NO_DATA, "服务开小差,请稍后再试!");
              }
              listener.onFinish(t);
            }
          });
        }
      });
    } catch (Exception e) {

    }
  }

  /**
   * 取消请求
   */
  public static void cancelRequest(int requestCode) {
    Call call = requestQueue.get(requestCode);
    if (call != null && call.isExecuted()) {
      call.cancel();
    }
    requestQueue.remove(requestCode);
  }

  final class UIHandler<T> extends Handler {

    T t;

    UIHandler(Looper looper, T t) {
      super(looper);
      this.t = t;
    }

    T t() {
      return t;
    }
  }
}

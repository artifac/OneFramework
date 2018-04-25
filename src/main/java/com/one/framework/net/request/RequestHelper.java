package com.one.framework.net.request;

import com.one.framework.net.base.BaseResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ludexiang on 2018/4/25.
 */

public final class RequestHelper {

  private static ConcurrentHashMap<Integer, Call> requestQueue = new ConcurrentHashMap<Integer, Call>();

  public static <T extends BaseResponse> int request(String url, HashMap<String, Object> params,
      final T t) {

    OkHttpClient mHttpClient = new OkHttpClient();
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
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        requestQueue.remove(requestCode);
        ResponseBody responseBody = response.body();
        String jsonString = responseBody.string();
        t.parse(jsonString);
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

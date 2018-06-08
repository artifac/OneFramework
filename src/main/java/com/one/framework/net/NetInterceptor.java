package com.one.framework.net;

import android.text.TextUtils;
import com.one.framework.utils.SafeUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ludexiang on 2018/6/8.
 */

public class NetInterceptor implements Interceptor {

  private IHeaderParams mHeaderParams;

  public NetInterceptor(IHeaderParams params) {
    mHeaderParams = params;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = beforeRequest(chain);
    Response response = chain.proceed(request);
    return response;
  }

  private Request beforeRequest(Chain chain) {
    Request request = chain.request();
    HttpUrl url = request.url();
    // 处理header
    Builder builder = request.newBuilder();
    for (String key : mHeaderParams.getParams().keySet()) {
      builder.addHeader(key, mHeaderParams.getParams().get(key).toString());
    }
    url = url.newBuilder()
        .addQueryParameter(ServerParams.PARAM_TIME, String.valueOf(System.currentTimeMillis()))
        .build();
    request = request.newBuilder().url(url).build();
    String sign = getSign(request);
    return request.newBuilder().addHeader(ServerParams.PARAM_SIGN, sign).build();
  }

  public String getSign(Request request) {
    Map<String, List<String>> map = new HashMap<String, List<String>>();
    Map<String, List<String>> params = getRequestParamsMap(request.url());
    map.putAll(params);

//        val headers = getHeadersMap(request.headers())
//        map.putAll(headers)

    if ("POST".equals(request.method())) {
      Map<String, List<String>> body = getRequestBodyMap(request.body());
      if (body != null) {
        map.putAll(body);
      }
    }

    return computeSign(map);
  }

  private Map<String, List<String>> getRequestParamsMap(HttpUrl url) {
    Map<String, List<String>> map = new HashMap<>();

    Set<String> params = url.queryParameterNames();
    for (String key : params) {
      List<String> values = url.queryParameterValues(key);
      List<String> encodedValues = new ArrayList<>();
      for (String v : values) {
        try {
          encodedValues.add(URLEncoder.encode(v, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }

      }
      map.put(key, encodedValues);
    }
    return map;
  }

  private Map<String, List<String>> getRequestBodyMap(RequestBody body) {
    if (body != null && body instanceof FormBody) {
      return getFormBodyMap((FormBody) body);
    } else {
      return null;
    }
  }

  private Map<String, List<String>> getFormBodyMap(FormBody body) {
    Map<String, List<String>> map = new HashMap<>();
    for (int i = 0; i < body.size(); i++) {
      String key = body.encodedName(i);
      String value = body.encodedName(i);
      String encode = null;
      try {
        encode = URLEncoder.encode(value, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      map.put(key, Collections.singletonList(encode));
    }
    return map;
  }

  /**
   * sign the map
   */
  /**
   * sign the map
   */
  private String computeSign(Map<String, List<String>> map) {
    List<String> list = new ArrayList<String>();

    for (String key : map.keySet()) {
      for (String value : map.get(key)) {
        if (TextUtils.isEmpty(value)) {
          list.add(key + "=" + value + "&");
        }
      }
    }

    Collections.sort(list, String.CASE_INSENSITIVE_ORDER);

    StringBuilder sb = new StringBuilder();
    for (String str : list) {
      sb.append(str);
    }
    String result = sb.toString();
    String signContent;
    if (result.length() > 1) {
      signContent = result.substring(0, result.length() - 1);
    } else {
      signContent = result;
    }

    return SafeUtil.getJniString(signContent);
  }
}

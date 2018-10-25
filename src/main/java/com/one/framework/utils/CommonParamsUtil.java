package com.one.framework.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.one.framework.app.base.OneApplication;
import com.one.framework.app.login.UserProfile;
import com.one.map.location.LocationProvider;
import com.one.map.model.Address;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonParamsUtil {

  /**
   * 增加公共参数
   */
  public static HashMap<String, Object> addCommonParam(HashMap<String, Object> urlParams,
      Context context) {
    if (urlParams == null) {
      urlParams = new HashMap<String, Object>();
    }
    urlParams.put(CommonParams.PARAM_VCODE, SystemUtils.getVersionCode());
    urlParams.put(CommonParams.PARAM_APP_VERSION, SystemUtils.getVersionName());
    urlParams.put(CommonParams.PARAM_MODEL, SystemUtils.getModel());
    urlParams.put(CommonParams.PARAM_OS, Build.VERSION.RELEASE);
    urlParams.put(CommonParams.PARAM_IMEI, SystemUtils.getIMEI());
    urlParams.put(CommonParams.PARAM_CHANNEL, ChannelUtil.getChannel(context));
    urlParams.put(CommonParams.PARAM_DATA_TYPE, 1);

    Address location = LocationProvider.getInstance().getLocation();

    if (urlParams.get(CommonParams.PARAM_MAP_TYPE) == null || "".equals(
        String.valueOf(urlParams.get(CommonParams.PARAM_MAP_TYPE)))) {
      urlParams.put(CommonParams.PARAM_MAP_TYPE, "soso");
      if (location != null) {
      }
    }
    urlParams.put(CommonParams.PARAM_SCREEN_PIXELS, SystemUtils.getScreenWidth());
    urlParams.put(CommonParams.PARAM_MAC, SystemUtils.getMacSerialno());
    String cpuSerialno = SystemUtils.getCPUSerialno();
//        Log.d("CommonParamsUtil", "cpu serial no: " + cpuSerialno);
    cpuSerialno = filterChars(cpuSerialno);
    if (!TextUtils.isEmpty(cpuSerialno)) {
      urlParams.put(CommonParams.PARAM_CPU, cpuSerialno);
    }
    urlParams.put(CommonParams.PARAM_ANDROID_ID, SystemUtils.getAndroidID());
    urlParams.put(CommonParams.PARAM_NET_WORK_TYPE, SystemUtils.getNetworkType());
    urlParams.put(CommonParams.PARAM_TIME, System.currentTimeMillis() + "");

    if (urlParams.get(CommonParams.PARAM_TOKEN) == null || "".equals(
        String.valueOf(urlParams.get(CommonParams.PARAM_TOKEN)))) {
      String token = UserProfile.getInstance(context).getTokenValue();
      if (TextUtils.isEmpty(token)) {
        urlParams.put(CommonParams.PARAM_TOKEN, "0");
      } else {
        urlParams.put(CommonParams.PARAM_TOKEN, token);
      }
    }

    if (urlParams.get(CommonParams.PARAM_LATITUDE) == null) {
      urlParams
          .put(CommonParams.PARAM_LATITUDE, location == null ? "0" : location.mAdrLatLng.latitude);
    }

    if (urlParams.get(CommonParams.PARAM_LONGITUDE) == null) {
      urlParams.put(CommonParams.PARAM_LONGITUDE,
          location == null ? "0" : location.mAdrLatLng.longitude);
    }

    return urlParams;
  }

  public static String createCommonParamJson(Context context) {
    JSONObject urlParams = new JSONObject();
    try {

      urlParams.put(CommonParams.PARAM_DATA_TYPE, 1);
      urlParams.put(CommonParams.PARAM_IMEI, SystemUtils.getIMEI());
      urlParams.put(CommonParams.PARAM_MAC, SystemUtils.getMacSerialno());
      urlParams.put(CommonParams.PARAM_APP_VERSION, SystemUtils.getVersionName());
      urlParams.put(CommonParams.PARAM_CHANNEL, ChannelUtil.getChannel(context));
      urlParams.put(CommonParams.PARAM_ANDROID_ID, SystemUtils.getAndroidID());
      urlParams.put(CommonParams.PARAM_MODEL, SystemUtils.getModel());
      urlParams.put(CommonParams.PARAM_NET_WORK_TYPE, SystemUtils.getNetworkType());

      Address location = LocationProvider.getInstance().getLocation();

      urlParams.put(CommonParams.PARAM_MAP_TYPE, "soso");
      if (location != null) {
      }

      urlParams.put(CommonParams.PARAM_TIME, System.currentTimeMillis() + "");

      String token = UserProfile.getInstance(context).getTokenValue();
      if (TextUtils.isEmpty(token)) {
        urlParams.put(CommonParams.PARAM_TOKEN, "0");
      } else {
        urlParams.put(CommonParams.PARAM_TOKEN, token);
      }

      urlParams
          .put(CommonParams.PARAM_LATITUDE, location == null ? "0" : location.mAdrLatLng.latitude);

      urlParams.put(CommonParams.PARAM_LONGITUDE,
          location == null ? "0" : location.mAdrLatLng.longitude);

    } catch (JSONException e) {
    } catch (Exception e) {
    }

    return urlParams.toString();
  }

  public static String createCommonParamString(Context context) {
    if (context == null) {
      return "";
    }
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap = addCommonParam(hashMap, context);

    Set<Entry<String, Object>> set = hashMap.entrySet();
    StringBuilder stringBuilder = new StringBuilder();
    for (Map.Entry<String, Object> entry : set) {
      String key = entry.getKey();
      String value = entry.getValue().toString();
      stringBuilder.append(key + "=" + value + "&");
    }
    if (stringBuilder.length() > 0) {
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    }
    return stringBuilder.toString();
  }

  private static String filterChars(String cpuSerialno) {
    if (TextUtils.isEmpty(cpuSerialno)) {
      return "";
    }
    char[] chars = cpuSerialno.toCharArray();
    char[] result = new char[chars.length];
    int index = 0;
    for (int i = 0; i < chars.length; ++i) {
      if (chars[i] <= '\u001f' || chars[i] >= '\u007f') {
        continue;
      }

      result[index++] = chars[i];
    }
    return new String(result, 0, index);
  }

  public static class CommonParams {

    public static final String PARAM_VCODE = "vcode";
    public static final String PARAM_DEVICE_ID = "dviceid";
    public static final String PARAM_DEVICE_ID_NEW = "deviceid";
    public static final String PARAM_APP_VERSION = "appversion";
    public static final String PARAM_MODEL = "model";
    public static final String PARAM_OS = "os";
    public static final String PARAM_IMEI = "imei";
    public static final String PARAM_SUUID = "suuid";
    public static final String PARAM_CHANNEL = "channel";
    public static final String PARAM_DATA_TYPE = "datatype";
    public static final String PARAM_CANCEL = "cancel";
    public static final String PARAM_MAP_TYPE = "maptype";

    public static final String PARAM_SIGNATURE = "sig";

    public static final String PARAM_SCREEN_PIXELS = "pixels";
    public static final String PARAM_MAC = "mac";
    public static final String PARAM_CPU = "cpu";
    public static final String PARAM_ANDROID_ID = "android_id";
    public static final String PARAM_NET_WORK_TYPE = "networkType";

    public static final String PARAM_UUID = "uuid";
    public static final String PARAM_CITY_ID = "city_id";

    public static final String PARAM_LATITUDE = "lat";
    public static final String PARAM_LONGITUDE = "lng";

    public static final String PARAM_TIME = "time";//timestamp
    public static final String PARAM_TOKEN = "token";
  }
}

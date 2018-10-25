package com.one.framework.net;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.one.framework.app.login.UserProfile;
import com.one.framework.utils.MD5Util;
import com.one.framework.utils.SystemUtils;
import com.one.map.location.LocationProvider;
import com.one.map.model.Address;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by ludexiang on 2018/4/25.
 */

public class HeaderParams implements IHeaderParams {

  private HashMap<String, Object> mHeaderParams = new HashMap<String, Object>();
  private String phoneNumber;
  private Context mContext;

  public HeaderParams(Context context) {
    mContext = context;
  }

  @Override
  public void setLoginPhone(String phone) {
    phoneNumber = phone;
  }

  @Override
  public HashMap<String, Object> getParams() {
    long time = System.currentTimeMillis();
    String userPhone = UserProfile.getInstance(mContext).getMobile();
    phoneNumber = TextUtils.isEmpty(userPhone) ? phoneNumber : userPhone;
    mHeaderParams.put(ServerParams.PARAM_VCODE, SystemUtils.getVersionCode());
    mHeaderParams.put(ServerParams.PARAM_DEVICE_ID, SystemUtils.getAndroidID());
    mHeaderParams.put(ServerParams.PARAM_MODEL, SystemUtils.getModel());
    mHeaderParams.put(ServerParams.PARAM_IMEI, SystemUtils.getIMEI());
    mHeaderParams.put(ServerParams.PARAM_UUID, UserProfile.getInstance(mContext).getPushKey());
    mHeaderParams.put(ServerParams.PARAM_CHANNEL, SystemUtils.getChannelId());
    mHeaderParams.put(ServerParams.PARAM_TOKEN, UserProfile.getInstance(mContext).getTokenValue());
    mHeaderParams.put(ServerParams.PARAM_VERSION, SystemUtils.getVersion());
    mHeaderParams.put(ServerParams.PARAM_OS, VERSION.RELEASE);
    mHeaderParams.put(ServerParams.PARAM_SCREEN_PIXELS, SystemUtils.getScreenWidth() + "*" + SystemUtils.getScreenHeight());
    mHeaderParams.put(ServerParams.PARAM_CLIENT_ID, "android");
    mHeaderParams.put(ServerParams.PARAM_APP, "0");
    mHeaderParams.put(ServerParams.PARAM_PLATFORM, "1");
    mHeaderParams.put(ServerParams.PARAM_MOBILE, phoneNumber);
    mHeaderParams.put(ServerParams.PARAM_EPTION, MD5Util.GetMD5Code(phoneNumber + "#" + time).substring(2, 7));
    mHeaderParams.put(ServerParams.PARAM_LANG, Locale.getDefault().getLanguage());
    mHeaderParams.put(ServerParams.PARAM_USERID, UserProfile.getInstance(mContext).getUserId());
    Address address = LocationProvider.getInstance().getLocation();
    if (address != null && address.mAdrLatLng != null) {
      mHeaderParams.put(ServerParams.PARAM_LATITUDE, address.mAdrLatLng.latitude);
      mHeaderParams.put(ServerParams.PARAM_LONGITUDE, address.mAdrLatLng.longitude);
      mHeaderParams.put(ServerParams.PARAM_CITYCODE, LocationProvider.getInstance().getCityCode());
    }
    mHeaderParams.put(ServerParams.PARAM_TIME, time);
    return mHeaderParams;
  }

}

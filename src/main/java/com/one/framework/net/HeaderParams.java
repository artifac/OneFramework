package com.one.framework.net;

import android.os.Build.VERSION;
import com.one.framework.utils.SystemUtils;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/4/25.
 */

public class HeaderParams implements IHeaderParams {

  private HashMap<String, Object> mHeaderParams = new HashMap<String, Object>();

  @Override
  public HashMap<String, Object> getParams() {
    mHeaderParams.put(ServerParams.PARAM_VCODE, SystemUtils.getVersionCode());
    mHeaderParams.put(ServerParams.PARAM_DEVICE_ID, "android");
    mHeaderParams.put(ServerParams.PARAM_MODEL, SystemUtils.getModel());
    mHeaderParams.put(ServerParams.PARAM_IMEI, SystemUtils.getIMEI());
    mHeaderParams.put(ServerParams.PARAM_SUUID, "android");
    mHeaderParams.put(ServerParams.PARAM_CHANNEL, SystemUtils.getChannelId());
//    urlParams.put(ServerParams.PARAM_TOKEN, "android");
    mHeaderParams.put(ServerParams.PARAM_VERSION, SystemUtils.getVersion());
    mHeaderParams.put(ServerParams.PARAM_OS, VERSION.RELEASE);
    mHeaderParams.put(ServerParams.PARAM_SCREEN_PIXELS,
        SystemUtils.getScreenWidth() + "*" + SystemUtils.getScreenWidth());
    return mHeaderParams;
  }

}

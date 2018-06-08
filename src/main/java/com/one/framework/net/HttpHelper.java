package com.one.framework.net;

import com.one.framework.R;
import com.one.framework.net.base.BaseObject;

/**
 * Http请求帮助类
 */
public class HttpHelper {

  /**
   * 验证当前数据合法性
   */
  public static boolean validate(BaseObject obj) {
    return validate(obj, false);
  }

  /**
   * 验证当前数据合法性
   */
  public static boolean validate(BaseObject obj, boolean logEvent) {
    if (obj == null) {
      return false;
    }
    return validate(obj.code, obj.message, logEvent);
  }

  /**
   * 验证当前数据合法性
   *
   * @param logEvent 是否统计日志
   */
  public static boolean validate(int errno, String errmsg, boolean logEvent) {
    boolean valid = true;
    switch (errno) {
      case NetConstant.OK:
        return valid;
      case NetConstant.ERROR_INVALID_DATA_FORMAT:
        valid = false;
        break;
      case NetConstant.INVALID_TOKEN:
      case NetConstant.TOKEN_FAILED:
        valid = false;
        break;
      case NetConstant.ONLY_TEXT_ERROR:
        valid = true;
        break;
      default:
        valid = false;
    }
    return valid;
  }

  /**
   * 根据ResponseCode映射对应的错误提示(服务器出现404、502等)
   */
  public static int getHttpErrMsg(int responseCode) {
    switch (responseCode) {
      case NetConstant.SOCKE_TTIMEOUT:
      case NetConstant.CONNECT_TIMEOUT:
      case NetConstant.ERROR_NETWORK_FAIL:
      case NetConstant.MSG_ERROR:
      case NetConstant.HTTP_HOST_CONNECT:
        return R.string.one_action_settings;
      default:
        if (responseCode != 200 && responseCode != 206) {
          return R.string.one_action_settings;
        }
    }
    return R.string.one_action_settings;
  }

  /**
   * 验证HTTP协议状态
   */
  public static boolean validateHttpState(int errno, String errmsg, boolean logEvent) {
    boolean valid = true;
    int toastStringId = -1;
    switch (errno) {
      case NetConstant.SOCKE_TTIMEOUT:
      case NetConstant.CONNECT_TIMEOUT:
        valid = false;
        break;
      case NetConstant.ERROR_NETWORK_FAIL:
      case NetConstant.MSG_ERROR:
      case NetConstant.HTTP_HOST_CONNECT:
        valid = false;
        break;
      default:
        if (errno != 200 && errno != 206) {
          valid = false;
        }
    }
    return valid;
  }

  public static boolean validate(BaseObject obj, int dialogTipResId) {
    if (obj == null) {
      return false;
    }
    int errno = obj.getErrorCode();
    if (errno == NetConstant.OK) {
      return true;
    }
    return false;
  }
}

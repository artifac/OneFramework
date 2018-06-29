package com.mobike.mobikeapp.util;

/**
 * ==========================================
 * <p/>
 * 作    者 : Rock
 * <p/>
 * 创建时间 ： 2017/4/19.
 * <p/>
 * 用   途 :
 * <p/>M
 * <p/>
 * ==========================================
 */
public class SafeUtil {

  static {
    System.loadLibrary("native-lib");
  }

  public static native String getJniString(String json);

  public static native String getBKSString();

  public static native String getWXID();

  public static native String getWXSecret();

  public static native String getWxPayID();

  public static native String getWBKey();

  public static native String getWBSecret();

  public static native String getQQID();

  public static native String getQQKey();

  public static native String getStripeKey();

  public static native String getTalkingKey();

  public static native String getIMKey();

  public static native String getIMID();


}

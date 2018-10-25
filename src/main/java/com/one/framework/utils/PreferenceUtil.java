package com.one.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by ludexiang on 2017/12/5.
 */

public class PreferenceUtil {
  private SharedPreferences mPreference;
  private static final String PREFER_NAME = "one_framework_preference";
  private SharedPreferences.Editor mEditor;
  private PreferenceUtil(Context context, String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      fileName = PREFER_NAME;
    }
    mPreference = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    mEditor = mPreference.edit();
  }

  /**
   *
   * @param context
   * @param fileName 将存储值放到哪个文件中 可为null 默认存储到mocha_taxi_preference文件
   * @return
   */
  public static PreferenceUtil instance(Context context, String fileName) {
    return Factory.instance(context, fileName);
  }

  public static PreferenceUtil instance(Context context) {
    return instance(context, PREFER_NAME);
  }
  
  private static final class Factory {
    private static PreferenceUtil sPrefer;
    public static PreferenceUtil instance(Context context, String fileName) {
      if (sPrefer == null) {
        sPrefer =  new PreferenceUtil(context, fileName);
      }
      return sPrefer;
    }
  }
  
  public void putString(String key, String value) {
    mEditor.putString(key, value);
    mEditor.commit();
  }
  
  public void putInt(String key, int value) {
    mEditor.putInt(key, value);
    mEditor.commit();
  }

  public void putBoolean(String key, boolean flag) {
    mEditor.putBoolean(key, flag);
    mEditor.commit();
  }
  
  public int getInt(String key) {
    return mPreference.getInt(key, -1);
  }

  public boolean getBoolean(String key) {
    return mPreference.getBoolean(key, false);
  }
  
  public String getString(String key) {
    return mPreference.getString(key, "");
  }
  
}

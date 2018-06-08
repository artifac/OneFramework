package com.one.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class ZipCommentFetcher {

  private static final String COMMENT_KEY = "__comment__";
  private static final String VERSION_KEY = "__version__";
  public static final String SHAREDPREFERENCES_FILES = "__zcmt__";

  private static class Holder {

    private static final ZipCommentFetcher FETCHER = new ZipCommentFetcher();
  }

  public static ZipCommentFetcher getInstance() {
    return Holder.FETCHER;
  }

  private String comment;
  private AtomicInteger lock = new AtomicInteger(0);

  private ZipCommentFetcher() {
  }

  /**
   * 获取 APK 文件中的 comment 区域数据，自带三级缓存
   */
  public String getComment(Context context) {
    if (TextUtils.isEmpty(comment)) {
      while (lock.get() != 2) {
        if (lock.compareAndSet(0, 1)) {
          comment = getFromExternalStorage(context);
          lock.set(2);
        }
      }
    }

    return comment;
  }

  /**
   * 从内存以外获取 comment 信息
   */
  private String getFromExternalStorage(Context context) {
    String cmt = getFromSharedPreferences(context);
    if (TextUtils.isEmpty(cmt)) {
      try {
        cmt = getFromApk(context);
      } catch (IOException e) {
        return "";
      }

      if (!TextUtils.isEmpty(cmt)) {
        saveToSharedPreferences(context, cmt);
      }
    }

    return cmt;
  }

  /**
   * 将 comment 信息存入 SharedPreferences
   */
  private void saveToSharedPreferences(Context context, String cmt) {
    if (TextUtils.isEmpty(cmt)) {
      return;
    }

    SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCES_FILES, 0);
    int currentVersion = getVersionCode(context);
    if (currentVersion != -1) {
      SharedPreferences.Editor edit = preferences.edit();
      edit.putInt(VERSION_KEY, currentVersion);
      edit.putString(COMMENT_KEY, cmt);
      edit.apply();
    }
  }

  /**
   * 从 SharedPreferences 中获取 comment 信息
   */
  private String getFromSharedPreferences(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCES_FILES, 0);
    int version = preferences.getInt(VERSION_KEY, -2);
    int currentVersion = getVersionCode(context);

    if (version != currentVersion) {
      return "";
    } else {
      return preferences.getString(COMMENT_KEY, "");
    }
  }

  /**
   * 从 APK 文件中获取 comment 信息
   */
  private String getFromApk(Context context) throws IOException {
    String packageCodePath = context.getPackageCodePath();
    RandomAccessFile randomAccessFile = new RandomAccessFile(packageCodePath, "r");
    try {
      randomAccessFile.seek(randomAccessFile.length() - 2);
      byte[] lenBytes = new byte[2];
      randomAccessFile.readFully(lenBytes);
      ByteBuffer buffer = ByteBuffer.wrap(lenBytes).order(ByteOrder.LITTLE_ENDIAN);
      int len = buffer.getShort();

      if (len <= 0) {
        return "";
      }

      byte[] commentBytes = new byte[len - 2];
      randomAccessFile.seek(randomAccessFile.length() - len);
      randomAccessFile.readFully(commentBytes);
      return new String(commentBytes, "UTF-8");
    } finally {
      randomAccessFile.close();
    }
  }

  /**
   * 获取应用程序的版本信息
   */
  private static int getVersionCode(Context context) {
    try {
      return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    } catch (Throwable var2) {
      return -1;
    }
  }

}

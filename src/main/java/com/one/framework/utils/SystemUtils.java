package com.one.framework.utils;

import android.Manifest.permission;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ludexiang on 2018/6/8.
 */
@Keep
public class SystemUtils {

  private static String mMacSerial = null;
  private static String mCPUSerial = null;
  public static final String CHANNEL_ID = "channel_id";
  private static final String LOC_GPS = "gps";
  private static final Lock MAIN_PROCESS_LOCK = new ReentrantLock();
  private static Context sContext;
  private static volatile int MAIN_PROCESS_PID = -1;
  private static boolean isCPUSerialnoObtained = false;
  private static boolean isMacSerialnoObtained = false;
  private static final Pattern VERSION_NAME_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+)\\-*.*");

  public static void init(Context context) {
    if (sContext == null && context != null) {
      sContext = context.getApplicationContext();
    }
  }

  public static String getModel() {
    String temp = Build.MODEL;
    return TextUtils.isEmpty(temp) ? "" : temp;
  }

  public static String getVersion() {
    String sdk = VERSION.SDK;
    return TextUtils.isEmpty(sdk) ? "" : sdk;
  }

  public static String getDeviceTime() {
    long time = System.currentTimeMillis();
    return time + "";
  }

  public static String getIMEI() {
    TelephonyManager manager = (TelephonyManager) sContext
        .getSystemService(Context.TELEPHONY_SERVICE);
    String strImei = "";
    if (manager != null) {
      try {
        if (ActivityCompat.checkSelfPermission(sContext, permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
          return manager.getDeviceId();
        }
      } catch (Throwable var3) {
        Log.e("SystemUtil", "", var3);
      }
    }

    if (strImei == null || strImei.length() == 0 || strImei.equals("null")) {
      strImei =
          "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
              + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
              + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
              + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
              + Build.TYPE.length() % 10 + Build.USER.length() % 10;
    }

    String last = getLastModifiedMD5Str();
    return strImei + last;
  }

  public static String getCPUSerialno() {
    if (!TextUtils.isEmpty(mCPUSerial)) {
      return mCPUSerial;
    } else if (isCPUSerialnoObtained) {
      mCPUSerial = "";
      return mCPUSerial;
    } else {
      String str = "";

      try {
        isCPUSerialnoObtained = true;
        Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
        if (pp == null) {
          return null;
        }

        InputStreamReader ir = new InputStreamReader(pp.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);

        while (null != str) {
          str = input.readLine();
          if (str != null) {
            mCPUSerial = str.trim();
            break;
          }
        }
      } catch (IOException var4) {
        var4.printStackTrace();
      }

      return mCPUSerial;
    }
  }

  public static String getAndroidID() {
    return Secure.getString(sContext.getContentResolver(), "android_id");
  }

  public static String getMacSerialno() {
    if (!TextUtils.isEmpty(mMacSerial)) {
      return mMacSerial;
    } else if (isMacSerialnoObtained) {
      mMacSerial = "";
      return mMacSerial;
    } else {
      String str = "";

      try {
        isMacSerialnoObtained = true;
        Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
        if (pp == null) {
          return null;
        }

        InputStreamReader ir = new InputStreamReader(pp.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);

        while (null != str) {
          str = input.readLine();
          if (str != null) {
            mMacSerial = str.trim();
            break;
          }
        }
      } catch (IOException var4) {
        var4.printStackTrace();
      }

      return mMacSerial;
    }
  }

  public static int getScreenWidth() {
    return sContext.getResources().getDisplayMetrics().widthPixels;
  }

  public static int getScreenHeight() {
    return sContext.getResources().getDisplayMetrics().heightPixels;
  }

  public static int getScreenDpi() {
    return sContext.getResources().getDisplayMetrics().densityDpi;
  }

  /** @deprecated */
  @Deprecated
  public static String getVersionName() {
    return getVersionName(sContext);
  }

  public static String getVersionName(Context context) {
    String appVersion = "";

    try {
      String pkgName = context.getApplicationInfo().packageName;
      appVersion = context.getPackageManager().getPackageInfo(pkgName, 0).versionName;
      if (appVersion != null && appVersion.length() > 0) {
        Matcher matcher = VERSION_NAME_PATTERN.matcher(appVersion);
        if (matcher.matches()) {
          appVersion = matcher.group(1);
        }
      }
    } catch (Exception var4) {
      ;
    }

    return appVersion;
  }

  public static int getVersionCode() {
    String pkgName = sContext.getPackageName();

    try {
      PackageInfo pinfo = sContext.getPackageManager()
          .getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS);
      if (pinfo != null) {
        return pinfo.versionCode;
      }
    } catch (Exception var2) {
      var2.printStackTrace();
    }

    return 1;
  }

  public static String getChannelId() {
    return ChannelUtil.getChannel(sContext);
  }

  public static String getAllInstalledPkg() {
    try {
      Intent mainIntent = new Intent("android.intent.action.MAIN");
      mainIntent.addCategory("android.intent.category.LAUNCHER");
      PackageManager pm = sContext.getPackageManager();
      List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
      StringBuilder sb = new StringBuilder();
      if (resolveInfos != null && !resolveInfos.isEmpty()) {
        for (int i = 0; i < resolveInfos.size(); ++i) {
          ResolveInfo ri = (ResolveInfo) resolveInfos.get(i);
          String pkgName = ri.activityInfo.packageName;
          sb.append(pkgName).append("*");
        }
      }

      return sb.toString();
    } catch (Exception var7) {
      return "";
    }
  }

  public static PackageInfo getPackageApkInfo(String packageName) {
    PackageManager packageManager = sContext.getPackageManager();
    List<PackageInfo> pinfos = packageManager.getInstalledPackages(0);

    for (int i = 0; i < pinfos.size(); ++i) {
      if (((PackageInfo) pinfos.get(i)).packageName.equalsIgnoreCase(packageName)) {
        return (PackageInfo) pinfos.get(i);
      }
    }

    return null;
  }

  public static boolean needDownOrUpdate(String packageName, int versionCode) {
    PackageInfo info = getPackageApkInfo(packageName);
    return info == null ? true : versionCode > info.versionCode;
  }

  public static String getNetworkType() {
    String name = "UNKNOWN";
    ConnectivityManager connMgr = (ConnectivityManager) sContext
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (networkInfo != null && 1 == networkInfo.getType()) {
      return "WIFI";
    } else {
      TelephonyManager tm = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
      if (tm == null) {
        return name;
      } else {
        int type = tm.getNetworkType();
        switch (type) {
          case 0:
            name = "UNKNOWN";
            break;
          case 1:
          case 2:
          case 4:
          case 7:
          case 11:
            name = "2G";
            break;
          case 3:
          case 5:
          case 6:
          case 8:
          case 9:
          case 10:
          case 12:
          case 14:
          case 15:
            name = "3G";
            break;
          case 13:
            name = "4G";
            break;
          default:
            name = "UNKNOWN";
        }

        return name;
      }
    }
  }

  public static boolean isWifiEnabled() {
    WifiManager wifiManager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
    return wifiManager.isWifiEnabled();
  }

  public static boolean isGpsEnabled() {
    String gps = android.provider.Settings.System
        .getString(sContext.getContentResolver(), "location_providers_allowed");
    return !TextUtils.isEmpty(gps) && gps.contains("gps");
  }

  private static final String getLastModifiedMD5Str() {
    String path = "/system/build.prop";
    File f = new File(path);
    Long modified = Long.valueOf(f.lastModified());
    char[] data = md5(modified.toString());
    return data == null ? null : new String(data);
  }

  private static final char[] md5(String s) {
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      digest.update(s.getBytes());
      byte[] messageDigest = digest.digest();
      int count = messageDigest.length << 1;
      char[] data = new char[count];
      byte idx = 0;

      for (int i = 0; i < count; i += 2) {
        int tmp = messageDigest[idx] & 255;
        ++idx;
        if (tmp < 16) {
          data[i] = 48;
          data[i + 1] = getHexchar(tmp);
        } else {
          data[i] = getHexchar(tmp >> 4);
          data[i + 1] = getHexchar(tmp & 15);
        }
      }

      return data;
    } catch (NoSuchAlgorithmException var8) {
      var8.printStackTrace();
      return null;
    }
  }

  private static final char getHexchar(int value) {
    return value < 10 ? (char) (48 + value) : (char) (65 + value - 10);
  }

  public static void showSofyKeyboard(final View focusView) {
    focusView.postDelayed(new Runnable() {
      public void run() {
        focusView.setFocusable(true);
        focusView.setFocusableInTouchMode(true);
        focusView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) focusView.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(focusView, 0);
      }
    }, 500L);
  }

  public static void hideSoftKeyboard(Activity context) {
    View currentFocus = context.getCurrentFocus();
    if (currentFocus != null) {
      InputMethodManager manager = (InputMethodManager) context
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      manager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
    } else {
      context.getWindow().setSoftInputMode(3);
    }

  }

  public static void hideSoftKeyboard(View rootView) {
    if (rootView != null) {
      ((InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
          .hideSoftInputFromWindow(rootView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

  }

  public static boolean isSoftInputOpen(Context context) {
    InputMethodManager imm = (InputMethodManager) context
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    return imm.isActive();
  }

  public static boolean isMainProcess(Context context, int pid) {
    if (MAIN_PROCESS_PID == -1) {
      MAIN_PROCESS_LOCK.lock();

      try {
        if (MAIN_PROCESS_PID == -1) {
          String packageName = context.getPackageName();
          ActivityManager activityManager = (ActivityManager) context
              .getSystemService(Context.ACTIVITY_SERVICE);
          List<RunningAppProcessInfo> runningAppProcesses = activityManager
              .getRunningAppProcesses();
          Iterator var5 = runningAppProcesses.iterator();

          while (var5.hasNext()) {
            RunningAppProcessInfo info = (RunningAppProcessInfo) var5.next();
            if (packageName.equals(info.processName)) {
              MAIN_PROCESS_PID = info.pid;
              break;
            }
          }
        }
      } finally {
        MAIN_PROCESS_LOCK.unlock();
      }
    }

    return MAIN_PROCESS_PID != -1 && MAIN_PROCESS_PID == pid;
  }

  public static String getProcessName(Context context, int pid) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
    if (runningApps != null && !runningApps.isEmpty()) {
      Iterator var4 = runningApps.iterator();

      while (var4.hasNext()) {
        RunningAppProcessInfo procInfo = (RunningAppProcessInfo) var4.next();
        if (procInfo.pid == pid) {
          return procInfo.processName;
        }
      }
    }

    return null;
  }

  public static String getPhoneNumber() {
    TelephonyManager tm = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
    if (ActivityCompat.checkSelfPermission(sContext, permission.READ_SMS)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(sContext, permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED) {
      return tm.getLine1Number();
    }
    return null;
  }

  public static boolean checkPermission(Context context, String permission) {
    return checkPermission(context, permission, false);
  }

  public static boolean checkPermission(Context context, String permission, boolean defalutValue) {
    try {
      return PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(permission);
    } catch (Exception var4) {
      return defalutValue;
    }
  }

  public static WifiInfo getWifiInfo(Context context) {
    try {
      if (checkPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return null == wifi ? null : wifi.getConnectionInfo();
      }
    } catch (Exception var2) {
      ;
    }

    return null;
  }

  public static String getCellID(Context context) {
    if (checkPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      CellLocation location;
      if (tm == null || (location = tm.getCellLocation()) == null) {
        return null;
      }

      if (location instanceof GsmCellLocation) {
        GsmCellLocation gsm = (GsmCellLocation) location;
        return gsm == null ? null : Integer.toString(gsm.getCid());
      }

      if (location instanceof CdmaCellLocation) {
        CdmaCellLocation cdma = (CdmaCellLocation) location;
        return cdma == null ? null : Integer.toString(cdma.getBaseStationId());
      }
    }

    return null;
  }

  public static String getLac(Context context) {
    if (checkPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      CellLocation location;
      if (tm == null || (location = tm.getCellLocation()) == null) {
        return null;
      }

      if (location instanceof GsmCellLocation) {
        GsmCellLocation gsm = (GsmCellLocation) location;
        return gsm == null ? null : Integer.toString(gsm.getLac());
      }

      if (location instanceof CdmaCellLocation) {
        CdmaCellLocation cdma = (CdmaCellLocation) location;
        return cdma == null ? null : Integer.toString(cdma.getNetworkId());
      }
    }

    return null;
  }

  public static boolean isRoot() {
    String[] suFiles = new String[]{"/system/bin/su", "/system/xbin/su"};
    String[] var1 = suFiles;
    int var2 = suFiles.length;

    for (int var3 = 0; var3 < var2; ++var3) {
      String suFile = var1[var3];
      File f = new File(suFile);
      if (f != null && f.exists()) {
        return true;
      }
    }

    return false;
  }

  public static boolean isRunningOnEmualtor(Context context) {
    boolean qemuKernel = false;
    boolean hasPhoneNumber = false;
    boolean buildVersion = false;

    try {
      String deviceId = getIMEI();
      hasPhoneNumber = deviceId != null && deviceId.equals("000000000000000");
      Method getPropertyMethod = Class.forName("android.os.SystemProperties")
          .getDeclaredMethod("get", new Class[]{String.class});
      if (getPropertyMethod != null) {
        getPropertyMethod.setAccessible(true);
        qemuKernel = "1"
            .equals(getPropertyMethod.invoke((Object) null, new Object[]{"ro.kernel.qemu"}));
      }

      buildVersion =
          Build.MODEL.equalsIgnoreCase("sdk") || Build.MODEL.equalsIgnoreCase("google_sdk")
              || Build.MODEL.contains("Droid4X");
    } catch (Exception var7) {
    }

    return qemuKernel || buildVersion || hasPhoneNumber;
  }

  public static String getIMEI(Context context) {
    if (!checkPermission(context, "android.permission.READ_PHONE_STATE")) {
      return null;
    } else {
      TelephonyManager manager = (TelephonyManager) context
          .getSystemService(Context.TELEPHONY_SERVICE);
      return manager == null ? null : manager.getDeviceId();
    }
  }

  public static String getServiceProvider(Context context) {
    TelephonyManager manager = (TelephonyManager) context
        .getSystemService(Context.TELEPHONY_SERVICE);
    String name = "";
    if (ActivityCompat.checkSelfPermission(sContext, permission.READ_PHONE_STATE)
        != PackageManager.PERMISSION_GRANTED) {
      String imsi = manager.getSubscriberId();
      if(!TextUtils.isEmpty(imsi)) {
        if(!imsi.startsWith("46000") && !imsi.startsWith("46002")) {
          if(imsi.startsWith("46001")) {
            name = "中国联通";
          } else if(imsi.startsWith("46003")) {
            name = "中国电信";
          }
        } else {
          name = "中国移动";
        }
      }
    }
    return name;
  }

  public static boolean isAppInstalled(Context context, String pageName) {
    try {
      PackageInfo packinfo = context.getPackageManager().getPackageInfo(pageName, 0);
      return true;
    } catch (NameNotFoundException var3) {
      return false;
    }
  }

  public static int getNavigationBarHeight(Context context) {
    int result = 0;
    if(hasNavBar(context)) {
      Resources res = context.getResources();
      int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
      if(resourceId > 0) {
        result = res.getDimensionPixelSize(resourceId);
      }
    }

    return result;
  }

  private static boolean hasNavBar(Context context) {
    Resources res = context.getResources();
    int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
    if(resourceId != 0) {
      boolean hasNav = res.getBoolean(resourceId);
      String sNavBarOverride = getNavBarOverride();
      if("1".equals(sNavBarOverride)) {
        hasNav = false;
      } else if("0".equals(sNavBarOverride)) {
        hasNav = true;
      }

      return hasNav;
    } else {
      return !ViewConfiguration.get(context).hasPermanentMenuKey();
    }
  }

  private static String getNavBarOverride() {
    String sNavBarOverride = null;
    if(VERSION.SDK_INT >= 19) {
      try {
        Class c = Class.forName("android.os.SystemProperties");
        Method m = c.getDeclaredMethod("get", new Class[]{String.class});
        m.setAccessible(true);
        sNavBarOverride = (String)m.invoke((Object)null, new Object[]{"qemu.hw.mainkeys"});
      } catch (Throwable var3) {
        ;
      }
    }

    return sNavBarOverride;
  }
}

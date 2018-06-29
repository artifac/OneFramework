
package com.one.framework.app.style;


import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ThemeStyles {

  /**
   * MIUI的沉浸支持透明白色字体和透明黑色字体
   * https://dev.mi.com/console/doc/detail?pId=1159
   */
  public static boolean setMIUIStatusBarLightMode(Activity activity, boolean darkmode) {
    Window window = activity.getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    boolean result = false;
    Class<? extends Window> clazz = activity.getWindow().getClass();
    try {
      int darkModeFlag = 0;
      Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
      Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
      darkModeFlag = field.getInt(layoutParams);
      Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
      extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
   */
  public static boolean setFlymeStatusBarLightMode(Activity activity, boolean darkmode) {
    boolean result = false;
    try {
      WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
      Field darkFlag = WindowManager.LayoutParams.class
          .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
      Field meizuFlags = WindowManager.LayoutParams.class
          .getDeclaredField("meizuFlags");
      darkFlag.setAccessible(true);
      meizuFlags.setAccessible(true);
      int bit = darkFlag.getInt(null);
      int value = meizuFlags.getInt(lp);
      if (darkmode) {
        value |= bit;
      } else {
        value &= ~bit;
      }
      meizuFlags.setInt(lp, value);
      activity.getWindow().setAttributes(lp);
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  static void setContentTopPadding(Activity activity, int padding) {
    ViewGroup mContentView = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
    mContentView.setPadding(0, padding, 0, 0);
  }

  static int getPxFromDp(Context context, float dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        context.getResources().getDisplayMetrics());
  }
}


package com.one.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.one.framework.R;

public class ToastUtils {

  /**
   * 要展示的文案
   */
  public static String showStr = "";
  private static LayoutInflater mInflater;

  /**
   * 弹出dialog
   */
  public static void safeShowDialog(Activity activity, int id, String str) {
    try {
      showStr = str;
      activity.showDialog(id);
    } catch (Exception e) {

    }
  }

  /**
   * 隐藏dialog
   */
  public static void safeDismissDialog(Activity activity, int id) {
    try {
      showStr = "";
      Activity realActivity = activity;
      if (realActivity != null && !realActivity.isFinishing()) {
        activity.removeDialog(id);
      }
    } catch (Throwable e) {
      Log.e("globalUtils", "dialog Exception", e);
    }
  }


  public static void toast(Context context, String text, int iconResId) {
    if (TextUtils.isEmpty(text)) { //如果传入的是空，则不用弹toast，直接返回
      return;
    }

    if (mInflater == null) {
      mInflater = LayoutInflater.from(context);
    }

    View v = mInflater.inflate(R.layout.one_base_toast_layout, null);
    if (null != v) {
      TextView msg = (TextView) v.findViewById(R.id.mocha_base_toast_message);
      if (null == msg) {
        return;
      }
      msg.setText(text);
      ImageView icon = (ImageView) v.findViewById(R.id.mocha_base_toast_icon);
      if (null != icon && iconResId > 0) {
        icon.setImageResource(iconResId);
        icon.setVisibility(View.VISIBLE);
      }

      // final int Y_OFFSET = 120;
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.show();
      } else {
        OneBaseToast walletToast = new OneBaseToast(context);
        walletToast.setDuration(Toast.LENGTH_SHORT);
//        walletToast.setGravity(Gravity.BOTTOM,0,0);
        walletToast.setView(v);
        walletToast.show();
      }
    }
  }

  public static void toast(Context context, String text) {
    toast(context, text, 0);
  }

}

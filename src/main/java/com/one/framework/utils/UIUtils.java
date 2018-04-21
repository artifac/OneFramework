package com.one.framework.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.view.WindowManager;

/**
 * Created by ludexiang on 2018/4/16.
 */

public class UIUtils {

  private static long sLastClickTime = 0;
  private static final long DURATION = 500L;

  private static Integer sScreenWidth = null;
  private static Integer sScreenHeight = null;

  /**
   * 检验是否是快速点击
   */
  public static boolean isFastDoubleClick() {
    long curTime = System.currentTimeMillis();
    boolean fastClick = curTime - sLastClickTime < DURATION;
    sLastClickTime = curTime;
    return fastClick;
  }

  /**
   * 获取屏幕宽度
   */
  public static int getScreenWidth(Context ctx) {
    if (sScreenWidth != null) {
      return sScreenWidth;
    }

    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    sScreenWidth = wm.getDefaultDisplay().getWidth();
    sScreenHeight = wm.getDefaultDisplay().getHeight();
    return sScreenWidth;
  }

  /**
   * 获取屏幕高度
   */
  public static int getScreenHeight(Context ctx) {
    if (sScreenHeight != null) {
      return sScreenHeight;
    }

    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    sScreenWidth = wm.getDefaultDisplay().getWidth();
    sScreenHeight = wm.getDefaultDisplay().getHeight();
    return sScreenHeight;
  }

  public static Drawable rippleDrawableRect(int color, int maskColor) {
    Drawable rectDrawable = color == 0 ? null : new ColorDrawable(color);
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable maskDrawable = new ColorDrawable(Color.WHITE);
      return new RippleDrawable(ColorStateList.valueOf(maskColor), rectDrawable, maskDrawable);
    } else {
      StateListDrawable maskDrawable = new StateListDrawable();
      maskDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(maskColor));
      Drawable layerDrawable = rectDrawable == null ? maskDrawable
          : new LayerDrawable(new Drawable[]{rectDrawable, maskDrawable});
      return layerDrawable;
    }
  }

  /**
   * unlike Android api, the rounded corners accepts only 4 value
   */
  public static Drawable rippleDrawableRounded(int color, int maskColor, int[] roundedCorners) {
    Drawable rectDrawable = color == 0 ? null : colorDrawableRounded(color, roundedCorners);
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable maskDrawable = colorDrawableRounded(Color.WHITE, roundedCorners);
      return new RippleDrawable(ColorStateList.valueOf(maskColor), rectDrawable, maskDrawable);
    } else {
      StateListDrawable maskDrawable = new StateListDrawable();
      maskDrawable.addState(new int[]{android.R.attr.state_pressed}, colorDrawableRounded(maskColor, roundedCorners));
      Drawable layerDrawable = rectDrawable == null ? maskDrawable
          : new LayerDrawable(new Drawable[]{rectDrawable, maskDrawable});
      return layerDrawable;
    }
  }

  /**
   * unlike Android api, the rounded corners accepts only 4 value
   */
  private static Drawable colorDrawableRounded(int color, int[] rc) {
    float[] radii = new float[]{
        rc[0], rc[0],
        rc[1], rc[1],
        rc[2], rc[2],
        rc[3], rc[3]
    };
    RoundRectShape shape = new RoundRectShape(radii, null, null);
    ShapeDrawable drawable = new ShapeDrawable(shape);
    drawable.getPaint().setColor(color);
    return drawable;
  }

  private static Drawable colorDrawableRounded(int color, int roundedCorner) {
    float[] radii = new float[]{
        roundedCorner, roundedCorner,
        roundedCorner, roundedCorner,
        roundedCorner, roundedCorner,
        roundedCorner, roundedCorner};
    RoundRectShape shape = new RoundRectShape(radii, null, null);
    ShapeDrawable drawable = new ShapeDrawable(shape);
    drawable.getPaint().setColor(color);
    return drawable;
  }
}

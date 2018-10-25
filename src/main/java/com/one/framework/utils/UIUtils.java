package com.one.framework.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ludexiang on 2018/4/16.
 */

public class UIUtils {

  private static final String FORMAT = "\\{[^}]*\\}";
  private static final int FORMAT_COLOR = Color.parseColor("#f05b48");

  private static long sLastClickTime = 0;
  private static final long DURATION = 500L;

  private static Integer sScreenWidth = null;
  private static Integer sScreenHeight = null;
  private static Integer sStatusBarHeight = null;

  /**
   * 检验是否是快速点击
   */
  public static boolean isFastDoubleClick() {
    long curTime = System.currentTimeMillis();
    boolean fastClick = curTime - sLastClickTime < DURATION;
    sLastClickTime = curTime;
    return fastClick;
  }

  public static int dip2pxInt(Context context, float dip) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return (int) (dip * metrics.density);
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

  /**
   * 获取StatusBar的高度
   */
  public static int getStatusbarHeight(Context context) {
    if (sStatusBarHeight != null) {
      return sStatusBarHeight;
    }

    Resources resources = context.getResources();
    int resId = resources.getIdentifier("status_bar_height", "dimen", "android");
    if (resId <= 0) {
      return (sStatusBarHeight = dip2pxInt(context, 25));
    }

    try {
      return (sStatusBarHeight = resources.getDimensionPixelSize(resId));
    } catch (Resources.NotFoundException e) {
      return (sStatusBarHeight = dip2pxInt(context, 25));
    }

  }

  public static int getViewWidth(View view) {
    calculateViewMeasure(view);
    return view.getMeasuredWidth();
  }

  public static int getViewHeight(View view) {
    calculateViewMeasure(view);
    return view.getMeasuredHeight();
  }

  /**
   * 测量控件的尺寸
   */
  private static void calculateViewMeasure(View view) {
    try {
      int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
      int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
      view.measure(w, h);
    } catch (Exception e) {

    }

  }

  public static Drawable rippleDrawableRect(int color, int maskColor) {
    Drawable rectDrawable = color == 0 ? null : colorDrawableRounded(color, 0);
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable maskDrawable = colorDrawableRounded(color, 0);
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
      maskDrawable.addState(new int[]{android.R.attr.state_pressed},
          colorDrawableRounded(maskColor, roundedCorners));
      Drawable layerDrawable = rectDrawable == null ? maskDrawable
          : new LayerDrawable(new Drawable[]{rectDrawable, maskDrawable});
      return layerDrawable;
    }
  }

  public static Drawable rippleDrawableRounded(int color, int maskColor, int roundedCorners) {
    Drawable rectDrawable = color == 0 ? null : colorDrawableRounded(color, roundedCorners);
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable maskDrawable = colorDrawableRounded(Color.WHITE, roundedCorners);
      return new RippleDrawable(ColorStateList.valueOf(maskColor), rectDrawable, maskDrawable);
    } else {
      StateListDrawable maskDrawable = new StateListDrawable();
      maskDrawable.addState(new int[]{android.R.attr.state_pressed},
          colorDrawableRounded(maskColor, roundedCorners));
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

  /**
   * view获取bitmap
   *
   * @param addViewContent
   * @return
   */
  public static Bitmap getViewBitmap(View addViewContent) {
    addViewContent.setDrawingCacheEnabled(true);
    addViewContent.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    addViewContent.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    addViewContent.layout(0, 0,
        addViewContent.getMeasuredWidth(),
        addViewContent.getMeasuredHeight());

    addViewContent.buildDrawingCache(true);
    Bitmap bitmap = Bitmap.createBitmap(addViewContent.getDrawingCache());
    addViewContent.setDrawingCacheEnabled(false);

    return bitmap;
  }

  public static CharSequence highlight(String input, String format, int color) {
    Pattern pattern = Pattern.compile(format);
    Matcher matcher = pattern.matcher(input);

    Stack<Range> matches = new Stack<>();
    while (matcher.find()) {
      matches.push(new Range(matcher.start(), matcher.end()));
    }

    SpannableStringBuilder builder = new SpannableStringBuilder(input);
    while (matches.size() > 0) {
      Range range = matches.pop();
      builder.delete(range.start, range.start + 1);
      builder.delete(range.end - 2, range.end - 1);
      builder.setSpan(new ForegroundColorSpan(color), range.start, range.end - 2,
          Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    return builder;
  }

  public static CharSequence highlight(String input, int color) {
    return highlight(input, FORMAT, color);
  }

  public static CharSequence highlight(String input) {
    return highlight(input, FORMAT, FORMAT_COLOR);
  }

  static class Range {

    public final int start;
    public final int end;

    public Range(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }
}

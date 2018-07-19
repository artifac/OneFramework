/*
 * Copyright (C) 2013 Baidu Inc. All rights ResUtilserved.
 */
package com.one.framework.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import com.one.framework.R;
import com.one.framework.log.Logger;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Toast控件，为了解决4.1上取消勾选通知消息，导致系统toast不能正常弹出问题。
 */
public class OneBaseToast {

  private final static String TAG = OneBaseToast.class.getSimpleName();//SUPPRESS CHECKSTYLE

  /**
   * 标示短时间显示toast类型
   */
  public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;

  /**
   * 标示识长时间显示toast类型
   */
  public static final int LENGTH_LONG = Toast.LENGTH_LONG;

  private static final int LONG_DELAY = 3500; // 3.5 seconds
  private static final int SHORT_DELAY = 2000; // 2 seconds
  Context mContext;

  private WindowManager mWdm;
  int mDuration;
  int mGravity;

  private View mNextView;
  private LayoutParams mParams;

  int mX, mY;
  float mVerticalMargin, mHorizontalMargin;

  private Handler mHandler = new Handler(Looper.getMainLooper());

  private static List<View> mRecords = new ArrayList<View>();
  private static CancleRunnable mCancleRunnale;

  /**
   * @param context 上下文
   */
  public OneBaseToast(Context context) {
    mWdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    mContext = context;
    Toast toast = new Toast(context);
    mY = toast.getYOffset();
    initToastParams(toast);
  }

  /**
   * 自定义toast 构造函数
   *
   * @param context 上下文
   * @param text toast显示的文字
   * @param duration 显示长短时间类型
   */
  public OneBaseToast(Context context, String text, int duration) {
    mWdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    mContext = context;
    Toast toast = Toast.makeText(context, text, duration);
    mY = toast.getYOffset();
    initToastParams(toast);
  }

  private void initToastParams(Toast toast) {
    try {
      Field field = toast.getClass().getDeclaredField("mTN");
      field.setAccessible(true);
      Object obj = field.get(toast);
      Field paramsField = obj.getClass().getDeclaredField("mParams");
      paramsField.setAccessible(true);
      mParams = (LayoutParams) paramsField.get(obj);
    } catch (Exception e) {
      e.printStackTrace();
      mParams = new LayoutParams();
      mParams.height = LayoutParams.WRAP_CONTENT;
      mParams.width = LayoutParams.WRAP_CONTENT;
      mParams.format = PixelFormat.TRANSLUCENT;
      mParams.type = LayoutParams.TYPE_TOAST;
      mParams.windowAnimations = R.style.OneToastAnim;
      mParams.setTitle("Toast");
      mParams.flags = LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_NOT_FOCUSABLE
          | LayoutParams.FLAG_NOT_TOUCHABLE;
    }
    mGravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
    mY = 0;
    mNextView = toast.getView();
  }

  /**
   * 显示toast
   */
  public void show() {
    // 主要解决 线程同步问题，使用messagequene解决极端条件下线程同步问题。
    mHandler.post(new Runnable() {
      @Override
      public void run() {
        cancleAll();
        handleShow(mNextView);
      }
    });
  }

  private void cancleAll() {
    if (mCancleRunnale != null) {
      mCancleRunnale.discard();
      mHandler.removeCallbacks(mCancleRunnale);
      mCancleRunnale = null;
    }
    for (View view : mRecords) {
      handleHide(view);
    }
    mRecords.clear();
  }

  private void handleShow(View view) {
    Context context = mContext.getApplicationContext();
    if (context == null) {
      context = mContext;
    }
    mWdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    // We can resolve the Gravity here by using the Locale for getting
    // the layout direction
    final int gravity = mGravity;
    mParams.gravity = gravity;
    if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
      mParams.horizontalWeight = 1.0f;
    }
    if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
      mParams.verticalWeight = 1.0f;
    }
    mParams.x = mX;
    mParams.y = mY;
    mParams.verticalMargin = mVerticalMargin;
    mParams.horizontalMargin = mHorizontalMargin;
    try {
      if (view.getParent() != null) {
        mWdm.updateViewLayout(view, mParams);
      } else {
        mWdm.addView(view, mParams);
      }
      mRecords.add(view);
      mCancleRunnale = new CancleRunnable();
      mHandler.postDelayed(mCancleRunnale, mDuration == LENGTH_LONG ? LONG_DELAY : SHORT_DELAY);
    } catch (Exception e) {
      Logger.e("ldx", "e " + e);
      // catch java.lang.RuntimeException: Adding window failed
    }

  }

  private void handleHide(View view) {
    if (view != null) {
      // note: checking parent() just to make sure the view has
      // been added... i have seen cases where we get here when
      // the view isn't yet added, so let's try not to crash.
      if (view.getParent() != null) {
        try {
          mWdm.removeView(view);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Set the view to show.
   *
   * @see #getView
   */
  public void setView(View view) {
    mNextView = view;
  }

  /**
   * Return the view.
   *
   * @see #setView
   */
  public View getView() {
    return mNextView;
  }

  /**
   * Set how long to show the view for.
   *
   * @see #LENGTH_SHORT
   * @see #LENGTH_LONG
   */
  public void setDuration(int duration) {
    mDuration = duration;
  }

  /**
   * Return the duration.
   *
   * @see #setDuration
   */
  public int getDuration() {
    return mDuration;
  }

  /**
   * Set the margins of the view.
   *
   * @param horizontalMargin The horizontal margin, in percentage of the container width, between
   * the container's edges and the notification
   * @param verticalMargin The vertical margin, in percentage of the container height, between the
   * container's edges and the notification
   */
  public void setMargin(float horizontalMargin, float verticalMargin) {
    mHorizontalMargin = horizontalMargin;
    mVerticalMargin = verticalMargin;
  }

  /**
   * Return the horizontal margin.
   */
  public float getHorizontalMargin() {
    return mHorizontalMargin;
  }

  /**
   * Return the vertical margin.
   */
  public float getVerticalMargin() {
    return mVerticalMargin;
  }

  /**
   * Set the location at which the notification should appear on the screen.
   *
   * @see Gravity
   * @see #getGravity
   */
  public void setGravity(int gravity, int xOffset, int yOffset) {
    mGravity = gravity;
    mX = xOffset;
    mY = yOffset;
  }

  /**
   * Get the location at which the notification should appear on the screen.
   *
   * @see Gravity
   * @see #getGravity
   */
  public int getGravity() {
    return mGravity;
  }

  /**
   * Return the X offset in pixels to apply to the gravity's location.
   */
  public int getXOffset() {
    return mX;
  }

  /**
   * Return the Y offset in pixels to apply to the gravity's location.
   */
  public int getYOffset() {
    return mY;
  }

  /**
   * 取消ToastRunnable
   */
  public class CancleRunnable implements Runnable {

    volatile boolean discarded = false;

    @Override
    public void run() {
      if (!discarded) {
        cancleAll();
      }
    }

    public void discard() {
      discarded = true;
    }
  }

}

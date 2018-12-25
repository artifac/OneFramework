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
import java.lang.ref.WeakReference;
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

  private static final int LONG_DELAY = 2000; // 2 seconds
  private static final int SHORT_DELAY = 1500; // 1.5 seconds
  private WeakReference<Context> mWeakActivity;

  private WindowManager mWdm;
  int mDuration;
  int mGravity;

  private View mNextView;

  int mX, mY;
  float mVerticalMargin, mHorizontalMargin;

  private Handler mHandler = new Handler(Looper.getMainLooper());

  private static List<View> mRecords = new ArrayList<View>();
  private static CancleRunnable mCancelRunnable;

  /**
   * @param context 上下文
   */
  public OneBaseToast(Context context) {
    mWeakActivity = new WeakReference<>(context);
    if (mWeakActivity.get() != null) {
      Toast toast = new Toast(mWeakActivity.get());
      mY = toast.getYOffset();
    }
  }

  /**
   * 自定义toast 构造函数
   *
   * @param context 上下文
   * @param text toast显示的文字
   * @param duration 显示长短时间类型
   */
  public OneBaseToast(Context context, String text, int duration) {
    mWeakActivity = new WeakReference<>(context);
    if (mWeakActivity.get() != null) {
      Toast toast = Toast.makeText(mWeakActivity.get(), text, duration);
      mY = toast.getYOffset();
    }
  }

  /**
   * 显示toast
   */
  public void show() {
    // 主要解决 线程同步问题，使用messagequene解决极端条件下线程同步问题。
    mHandler.post(() -> {
      cancleAll();
      handleShow(mNextView);
    });
  }

  private void cancleAll() {
    if (mCancelRunnable != null) {
      mCancelRunnable.discard();
      mHandler.removeCallbacks(mCancelRunnable);
      mCancelRunnable = null;
    }
    for (View view : mRecords) {
      handleHide(view);
    }
    mRecords.clear();
  }

  private void handleShow(View view) {
    if (mWeakActivity != null && mWeakActivity.get() != null) {
      mWdm = (WindowManager) mWeakActivity.get().getSystemService(Context.WINDOW_SERVICE);
      // We can resolve the Gravity here by using the Locale for getting
      // the layout direction
      WindowManager.LayoutParams params = new WindowManager.LayoutParams();
      final int gravity = mGravity;
      params.gravity = mGravity;
      if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
        params.horizontalWeight = 1.0f;
      }
      if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
        params.verticalWeight = 1.0f;
      }
      params.height = LayoutParams.WRAP_CONTENT;
      params.width = LayoutParams.WRAP_CONTENT;
      params.x = mX;
      params.y = mY;
      params.verticalMargin = mVerticalMargin;
      params.horizontalMargin = mHorizontalMargin;
      params.format = PixelFormat.TRANSLUCENT;
      params.windowAnimations = R.style.OneToastAnim;
      if (view.getParent() != null) {
        mWdm.updateViewLayout(view, params);
      } else {
        mWdm.addView(view, params);
      }
      mRecords.add(view);
      mCancelRunnable = new CancleRunnable();
      mHandler.postDelayed(mCancelRunnable, mDuration == LENGTH_LONG ? LONG_DELAY : SHORT_DELAY);
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
          mWdm = null;
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

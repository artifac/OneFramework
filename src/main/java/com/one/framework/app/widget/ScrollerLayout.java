package com.one.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import com.one.framework.app.widget.base.IMovePublishListener;

/**
 * Created by ludexiang on 2018/4/21.
 */

public class ScrollerLayout extends RelativeLayout {

  private IMovePublishListener mMoveListener;

  private static final int RATIO = 2;

  private int mDownX;
  private int mDownY;
  private int mLastDownX;
  private int mLastDownY;

  private VelocityTracker mVelocityTracker;

  private int mMinVelocity;
  private int mMaxVelocity;
  private int mMinScaleTouch;

  public ScrollerLayout(Context context) {
    this(context, null);
  }

  public ScrollerLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ScrollerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    ViewConfiguration config = ViewConfiguration.get(context);
    mMinScaleTouch = config.getScaledTouchSlop();
    mMinVelocity = config.getScaledMinimumFlingVelocity();
    mMaxVelocity = config.getScaledMaximumFlingVelocity();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (!checkListener()) {
      return super.dispatchTouchEvent(ev);
    }
    addTracker(ev);
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        mLastDownX = mDownX = (int) ev.getX();
        mLastDownY = mDownY = (int) ev.getY();
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        int x = (int) (ev.getX() + 0.5);
        int y = (int) (ev.getY() + 0.5);

        int offsetX = (x - mDownX) / RATIO;
        int offsetY = (y - mDownY) / RATIO;
        mMoveListener.onMove(offsetX, offsetY);
        break;
      }
      case MotionEvent.ACTION_OUTSIDE:
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        int x = (int) (ev.getX() + 0.5);
        int y = (int) (ev.getY() + 0.5);

//        if (Math.abs(x - mLastDownX) <= mMinScaleTouch && Math.abs(y - mLastDownY) <= mMinScaleTouch) {
//          return true;
//        }
        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
        float yVelocity = mVelocityTracker.getYVelocity();
        boolean bottomToUp = mVelocityTracker.getYVelocity() < 0;
        boolean isFling = Math.abs(yVelocity) > mMinVelocity;
        mMoveListener.onUp(bottomToUp, isFling);
        releaseVelocityTracker();
        break;
      }
    }
    return super.dispatchTouchEvent(ev);
  }

  public void setMoveListener(IMovePublishListener listener) {
    mMoveListener = listener;
  }

  private boolean checkListener() {
    return mMoveListener != null;
  }

  private void addTracker(MotionEvent ev) {
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(ev);
  }

  private void releaseVelocityTracker() {
    if (mVelocityTracker != null) {
      mVelocityTracker.recycle();
      mVelocityTracker = null;
    }
  }

}

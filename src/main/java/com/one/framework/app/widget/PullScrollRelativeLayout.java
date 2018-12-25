package com.one.framework.app.widget;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import com.one.framework.app.widget.base.IMovePublishListener;
import com.one.framework.app.widget.base.IPullView;
import com.one.framework.log.Logger;
import com.one.framework.utils.SystemUtils;
import com.one.framework.utils.UIUtils;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class PullScrollRelativeLayout extends RelativeLayout {

  private static final String TAG = PullScrollRelativeLayout.class.getSimpleName();

  private static float RATIO = 3.0f;
  private Context mContext;
  private IMovePublishListener mMoveListener;
  private VelocityTracker mTracker;
  private int mMinScroll;
  private int mMinVelocity;
  private int mMaxVelocity;
  private int mLastDownX, mLastDownY;
  private int mActionDownX, mActionDownY;
  private int mTouchDownX, mTouchDonwY;
  private View mScrollView;
  private IPullView mPullView;
  private boolean isScrolling = false;
  private int mActivePointerId = INVALID_POINTER_ID;

  public PullScrollRelativeLayout(Context context) {
    this(context, null);
  }

  public PullScrollRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullScrollRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mContext = context;
    ViewConfiguration configuration = ViewConfiguration.get(context);
    mMinScroll = configuration.getScaledTouchSlop();
    mMinVelocity = configuration.getScaledMinimumFlingVelocity();
    mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
    setClickable(true);
  }

  public void setMoveListener(IMovePublishListener listener) {
    mMoveListener = listener;
  }

  public void setScrollView(IPullView scrollView) {
    mPullView = scrollView;
    mScrollView = mPullView.getView();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (!checkScrollView()) {
      return super.dispatchTouchEvent(ev);
    }
    addVelocityTracker(ev);
    switch (ev.getAction() & MotionEvent.ACTION_MASK) {
      // 处理两个手交替 begin
      case MotionEvent.ACTION_POINTER_DOWN: {
//        mActionDownX = (int) ev.getX();
//        mActionDownY = (int) ev.getY();
        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        mActionDownX = (int) MotionEventCompat.getX(ev, pointerIndex);
        mActionDownY = (int) MotionEventCompat.getY(ev, pointerIndex);
        break;
      }
      case MotionEvent.ACTION_POINTER_UP: {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

        if (pointerId == mActivePointerId) {
          // This was our active pointer going up. Choose a new
          // active pointer and adjust accordingly.
          final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
          mActionDownX = (int) MotionEventCompat.getX(ev, newPointerIndex);
          mActionDownY = (int) MotionEventCompat.getY(ev, newPointerIndex);
          mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
        break;
      }
      // 处理两个手交替 end
      case MotionEvent.ACTION_DOWN: {
//        float x = ev.getX();
//        float y = ev.getY();
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final float x = MotionEventCompat.getX(ev, pointerIndex);
        final float y = MotionEventCompat.getY(ev, pointerIndex);
        if (checkScrollView()) {
          mLastDownX = mActionDownX = (int) x;
          mLastDownY = mActionDownY = (int) y;
          // Save the ID of this pointer (for dragging)
          mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
          Logger.i("Pull", "downX >>>> " + mLastDownY  + " downY " + mLastDownY);
        }
        break;
      }
      case MotionEvent.ACTION_MOVE: {
//        int curX = (int) ev.getX();
//        int curY = (int) ev.getY();
        SystemUtils.hideSoftKeyboard(this);
        final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
        final int curX = (int) MotionEventCompat.getX(ev, pointerIndex);
        final int curY = (int) MotionEventCompat.getY(ev, pointerIndex);
        boolean isScrollTop = (curY - mLastDownY >= mMinScroll && mPullView.getScrollingPadding() == 0) || (mScrollView.getTranslationY() > 0 && !mPullView.isScrollBottom()) || mPullView.isHeaderNeedScroll();
        boolean isScrollBottom = (mLastDownY - curY >= mMinScroll && mPullView.isScrollBottom()) && mScrollView.getTranslationY() <= 0;
        isScrolling = isScrollTop || isScrollBottom;
        if (checkScrollView()) {
          if (canScroll()) {
            if (Math.abs(mScrollView.getTranslationY()) >= UIUtils.getScreenHeight(mContext) / 2
                || mPullView.getHeaderScrollHeight() >= UIUtils.getScreenHeight(mContext) / 2) {
              RATIO += .085f;
            }
            float offsetX = (curX - mActionDownX) / RATIO;
            float offsetY = (curY - mActionDownY) / RATIO;
            handleMove(offsetX, offsetY);
            mActionDownX = curX;
            mActionDownY = curY;
//            return true; // 去掉此处之后 有滑动阻尼的时候 listItem 就不会出现按下的效果了
          } else {
            // 不是顶部 | 底部的时候设置mScrollView的Y轴方向滚动为0 so far 简单粗暴
            mScrollView.setTranslationY(0);
//            handleMove(0, 0);
          }
        }

        break;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_OUTSIDE:
      case MotionEvent.ACTION_UP: {
        int upActionX = (int) ev.getRawX();
        int upActionY = (int) ev.getRawY();
        int scrollY = (int) mScrollView.getTranslationY();
        Logger.v("Pull", "up mLastDownX >>>> " + mLastDownX + " DownY " + mLastDownY + " upX " + upActionX + " upY " + upActionY + " scrollY " + scrollY);
        if (scrollY != 0) { // 有阻尼效果
          mTracker.computeCurrentVelocity(1000, mMaxVelocity);
          float yVelocity = mTracker.getYVelocity();
          boolean isFling = Math.abs(yVelocity) > mMinVelocity ? true : false;
          boolean bottom2Up = scrollY > 0 ? true : false;
          handleUp(bottom2Up, isFling);
          recycleVelocityTracker();
          return true;
        }
        break;
      }
    }
    return super.dispatchTouchEvent(ev);
  }

  /**
   * 这段代码来自谷歌官方的 SwipeRefreshLayout
   * 应用场景已经在英文注释中解释清楚
   * 大部分第三方下拉刷新库都保留了这段代码，本库也不例外
   */
  @Override
  public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    // if this is a List < L or another view that doesn't support nested
    // scrolling, ignore this request so that the vertical scroll event
    // isn't stolen
    View target = mPullView.getView();
    if ((android.os.Build.VERSION.SDK_INT >= 21 || !(target instanceof AbsListView))
        && (target == null || ViewCompat.isNestedScrollingEnabled(target))) {
      super.requestDisallowInterceptTouchEvent(disallowIntercept);
      //} else {
      // Nope.
    }
  }

  private void handleMove(float offsetX, float offsetY) {
    if (mMoveListener != null) {
      mMoveListener.onMove(offsetX, offsetY);
    }
  }

  private void handleUp(boolean bottom2Up, boolean isFling) {
    RATIO = 3;
    if (mMoveListener != null) {
      mMoveListener.onUp(bottom2Up, isFling);
    }
  }

  private boolean checkScrollView() {
    return mScrollView != null;
  }

  /**
   * 判断是否可滚动
   */
  private boolean canScroll() {
    return isScrolling && (mPullView.getScrollingPadding() == 0 || mPullView.isScrollBottom());
  }

  private void addVelocityTracker(MotionEvent event) {
    if (mTracker == null) {
      mTracker = VelocityTracker.obtain();
    }
    mTracker.addMovement(event);
  }

  private void recycleVelocityTracker() {
    if (mTracker != null) {
      mTracker.recycle();
      mTracker = null;
    }
  }
}

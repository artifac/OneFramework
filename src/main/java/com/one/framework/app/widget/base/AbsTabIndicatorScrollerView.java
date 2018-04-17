package com.one.framework.app.widget.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * Created by ludexiang on 2018/3/28.
 */

public abstract class AbsTabIndicatorScrollerView extends HorizontalScrollView {

  private View mChildView;
  private float mLastDownX;
  private float mDownX;
  private static final float RATIO = 2f;
  private boolean isMove = false;

  public AbsTabIndicatorScrollerView(Context context) {
    this(context, null);
  }

  public AbsTabIndicatorScrollerView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AbsTabIndicatorScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    if (getChildCount() > 0) {
      mChildView = getChildAt(0);
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    mLastDownX = mDownX = ev.getX();
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_MOVE: {
        float curX = ev.getX() + 0.5f;
        if (isScroll()) {
          int scrollX = getScrollX();
          isMove = (scrollX == 0 && curX > mDownX) || (
              (scrollX == getScrollXOffset() || mChildView.getMeasuredWidth() <= getWidth())
                  && curX < mDownX);
        } else {
          isMove = false;
        }
        if (isMove) {
          float offset = (curX - mLastDownX) / RATIO;
          handleMove(offset);
          mLastDownX = curX; // 重新设置为了不让offset值过大导致滚动太快
          return true;
        }
      }
      case MotionEvent.ACTION_UP: {
        if (isMove) {
          handleUp();
        }
        break;
      }
    }
    return super.onTouchEvent(ev);
  }

  private void handleMove(float moveX) {
    int x = (int) (getTranslationX() + moveX + 0.5f);
    float scale = Math.abs(x * 1f / getMeasuredWidth());
    onScale(scale);
    setTranslationX(x);
  }

  private void handleUp() {
    float move = getTranslationX();
    float scale = Math.abs(move / getWidth());
    int duration = (int) (200 - scale * 200);
    goonTranslate(duration);
  }

  private void goonTranslate(long duration) {
    float moveTranslateX = getTranslationX();

    ValueAnimator translate = ValueAnimator.ofFloat(moveTranslateX, 0);
    translate.setDuration(duration);
    translate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float translate = (Float) animation.getAnimatedValue();
        setTranslationX(translate);
      }
    });
    translate.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        isMove = false;
      }
    });
    translate.start();
  }

  private boolean isScroll() {
    int scrollX = getScrollX(); // ScrollView 滑动的距离
    int offset = getScrollXOffset();
    if (scrollX == 0 || scrollX == offset) {
      return true;
    }
    return false;
  }

  private int getScrollXOffset() {
    // ChildView getMeasureWidth() getMeasureHeight() 不固定
    int childWidth = mChildView.getMeasuredWidth();
    int width = getWidth(); // ScrollView 宽度 getHeight() 高度固定
    return childWidth - width;
  }

  protected abstract void onScale(float scale);
}

package com.one.framework.animators;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ViewAnimator extends ValueAnimator {

  private static final long DEFAULT_DURATION = 500;
  protected View[] mViews;
  protected Set<AnimatorItem>[] mAnimators;

  public ViewAnimator() {
    setDuration(DEFAULT_DURATION);
  }

  public final ViewAnimator attachView(View... views) {
    mViews = views;

    onViewAttached(views);

    int size = views != null ? views.length : 0;
    mAnimators = new LinkedHashSet[size];
    for (int i = 0; i < size; i++) {
      mAnimators[i] = new LinkedHashSet<>();
      newAnimators(i, mAnimators[i]);
      if (views[i] == null) {
        continue;
      }
      attach(mAnimators[i], views[i]);
    }
    super.addUpdateListener(mUpdateListener);
    setFloatValues(0.0f, 1.0f);
    return this;
  }

  protected abstract void onViewAttached(View... views);

  private void attach(Set<AnimatorItem> container, View view) {
    for (AnimatorItem item : container) {
      item.onViewAttached(view);
    }
  }

  protected abstract void newAnimators(int viewIndex, Set<AnimatorItem> container);

  protected final void onUpdate(View... views) {
    int size = mAnimators != null ? mAnimators.length : 0;
    float fraction = getAnimatedFraction();
    for (int i = 0; i < size; i++) {
      Set<AnimatorItem> items = mAnimators[i];
      for (AnimatorItem item : items) {
        if (views[i] == null) {
          continue;
        }
        TimeInterpolator interpolator = item.getInterpolator();
        if (interpolator != null) {
          item.onUpdate(views[i], interpolator.getInterpolation(fraction));
        } else {
          item.onUpdate(views[i], fraction);
        }
      }
    }
  }

  @Override
  public final void addUpdateListener(AnimatorUpdateListener listener) {
    throw new UnsupportedOperationException("不支持外部设置AnimatorUpdateListener!");
  }

  private AnimatorUpdateListener mUpdateListener = new AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
      onUpdate(mViews);
    }
  };

  /**
   * 默认Animator监听
   */
  public static class DefaultAnimatorListener implements AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }
  }

  /**
   * 针对两个View的动画
   */
  public abstract static class ViewPairAnimator extends ViewAnimator {

    @Override
    protected void onViewAttached(View... views) {
    }

    @Override
    protected final void newAnimators(int viewIndex, Set<AnimatorItem> container) {
      if (viewIndex == 0) {
        firstViewAnimators(container);
      } else if (viewIndex == 1) {
        secondViewAnimators(container);
      }
    }

    protected abstract void firstViewAnimators(Set<AnimatorItem> container);

    protected abstract void secondViewAnimators(Set<AnimatorItem> container);
  }

  /**
   * 针对一个View的一个单一动画的接口
   */
  public interface AnimatorItem {

    /**
     * 关联到某个视图上
     */
    void onViewAttached(View view);

    /**
     * 根据变化的值更新视图
     */
    void onUpdate(View view, float fraction);

    /**
     * 设置一个插值器
     */
    void setInterpolator(TimeInterpolator value);

    /**
     * 获取这个item的插值器
     */
    TimeInterpolator getInterpolator();
  }

  /**
   * 带有插值器功能的AnimatorItem
   */
  public abstract static class BaseAnimItem implements AnimatorItem {

    protected TimeInterpolator mInterpolator;

    @Override
    public void setInterpolator(TimeInterpolator value) {
      mInterpolator = value;
    }

    @Override
    public TimeInterpolator getInterpolator() {
      return mInterpolator;
    }
  }

  public static int getScreenWidth(Context ctx) {
    return ctx.getResources().getDisplayMetrics().widthPixels;
  }

  public static int getScreenHeight(Context ctx) {
    return ctx.getResources().getDisplayMetrics().heightPixels;
  }

}

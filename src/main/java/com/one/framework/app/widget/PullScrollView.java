package com.one.framework.app.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.one.framework.R;
import com.one.framework.app.widget.base.IHeaderView;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.app.widget.base.IMovePublishListener;
import com.one.framework.app.widget.base.IPullView;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class PullScrollView extends ScrollView implements IPullView, IMovePublishListener {

  private IHeaderView mHeaderView;
  private int mScroller; // 0 scroll Header 1 scroll self
  private int mMaxHeight;
  private int mHeaderViewId;

  public PullScrollView(Context context) {
    this(context, null);
  }

  public PullScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullView);
    mScroller = a.getInt(R.styleable.PullView_scroll_view, 1);
    mMaxHeight = a.getDimensionPixelSize(R.styleable.PullView_scroll_max_height, 0);
    mHeaderViewId = a.getResourceId(R.styleable.PullView_header_view_id, 0);
    if (mScroller == 0 && mMaxHeight == 0) {
      throw new IllegalArgumentException("ScrollMaxHeight is 0");
    }
    a.recycle();

    setFillViewport(true);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    View view = getChildAt(0);
    if (mHeaderViewId != 0) {
      if (view != null && view instanceof ViewGroup) {
        mHeaderView = (NavigatorHeaderView) view.findViewById(mHeaderViewId);
      } else {
        mHeaderView = (NavigatorHeaderView) findViewById(mHeaderViewId);
      }
    } else {
      // dynamic add
    }
  }

  /**
   * @param offsetY > 0 往下滑动
   */
  @Override
  public void onMove(float offsetX, float offsetY) {
    boolean flag = offsetY > 0 || isHeaderNeedScroll();
    if (mScroller == 0 && flag) { // 滑动header
//      getScrollY() == 0 表示未滚动 或 子view 高度 <= ScrollView 高度
      mHeaderView.onMove(offsetX, offsetY);
    } else {
      selfScrollerMove(offsetY);
    }
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    boolean flag = isHeaderNeedScroll();
    if (mScroller == 0 && flag) { // 如果是滚到到底部了则滚动listview
      mHeaderView.onUp(bottom2Up, isFling);
    } else {
      selfScrollerUp(bottom2Up, isFling);
    }
  }

  private void selfScrollerMove(float offsetY) {
    int translateY = (int) (getTranslationY() + offsetY + 0.5);
    setTranslationY(translateY);
  }

  @Override
  public boolean isScrollBottom() {
    View childView = getChildAt(0);
    if (childView != null) {
      return childView.getMeasuredHeight() <= getScrollingPadding() + getHeaderScrollHeight()
          || childView.getMeasuredHeight() <= getHeight();
    }
    return false;
  }

  @Override
  public int getHeaderScrollHeight() {
    return mHeaderView.getScrollHeaderHeight();
  }

  @Override
  public int getScrollingPadding() {
    return getScrollY();
  }

  @Override
  public View getView() {
    return this;
  }

  private void selfScrollerUp(boolean bottom2Up, boolean isFling) {
    int tranlationY = (int) getTranslationY();
    goonMove(200);
  }

  private void goonMove(long duration) {
    ValueAnimator translate = ValueAnimator.ofFloat(1f, 0f);
    translate.setDuration(duration);
    translate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animValue = animation.getAnimatedFraction();
        float fraction = 1f - animValue;
        setTranslationY(fraction * getTranslationY());
      }
    });
    translate.start();
  }

  @Override
  public boolean isHeaderNeedScroll() {
    return mHeaderView.isNeedScroll();
  }

  @Override
  public int getHeaderHeight() {
    return mHeaderView.getHeaderHeight();
  }

  @Override
  public void setItemClickListener(IItemClickListener listener) {

  }
}

package com.one.framework.app.widget.base;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import com.one.framework.R;

/**
 * Created by ludexiang on 2018/4/21.
 */

public abstract class BaseHeaderView extends FrameLayout implements IHeaderView {

  protected ViewGroup mParentLayout;
  /**
   * header 最大滚动距离，暂时未用
   */
  protected int mScrollMaxHeight;
  protected int mScrollHeight;
  /**
   * header height
   */
  protected int mViewHeight;

  private ViewGroup.LayoutParams mParams;

  public BaseHeaderView(Context context, int maxScrollHeight) {
    super(context);
    mScrollMaxHeight = maxScrollHeight;
  }

  public BaseHeaderView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BaseHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    View view = createView(context);
    initView(view);
    view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        measure(width, height);
        mViewHeight = getMeasuredHeight();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
    });
  }

  @Override
  public void updateView(View view) {

  }

  private void initView(View view) {
    mParentLayout = view.findViewById(R.id.list_header_parent_layout);
    if (mParentLayout == null) {
      throw new IllegalArgumentException("HeaderView id must be list_header_parent_layout");
    }

    mParams = mParentLayout.getLayoutParams();
    mViewHeight = mParams.height;
  }


  protected abstract View createView(Context context);


  @Override
  public void onMove(float offsetX, float offsetY) {
    mParams.height += (int) (offsetY);
    mParentLayout.setLayoutParams(mParams);
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    mScrollHeight = mParams.height - mViewHeight;
    goonMove(200);
  }

  @Override
  public int getScrollHeaderHeight() {
    return mParams.height;
  }

  @Override
  public boolean isNeedScroll() {
    return mParams.height > mViewHeight;
  }

  @Override
  public int getHeaderHeight() {
    return mViewHeight;
  }

  private void goonMove(long duration) {
    ValueAnimator translate = ValueAnimator.ofFloat(1f, 0f);
    translate.setDuration(duration);
    translate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animValue = animation.getAnimatedFraction();
        LayoutParams params = (LayoutParams) mParentLayout.getLayoutParams();
        float fraction = 1f - animValue;
        params.height = (int) (mScrollHeight * fraction) + mViewHeight;
        mParentLayout.setLayoutParams(params);
      }
    });
    translate.start();
  }
}

package com.one.framework.app.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import com.one.framework.R;
import com.one.framework.app.widget.base.IHeaderView;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class HeaderView extends FrameLayout implements IHeaderView {

  private FrameLayout mParentLayout;
  private int mScrollMaxHeight;
  private int mScrollHeight;
  private int mViewHeight;

  public HeaderView(@NonNull Context context, int maxHeight) {
    this(context, null);
    mScrollMaxHeight = maxHeight;
  }

  public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.one_list_header_view_layout, this, true);
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

  private void initView(View view) {
    mParentLayout = (FrameLayout) view.findViewById(R.id.list_header_parent_layout);
  }

  @Override
  public void onMove(float offsetX, float offsetY) {
    LayoutParams params = (LayoutParams) mParentLayout.getLayoutParams();
    params.height += (int) (offsetY);
    mParentLayout.setLayoutParams(params);
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    LayoutParams params = (LayoutParams) mParentLayout.getLayoutParams();
    mScrollHeight = params.height - mViewHeight;
    goonMove(200);

  }

  @Override
  public int getHeaderHeight() {
    return mViewHeight;
  }

  @Override
  public int getScrollHeaderHeight() {
    LayoutParams params = (LayoutParams) mParentLayout.getLayoutParams();
    return params.height;
  }

  @Override
  public boolean isNeedScroll() {
    LayoutParams params = (LayoutParams) mParentLayout.getLayoutParams();
    return params.height > mViewHeight;
  }

  @Override
  public View getView() {
    return this;
  }

  private void goonMove(long duration) {
    ValueAnimator translate = ValueAnimator.ofFloat(1f, 0f);
    translate.setDuration(duration);
    translate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animValue = animation.getAnimatedFraction();
        LayoutParams params = (LayoutParams) mParentLayout
            .getLayoutParams();
        float fraction = 1f - animValue;
        params.height = (int) (mScrollHeight * fraction) + mViewHeight;
        mParentLayout.setLayoutParams(params);
      }
    });
    translate.start();
  }
}

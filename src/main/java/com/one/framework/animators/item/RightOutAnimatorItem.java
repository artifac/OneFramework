package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

/**
 * Created by yuhenghui on 16/9/2.
 */
public class RightOutAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mHideLocationStart = 0.0f; // 要隐藏的视图开始动画时在屏幕中的位置

  @Override
  public void onViewAttached(View view) {
    int[] location = new int[2];
    view.getLocationOnScreen(location);
    mHideLocationStart = location[0];
  }

  @Override
  public void onUpdate(View view, float fraction) {
    int screenWidth = ViewAnimator.getScreenWidth(view.getContext());
    float hideX = fraction * (screenWidth - mHideLocationStart);
    view.setTranslationX(hideX);
  }
}

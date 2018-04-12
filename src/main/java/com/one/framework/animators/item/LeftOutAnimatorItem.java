package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class LeftOutAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mHideLocationStart = 0.0f; // 要隐藏的视图开始动画时在屏幕中的位置

  @Override
  public void onViewAttached(View view) {
    int[] location = new int[2];
    view.getLocationOnScreen(location);
    mHideLocationStart = location[0];
  }

  @Override
  public void onUpdate(View view, float fraction) {
    float hideX = -fraction * (mHideLocationStart + view.getWidth());
    view.setTranslationX(hideX);
  }
}

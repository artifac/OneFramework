package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class BottomOutAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mHideLocationStart = 0.0f; // 要隐藏的视图开始动画时在屏幕中的位置

  @Override
  public void onViewAttached(View view) {
    /** 计算出要隐藏的视图在屏幕中的位置*/
    int[] location = new int[2];
    view.getLocationOnScreen(location);
    mHideLocationStart = location[1];
  }

  @Override
  public void onUpdate(View view, float fraction) {
    int screenHeight = ViewAnimator.getScreenHeight(view.getContext());
    float hideY = (screenHeight - mHideLocationStart) * fraction;
    view.setTranslationY(hideY);
  }
}

package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class TopOutAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mHideLocationStart = 0.0f; // 要隐藏的视图开始动画时在屏幕中的位置

  @Override
  public void onViewAttached(View view) {
    /** 计算出出要隐藏的视图的位置信息*/
    int[] location = new int[2];
    view.getLocationOnScreen(location);
    mHideLocationStart = location[1];
  }

  @Override
  public void onUpdate(View view, float fraction) {
    float hideY = -fraction * (mHideLocationStart + view.getHeight());
    view.setTranslationY(hideY);
  }
}

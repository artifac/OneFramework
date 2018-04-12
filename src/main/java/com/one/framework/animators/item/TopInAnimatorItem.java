package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class TopInAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mShowTranslationStart = 0.0f; // 要显示的视图开始动画时的TranslationY的值

  @Override
  public void onViewAttached(View view) {
    /** 计算出要显示的视图的位置信息*/
    int[] location = new int[2];
    view.getLocationOnScreen(location);
    view.setTranslationY(-view.getHeight() - location[1] + view.getTranslationY());
    mShowTranslationStart = view.getTranslationY();
  }

  @Override
  public void onUpdate(View view, float fraction) {
    float showY = (1.0f - fraction) * mShowTranslationStart;
    view.setTranslationY(showY);
  }
}

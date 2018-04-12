package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class LeftInAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mShowTranslationStart = 0.0f; // 要显示的视图开始动画时的TranslationX的值

  @Override
  public void onViewAttached(View view) {
    int[] location = new int[2];
    view.getLocationOnScreen(location);
    view.setTranslationX(-view.getWidth() - location[0] + view.getTranslationX());
    mShowTranslationStart = view.getTranslationX();

  }

  @Override
  public void onUpdate(View view, float fraction) {
    float showX = (1.0f - fraction) * mShowTranslationStart;
    view.setTranslationX(showX);
  }
}

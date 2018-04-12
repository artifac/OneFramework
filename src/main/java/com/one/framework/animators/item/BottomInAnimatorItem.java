package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class BottomInAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mShowTranslationStart = 0.0f; // 要显示的视图开始动画时的TranslationY的值

  @Override
  public void onViewAttached(View view) {
    /** 计算出要显示出来的视图的位置信息*/
    int[] location = new int[2];
    int screenHeight = ViewAnimator.getScreenHeight(view.getContext());
    view.getLocationOnScreen(location);
    view.setTranslationY(screenHeight - location[1] + view.getTranslationY());
    mShowTranslationStart = view.getTranslationY();
  }

  @Override
  public void onUpdate(View view, float fraction) {
    float showY = (1.0f - fraction) * mShowTranslationStart;
    view.setTranslationY(showY);
    if (showY == 0) {
      //解决底部服务卡片无法出现的问题
      view.requestLayout();
    }
  }
}

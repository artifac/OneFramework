package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class FadeOutAnimatorItem extends ViewAnimator.BaseAnimItem {

  @Override
  public void onViewAttached(View view) {
    view.setAlpha(1.0f);
  }

  @Override
  public void onUpdate(View view, float fraction) {
    view.setAlpha(1.0f - fraction);
  }
}

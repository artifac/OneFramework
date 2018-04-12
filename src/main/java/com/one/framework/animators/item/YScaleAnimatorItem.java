package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class YScaleAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mFrom;
  private float mTo;

  public YScaleAnimatorItem(float from, float to) {
    mFrom = from;
    mTo = to;
  }

  @Override
  public void onViewAttached(View view) {
    view.setScaleY(mFrom);
  }

  @Override
  public void onUpdate(View view, float fraction) {
    float value = mFrom + (mTo - mFrom) * fraction;
    view.setScaleY(value);
  }
}

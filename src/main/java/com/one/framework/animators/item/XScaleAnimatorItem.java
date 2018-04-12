package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class XScaleAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mFrom;
  private float mTo;

  public XScaleAnimatorItem(float from, float to) {
    mFrom = from;
    mTo = to;
  }

  @Override
  public void onViewAttached(View view) {
    view.setScaleX(mFrom);
  }

  @Override
  public void onUpdate(View view, float fraction) {
    float value = mFrom + (mTo - mFrom) * fraction;
    view.setScaleX(value);
  }
}

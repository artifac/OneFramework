package com.one.framework.animators.item;

import android.view.View;
import com.one.framework.animators.ViewAnimator;

public class YMoveAnimatorItem extends ViewAnimator.BaseAnimItem {

  private float mMoveHeight = 0;
  private float mStartTranslate = 0;

  public YMoveAnimatorItem(float move) {
    mMoveHeight = move;
  }

  @Override
  public void onViewAttached(View view) {
    mStartTranslate = view.getTranslationY();
  }

  @Override
  public void onUpdate(View view, float fraction) {
    view.setTranslationY(mStartTranslate + mMoveHeight * fraction);
  }
}

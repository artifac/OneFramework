package com.one.framework.animators;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import com.one.framework.animators.item.BottomInAnimatorItem;
import com.one.framework.animators.item.BottomOutAnimatorItem;
import com.one.framework.animators.item.FadeInAnimatorItem;
import com.one.framework.animators.item.FadeOutAnimatorItem;
import java.util.Set;

public class BottomInBottomOutAnimator extends ViewAnimator.ViewPairAnimator {

  @Override
  protected void firstViewAnimators(Set<AnimatorItem> container) {
    BottomInAnimatorItem bottomInAnimator = new BottomInAnimatorItem();
    bottomInAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    container.add(bottomInAnimator);

    FadeInAnimatorItem fadeInAnimator = new FadeInAnimatorItem();
    fadeInAnimator.setInterpolator(mFadeInInterpolator);
    container.add(fadeInAnimator);
  }

  @Override
  protected void secondViewAnimators(Set<AnimatorItem> container) {
    BottomOutAnimatorItem bottomOutAnimator = new BottomOutAnimatorItem();
    bottomOutAnimator.setInterpolator(new AccelerateInterpolator());
    container.add(bottomOutAnimator);

    FadeOutAnimatorItem fadeOutAnimator = new FadeOutAnimatorItem();
    fadeOutAnimator.setInterpolator(mFadeOutInterpolator);
    container.add(fadeOutAnimator);
  }

  private TimeInterpolator mFadeInInterpolator = new TimeInterpolator() {
    @Override
    public float getInterpolation(float input) {
      return 0.2f + input * 0.8f;
    }
  };

  private TimeInterpolator mFadeOutInterpolator = new TimeInterpolator() {
    @Override
    public float getInterpolation(float input) {
      return input * 0.8f;
    }
  };
}

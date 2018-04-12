package com.one.framework.animators;

import com.one.framework.animators.item.FadeInAnimatorItem;
import com.one.framework.animators.item.FadeOutAnimatorItem;
import java.util.Set;

public class FadeInFadeOutAnimator extends ViewAnimator.ViewPairAnimator {

  @Override
  protected void firstViewAnimators(Set<AnimatorItem> container) {
    container.add(new FadeInAnimatorItem());
  }

  @Override
  protected void secondViewAnimators(Set<AnimatorItem> container) {
    container.add(new FadeOutAnimatorItem());
  }
}

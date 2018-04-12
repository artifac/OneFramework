package com.one.framework.animators;

import com.one.framework.animators.item.FadeInAnimatorItem;
import com.one.framework.animators.item.FadeOutAnimatorItem;
import com.one.framework.animators.item.LeftInAnimatorItem;
import com.one.framework.animators.item.RightOutAnimatorItem;
import java.util.Set;

public class LeftInRightOutAnimator extends ViewAnimator.ViewPairAnimator {

  @Override
  protected void firstViewAnimators(Set<AnimatorItem> container) {
    container.add(new LeftInAnimatorItem());
    container.add(new FadeInAnimatorItem());
  }

  @Override
  protected void secondViewAnimators(Set<AnimatorItem> container) {
    container.add(new RightOutAnimatorItem());
    container.add(new FadeOutAnimatorItem());
  }
}

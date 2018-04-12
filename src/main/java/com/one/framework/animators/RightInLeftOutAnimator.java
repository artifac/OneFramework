package com.one.framework.animators;

import com.one.framework.animators.item.FadeInAnimatorItem;
import com.one.framework.animators.item.FadeOutAnimatorItem;
import com.one.framework.animators.item.LeftOutAnimatorItem;
import com.one.framework.animators.item.RightInAnimatorItem;
import java.util.Set;

public class RightInLeftOutAnimator extends ViewAnimator.ViewPairAnimator {

  @Override
  protected void firstViewAnimators(Set<AnimatorItem> container) {
    container.add(new RightInAnimatorItem());
    container.add(new FadeInAnimatorItem());
  }

  @Override
  protected void secondViewAnimators(Set<AnimatorItem> container) {
    container.add(new LeftOutAnimatorItem());
    container.add(new FadeOutAnimatorItem());
  }
}

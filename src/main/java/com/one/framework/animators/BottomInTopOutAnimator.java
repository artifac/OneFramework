package com.one.framework.animators;

import com.one.framework.animators.item.BottomInAnimatorItem;
import com.one.framework.animators.item.FadeInAnimatorItem;
import com.one.framework.animators.item.FadeOutAnimatorItem;
import com.one.framework.animators.item.TopOutAnimatorItem;
import java.util.Set;

public class BottomInTopOutAnimator extends ViewAnimator.ViewPairAnimator {

  @Override
  protected void firstViewAnimators(Set<AnimatorItem> container) {
    container.add(new BottomInAnimatorItem());
    container.add(new FadeInAnimatorItem());
  }

  @Override
  protected void secondViewAnimators(Set<AnimatorItem> container) {
    container.add(new TopOutAnimatorItem());
    container.add(new FadeOutAnimatorItem());
  }
}

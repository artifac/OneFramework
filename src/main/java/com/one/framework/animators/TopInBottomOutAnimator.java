package com.one.framework.animators;

import com.one.framework.animators.item.BottomOutAnimatorItem;
import com.one.framework.animators.item.FadeInAnimatorItem;
import com.one.framework.animators.item.FadeOutAnimatorItem;
import com.one.framework.animators.item.TopInAnimatorItem;
import java.util.Set;

public class TopInBottomOutAnimator extends ViewAnimator.ViewPairAnimator {

  @Override
  protected void firstViewAnimators(Set<AnimatorItem> container) {
    container.add(new TopInAnimatorItem());
    container.add(new FadeInAnimatorItem());
  }

  @Override
  protected void secondViewAnimators(Set<AnimatorItem> container) {
    container.add(new BottomOutAnimatorItem());
    container.add(new FadeOutAnimatorItem());
  }
}

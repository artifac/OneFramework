package com.one.framework.app.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.one.framework.app.model.IBusinessContext;
import com.onecore.SupportFragment;
import com.onecore.core.anim.FragmentAnimator;

/**
 * Created by ludexiang on 2018/3/28.
 * 界面路由器
 */

public interface INavigator {
  String BUNDLE_COMPONENT_NAME_FRAGMENT = "bundle_component_name_fragment";
  String BUNDLE_ADD_TO_BACK_STACK = "bundle_add_to_back_stack";
  String BUNDLE_FORWARD_FRAGMENT_STYLE = "bundle_froward_fragment_style";
  String BUNDLE_EXTRA_INFO = "bundle_extra_info";

  void onResume();

  void onPause();

  Fragment startFragment(Intent intent, IBusinessContext businessContext, FragmentAnimator animator);

  void safePost(Runnable runnable);

  /**
   * 获得当前Fragment
   * @return
   */
  Fragment getCurrentFragment();

  /**
   * 获取Fragment pre Fragment
   */
  Fragment getPreFragment(Fragment fragment);

  void lockDrawerLayout(boolean lock);
  void backToRoot();
}

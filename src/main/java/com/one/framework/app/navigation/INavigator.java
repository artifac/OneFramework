package com.one.framework.app.navigation;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.one.framework.app.model.IBusinessContext;

/**
 * Created by ludexiang on 2018/3/28.
 * 界面路由器
 */

public interface INavigator {
  String BUNDLE_COMPONENT_NAME_FRAGMENT = "bundle_component_name_fragment";
  String BUNDLE_ADD_TO_BACK_STACK = "bundle_add_to_back_stack";
  String BUNDLE_FORWARD_FRAGMENT_STYLE = "bundle_froward_fragment_style";
  String BUNDLE_EXTRA_INFO = "bundle_extra_info";

  Fragment startFragment(Intent intent, IBusinessContext businessContext);

  /**
   * 获得当前Fragment
   * @return
   */
  Fragment getCurrentFragment();
  void lockDrawerLayout(boolean lock);
  void backToRoot();
}

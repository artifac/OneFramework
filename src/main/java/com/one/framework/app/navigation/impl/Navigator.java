package com.one.framework.app.navigation.impl;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.manager.PageDelegateManager;

/**
 * Created by ludexiang on 2018/3/28.
 * 加载Fragment 动画逻辑
 * 界面路由导航
 */

public class Navigator implements INavigator {

  private PageDelegateManager mPageManager;
  private FragmentManager mFragmentManager;

  public Navigator(FragmentManager manager) {
    mFragmentManager = manager;
    mPageManager = PageDelegateManager.getInstance();
  }

  @Override
  public Fragment getFragment(Context context, Intent intent, IBusinessContext businessContext) {
    return mPageManager.getFragment(context, intent, businessContext);
  }

  @Override
  public void fillPage(Fragment fragment, int contentId) {
    if (fragment != null) {
      FragmentTransaction transaction = mFragmentManager.beginTransaction();
      String fragmentTag = getFragmentTag(fragment.getClass());
      transaction.replace(contentId, fragment, fragmentTag);
      transaction.commitAllowingStateLoss();
    }
  }

  private String getFragmentTag(Class<? extends Fragment> fragment) {
    String canonicalName = fragment.getCanonicalName();
    return canonicalName + "@" + System.identityHashCode(fragment);
  }
}

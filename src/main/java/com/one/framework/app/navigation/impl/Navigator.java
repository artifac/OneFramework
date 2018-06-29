package com.one.framework.app.navigation.impl;

import static com.onecore.core.ISupportFragment.STANDARD;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import com.one.framework.MainActivity;
import com.one.framework.R;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.log.Logger;
import com.one.framework.manager.FragmentDelegateManager;
import com.onecore.SupportFragment;
import com.onecore.core.ISupportFragment;
import com.onecore.core.ISupportFragment.LaunchMode;
import com.onecore.core.anim.FragmentAnimator;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/28.
 * 加载Fragment 动画逻辑
 * 界面路由导航
 */

public final class Navigator implements INavigator {

  private SoftReference<Context> mSoftReference;
  private FragmentDelegateManager mPageManager;
  private FragmentManager mFragmentManager;

  public Navigator(Context context, FragmentManager manager) {
    mSoftReference = new SoftReference<Context>(context);
    mFragmentManager = manager;
    mPageManager = FragmentDelegateManager.getInstance();
  }

  private Fragment getFragment(Intent intent, IBusinessContext businessContext) {
    return mPageManager.getFragment(intent, businessContext);
  }

  @Override
  public Fragment getRootFragment(Intent intent, IBusinessContext businessContext) {
    return getFragment(intent, businessContext);
  }

  @Override
  public Fragment startFragment(Intent intent, IBusinessContext businessContext, FragmentAnimator animator) {
    Fragment fragment = getFragment(intent, businessContext);
    if (fragment != null) {
      FragmentTransaction transaction = mFragmentManager.beginTransaction();
      if (animator != null) {
        transaction.setCustomAnimations(animator.getEnter(), animator.getExit(), animator.getPopEnter(), animator.getPopExit());
      }
      String fragmentTag = getFragmentTag(fragment.getClass());
      boolean isAddToBackStack = intent.getBooleanExtra(BUNDLE_ADD_TO_BACK_STACK, true);
      if (isAddToBackStack) {
        transaction.addToBackStack(fragmentTag);
      }
      boolean isAdd = intent.getBooleanExtra(BUNDLE_FORWARD_FRAGMENT_STYLE, false);
      if (isAdd) {
        transaction.add(R.id.content_view_container, fragment, fragmentTag);
      } else {
        transaction.replace(R.id.content_view_container, fragment, fragmentTag);
      }
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        fragment.setArguments(bundle);
      }
      transaction.commitAllowingStateLoss();
//      try {
        mFragmentManager.executePendingTransactions();
        fragment.setUserVisibleHint(true);
//      } catch (Exception e) {
//        Logger.e("ldx", "forward exception ..." + e);
//      }
    } else {
      // 跳转Activity
      if (mSoftReference != null && mSoftReference.get() != null) {
        if (!(mSoftReference.get() instanceof Activity)) {
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mSoftReference.get().startActivity(intent);
//        ((Activity)mSoftReference.get()).overridePendingTransition();
      }
    }
    return fragment;
  }

  /**
   * 若为0表示在首页
   */
  @Override
  public Fragment getCurrentFragment() {
    int backStackCount = mFragmentManager.getBackStackEntryCount();
    if (backStackCount != 0) { // 返回栈中存在Fragment
      BackStackEntry stackEntry = mFragmentManager.getBackStackEntryAt(backStackCount - 1);
      String fragmentTag = stackEntry.getName();
      return mFragmentManager.findFragmentByTag(fragmentTag);
    }
    return null;
  }

  @Override
  public Fragment getPreFragment(Fragment fragment) {
    return null;
  }

  private String getFragmentTag(Class<? extends Fragment> fragment) {
    String canonicalName = fragment.getCanonicalName();
    return canonicalName + "@" + System.identityHashCode(fragment);
  }

  @Override
  public void lockDrawerLayout(boolean lock) {
    if (mSoftReference.get() != null && mSoftReference.get() instanceof MainActivity) {
      ((MainActivity) mSoftReference.get()).lockDrawerLayout(lock);
    }
  }

  @Override
  public void backToRoot() {
    int entryCount = mFragmentManager.getBackStackEntryCount();
    if (entryCount > 0) {
      final String tag = mFragmentManager.getBackStackEntryAt(0).getName();
      mFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
  }
}

package com.one.framework.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import com.one.framework.MainActivity;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.navigation.impl.Navigator;
import com.one.framework.app.page.IComponent;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.base.ITopTitleView;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.log.Logger;
import com.one.framework.manager.PageDelegateManager;
import com.one.map.IMap;
import java.lang.ref.SoftReference;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BizEntranceFragment extends Fragment implements IComponent, KeyEvent.Callback,
    ITopTitleListener {

  private SoftReference<MainActivity> mActivity;
  protected IBusinessContext mBusContext;
  private ITopbarFragment mTopbarView;
  protected IMap mMap;
  protected boolean isRootFragment;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = new SoftReference<MainActivity>((MainActivity) activity);
    if (isRootFragment && mActivity != null && mActivity.get() != null) {
      mTopbarView.setTitleClickListener(mActivity.get().getTopTitleListener());
    }
  }

  /**
   * 优先于 onAttach
   * @param businessContext
   */
  @Override
  public void setBusinessContext(IBusinessContext businessContext) {
    mBusContext = businessContext;
    mTopbarView = mBusContext.getTopbar();
    mMap = mBusContext.getMap();

    ServiceProvider provider = getClass().getAnnotation(ServiceProvider.class);
    if (provider != null) {
      isRootFragment = true;
    } else {
      isRootFragment = false;
      mTopbarView.setTitleClickListener(this);
    }
  }

  /**
   * 跳转到下一个界面
   * @param clazz
   */
  protected final void forward(Class<? extends Fragment> clazz) {
    forward(clazz, null);
  }

  /**
   * 是否保留tab 入口
   * @param clazz
   * @param args
   */
  protected final void forward(Class<? extends Fragment> clazz, Bundle args) {
    forward(clazz, args, false);
  }

  /**
   * 跳转到下一个界面
   * @param clazz
   * @param args
   */
  protected final void forward(Class<? extends Fragment> clazz, Bundle args, boolean isSaveTabEntrance) {
    if (mBusContext == null) {
      return;
    }
    FragmentManager fragmentManager = getFragmentManager();
    int backStackCount = fragmentManager.getBackStackEntryCount();
    if (backStackCount == 0) { // 若返回栈为空则表示离开首页
      onLeaveHome();
    }
    if (!isSaveTabEntrance) {
      mBusContext.getTopbar().tabIndicatorAnim(false);
    }
    Intent intent = new Intent();
    intent.setClass(getContext(), clazz);
    args = args == null ? new Bundle() : args;
    intent.putExtras(args);
    mBusContext.getNavigator().startFragment(intent, mBusContext);
  }

  /**
   * 离开首页
   * 跳转到其他页面
   */
  protected void onLeaveHome() {
    Logger.e("ldx", "onLeaveHome ......");
  }

  /**
   * 从其他页面回到根页面
   */
  protected void onBackHome() {
    mBusContext.getTopbar().tabIndicatorAnim(true);
    mBusContext.getNavigator().backToRoot();
  }

  /** view 设置 **/
  protected void setTitle(String title) {
    mTopbarView.setTitle(title);
  }

  protected void setTitle(int resId) {
    mTopbarView.setTitle(resId);
  }

  /**
   * sub child Fragment 可以复写此方法处理title bar 点击事件
   * @param position
   */
  @Override
  public void onTitleItemClick(ClickPosition position) {
    switch (position) {
      case LEFT: {
        onBackInvoke();
        break;
      }
    }
  }

  /**
   * 若返回键做处理
   * @param keyCode
   * @param event
   * @return
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      // 点一次返回键也要做一次左上角返回键的pop
      mTopbarView.popBackListener();
      return onBackInvoke();
    }
    return false;
  }



  @Override
  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    return false;
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return false;
  }

  @Override
  public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
    return false;
  }

  /**
   * 左上角返回键 或 返回键回调
   * return true 表示self 消费此事件, return false 由上层基类处理
   */
  public boolean onBackInvoke() {
    FragmentManager manager = getFragmentManager();
    if (manager != null) {
      manager.popBackStackImmediate();
      int stackCount = manager.getBackStackEntryCount();
      // 如果当前返回栈中为1且点击了返回键则表示回了首页
      if (stackCount == 0) {
        onBackHome();
        return true;
      }
    }
    return true;
  }
}

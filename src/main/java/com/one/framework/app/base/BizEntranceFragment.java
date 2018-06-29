package com.one.framework.app.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.one.framework.MainActivity;
import com.one.framework.R;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.page.IComponent;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.base.IMapCenterPinView;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.dialog.DialogLoading;
import com.one.framework.utils.UIThreadHandler;
import com.one.map.IMap;
import com.onecore.core.anim.DefaultVerticalAnimator;
import java.lang.ref.SoftReference;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BizEntranceFragment extends Fragment implements IComponent, KeyEvent.Callback,
    ITopTitleListener {
  protected SoftReference<MainActivity> mActivity;
  protected IBusinessContext mBusContext;
  protected ITopbarFragment mTopbarView;
  protected INavigator mNavigator;
  protected IMap mMap;
  private IMapCenterPinView mPinView;
  protected static boolean isRootFragment = true;
  protected DialogLoading mLoading;
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = new SoftReference<MainActivity>((MainActivity) activity);
    if (isRootFragment && mActivity != null && mActivity.get() != null && mTopbarView != null) {
      mTopbarView.setTitleClickListener(mActivity.get().getTopTitleListener());
    }

    mLoading = new DialogLoading.Builder(activity).setDlgInfo(R.string.one_dlg_loading).create();
    mLoading.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          return onBackPressed();
        }
        return false;
      }
    });
  }

  /**
   * 优先于 onAttach
   */
  @Override
  public void setBusinessContext(IBusinessContext businessContext) {
    mBusContext = businessContext;
    mTopbarView = mBusContext.getTopbar();
    mMap = mBusContext.getMap();
    mNavigator = mBusContext.getNavigator();
    mPinView = mBusContext.getPinView();

    ServiceProvider provider = getClass().getAnnotation(ServiceProvider.class);
    if (provider != null) {
      isRootFragment = true;
    } else {
      isRootFragment = false;
      if (isAddLeftClick()) {
        mTopbarView.setTitleClickListener(this);
      }
    }
  }

  /**
   * all extends BizEntranceFragment while invoke
   * link {@link #setBusinessContext(IBusinessContext businessContext)}
   * default set setTitleClickListener this
   * @return
   */
  protected boolean isAddLeftClick() {
    return true;
  }

  /**
   * 跳转到下一个界面
   */
  protected final void forward(Class<? extends Fragment> clazz) {
    forward(clazz, null);
  }

  /**
   * 是否保留tab 入口
   */
  protected final void forward(Class<? extends Fragment> clazz, boolean isSaveTabEntrance) {
    forward(clazz, null, isSaveTabEntrance);
  }


  protected final void forward(Class<? extends Fragment> clazz, Bundle args) {
    forward(clazz, args, false);
  }

  /**
   * 跳转到下一个界面
   */
  protected final void forward(Class<? extends Fragment> clazz, Bundle args, boolean isSaveTabEntrance) {
    if (mBusContext == null) {
      return;
    }
    FragmentManager fragmentManager = getChildFragmentManager();
    int backStackCount = fragmentManager.getBackStackEntryCount();
    if (backStackCount == 0) { // 若返回栈为空则表示离开首页
      onLeaveHome();
    }
    if (!isSaveTabEntrance) {
      mTopbarView.tabIndicatorAnim(false);
    }
    Intent intent = new Intent();
    intent.setClass(getContext(), clazz);
    args = args == null ? new Bundle() : args;
    intent.putExtras(args);
    mNavigator.startFragment(intent, mBusContext, new DefaultVerticalAnimator());
  }

  /**
   * 启动目标Fragment并pop self
   */
  protected final void forwardWithPop(final Class<? extends Fragment> clazz, final Bundle args) {
    if (mBusContext == null) {
      return;
    }
    final FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.executePendingTransactions();
    Fragment currentFragment = mNavigator.getCurrentFragment();
    final Fragment preFragment = mNavigator.getPreFragment(currentFragment);
    fragmentManager.popBackStackImmediate();
    forward(clazz, args);
    mTopbarView.popBackListener();
  }

  protected final void finishSelf() {
    onBackInvoke();
  }


  /**
   * 离开首页
   * 跳转到其他页面
   *
   * @CallSuper 子类必须 invoke super.onLeaveHome
   */
  @CallSuper
  protected void onLeaveHome() {
    // 锁定DrawerLayout 不允许侧滑
    isRootFragment = false;
    mNavigator.lockDrawerLayout(true);
  }

  /**
   * 从其他页面回到根页面
   *
   * @CallSuper 子类必须 invoke super.onBackHome
   */
  @CallSuper
  protected void onBackHome() {
    isRootFragment = true;
    mTopbarView.tabIndicatorAnim(true);
    mTopbarView.titleBarReset();
    mNavigator.backToRoot();
    mNavigator.lockDrawerLayout(false);
    mPinView.hide(false);

    doBackHome();
  }

  private void doBackHome() {
    FragmentManager childManager = getFragmentManager();
    List<Fragment> fragments = childManager.getFragments();
    for (Fragment fragment : fragments) {
      ServiceProvider provider = fragment.getClass().getAnnotation(ServiceProvider.class);
      if (provider == null) {
        continue;
      }
      fragment.onHiddenChanged(true);
    }
  }

  /**
   * 隐藏大头针
   */
  protected void pinViewHide(boolean isHide) {
    mPinView.hide(isHide);
  }

  protected void updatePinViewPosition(final int topHeight, final int bottomHeight) {
    UIThreadHandler.post(new Runnable() {
      @Override
      public void run() {
        FrameLayout parent = (FrameLayout) mPinView.getView().getParent();
        if (parent != null) {
          RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) parent.getLayoutParams();
          params.topMargin = topHeight;
          params.bottomMargin = bottomHeight;
          parent.setLayoutParams(params);
        }
      }
    });
  }

  /**
   * view 设置
   **/
  protected void setTitle(String title) {
    mTopbarView.setTitle(title);
  }

  protected void setTitle(int resId) {
    mTopbarView.setTitle(resId);
  }

  /**
   * sub child Fragment 可以复写此方法处理title bar 点击事件
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
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && !onBackPressed()) {
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

  @Override
  public boolean onBackPressed() {
    if (mLoading != null && mLoading.isShowing()) {
      mLoading.dismissDlg();
      return true;
    }
    return false;
  }

  /**
   * 左上角返回键 或 返回键回调
   * return true 表示self 消费此事件, return false 由上层基类处理
   */
  public boolean onBackInvoke() {
    FragmentManager manager = getFragmentManager();
    if (manager != null) {
      manager.popBackStackImmediate(); // 先弹出栈
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

package com.one.framework.app.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.one.framework.MainActivity;
import com.one.framework.R;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.page.IComponent;
import com.one.framework.app.page.IComponent.IRootListener;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.page.ITopbarFragment.TitlePosition;
import com.one.framework.app.widget.base.IMapCenterPinView;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.dialog.DialogLoading;
import com.one.framework.log.Logger;
import com.one.framework.utils.PermissionProxy;
import com.one.framework.utils.UIThreadHandler;
import com.one.map.IMap;
import com.one.map.location.LocationProvider;
import com.one.map.location.LocationProvider.OnLocationChangedListener;
import com.one.map.model.Address;
import java.lang.ref.SoftReference;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BizEntranceFragment extends Fragment implements IComponent, KeyEvent.Callback,
    ITopTitleListener, IRootListener, OnBackStackChangedListener {

  private static final String TAG = BizEntranceFragment.class.getSimpleName();

  protected SoftReference<MainActivity> mActivity;
  protected IBusinessContext mBusContext;
  protected ITopbarFragment mTopbarView;
  protected INavigator mNavigator;
  protected IMap mMap;
  protected IMapCenterPinView mPinView;
  protected DialogLoading mLoading;
  private boolean isLocationSuccess = false;
  private FragmentManager mFragmentMgr;
  private IRootListener mRootListener;
  private PermissionProxy mPermissionProxy;
  protected static boolean isHomePage = true;

  protected static final String REQUEST_CODE = "requestCode";

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = new SoftReference<MainActivity>((MainActivity) activity);

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
    mFragmentMgr = getFragmentManager();
    mFragmentMgr.addOnBackStackChangedListener(this);
    setRootListener(this);
    mPermissionProxy = new PermissionProxy(mActivity.get());
  }

  /**
   * 优先于 onAttach
   */
  @Override
  public void setBusinessContext(IBusinessContext businessContext) {
    Logger.e("ldx", "BizEntranceFragment ... setBusinessContext");
    mBusContext = businessContext;
    mTopbarView = mBusContext.getTopbar();
    mMap = mBusContext.getMap();
    mNavigator = mBusContext.getNavigator();
    mPinView = mBusContext.getPinView();
    LocationProvider.getInstance().addLocationChangeListener(locationChangedListener);

    ServiceProvider provider = getClass().getAnnotation(ServiceProvider.class);
    Logger.e(TAG, "BizEntranceFragment ... provider >>>>>>>>> " + provider);
    if (provider == null) {
      if (isAddLeftClick()) {
        mTopbarView.setTitleClickListener(this);
      }
//      updateTitlebar();
    }
  }

  @Override
  public void onNewIntent(Intent intent) {

  }

  protected void updateTitleBar(Address address) {
    if (mNavigator.isRootFragment()) {
      if (address != null) {
        mTopbarView.setTitle(address.mCity, 14, Typeface.defaultFromStyle(Typeface.BOLD));
      } else {
        mTopbarView.setTitle(getString(R.string.one_locationing), 14, Typeface.defaultFromStyle(Typeface.BOLD));
      }
    }
  }

  @Override
  public void onFragmentForResult(int requestCode, Bundle args) {

  }

  /**
   * all extends BizEntranceFragment while invoke
   * link {@link #setBusinessContext(IBusinessContext businessContext)}
   * default set setTitleClickListener this
   */
  protected boolean isAddLeftClick() {
    return true;
  }

  /**
   * 返回键Pop
   */
  protected void popBackListener() {
    mTopbarView.popBackListener();
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
  protected final void forward(Class<? extends Fragment> clazz, Bundle args,
      boolean isSaveTabEntrance) {
    if (mBusContext == null) {
      return;
    }

    if (getActivity() != null) {
      FragmentManager fragmentManager = getChildFragmentManager();
      int backStackCount = fragmentManager.getBackStackEntryCount();
      if (backStackCount == 0) { // 若返回栈为空则表示离开首页
        if (mRootListener != null) {
          mRootListener.onLeaveHome();
        }
      }

      if (!isSaveTabEntrance) {
        mTopbarView.tabIndicatorAnim(false);
      }

      Intent intent = new Intent();
      intent.setClass(getContext(), clazz);
      args = args == null ? new Bundle() : args;
      intent.putExtras(args);
      mNavigator.startFragment(intent, mBusContext/*, new DefaultVerticalAnimator()*/);
    } else {

    }
  }

  protected final void forwardForResult(Class<? extends Fragment> clazz, Bundle bundle, int requestCode) {
    forward(clazz, bundle, requestCode);
  }

  private final void forward(Class<? extends Fragment> clazz, Bundle args, int requestCode) {
    if (mBusContext == null) {
      return;
    }

    if (getActivity() != null) {
      FragmentManager fragmentManager = getChildFragmentManager();
      int backStackCount = fragmentManager.getBackStackEntryCount();
      if (backStackCount == 0) { // 若返回栈为空则表示离开首页
        if (mRootListener != null) {
          mRootListener.onLeaveHome();
        }
      }

      Intent intent = new Intent();
      intent.setClass(getContext(), clazz);
      args = args == null ? new Bundle() : args;
      args.putInt("requestCode", requestCode);
      intent.putExtras(args);
      mNavigator.startFragment(intent, mBusContext/*, new DefaultVerticalAnimator()*/);
    } else {

    }
  }

  /**
   * 启动目标Fragment并pop self
   */
  protected final void forwardWithPop(final Class<? extends Fragment> clazz, final Bundle args) {
    if (mBusContext == null) {
      return;
    }
    mTopbarView.popBackListener();
    mNavigator.safePost(new Runnable() {
      @Override
      public void run() {
        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.executePendingTransactions();
        Fragment currentFragment = mNavigator.getCurrentFragment();
        final Fragment preFragment = mNavigator.getPreFragment(currentFragment);
        popBackStack();
        forward(clazz, args);
      }
    });
  }

  protected void setResult(int resultOk) {
    if (resultOk == 0) {
      mNavigator.safePost(new Runnable() {
        @Override
        public void run() {
          Fragment curFragment = mNavigator.getCurrentFragment();
          Fragment preFragment = mNavigator.getPreFragment(curFragment);
          if (preFragment == null) {
            return;
          }

          Bundle args = curFragment.getArguments();
          if (args != null) {
            int requestCode = args.getInt(REQUEST_CODE);
            if (curFragment instanceof BizEntranceFragment) {
              BizEntranceFragment component = (BizEntranceFragment) curFragment;
              component.finishSelf();
            }

            if (preFragment instanceof IComponent) {
              IComponent component = (IComponent) preFragment;
              component.onFragmentForResult(requestCode, args);
            }

          }
        }
      });
    }
  }

  /**
   * 回退到前一个Page
   */
  private void popBackExclusive(Bundle bundle) {
    popBackStack(bundle);
  }

  /**
   * 回退到根Page
   */
  private void popBackOnRoot(Bundle bundle) {
    int count = mFragmentMgr.getBackStackEntryCount();

    if (count <= 1) {
      Logger.e("ldx", "current is root Page");
      return;
    }

    String name = mFragmentMgr.getBackStackEntryAt(0).getName();
    popBackStack(name, 0, bundle);

  }

  /**
   * 清空Page栈
   */
  private void popBackClearStack(Bundle bundle) {
    int count = getFragmentManager().getBackStackEntryCount();
    if (count <= 0) {
      return;
    }
    String rootName = getFragmentManager().getBackStackEntryAt(0).getName();
    popBackStack(rootName, FragmentManager.POP_BACK_STACK_INCLUSIVE, bundle);
  }

  public void popBackStack() {
    popBackStack(null);
  }

  public void popBackStack(final Bundle bundle) {
    mNavigator.safePost(new Runnable() {
      @Override
      public void run() {
        onPopBackStackResult(null, 0, bundle);
        mFragmentMgr.popBackStack();
      }
    });
  }

  public void popBackStack(final String name, final int flags) {
    popBackStack(name, flags, null);
  }

  public void popBackStack(final String name, final int flags, final Bundle bundle) {
    mNavigator.safePost(new Runnable() {
      @Override
      public void run() {
        onPopBackStackResult(name, flags, bundle);
        mFragmentMgr.popBackStack(name, flags);
      }
    });
  }

  private void onPopBackStackResult(String name, int flags, Bundle bundle) {
    boolean onBack2Home = false;
    Fragment fragment = null;
    if (mFragmentMgr.getBackStackEntryCount() == 1) {
      onBack2Home = true;
    } else {
      if (name == null) {
        fragment = mNavigator.getLastIndexFragment(mFragmentMgr, 2);
      } else {
        int count = mFragmentMgr.getBackStackEntryCount();
        BackStackEntry stackEntry;
        for (int i = count - 1; i >= 0; i--) {
          stackEntry = mFragmentMgr.getBackStackEntryAt(i);
          if (name.equals(stackEntry.getName())) {
            if (flags == FragmentManager.POP_BACK_STACK_INCLUSIVE) {
              if (i - 1 >= 0) {
                stackEntry = mFragmentMgr.getBackStackEntryAt(i - 1);
                fragment = mFragmentMgr.findFragmentByTag(stackEntry.getName());
              } else {
                onBack2Home = true;
              }
            } else {
              fragment = mFragmentMgr.findFragmentByTag(name);
            }
            break;
          }
        }
      }
    }

    if (fragment != null) {
      Bundle arguments = fragment.getArguments();
      if (arguments != null && bundle != null) {
        arguments.putAll(bundle);
      }
    } else if (onBack2Home) {
      // 首页处理
//      if (onBackResultListener != null) {
//        onBackResultListener.onPopBackToHome(bundle);
//      }
    }
  }

  protected final void finishSelf() {
    onBackInvoke();
  }

  private OnLocationChangedListener locationChangedListener = new OnLocationChangedListener() {
    @Override
    public void onLocationChanged(Address location) {
      if (location != null) {
        isLocationSuccess = true;
//        LocationProvider.getInstance().removeLocationChangedListener(this);
        updateMyLocation(location);
      }
    }
  };

  private void updateMyLocation(Address location) {
    mMap.updateMyLocation(location);
  }

  /**
   * 离开首页
   * 跳转到其他页面
   *
   * @CallSuper 子类必须 invoke super.onLeaveHome
   */
  @CallSuper
  public void onLeaveHome() {
    // 锁定DrawerLayout 不允许侧滑
    mNavigator.lockDrawerLayout(true);
    isHomePage = false;
    Logger.i(TAG, "onLeaveHome() >> " + isHomePage);
  }

  /**
   * 从其他页面回到根页面
   *
   * @CallSuper 子类必须 invoke super.onBackToHome
   */
  @Override
  public void onBackToHome() {
    isHomePage = true;
    Logger.i(TAG, "onBackToHome() >> " + isHomePage);
  }

  protected void recoverDefault() {
    mTopbarView.tabIndicatorAnim(false);
    mTopbarView.titleBarReset();
    mNavigator.backToRoot();
    mNavigator.lockDrawerLayout(false);
    mPinView.hide(false);
    mMap.showMyLocation();
    doBackHome();
  }

  private void doBackHome() {
    FragmentManager childManager = getFragmentManager();
    if (childManager != null) {
      List<Fragment> fragments = childManager.getFragments();
      for (Fragment fragment : fragments) {
        ServiceProvider provider = fragment.getClass().getAnnotation(ServiceProvider.class);
        if (provider == null) {
          continue;
        }
        fragment.onHiddenChanged(true);
      }
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
          RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) parent
              .getLayoutParams();
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

  protected void setTitleWithPosition(int resId, @TitlePosition int position) {
    mTopbarView.setTitleWithPosition(resId, position);
  }

  protected void hideRightImage(boolean hide) {
    mTopbarView.hideRightImage(hide);
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
    mNavigator.safePost(new Runnable() {
      @Override
      public void run() {
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
          manager.popBackStackImmediate(); // 先弹出栈
        }
      }
    });
    return true;
  }

  @Override
  public void onBackStackChanged() {
    mNavigator.safePost(new Runnable() {
      @Override
      public void run() {
        performBackStackChanged();
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Logger.e("BizEntranceFragment", "onDestroyView >>>>>");
//    popBackListener();
  }

  private void performBackStackChanged() {
    // 得到栈顶的Fragment 根据里边的arguments来 决定首页Fragment的隐藏
    Fragment topFragment = mNavigator.getCurrentFragment();

    if (topFragment != null && topFragment.isHidden()) {
      // 在跳转到一个新的Fragment时 之前的Fragment都会被hide 在back时需要被show出来
      FragmentTransaction transaction = mFragmentMgr.beginTransaction();
      transaction.show(topFragment);
      transaction.commitAllowingStateLoss();
    }

    Logger.e("ldx", "performBackStackChanged >>>>>>> topFragment " + topFragment);
    if (mRootListener != null) {
      // 如果栈顶没有Fragment了 表示已经回到了首页
      if (topFragment == null) {
        Logger.e("ldx", "onBackStackChanged is popBackStack to home");
        mRootListener.onBackToHome();
      }
    }
  }

  @Override
  public void setRootListener(IRootListener listener) {
    mRootListener = listener;
  }
}

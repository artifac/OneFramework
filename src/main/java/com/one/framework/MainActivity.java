package com.one.framework;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.widget.Toast;
import com.one.framework.app.base.BaseActivity;
import com.one.framework.app.common.SrcCarType;
import com.one.framework.app.login.ILogin;
import com.one.framework.app.login.ILogin.ILoginListener;
import com.one.framework.app.login.Login;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.model.BusinessContext;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.navigation.impl.Navigator;
import com.one.framework.app.page.IComponent;
import com.one.framework.app.page.ISlideDrawer;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.page.impl.NavigatorFragment;
import com.one.framework.app.page.impl.TopBarFragment;
import com.one.framework.app.widget.MapCenterPinView;
import com.one.framework.app.widget.base.IMapCenterPinView;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.log.Logger;
import com.one.framework.manager.ActivityDelegateManager;
import com.one.framework.net.Api;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.model.OrderTravelling;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.ToastUtils;
import com.one.map.IMap;
import com.one.map.MapFragment;
import com.one.map.model.Address;
import com.one.map.view.IMapDelegate.CenterLatLngParams;
import com.onecore.core.ISupportFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/26.
 */

public class MainActivity extends BaseActivity implements ITabItemListener, ILoginListener {

  private ActivityDelegateManager mDelegateManager;
  private IMap mMapFragment;
  private ITopbarFragment mTopbarFragment;
  private INavigator mNavigator;
  private IBusinessContext mBusinessContext;
  private DrawerLayout mDrawerLayout;
  private int mCurrentPosition = -1;
  private ISlideDrawer mNavigatorFragment;
  private LocalBroadcastManager mLocalBroadcastManager;
  private IMapCenterPinView mMapPinView;
  private ILogin mLogin;

  // 再点一次退出程序时间设置
  private static final long WAIT_TIME = 2000L;
  private long TOUCH_TIME = 0;

  /**
   * 是否有行程中订单，订单所属业务线
   */
  private int mHaveTrippingOrder;

  private List<TabItem> mBusinessEntrance;
  /**
   * 当前业务线 入口 Fragment
   */
  private Fragment mCurrentFragment;

  private ITopTitleListener mTopTitleListener = new ITopTitleListener() {
    @Override
    public void onTitleItemClick(ClickPosition position) {
      switch (position) {
        case LEFT: {
          if (!mLogin.isLogin()) {
            mLogin.showLogin(ILogin.DIALOG);
            return;
          }
          mDrawerLayout.openDrawer(Gravity.START);
          break;
        }
        case TITLE: {
          break;
        }
        case RIGHT: {
          break;
        }
      }
    }
  };

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.one_main_activity);

    addNavigator();

    mDrawerLayout = (DrawerLayout) findViewById(R.id.one_drawer_layout);
    mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.one_map_fragment);
    mTopbarFragment = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.one_top_bar_fragment);
    mNavigatorFragment = (NavigatorFragment) getSupportFragmentManager().findFragmentById(R.id.one_navigator_fragment);
    mMapPinView = (MapCenterPinView) findViewById(R.id.one_map_center_pin);
    mTopbarFragment.setTabItemListener(this);

    mDelegateManager = new ActivityDelegateManager(this);
    mDelegateManager.notifyOnCreate();

    mBusinessContext = new BusinessContext(this, mNavigatorFragment, mMapFragment, mTopbarFragment, mNavigator, mMapPinView);
    mBusinessEntrance = testTabItems();
    mTopbarFragment.setTabItems(mBusinessEntrance);
    mTopbarFragment.setAllBusiness(mBusinessEntrance);
    mMapFragment.setMapListener(this);
    mMapPinView.setMap(mMapFragment);
    mNavigatorFragment.setBusinessContext(mBusinessContext);

    mLogin = new Login(this);
    mLogin.setLoginListener(this);
    UserProfile.getInstance(this).setILogin(mLogin);
    lockDrawerLayout(!mLogin.isLogin());
    mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    obtainAllProjects();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mDelegateManager.notifyOnStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mDelegateManager.notifyOnResume();
  }

  @Override
  public void onMapMoveFinish(CenterLatLngParams params) {
    mMapPinView.reverseGeoAddress(params.center);
  }

  /**
   * 地址反转
   */
  @Override
  public void onMapGeo2Address(Address address) {
    mMapPinView.stop();
    HomeDataProvider.getInstance().saveLocAddress(address);
    // notify observer

    // todo current use LocalBroadcastManager
    Intent intent = new Intent("INTENT_CURRENT_LOCATION_ADDRESS");
    intent.putExtra("current_location_address", address);
    mLocalBroadcastManager.sendBroadcast(intent);
  }

  @Override
  public void onMapPoiAddresses(int type, List<Address> addresses) {
    HomeDataProvider.getInstance().savePoiAddresses(type, addresses);
  }

  @Override
  public void onItemClick(SrcCarType type) {
    TabItem selectTab = null;
    for (TabItem tab : mBusinessEntrance) {
      if (tab.tabBizType == type.bizCode) {
        selectTab = tab;
        break;
      } else {
        continue;
      }
    }
    if (selectTab != null) {
      onItemClick(selectTab);
    }
  }

  @Override
  public void onItemClick(TabItem item) {
    if (item.position == mCurrentPosition) {
      return;
    }
    mCurrentPosition = item.position;
    mTopbarFragment.tabItemClick(mCurrentPosition);
    String filter = constructUriString(item);
    Uri uri = Uri.parse(filter);
    Intent intent = new Intent();
    intent.setData(uri);
    intent.putExtra(INavigator.BUNDLE_ADD_TO_BACK_STACK, false);

    Fragment rootFragment = mNavigator.startFragment(intent, mBusinessContext, null);
    if (rootFragment == null) {
      // todo
    }
    Fragment lastEntranceFragment = mCurrentFragment;
    mCurrentFragment = rootFragment == null ? new Fragment() : rootFragment;
    if (lastEntranceFragment != null) {
      lastEntranceFragment.setUserVisibleHint(false);
    }
//    loadRootFragment(R.id.content_view_container, (ISupportFragment) mCurrentFragment);
    mCurrentFragment.setUserVisibleHint(true);
  }

  private String constructUriString(TabItem tab) {
    String uriString = "OneFramework://" + tab.tabBiz + "/entrance";
    return uriString;
  }

  @Override
  public void onLoginSuccess() {
    obtainAllProjects();
  }

  @Override
  public void onLoginFail() {

  }

  private void obtainAllProjects() {
    Api.allTravelling(new IResponseListener<OrderTravelling>() {
      @Override
      public void onSuccess(OrderTravelling travelling) {
        List<OrderDetail> lists = travelling.getOrderDetail();
        if (lists != null && !lists.isEmpty()) {
          OrderDetail selectBizEntrance = lists.get(0);
          mHaveTrippingOrder = selectBizEntrance.getBizType();
          if (mBusinessEntrance != null && !mBusinessEntrance.isEmpty()) {
            for (TabItem entrance: mBusinessEntrance) {
              if (entrance.tabBizType == mHaveTrippingOrder) {
                onItemClick(entrance);
              } else {
                continue;
              }
            }
          }
          HomeDataProvider.getInstance().saveOrderDetail(selectBizEntrance);
          Intent intent = new Intent("INTENT_FROM_RECOVERY_ACTION");
          intent.putExtra("recovery_data", selectBizEntrance);
          mLocalBroadcastManager.sendBroadcast(intent);
        }
      }

      @Override
      public void onFail(int errCode, OrderTravelling travelling) {

      }

      @Override
      public void onFinish(OrderTravelling travelling) {

      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    mDelegateManager.notifyOnPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mDelegateManager.notifyOnStop();
  }

  @Override
  public void onBackPressed() {
    FragmentManager manager = getSupportFragmentManager();
    if (manager != null) {
      int backStackCount = manager.getBackStackEntryCount();
      Logger.e("ldx", "MainActivity onBackPressed >>>>> " + backStackCount);
      if (backStackCount > 0) {
        // 在首页
        return;
      }
    }
    if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
      // TODO: 2018/6/29 do release job
      finish();
    } else {
      TOUCH_TIME = System.currentTimeMillis();
      ToastUtils.toast(this, getString(R.string.one_press_again_exit));
      Toast.makeText(this, getString(R.string.one_press_again_exit), Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    /**
     * 如果返回的currentFragment为空 则表示在首页点击返回
     */
    Fragment currentFragment = mNavigator.getCurrentFragment();
    if (currentFragment != null && currentFragment instanceof KeyEvent.Callback) {
      boolean flag = ((Callback) currentFragment).onKeyDown(keyCode, event);
      return !flag ? super.onKeyDown(keyCode, event) : flag;
    } else if (currentFragment == null && mCurrentFragment != null && mCurrentFragment instanceof IComponent) {
      // 在首页
      IComponent component = (IComponent) mCurrentFragment;
      if (component.onBackPressed()) {
        // 控件已经消费了此事件
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    return super.onKeyLongPress(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
    return super.onKeyMultiple(keyCode, repeatCount, event);
  }

  public void addNavigator() {
    mNavigator = new Navigator(this, getSupportFragmentManager());
  }

  public ITopTitleListener getTopTitleListener() {
    return mTopTitleListener;
  }

  public void lockDrawerLayout(boolean lock) {
    if (lock) {
      mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    } else {
      mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mDelegateManager.notifyOnDestroy();
  }

  /////////////////////// test //////
  private List<TabItem> testTabItems() {
    List<TabItem> items = new ArrayList<>();

    TabItem tab1 = new TabItem();
    tab1.tab = "快车";
    tab1.position = 0;
    tab1.tabBiz = "flash";
    tab1.tabIconResId = R.drawable.one_tab_business_bike;
    tab1.tabBizType = 2;
    tab1.isRedPoint = false;
    tab1.isSelected = false;
//
//    PopTabItem tab2 = new PopTabItem();
//    tab2.tab = "站点巴士";
//    tab2.position = 1;
//    tab2.tabIconResId = R.drawable.one_tab_business_bike;
//    tab2.tabBiz = "calendar";
//    tab2.isRedPoint = false;
//    tab2.isSelected = false;
//
//    PopTabItem tab3 = new PopTabItem();
//    tab3.tab = "专车";
//    tab3.position = 2;
//    tab3.tabIconResId = R.drawable.one_tab_business_bike;
//    tab3.tabBiz = "premium";
//    tab3.isRedPoint = false;
//    tab3.isSelected = false;

    TabItem tab4 = new TabItem();
    tab4.tab = "出租车";
    tab4.position = 1;
    tab4.tabBiz = "taxi";
    tab4.isRedPoint = false;
    tab4.tabBizType = 3;
    tab4.tabIconResId = R.drawable.one_tab_business_bike;
    tab4.isSelected = true;

//    PopTabItem tab5 = new PopTabItem();
//    tab5.tab = "共享单车";
//    tab5.position = 4;
//    tab5.tabBiz = "driver";
//    tab5.isRedPoint = true;
//    tab5.tabIconResId = R.drawable.one_tab_business_bike;
//    tab5.isSelected = false;
//
//    PopTabItem tab6 = new PopTabItem();
//    tab6.tab = "专车";
//    tab6.position = 5;
//    tab6.tabBiz = "premium";
//    tab6.isRedPoint = false;
//    tab6.isSelected = false;
//    tab6.tabIconResId = R.drawable.one_tab_business_bike;
//
//    PopTabItem tab7 = new PopTabItem();
//    tab7.tab = "香港专车";
//    tab7.position = 6;
//    tab7.tabBiz = "premium";
//    tab7.isRedPoint = false;
//    tab7.isSelected = false;
//    tab7.tabIconResId = R.drawable.one_tab_business_bike;

    items.add(tab1);
//    items.add(tab2);
//    items.add(tab3);
    items.add(tab4);
//    items.add(tab5);
//    items.add(tab6);
//    items.add(tab7);

    return items;
  }
}

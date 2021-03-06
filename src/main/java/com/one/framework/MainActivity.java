package com.one.framework;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.View;
import com.mobike.mocha.base.MqttOptions;
import com.netease.nim.uikit.api.NimUIKit;
import com.one.framework.app.ads.AdsBean;
import com.one.framework.app.ads.AdsModel;
import com.one.framework.app.ads.AdsPopup;
import com.one.framework.app.ads.IAdsPopup;
import com.one.framework.app.base.BaseActivity;
import com.one.framework.app.common.SrcCarType;
import com.one.framework.app.login.ILogin;
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
import com.one.framework.model.ContactLists;
import com.one.framework.mqtt.MqttConfig;
import com.one.framework.mqtt.MqttLink;
import com.one.framework.net.Api;
import com.one.framework.net.model.Entrance;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.model.OrderTravelling;
import com.one.framework.net.response.IResponseListener;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.GlobalUtils;
import com.one.framework.utils.ToastUtils;
import com.one.map.IMap;
import com.one.map.MapFragment;
import com.one.map.model.Address;
import com.one.map.view.IMapDelegate.CenterLatLngParams;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by ludexiang on 2018/3/26.
 */

public class MainActivity extends BaseActivity implements ITabItemListener {

  private ActivityDelegateManager mDelegateManager;
  public static final int LOGIN_REQUEST_CODE = 1;
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

  private IAdsPopup mAdsPop;
  private boolean isShowAd;
  private AdsModel mAdsModel;

  private MqttLink mqttLink;

  private ITopTitleListener mTopTitleListener = new ITopTitleListener() {
    @Override
    public void onTitleItemClick(ClickPosition position) {
      switch (position) {
        case LEFT: {
          if (!mLogin.isLogin()) {
//            mLogin.showLogin(ILogin.DIALOG);
            GlobalUtils.login(MainActivity.this, LOGIN_REQUEST_CODE);
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
    mTopbarFragment.setTitleClickListener(mTopTitleListener);

    mDelegateManager = new ActivityDelegateManager(this);
    mDelegateManager.notifyOnCreate();

    mBusinessContext = new BusinessContext(this, mNavigatorFragment, mMapFragment, mTopbarFragment, mNavigator, mMapPinView);
    mBusinessEntrance = addEntrances();
    mTopbarFragment.setTabItems(mBusinessEntrance);
    mTopbarFragment.setAllBusiness(mBusinessEntrance);
    mMapFragment.setMapListener(this);
    mMapPinView.setMap(mMapFragment);
    mNavigatorFragment.setBusinessContext(mBusinessContext);

    mLogin = new Login(this);
//    mLogin.setLoginListener(this);
    UserProfile.getInstance(this).setILogin(mLogin);
    lockDrawerLayout(!mLogin.isLogin());
    mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    mAdsPop = new AdsPopup(this, null);

    // cpu 架构
    Logger.d("ldx", "cpu arch = " + (Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0]));

    // 检查 so 是否存在
    File file = new File(this.getApplicationInfo().nativeLibraryDir + File.separator + "libgetuiext2.so");
    Logger.e("ldx", "libgetuiext2.so exist = " + file.exists());

    NimUIKit.init(this);

    String userId = UserProfile.getInstance(this).getUserId();
    String userToken = UserProfile.getInstance(this).getTokenValue();
    if (!TextUtils.isEmpty(userId) || !TextUtils.isEmpty(userToken)) {
      loginSuccess();
    }

    obtainAds();
    GlobalUtils.requestAppVersion(this);

    mDrawerLayout.addDrawerListener(new DrawerListener() {
      @Override
      public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

      }

      @Override
      public void onDrawerOpened(@NonNull View drawerView) {

      }

      @Override
      public void onDrawerClosed(@NonNull View drawerView) {
        Logger.e("ldx", "onDrawerLayout closed >>>>");
        mNavigatorFragment.recoveryDefault();
      }

      @Override
      public void onDrawerStateChanged(int newState) {

      }
    });

    initMqtt();
  }

  private void initMqtt() {
    MqttConfig config = new MqttConfig(new MqttOptions(this));
    mqttLink = MqttLink.INSTANCE;
    mqttLink.init(this , config);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Fragment currentFragment = mNavigator.getCurrentFragment();
    Logger.e("ldx", "MainActivity onNewIntent >>>>> curFragment > " + currentFragment);
    if (currentFragment != null && currentFragment instanceof IComponent) {
      IComponent component = (IComponent) currentFragment;
      component.onNewIntent(intent);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Logger.e("ldx", "onActivityResult >>>>>> "+ requestCode + " resultCode " + resultCode);
    if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
      loginSuccess();
    }
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
    mNavigator.onResume();
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
    // 保存geo reverse address
    HomeDataProvider.getInstance().saveCurAddress(address);
    // notify observer

    Logger.e("ldx", "reverse geo success send broadcast");
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

    Fragment rootFragment = mNavigator.startFragment(intent, mBusinessContext);
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

    if (mAdsModel != null) {
      List<AdsBean> ads = mAdsModel.getAds();
      if (ads != null && !ads.isEmpty()) {
        for (AdsBean adsBean : ads) {
          if (item.tabBizType == adsBean.getBizType()) {
            showAdsPop(adsBean);
            break;
          }
          continue;
        }
      }

    }
  }

  private String constructUriString(TabItem tab) {
    String uriString = "OneFramework://" + tab.tabBiz + "/entrance";
    return uriString;
  }

  private void loginSuccess() {
    Logger.e("ldx", "MainActivity >>> onLoginSuccess");
    mNavigatorFragment.refreshHeader();
    EventBus.getDefault().post(true);
    requestAppConfig();
    queryContact();
    obtainAllProjects();
  }

  /**
   * 获取是否有行程中订单
   */
  private void obtainAllProjects() {
    Api.allTravelling(new IResponseListener<OrderTravelling>() {
      @Override
      public void onSuccess(OrderTravelling travelling) {
        List<OrderDetail> lists = travelling.getOrderDetail();
        HomeDataProvider.getInstance().saveOrderDetails(lists);

        if (lists != null && !lists.isEmpty()) {
          OrderDetail selectBizEntrance = HomeDataProvider.getInstance().obtainOrderDetail();
          mHaveTrippingOrder = selectBizEntrance.getBizType();
          if (mBusinessEntrance != null && !mBusinessEntrance.isEmpty()) {
            for (TabItem entrance : mBusinessEntrance) {
              if (entrance.tabBizType == mHaveTrippingOrder) {
                onItemClick(entrance);
              } else {
                continue;
              }
            }
          }


          Intent intent = new Intent("INTENT_FROM_RECOVERY_ACTION");
          intent.putExtra("recovery_data", selectBizEntrance);
          mLocalBroadcastManager.sendBroadcast(intent);
        }
      }

      @Override
      public void onFail(int errCode, String message) {

      }

      @Override
      public void onFinish(OrderTravelling travelling) {

      }
    });
  }

  /**
   * 包含多业务线的运行广告位
   */
  private void obtainAds() {
    Api.bizAds(new IResponseListener<AdsModel>() {
      @Override
      public void onSuccess(AdsModel adsModel) {
        mAdsModel = adsModel;
        List<AdsBean> ads = mAdsModel.getAds();
        if (ads != null && !ads.isEmpty()) {
          for (AdsBean adsBean : ads) {
            if (mTopbarFragment.getBizType(mCurrentPosition) == adsBean.getBizType()) {
              showAdsPop(adsBean);
              break;
            }
            continue;
          }
        }
      }

      @Override
      public void onFail(int errCod, String message) {
        Logger.e("ldx", "ads >>>>>>> fail");
      }

      @Override
      public void onFinish(AdsModel adsModel) {
        Logger.e("ldx", "ads >>>>>>> finish " + adsModel);
      }
    });
  }

  /**
   * 获取紧急联系人
   */
  private void queryContact() {
    Api.queryContact(new IResponseListener<ContactLists>() {
      @Override
      public void onSuccess(ContactLists contactLists) {
        HomeDataProvider.getInstance().saveContact(contactLists.getContactLists());
      }

      @Override
      public void onFail(int errCod, String message) {

      }

      @Override
      public void onFinish(ContactLists contactLists) {

      }
    });
  }

  private void showAdsPop(AdsBean model) {
    if (mAdsPop != null && (mAdsPop.isShowing() || mAdsPop.giftShowing())) {
      return;
    }
    mAdsPop.show(model);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mDelegateManager.notifyOnPause();
    mNavigator.onPause();
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
        // 非首页
        return;
      }
    }
//    if (adsPop != null && adsPop.isShowing) {
//      adsPop.dismiss()
//      return
//    }
//    if (adsPop != null) {
//      adsPop.noMore()
//    }
//
//    trip_gift_view.noMore()
    if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
      // TODO: 2018/6/29 do noMore job
      finish();
    } else {
      TOUCH_TIME = System.currentTimeMillis();
      ToastUtils.toast(this, getString(R.string.one_press_again_exit));
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
    } else if (currentFragment == null && mCurrentFragment != null
        && mCurrentFragment instanceof IComponent) {
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
    mqttLink.stop();
  }

  /**
   * 添加入口
   */
  private List<TabItem> addEntrances() {
    List<TabItem> items = new ArrayList<>();
    List<Entrance> entrances = HomeDataProvider.getInstance().obtainEntrance();
    for (Entrance entrance : entrances) {
      TabItem tab = new TabItem();
      tab.tab = entrance.getEntranceTab();
      tab.position = entrance.getEntranceTabPosition();
      tab.tabBiz = entrance.getEntranceTabBiz();
      tab.isRedPoint = entrance.isRedPoint();
      tab.tabBizType = entrance.getEntranceTabBizType();
      tab.tabIconResId = R.drawable.one_tab_business_bike;
      tab.isSelected = entrance.getEntranceTabSelected();
      items.add(tab);
    }

    return items;
  }

}

package com.one.framework;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import com.one.framework.app.model.BusinessContext;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.navigation.impl.Navigator;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.page.impl.NavigatorFragment;
import com.one.framework.app.page.impl.TopBarFragment;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.log.Logger;
import com.one.framework.manager.ActivityDelegateManager;
import com.one.map.IMap;
import com.one.map.MapFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/26.
 */

public class MainActivity extends FragmentActivity implements ITabItemListener {

  private ActivityDelegateManager mDelegateManager;
  private IMap mMapFragment;
  private ITopbarFragment mTopbarFragment;
  private INavigator mNavigator;
  private IBusinessContext mBusinessContext;
  private DrawerLayout mDrawerLayout;
  private int mCurrentPosition = -1;
  private NavigatorFragment mNavigatorFragment;

  private ITopTitleListener mTopTitleListener = new ITopTitleListener() {
    @Override
    public void onTitleItemClick(ClickPosition position) {
      Logger.e("ldx", "TOP title listener >>>>>" + position);
      switch (position) {
        case LEFT: {
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
    mMapFragment = (MapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.one_map_fragment);
    mTopbarFragment = (TopBarFragment) getSupportFragmentManager()
        .findFragmentById(R.id.one_top_bar_fragment);
    mNavigatorFragment = (NavigatorFragment) getSupportFragmentManager()
        .findFragmentById(R.id.one_navigator_fragment);
    mTopbarFragment.setTabItemListener(this);

    mDelegateManager = new ActivityDelegateManager(this);
    mDelegateManager.notifyOnCreate();

    mBusinessContext = new BusinessContext(this, mMapFragment, mTopbarFragment, mNavigator);

    mTopbarFragment.setTabItems(testTabItems());

    mTopbarFragment.setAllBusiness(testTabItems());
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
  public void onItemClick(TabItem item) {
    if (item.position == mCurrentPosition) {
      return;
    }
    mCurrentPosition = item.position;
    String filter = constructUriString(item);
    Uri uri = Uri.parse(filter);
    Intent intent = new Intent();
    intent.setData(uri);
    intent.putExtra(INavigator.BUNDLE_ADD_TO_BACK_STACK, false);

    mNavigator.startFragment(intent, mBusinessContext);
  }

  private String constructUriString(TabItem tab) {
    String uriString = "OneFramework://" + tab.tabBiz + "/entrance";
    return uriString;
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

  public void addNavigator() {
    mNavigator = new Navigator(this, getSupportFragmentManager());
  }

  public ITopTitleListener getTopTitleListener() {
    return mTopTitleListener;
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
    tab1.isRedPoint = false;
    tab1.isSelected = true;

    TabItem tab2 = new TabItem();
    tab2.tab = "站点巴士";
    tab2.position = 1;
    tab2.tabIconResId = R.drawable.one_tab_business_bike;
    tab2.tabBiz = "calendar";
    tab2.isRedPoint = false;
    tab2.isSelected = false;

    TabItem tab3 = new TabItem();
    tab3.tab = "专车";
    tab3.position = 2;
    tab3.tabIconResId = R.drawable.one_tab_business_bike;
    tab3.tabBiz = "premium";
    tab3.isRedPoint = false;
    tab3.isSelected = false;

    TabItem tab4 = new TabItem();
    tab4.tab = "出租车";
    tab4.position = 3;
    tab4.tabBiz = "taxi";
    tab4.isRedPoint = true;
    tab4.tabIconResId = R.drawable.one_tab_business_bike;
    tab4.isSelected = false;

    TabItem tab5 = new TabItem();
    tab5.tab = "摩拜单车";
    tab5.position = 4;
    tab5.tabBiz = "mobike";
    tab5.isRedPoint = true;
    tab5.tabIconResId = R.drawable.one_tab_business_bike;
    tab5.isSelected = false;

    TabItem tab6 = new TabItem();
    tab6.tab = "专车";
    tab6.position = 5;
    tab6.tabBiz = "premium";
    tab6.isRedPoint = false;
    tab6.isSelected = false;
    tab6.tabIconResId = R.drawable.one_tab_business_bike;

    TabItem tab7 = new TabItem();
    tab7.tab = "香港专车";
    tab7.position = 6;
    tab7.tabBiz = "premium";
    tab7.isRedPoint = false;
    tab7.isSelected = false;
    tab7.tabIconResId = R.drawable.one_tab_business_bike;

    items.add(tab1);
    items.add(tab2);
    items.add(tab3);
    items.add(tab4);
    items.add(tab5);
    items.add(tab6);
    items.add(tab7);

    return items;
  }
}

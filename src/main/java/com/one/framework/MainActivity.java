package com.one.framework;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.one.framework.app.model.BusinessContext;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.navigation.impl.Navigator;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.page.impl.TopBarFragment;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
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

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.one_main_activity);

    addNavigator();

    mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.one_map_fragment);
    mTopbarFragment = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.one_top_bar_fragment);
    mTopbarFragment.setTabItemListener(this);

    mDelegateManager = new ActivityDelegateManager(this);
    mDelegateManager.notifyOnCreate();

    mBusinessContext = new BusinessContext(this, mMapFragment, mTopbarFragment);

    mTopbarFragment.setTabItems(testTabItems());
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
    String filter = constructUriString(item);
    Uri uri = Uri.parse(filter);
    Intent intent = new Intent();
    intent.setData(uri);

    mNavigator.fillPage(mNavigator.getFragment(this, intent, mBusinessContext), R.id.content_view_container);
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
    mNavigator = new Navigator(getSupportFragmentManager());
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
    tab1.tab = "站点巴士";
    tab1.position = 0;
    tab1.tabBiz = "calendar";
    tab1.isRedPoint = false;
    tab1.isSelected = true;

    TabItem tab2 = new TabItem();
    tab2.tab = "快车";
    tab2.position = 1;
    tab2.tabBiz = "flash";
    tab2.isRedPoint = false;
    tab2.isSelected = false;

    TabItem tab3 = new TabItem();
    tab3.tab = "专车";
    tab3.position = 2;
    tab3.tabBiz = "premium";
    tab3.isRedPoint = false;
    tab3.isSelected = false;

    TabItem tab4 = new TabItem();
    tab4.tab = "出租车";
    tab4.position = 3;
    tab4.tabBiz = "taxi";
    tab4.isRedPoint = true;
    tab4.isSelected = false;

    TabItem tab5 = new TabItem();
    tab5.tab = "摩拜单车";
    tab5.position = 4;
    tab5.tabBiz = "mobike";
    tab5.isRedPoint = true;
    tab5.isSelected = false;

//    TabItem tab6 = new TabItem();
//    tab6.tab = "专车";
//    tab6.position = 5;
//    tab6.tabBiz = "premium";
//    tab6.isRedPoint = false;
//    tab6.isSelected = false;
//
//    TabItem tab7 = new TabItem();
//    tab7.tab = "香港专车";
//    tab7.position = 6;
//    tab7.tabBiz = "premium";
//    tab7.isRedPoint = false;
//    tab7.isSelected = false;

    items.add(tab1);
    items.add(tab2);
    items.add(tab3);
    items.add(tab4);
    items.add(tab5);
//    items.add(tab6);
//    items.add(tab7);

    return items;
  }
}

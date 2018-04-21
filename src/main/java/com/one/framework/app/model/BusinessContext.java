package com.one.framework.app.model;

import android.content.Context;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.page.ITopbarFragment;
import com.one.map.IMap;

/**
 * Created by ludexiang on 2018/4/10.
 * 业务上下文
 * 此类只提供get方法
 */

public class BusinessContext implements IBusinessContext {

  private Context mContext;
  private IMap mMap;
  private ITopbarFragment mTopbar;
  private INavigator mNavigator;

  public BusinessContext(Context context, IMap map, ITopbarFragment topbar, INavigator navigator) {
    mContext = context;
    mMap = map;
    mTopbar = topbar;
    mNavigator = navigator;
  }

  @Override
  public Context getContext() {
    return mContext;
  }

  @Override
  public IMap getMap() {
    return mMap;
  }

  @Override
  public ITopbarFragment getTopbar() {
    return mTopbar;
  }

  @Override
  public INavigator getNavigator() {
    return mNavigator;
  }
}

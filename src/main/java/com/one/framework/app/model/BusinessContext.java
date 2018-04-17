package com.one.framework.app.model;

import android.content.Context;
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

  public BusinessContext(Context context, IMap map, ITopbarFragment topbar) {
    mContext = context;
    mMap = map;
    mTopbar = topbar;
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
}

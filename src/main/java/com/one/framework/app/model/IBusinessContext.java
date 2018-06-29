package com.one.framework.app.model;

import android.content.Context;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.page.ISlideDrawer;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.base.IMapCenterPinView;
import com.one.map.IMap;
import java.io.Serializable;

/**
 * Created by ludexiang on 2018/4/10.
 * 业务上下文，包含各种model
 */

public interface IBusinessContext extends Serializable {
  Context getContext();
  IMap getMap();
  ITopbarFragment getTopbar();
  INavigator getNavigator();
  IMapCenterPinView getPinView();
  ISlideDrawer getSlideDrawer();
}

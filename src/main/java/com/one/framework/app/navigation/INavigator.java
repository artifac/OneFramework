package com.one.framework.app.navigation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.one.framework.app.model.IBusinessContext;

/**
 * Created by ludexiang on 2018/3/28.
 */

public interface INavigator {
  Fragment getFragment(Context context, Intent intent, IBusinessContext businessContext);
  void fillPage(Fragment fragment, int contentId);
}

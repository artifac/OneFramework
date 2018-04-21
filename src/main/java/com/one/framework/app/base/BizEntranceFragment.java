package com.one.framework.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import com.one.framework.MainActivity;
import com.one.framework.api.annotation.ServiceProvider;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.navigation.INavigator;
import com.one.framework.app.navigation.impl.Navigator;
import com.one.framework.app.page.IComponent;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.base.ITopTitleView;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.log.Logger;
import com.one.framework.manager.PageDelegateManager;
import com.one.map.IMap;
import java.lang.ref.SoftReference;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class BizEntranceFragment extends Fragment implements IComponent, KeyEvent.Callback,
    ITopTitleListener {

  private SoftReference<MainActivity> mActivity;
  protected IBusinessContext mBusContext;
  private ITopbarFragment mTopbarView;
  protected IMap mMap;
  protected boolean isRootFragment;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = new SoftReference<MainActivity>((MainActivity) activity);
    if (isRootFragment && mActivity != null && mActivity.get() != null) {
      mTopbarView.setTitleClickListener(mActivity.get().getTopTitleListener());
    }
  }

  /**
   * 优先于 onAttach
   * @param businessContext
   */
  @Override
  public void setBusinessContext(IBusinessContext businessContext) {
    mBusContext = businessContext;
    mTopbarView = mBusContext.getTopbar();
    mMap = mBusContext.getMap();

    ServiceProvider provider = getClass().getAnnotation(ServiceProvider.class);
    if (provider != null) {
      isRootFragment = true;
    } else {
      isRootFragment = false;
      mTopbarView.setTitleClickListener(this);
    }
  }

  /**
   * 跳转到下一个界面
   * @param clazz
   */
  protected final void forward(Class<? extends Fragment> clazz) {
    forward(clazz, null);
  }

  /**
   * 跳转到下一个界面
   * @param clazz
   * @param args
   */
  protected final void forward(Class<? extends Fragment> clazz, Bundle args) {
    if (mBusContext == null) {
      return;
    }
    mBusContext.getTopbar().getTabView().setVisibility(View.GONE);
    Intent intent = new Intent();
    intent.setClass(getContext(), clazz);
    args = args == null ? new Bundle() : args;
    intent.putExtras(args);
    mBusContext.getNavigator().startFragment(intent, mBusContext);
  }

  /**
   * 返回根布局
   */
  protected final void backToRoot() {
    mBusContext.getTopbar().getTabView().setVisibility(View.VISIBLE);
    mBusContext.getNavigator().backToRoot();
  }

  /** view 设置 **/
  protected void setTitle(String title) {
    mTopbarView.setTitle(title);
  }

  protected void setTitle(int resId) {
    mTopbarView.setTitle(resId);
  }

  /**
   * sub child Fragment 可以复写此方法处理title bar 点击事件
   * @param position
   */
  @Override
  public void onTitleItemClick(ClickPosition position) {
    Logger.e("ldx", "BizEntranceFragment >>>>>>>>>>>>>");
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    Logger.e("ldx", "keyCode ....." + keyCode);
    return false;
  }

  @Override
  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    return false;
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    Logger.e("ldx", "keyCode  onKeyUp....." + keyCode);
    return false;
  }

  @Override
  public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
    return false;
  }

}

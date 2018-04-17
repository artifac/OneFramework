package com.one.framework.app.page.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.TopTitleLayout;
import com.one.framework.app.widget.base.ITabIndicatorListener;
import com.one.framework.app.widget.base.ITabIndicatorListener.IScaleListener;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.app.widget.TabIndicator;
import com.one.framework.app.widget.base.ITopTitleView;
import com.one.framework.log.Logger;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class TopBarFragment extends Fragment implements ITopbarFragment, IScaleListener {
  private FrameLayout mTabParentView;
  private ITabIndicatorListener mTabIndicator;
  private ITopTitleView mTopTitleView;
  private ImageView mMenuView;
  private float mRotation;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.one_top_bar_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mTopTitleView = (TopTitleLayout) view.findViewById(R.id.one_top_title_layout);
    mTabParentView = (FrameLayout) view.findViewById(R.id.one_tab_container_parent);
    mTabIndicator = (TabIndicator) view.findViewById(R.id.one_top_bar_tab_indicator);
    mMenuView = (ImageView) view.findViewById(R.id.one_top_bar_tab_menu);
    mTabIndicator.setScaleListener(this);
  }

  @Override
  public void setTabItemListener(ITabItemListener listener) {
    mTabIndicator.setTabItemListener(listener);
  }

  @Override
  public void setTabItems(List<TabItem> items) {
    mTabIndicator.setTabItems(items);
  }

  @Override
  public void onScaleMove(float scale) {
    mRotation += scale;
    mMenuView.setRotation(mRotation * 360);
  }

  @Override
  public void onScaleUp() {

  }

  @Override
  public int getTopbarHeight() {
    Logger.e("ldx", "titleHeight " + mTopTitleView.getViewHeight() + " tabHeight " + mTabIndicator.getViewHeight());
    return mTopTitleView.getViewHeight() + mTabIndicator.getViewHeight();
  }

  @Override
  public View getTabView() {
    return mTabParentView;
  }

  @Override
  public void setTitle(String title) {
    mTopTitleView.setTitle(title);
  }

  @Override
  public void setTitle(int titleResId) {
    mTopTitleView.setTitle(titleResId);
  }
}

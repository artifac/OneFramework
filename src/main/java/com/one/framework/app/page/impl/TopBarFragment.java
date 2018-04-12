package com.one.framework.app.page.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.base.ITabIndicatorListener;
import com.one.framework.app.widget.base.ITabIndicatorListener.IScaleListener;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.app.widget.impl.TabIndicator;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class TopBarFragment extends Fragment implements ITopbarFragment, IScaleListener {
  private ITabIndicatorListener mTabIndicator;
  private ImageView mMenuView;

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
  public void onScale(float scale) {
    Log.e("ldx", "MenuView scale >>>> " + scale);
//    mMenuView.setPivotX(0.5f);
//    mMenuView.setPivotY(0.5f);
//    mMenuView.setScaleX(scale);
//    mMenuView.setScaleY(scale);
  }
}

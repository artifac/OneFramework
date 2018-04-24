package com.one.framework.app.widget.base;

import com.one.framework.app.model.TabItem;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/28.
 */

public interface ITabIndicatorListener {

  void setTabItems(List<TabItem> items);

  void setTabItemListener(ITabItemListener listener);

  void setScaleListener(IScaleListener listener);

  int getViewHeight();

  interface ITabItemListener {
    void onItemClick(TabItem item);
  }

  interface IScaleListener {
    void onScaleMove(float scale);
    void onScaleUp();
  }

  void update(int position);
}

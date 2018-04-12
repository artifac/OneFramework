package com.one.framework.app.page;

import com.one.framework.app.model.TabItem;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/28.
 */

public interface ITopbarFragment {
  void setTabItemListener(ITabItemListener listener);
  void setTabItems(List<TabItem> items);
}

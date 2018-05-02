package com.one.framework.app.page;

import android.view.View;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/28.
 */

public interface ITopbarFragment {
  void setTabItemListener(ITabItemListener listener);
  void setTitleClickListener(ITopTitleListener listener);
  void setTabItems(List<TabItem> items);
  void setTitle(String title);
  void setTitle(int titleResId);

  /**
   * 整体标题栏高度
   * @return
   */
  int getTopbarHeight();

  /**
   * 设置所有业务
   * @param tabs
   */
  void setAllBusiness(List<TabItem> tabs);

  /**
   * 弹出返回键监听
   */
  void popBackListener();

  void tabIndicatorAnim(boolean show);
}

package com.one.framework.app.widget.base;

import android.support.annotation.LayoutRes;
import android.view.View;

/**
 * Created by ludexiang on 2018/4/3.
 */

public interface IPullView {

  /**
   * 获取滚动的距离
   */
  int getScrollingPadding();

  /**
   * 获取header 滚动的高度
   */
  int getHeaderScrollHeight();

  /**
   * 是否滚动到底部
   */
  boolean isScrollBottom();

  /**
   * header 是否继续滚动
   */
  boolean isHeaderNeedScroll();

  int getHeaderHeight();

  void setItemClickListener(IItemClickListener listener);

  View getView();

  void setHaveHeaderView(boolean flag);

  void setHeaderView(View view);

  void setHeaderView(@LayoutRes int layout);

  void setHaveFooterView(boolean flag);

  interface IPullCallback {
    void move(float x, float y);
    void up(float y);
  }

  void setPullCallback(IPullCallback listener);
}

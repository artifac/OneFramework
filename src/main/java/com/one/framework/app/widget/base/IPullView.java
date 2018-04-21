package com.one.framework.app.widget.base;

import android.view.View;

/**
 * Created by ludexiang on 2018/4/3.
 */

public interface IPullView {

  /**
   * 获取滚动的高度
   */
  int getScrollingY();

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

  View getView();
}

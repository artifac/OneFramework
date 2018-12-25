package com.one.framework.app.widget.base;

import android.support.annotation.LayoutRes;
import android.view.View;

/**
 * Created by ludexiang on 2018/4/3.
 */

public interface IPullView {

  int ANIM_DURATION = 200;

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

  void setHeaderView(@LayoutRes int headerViewHeight);

  void setCustomHeaderViewHeight(int layout);

  void setHaveFooterView(boolean flag);

  void goonMove(long duration, boolean rollback);

  interface IPullCallback {

    /**
     * 手势下拉时 Move 事件
     * @param x
     * @param y
     */
    void move(float x, float y);

    /**
     * 放手之后 Up 回调
     * @param y
     */
    void up(float y);

    /**
     * up 之后的move事件(动画)
     * @param x
     * @param y
     */
    void moveAfterUp(float x, float y);
  }

  interface ILoadListener {

    void onLoadRefresh();
    void onLoadMore();
    void onLoadComplete();

  }
  void setLoadListener(ILoadListener listener);

  void setPullCallback(IPullCallback listener);
}

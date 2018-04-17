package com.one.framework.app.widget.base;

import android.view.View;

/**
 * Created by ludexiang on 2018/4/3.
 */

public interface IHeaderView {
  void onMove(float offsetX, float offsetY);
  void onUp(boolean bottom2Up, boolean isFling);
  int getHeaderHeight();
  int getScrollHeaderHeight();
  boolean isNeedScroll();
  View getView();
}
package com.one.framework.app.widget.base;

import android.support.annotation.LayoutRes;
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
  void setHeaderView(@LayoutRes int layout);
  void setHeaderView(View view);
  void setHeaderTitle(String text);
  void updateView(View view);
  View getView();
}

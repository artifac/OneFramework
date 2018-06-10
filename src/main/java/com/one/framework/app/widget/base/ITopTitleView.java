package com.one.framework.app.widget.base;


import android.view.View;

/**
 * Created by ludexiang on 2018/4/17.
 */

public interface ITopTitleView {

  enum ClickPosition {
    LEFT, TITLE, RIGHT
  }

  void setTitle(String title);
  void setTitle(int resId);
  void setRight(String txtBtn);
  void setRight(int txtResId);
  void setLeftImage(int resId);
  int getViewHeight();

  interface ITopTitleListener {
    void onTitleItemClick(ClickPosition position);
  }

  void setTopTitleListener(ITopTitleListener listener);

  void popBackListener();

  void titleReset();

  View getView();
}

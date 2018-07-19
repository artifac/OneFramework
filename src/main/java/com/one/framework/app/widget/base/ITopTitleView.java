package com.one.framework.app.widget.base;


import android.support.annotation.Keep;
import android.view.View;

/**
 * Created by ludexiang on 2018/4/17.
 */
@Keep
public interface ITopTitleView {

  enum ClickPosition {
    LEFT, TITLE, RIGHT
  }

  void setTitle(String title);
  void setTitle(int resId);
  void setTitle(String title, int sizeSp);
  void setRightText(String txtBtn);
  void setRightResId(int txtResId);
  void setRightCompoundDrawableBounds(int left, int top, int right, int bottom);
  void setLeftImage(int resId);
  int getViewHeight();

  @Keep
  interface ITopTitleListener {
    void onTitleItemClick(ClickPosition position);
  }

  void setTopTitleListener(ITopTitleListener listener);

  void popBackListener();

  void titleReset();

  View getRightView();

  View getView();
}

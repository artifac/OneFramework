package com.one.framework.app.widget.base;


import android.graphics.Typeface;
import android.support.annotation.Keep;
import android.view.View;
import com.one.framework.app.page.ITopbarFragment.TitlePosition;

/**
 * Created by ludexiang on 2018/4/17.
 */
@Keep
public interface ITopTitleView {

  @Keep
  enum ClickPosition {
    LEFT, TITLE, RIGHT
  }

  void setTitle(String title);
  void setTitle(int resId);
  void setTitle(String title, int sizeSp);
  void setTitle(String title, int sizeSp, Typeface typeface);
  void setTitleWithPosition(String title, @TitlePosition int position);
  void setRightText(String txtBtn);
  void setRightResId(int txtResId);
  void setRightResId(int txtResId, int color);
  void setRightCompoundDrawableBounds(int left, int top, int right, int bottom);
  void setLeftImage(int resId);
  void hideRightImage(boolean hide);
  void setRightImage(int resId);
  int getViewHeight();

  void setTitleBarBackground(int color);
  void setTitleBarBackgroundResources(int res);

  void setCloseVisible(boolean visible);

  @Keep
  interface ITopTitleListener {
    void onTitleItemClick(ClickPosition position);
  }

  void setTopTitleListener(ITopTitleListener listener);

  void popBackListener();

  void titleReset();

  /**
   * handle 添加紧急联系人 用户点击通讯录时 再点击 x 导致 返回键列表错误
   */
  void setSamePageBack(boolean samePage);

  View getRightView();

  View getView();
}

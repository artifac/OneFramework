package com.one.framework.app.page;

import android.graphics.Typeface;
import android.support.annotation.IntDef;
import android.view.View;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/28.
 */

public interface ITopbarFragment {
  int LEFT = 0;
  int CENTER = 1;
  void setTitleBarBackground(int color);
  void setTitleBarBackgroundResources(int resId);
  void setTabItemListener(ITabItemListener listener);
  void setTitleClickListener(ITopTitleListener listener);
  void setTabItems(List<TabItem> items);
  void setTitle(String title);
  void setTitle(String title, int sizeSp);
  void setTitle(int titleResId);
  void setTitle(String title, int sizeSp, Typeface typeface);
  void setTitleWithPosition(String title, @TitlePosition int position);
  void setTitleWithPosition(int titleResId, @TitlePosition int position);
  void hideRightImage(boolean hide);
  void titleBarReset();

  void setTitleRight(int txtResId);

  void setTitleRight(String right);

  void setTitleRight(int textResId, int color);

  void setCompoundDrawableBounds(int left, int top, int right, int bottom);

  void setLeft(int resId);

  void setRight(int resId);

  int getBizType(int position);

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

  void tabItemClick(int position);

  View getRightView();

  @IntDef({LEFT, CENTER})
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
  @interface TitlePosition {
  }
}

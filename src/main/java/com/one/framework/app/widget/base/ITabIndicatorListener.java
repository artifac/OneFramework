package com.one.framework.app.widget.base;

import android.support.annotation.Keep;
import com.one.framework.app.common.SrcCarType;
import com.one.framework.app.model.TabItem;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/28.
 */
@Keep
public interface ITabIndicatorListener {

  void setTabItems(List<TabItem> items);

  void setTabItemListener(ITabItemListener listener);

  void setScaleListener(IScaleListener listener);

  int getViewHeight();

  int getBizType(int position);

  @Keep
  interface ITabItemListener {
    void onItemClick(TabItem item);
    void onItemClick(SrcCarType type);
  }

  @Keep
  interface IScaleListener {
    void onScaleMove(float scale);
    void onScaleUp();
  }

  void update(int position);
}

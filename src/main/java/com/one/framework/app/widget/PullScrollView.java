package com.one.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import com.one.framework.app.widget.base.IPullView;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class PullScrollView extends ScrollView implements IPullView {

  public PullScrollView(Context context) {
    this(context, null);
  }

  public PullScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean isScrollBottom() {
    View childView = getChildAt(0);
    if (childView != null) {
      return childView.getMeasuredHeight() <= getScrollingY() + getHeaderScrollHeight();
    }
    return false;
  }

  @Override
  public int getHeaderScrollHeight() {
    return 0;
  }

  @Override
  public int getScrollingY() {
    return getScrollY();
  }

  @Override
  public View getView() {
    return this;
  }

  @Override
  public boolean isHeaderNeedScroll() {
    return false;
  }
}

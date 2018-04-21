package com.one.framework.app.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.one.framework.R;
import com.one.framework.app.widget.base.BaseHeaderView;

/**
 * Created by ludexiang on 2018/4/21.
 */

public class NavigatorHeaderView extends BaseHeaderView {

  public NavigatorHeaderView(@NonNull Context context, int maxScrollHeight) {
    this(context, null);
    mScrollMaxHeight = maxScrollHeight;
  }

  public NavigatorHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NavigatorHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected View createView(Context context, ViewGroup container) {
    return LayoutInflater.from(context).inflate(R.layout.one_navigator_header_layout, container, true);
  }
}

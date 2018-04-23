package com.one.framework.app.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.one.framework.R;
import com.one.framework.app.widget.base.BaseHeaderView;

/**
 * Created by ludexiang on 2018/4/3.
 */

public class HeaderView extends BaseHeaderView {

  public HeaderView(@NonNull Context context, int maxHeight) {
    this(context, null);
    mScrollMaxHeight = maxHeight;
  }

  public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected View createView(Context context) {
    return LayoutInflater.from(context).inflate(R.layout.one_list_header_view_layout, this, true);
  }

  @Override
  public View getView() {
    return this;
  }
}

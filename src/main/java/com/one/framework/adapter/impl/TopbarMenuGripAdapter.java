package com.one.framework.adapter.impl;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.adapter.impl.TopbarMenuGripAdapter.NavigatorHolder;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.model.NavigatorModel;

/**
 * Created by ludexiang on 2018/4/24.
 */

public class TopbarMenuGripAdapter extends AbsBaseAdapter<TabItem, NavigatorHolder> {

  public TopbarMenuGripAdapter(Context context) {
    super(context);
  }

  @Override
  protected NavigatorHolder createHolder() {
    return new NavigatorHolder();
  }

  @Override
  protected void initView(View view, NavigatorHolder holder) {
    holder.allMenuLayout = (LinearLayout) view.findViewById(R.id.one_top_bar_all_menu_layout);
    holder.icon = (ShapeImageView) view.findViewById(R.id.one_menu_grid_icon);
    holder.options = (TextView) view.findViewById(R.id.one_menu_grid_info);
  }

  @Override
  protected void bindData(TabItem model, NavigatorHolder holder, int position) {
    if (TextUtils.isEmpty(model.tab)) {
      holder.allMenuLayout.setEnabled(false);
    } else {
      holder.allMenuLayout.setEnabled(true);
      if (TextUtils.isEmpty(model.tabIcon)) {
        holder.icon.setImageResource(model.tabIconResId);
      } else {
        holder.icon.loadImageByUrl(null, model.tabIcon, "default");
      }
      holder.options.setText(model.tab);
    }
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.one_menu_grid_layout, null);
  }

  class NavigatorHolder {
    LinearLayout allMenuLayout;
    ShapeImageView icon;
    TextView options;
  }
}

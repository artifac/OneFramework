package com.one.framework.adapter.impl;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.adapter.impl.NavigatorOptionsAdapter.NavigatorHolder;
import com.one.framework.model.NavigatorModel;

/**
 * Created by ludexiang on 2018/4/21.
 */

public class NavigatorOptionsAdapter extends AbsBaseAdapter<NavigatorModel, NavigatorHolder> {

  public NavigatorOptionsAdapter(Context context) {
    super(context);
  }

  @Override
  protected NavigatorHolder createHolder() {
    return new NavigatorHolder();
  }

  @Override
  protected void initView(View view, NavigatorHolder holder) {
    holder.icon = (ImageView) view.findViewById(R.id.one_options_icon);
    holder.options = (TextView) view.findViewById(R.id.one_options);
    holder.optionsExtra = (TextView) view.findViewById(R.id.one_options_extra);
  }

  @Override
  protected void bindData(NavigatorModel model, NavigatorHolder holder, int position) {
    holder.options.setText(model.optionsInfo);
    holder.optionsExtra.setText(model.optionsExtra);
    holder.icon.setImageResource(model.optionsIconId);
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.one_navigator_option_layout, null);
  }

  class NavigatorHolder {

    ImageView icon;
    TextView options;
    TextView optionsExtra;
  }
}

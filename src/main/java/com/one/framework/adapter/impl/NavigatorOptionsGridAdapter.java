package com.one.framework.adapter.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.adapter.impl.NavigatorOptionsGridAdapter.NavigatorHolder;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.model.NavigatorModel;

/**
 * Created by ludexiang on 2018/4/23.
 */

public class NavigatorOptionsGridAdapter extends AbsBaseAdapter<NavigatorModel, NavigatorHolder> {

  public NavigatorOptionsGridAdapter(Context context) {
    super(context);
  }

  @Override
  protected NavigatorHolder createHolder() {
    return new NavigatorHolder();
  }

  @Override
  protected void initView(View view, NavigatorHolder holder) {
    holder.icon = (ShapeImageView) view.findViewById(R.id.one_navigator_options_icon);
    holder.options = (TextView) view.findViewById(R.id.one_navigator_options_txt);
  }

  @Override
  protected void bindData(NavigatorModel model, NavigatorHolder holder, int position) {
    if (TextUtils.isEmpty(model.optionsIconUrl)) {
      holder.icon.setImageResource(model.optionsIconId);
    } else {
      holder.icon.loadImageByUrl(null, model.optionsIconUrl, "default");
    }
    holder.options.setText(model.optionsInfo);
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.one_navigator_grid_options, null);
  }

  class NavigatorHolder {

    ShapeImageView icon;
    TextView options;
  }
}

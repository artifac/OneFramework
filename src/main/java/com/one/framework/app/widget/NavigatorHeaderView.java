package com.one.framework.app.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.app.blur.BlurKit;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.app.widget.base.BaseHeaderView;
import com.one.framework.log.Logger;
import com.trip.taxi.utils.H5Page;

/**
 * Created by ludexiang on 2018/4/21.
 */

public class NavigatorHeaderView extends BaseHeaderView implements View.OnClickListener {
  private ImageView mNavigatorHeader;
  private ImageView mHeaderAvator;

  public NavigatorHeaderView(@NonNull Context context, int maxScrollHeight) {
    super(context, maxScrollHeight);
  }

  public NavigatorHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NavigatorHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected View createView(Context context) {
    return LayoutInflater.from(context).inflate(R.layout.one_navigator_header_layout, this, true);
  }

  @Override
  public void setHeaderView(int layout) {

  }

  @Override
  public void setHeaderView(View view) {

  }

  @Override
  public void updateView(View view) {

  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.one_navigator_avator) {
      changeHeader();
    }
  }

  private void changeHeader() {
    WebViewModel webViewModel = new WebViewModel();
    webViewModel.title = "修改头像";
    webViewModel.url = H5Page.INSTANCE.changeAvator(UserProfile.getInstance(getContext()).getTokenValue());
    Intent intent = new Intent(getContext(), WebActivity.class);
    intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
    if (!(getContext() instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    getContext().startActivity(intent);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mNavigatorHeader = (ImageView) findViewById(R.id.one_navigator_bg);
    mHeaderAvator = findViewById(R.id.one_navigator_avator);

    mHeaderAvator.setOnClickListener(this);
  }

  @Override
  public View getView() {
    return this;
  }
}

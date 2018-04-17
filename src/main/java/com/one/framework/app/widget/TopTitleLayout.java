package com.one.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.widget.base.ITopTitleView;

/**
 * Created by ludexiang on 2018/4/17.
 */

public class TopTitleLayout extends RelativeLayout implements ITopTitleView {

  private TextView mTitle;
  private ImageView mLeft;
  private ImageView mRight;

  public TopTitleLayout(Context context) {
    this(context, null);
  }

  public TopTitleLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TopTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    LayoutInflater.from(context).inflate(R.layout.one_top_title_layout, this, true);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mTitle = (TextView) findViewById(R.id.one_top_title);
    mLeft = (ImageView) findViewById(R.id.one_top_left);
    mRight = (ImageView) findViewById(R.id.one_top_right);
  }

  @Override
  public int getViewHeight() {
    return getMeasuredHeight();
  }

  @Override
  public void setTitle(String title) {
    mTitle.setText(title);
  }

  @Override
  public void setTitle(int resId) {
    mTitle.setText(resId);
  }
}

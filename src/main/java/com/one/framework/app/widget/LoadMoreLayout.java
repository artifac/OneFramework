package com.one.framework.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.one.framework.R;

public class LoadMoreLayout extends LinearLayout {

  private LoadingView mLoadingView;
  private LinearLayout mNoMoreLayout;

  public LoadMoreLayout(Context context) {
    this(context, null);
  }

  public LoadMoreLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoadMoreLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mLoadingView = findViewById(R.id.one_footer_loading);
    mNoMoreLayout = findViewById(R.id.one_footer_no_more);
  }

  public void loading() {
    if (mLoadingView != null) {
      mLoadingView.setVisibility(View.VISIBLE);
    }
    if (mNoMoreLayout != null) {
      mNoMoreLayout.setVisibility(View.GONE);
    }
  }

  public void noMore(boolean noMore) {
    if (noMore) {
      mLoadingView.setVisibility(View.GONE);
      mNoMoreLayout.setVisibility(View.VISIBLE);
    }
  }
}

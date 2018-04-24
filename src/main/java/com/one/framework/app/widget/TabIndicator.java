package com.one.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.widget.base.AbsTabIndicatorScrollerView;
import com.one.framework.app.widget.base.ITabIndicatorListener;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/28.
 */

public class TabIndicator extends AbsTabIndicatorScrollerView implements ITabIndicatorListener {

  private LinearLayout mTabContainer;
  private ITabItemListener mTabItemListener;
  private LayoutInflater mInflater;
  private IScaleListener mScaleListener;

  public TabIndicator(Context context) {
    this(context, null);
  }

  public TabIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TabIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mInflater = LayoutInflater.from(context);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    if (getChildCount() > 1) {
      throw new IllegalArgumentException("ScrollView child must be have one");
    }
    mTabContainer = (LinearLayout) findViewById(R.id.one_tab_container);
  }

  @Override
  public void setTabItems(final List<TabItem> items) {
    setChildViewGravity(items.size());
    for (int i = 0; i < items.size(); i++) {
      final TabItem tab = items.get(i);
      View view = mInflater.inflate(R.layout.one_tab_item_layout, null);
      final TextView tabItem = (TextView) view.findViewById(R.id.one_tab_item);
      final View redPoint = view.findViewById(R.id.one_tab_item_red);
      tabItem.setTag(tab);
      tabItem.setText(tab.tab);
      tabItem.setSelected(tab.isSelected);
      redPoint.setVisibility(tab.isRedPoint ? View.VISIBLE : View.GONE);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT);
      if (i != items.size() - 1) {
        params.rightMargin = 30;
      }
      mTabContainer.addView(view, tab.position, params);
      if (tab.isSelected && mTabItemListener != null) {
        mTabItemListener.onItemClick(tab);
      }
      tabItem.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          update(items.indexOf(tab));
        }
      });
    }
  }

  @Override
  protected void onScaleMove(float scale) {
    if (mScaleListener != null) {
      mScaleListener.onScaleMove(scale);
    }
  }

  @Override
  protected void onScaleUp() {
    if (mScaleListener != null) {
      mScaleListener.onScaleUp();
    }
  }

  @Override
  public int getViewHeight() {
    return getMeasuredHeight();
  }

  private void setChildViewGravity(int size) {
    int width = getWidth();
    FrameLayout.LayoutParams params = (ScrollView.LayoutParams)mTabContainer.getLayoutParams();
    params.gravity = size <= 5 ? Gravity.CENTER : Gravity.NO_GRAVITY;
    mTabContainer.setLayoutParams(params);
  }

  @Override
  public void setScaleListener(IScaleListener listener) {
    mScaleListener = listener;
  }

  @Override
  public void setTabItemListener(ITabItemListener listener) {
    mTabItemListener = listener;
  }

  @Override
  public void update(int position) {
    for (int i = 0; i < mTabContainer.getChildCount(); i++) {
      TextView txt = (TextView) mTabContainer.getChildAt(i).findViewById(R.id.one_tab_item);
      if (i == position) {
        txt.setSelected(true);
      } else {
        txt.setSelected(false);
      }
    }
    View view = mTabContainer.getChildAt(position);
    TextView tabItem = (TextView) view.findViewById(R.id.one_tab_item);
    View redPoint = view.findViewById(R.id.one_tab_item_red);
    if (mTabItemListener != null) {
      mTabItemListener.onItemClick((TabItem) tabItem.getTag());
    }
    redPoint.setVisibility(View.GONE);
    tabItem.setSelected(true);
  }
}

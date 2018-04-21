package com.one.framework.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.one.framework.R;

public class SwipeListView extends ListView implements AbsListView.OnScrollListener,
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

  private View mFooterView;
  private ProgressBar mProgress;
  private TextView mFooterInfo;
  private int mLastItemPosition;
  private int mTotalCount;
  private SwipeRefreshLayout mRefreshLayout;

  private ILoadListener mListener;
  private IItemClickListener mItemListener;

  public SwipeListView(Context context) {
    this(context, null);
  }

  public SwipeListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mFooterView = LayoutInflater.from(context).inflate(R.layout.one_list_view_footer_layout, null);
    mProgress = (ProgressBar) mFooterView.findViewById(R.id.one_list_footer_progress);
    mFooterInfo = (TextView) mFooterView.findViewById(R.id.one_list_footer_info);
    addFooterView(mFooterView, null, false);
    setOnScrollListener(this);
    setOnItemClickListener(this);
  }

  @Override
  public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    if (mLastItemPosition == mTotalCount && scrollState == SCROLL_STATE_IDLE) {
      if (mListener != null && getAdapter() != null && getAdapter().getCount() > 0) {// 判断不是正在加载！
        mFooterView.setVisibility(View.VISIBLE);// 首先设置加载提示可见
        mListener.onLoadMore();
      }
    }
  }

  @Override
  public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    mLastItemPosition = firstVisibleItem + visibleItemCount;
    mTotalCount = totalItemCount;
  }

  public void setLoadMoreListener(ILoadListener listener, SwipeRefreshLayout layout) {
    mListener = listener;
    mRefreshLayout = layout;
    mRefreshLayout.setOnRefreshListener(this);
  }

  @Override
  public void onRefresh() {
    if (mListener != null) {
      mListener.onRefresh();
      mRefreshLayout.setRefreshing(true);
    }
  }

  public interface ILoadListener {

    void onLoadMore();

    void onRefresh();
  }

  public void loadComplete() {
    mFooterView.setVisibility(View.GONE);
    if (mRefreshLayout != null) {
      mRefreshLayout.setRefreshing(false);
    }
  }

  public void setItemListener(IItemClickListener listener) {
    mItemListener = listener;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
    if (mItemListener != null) {
      mItemListener.onItemClick(adapterView, view, position);
    }
  }

  public interface IItemClickListener {

    void onItemClick(AdapterView<?> adapterView, View view, int position);
  }
}

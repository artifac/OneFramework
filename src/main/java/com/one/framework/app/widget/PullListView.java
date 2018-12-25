package com.one.framework.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.one.framework.R;
import com.one.framework.app.widget.base.IHeaderView;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.app.widget.base.IMovePublishListener;
import com.one.framework.app.widget.base.IPullView;

/**
 * Created by ludexiang on 2018/4/3.
 */

@TargetApi(VERSION_CODES.M)
public class PullListView extends ListView implements IMovePublishListener, IPullView,
    OnScrollListener, OnItemClickListener {

  private SparseArray<ItemRecord> recordSp = new SparseArray(0);
  private int mCurrentFirstVisibleItem = 0;

  private IHeaderView mHeaderView;
  private int mScrollerType; // 0 scroll Header 1 scroll self
  private int mMaxHeight;

  private boolean isHaveFooterView;
  private boolean isHaveHeaderView;
  private boolean isResolveConflict;
  private boolean isHaveRefreshView;
  private int mRefreshLayoutId;
  private boolean isHaveLoadMoreView;
  private int mLoadMoreLayoutId;
  private LoadMoreLayout mLoadMoreLayout;
  private IItemClickListener mItemClickListener;
  private IPullCallback mPullListener;
  private boolean isLoading;
  private int mTotalCount;
  private ILoadListener mListener;
  private boolean hasMore;

  private int upTranslateY;

  public PullListView(Context context) {
    this(context, null);
  }

  public PullListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullView);
    mScrollerType = a.getInt(R.styleable.PullView_scroll_view, 1);
    mMaxHeight = a.getDimensionPixelSize(R.styleable.PullView_scroll_max_height, 0);

    isHaveFooterView = a.getBoolean(R.styleable.PullView_have_footer_view, true);
    isHaveHeaderView = a.getBoolean(R.styleable.PullView_have_header_view, true);
    isResolveConflict = a.getBoolean(R.styleable.PullView_resolve_conflict, false);
    isHaveRefreshView = a.getBoolean(R.styleable.PullView_have_refresh_view, false);
    mRefreshLayoutId = a.getResourceId(R.styleable.PullView_refresh_view_id, -1);
    isHaveLoadMoreView = a.getBoolean(R.styleable.PullView_have_load_more_view, false);
    mLoadMoreLayoutId = a.getResourceId(R.styleable.PullView_load_more_view_id, -1);
    a.recycle();

    if (mScrollerType == 0 && mMaxHeight == 0) {
      throw new IllegalArgumentException("ScrollMaxHeight is 0");
    }

    if (isHaveRefreshView && mRefreshLayoutId == -1) {
      throw new IllegalArgumentException("Please set RefreshViewLayout");
    }

    if (isHaveLoadMoreView && mLoadMoreLayoutId == -1) {
      throw new IllegalArgumentException("Please set LoadMoreViewLayout");
    } else {
      inflateFooterView();
    }

    mHeaderView = new HeaderView(getContext(), mMaxHeight);
    setOnScrollListener(this);
    setOnItemClickListener(this);
  }

  @Override
  public void setHaveHeaderView(boolean flag) {
    isHaveHeaderView = flag;
    if (isHaveHeaderView) {
      addHeaderView(mHeaderView.getView());
    }
  }

  @Override
  public void setHaveFooterView(boolean flag) {
    isHaveFooterView = flag;
    if (isHaveFooterView) {
//      addFooterView();
    }
  }

  @Override
  public void addHeaderView(View v) {
    if (isHaveHeaderView) {
      super.addHeaderView(v);
    }
  }

  @Override
  public void addFooterView(View v) {
    if (isHaveFooterView) {
      super.addFooterView(v, null, false);
    }
  }

  @Override
  public void setHeaderView(View view) {
    mHeaderView.setHeaderView(view);
  }

  @Override
  public void setHeaderView(int headerViewHeight) {
    mHeaderView.setHeaderView(headerViewHeight);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (isResolveConflict) {
      super.onMeasure(widthMeasureSpec,
          MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    int lastVisibleIndex = getLastVisiblePosition();
    if (!isLoading && scrollState == OnScrollListener.SCROLL_STATE_IDLE//停止滚动
        && lastVisibleIndex == mTotalCount - 1) {//滑动到最后一项
      isLoading = true;
      if (mListener != null && !hasMore) {
        loadFootView();
        mListener.onLoadMore();
      }
    }
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    mCurrentFirstVisibleItem = firstVisibleItem;
    mTotalCount = totalItemCount;
    View firstView = view.getChildAt(0);
    if (null != firstView) {
      ItemRecord itemRecord = recordSp.get(firstVisibleItem);
      if (null == itemRecord) {
        itemRecord = new ItemRecord();
      }
      //item高度
      itemRecord.height = firstView.getHeight();
      //滑动位置距顶部距离(负值)
      itemRecord.top = firstView.getTop();
      recordSp.append(firstVisibleItem, itemRecord);
    }
  }

  class ItemRecord {

    int height = 0;
    int top = 0;
  }

  /**
   * 获取滑动的距离
   */
  @Override
  public int getScrollingPadding() {
    int height = 0;
    for (int i = 0; i < mCurrentFirstVisibleItem; i++) {
      ItemRecord itemRecord = recordSp.get(i);
      if (itemRecord != null) {
        height += itemRecord.height;
      }
    }
    ItemRecord itemRecord = recordSp.get(mCurrentFirstVisibleItem);
    if (null == itemRecord) {
      itemRecord = new ItemRecord();
    }
    return height - itemRecord.top;
  }

  @Override
  public View getView() {
    return this;
  }

  @Override
  public int getHeaderScrollHeight() {
    return mHeaderView.getScrollHeaderHeight();
  }

  @Override
  public boolean isScrollBottom() {
    if (getLastVisiblePosition() != INVALID_POSITION && getLastVisiblePosition() == (getCount()
        - 1)) {
      final View bottomChildView = getChildAt(
          getLastVisiblePosition() - getFirstVisiblePosition());
      return getHeight() >= bottomChildView.getBottom();
    }
    return false;
  }

  @Override
  public void onMove(float offsetX, float offsetY) {
    boolean flag = offsetY > 0 || isHeaderNeedScroll();
    if (mScrollerType == 0 && flag && !isScrollBottom()) {
      mHeaderView.onMove(offsetX, offsetY);
    } else {
      selfScrollerMove(offsetX, (int) (offsetY + 0.5f));
    }
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    if (mScrollerType == 0 && isHeaderNeedScroll()) { // 如果是滚到到底部了则滚动listview
      mHeaderView.onUp(bottom2Up, isFling);
    } else {
      selfScrollerUp(bottom2Up, isFling);
    }
  }

  /**
   * @param offsetY Y轴上的偏移量
   */
  private void selfScrollerMove(float offsetX, int offsetY) {
    int translateY = (int) (getTranslationY() + offsetY);
    if (mPullListener != null) {
      mPullListener.move(offsetX, offsetY);
    }
    setTranslationY(translateY);
  }

  private void selfScrollerUp(boolean bottom2Up, boolean isFling) {
    int translationY = upTranslateY = (int) getTranslationY();
    goonMove(ANIM_DURATION, false);
  }

  @Override
  public void goonMove(long duration, boolean rollback) {
    ValueAnimator translate = ValueAnimator.ofFloat(1f, 0f);
    translate.setDuration(duration);
    translate.addUpdateListener(animation -> {
      float animValue = animation.getAnimatedFraction(); // 百分比 0 ~ 1
      float fraction = 1f - animValue; // value 1 ~ 0
      float translationY =
          (rollback || isScrollBottom() ? 0 : animValue * mCustomHeaderViewHeight)
              + fraction * getTranslationY();
      if (mPullListener != null && !rollback) { // 没满的时候自动回缩
        mPullListener.moveAfterUp(0, fraction);
      }
      setTranslationY(translationY);
    });
    translate.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (mPullListener != null && !rollback) {
          mPullListener.up(getTranslationY());
        }
      }
    });
    translate.setInterpolator(new LinearInterpolator());
    translate.start();
  }

  @Override
  public int getHeaderHeight() {
    return mHeaderView.getHeaderHeight();
  }

  @Override
  public boolean isHeaderNeedScroll() {
    return mHeaderView.isNeedScroll();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (mItemClickListener != null) {
      mItemClickListener.onItemClick(parent, view, position);
    }
  }

  @Override
  public void setItemClickListener(IItemClickListener listener) {
    mItemClickListener = listener;
  }

  @Override
  public void setPullCallback(IPullCallback listener) {
    mPullListener = listener;
  }

  @Override
  public void setLoadListener(ILoadListener listener) {
    mListener = listener;
  }

  /******************** 自定义 headerView *********************/
  private int mCustomHeaderViewHeight;

  /**
   * 设置刷新View
   */
  @Deprecated
  private void inflateRefreshView(int layoutId) {
    View refreshView = null;
    if (mRefreshLayoutId != -1) {
      refreshView = LayoutInflater.from(getContext()).inflate(mRefreshLayoutId, null);
    }
//    inflateRefreshView();
    View.inflate(getContext(), layoutId, null);
    addHeaderView(refreshView);
//    if (!isScrollBottom()) {
//      /**
//       * 默认刷新view不展示，根据收拾下滑展示刷新view scrollTo 向上向左滑动为正 向下向右为负
//       */
//      scrollTo(0, mRefreshView.getRefreshHeight());
//    }
  }

  @Override
  public void setCustomHeaderViewHeight(int viewHeight) {
    mCustomHeaderViewHeight = viewHeight;
  }

  /****************** 自定义 headerView *********************/

  private void inflateFooterView() {
    View footerView = null;
    if (mLoadMoreLayoutId != -1) {
      footerView = LayoutInflater.from(getContext()).inflate(mLoadMoreLayoutId, null);
      mLoadMoreLayout = footerView.findViewById(R.id.one_load_more);
    }
    if (footerView != null) {
//    inflateRefreshView();
      addFooterView(footerView);
    }
//    if (!isScrollBottom()) {
//      /**
//       * 默认刷新view不展示，根据收拾下滑展示刷新view scrollTo 向上向左滑动为正 向下向右为负
//       */
//      scrollTo(0, mRefreshView.getRefreshHeight());
//    }
  }

  public void loadFootView() {
    if (mLoadMoreLayout != null) {
      mLoadMoreLayout.loading();
    }
  }

  public void releaseFootView(boolean noMore) {
    hasMore = noMore;
    isLoading = false;
    if (mLoadMoreLayout != null) {
      mLoadMoreLayout.noMore(noMore);
    }
  }

  public void canLoadMore() {
    hasMore = false;
    isLoading = false;
  }
}

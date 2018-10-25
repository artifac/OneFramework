package com.one.framework.app.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.one.framework.R;
import com.one.framework.app.widget.base.IHeaderView;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.app.widget.base.IMovePublishListener;
import com.one.framework.app.widget.base.IPullView;

/**
 * Created by ludexiang on 2018/4/22.
 */

public class PullGridView extends GridView implements IMovePublishListener, IPullView,
    OnScrollListener, OnItemClickListener {

  private static String NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
  private SparseArray<ItemRecord> recordSp = new SparseArray(0);
  private int mCurrentFirstVisibleItem = 0;
  private final int mNumColumns;

  private IHeaderView mHeaderView;
  private int mScroller; // 0 scroll Header 1 scroll self
  private int mMaxHeight;

  private boolean isHaveFooterView;
  private boolean isHaveHeaderView;
  private boolean isResolveConflict;

  private IItemClickListener mItemClickListener;

  public PullGridView(Context context) {
    this(context, null);
  }

  public PullGridView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PullGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullView);
    mScroller = a.getInt(R.styleable.PullView_scroll_view, 1);
    mMaxHeight = a.getDimensionPixelSize(R.styleable.PullView_scroll_max_height, 0);

    isHaveFooterView = a.getBoolean(R.styleable.PullView_have_footer_view, true);
    isHaveHeaderView = a.getBoolean(R.styleable.PullView_have_header_view, true);
    isResolveConflict = a.getBoolean(R.styleable.PullView_resolve_conflict, false);

    if (mScroller == 0 && mMaxHeight == 0) {
      throw new IllegalArgumentException("ScrollMaxHeight is 0");
    }
    a.recycle();

    /**
     * extends GridView 时
     * 1.定义命名空间，
     * private static String NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
     * 2.在继承GridView的构造方法中增加下代码 //防止在api 11之前出错
     * columnNum = attrs.getAttributeIntValue(NAMESPACE_ANDROID,"numColumns",2);
     * 3.在使用到getNumColumns()的地方替换成columnNum ；
     * 注意：使用该方式，在布局文件中写GridView布局时，属性android:numColumns="2"必须设固定值，不可使用auto_fit,否则获取的列数将会为0
     **/
    mNumColumns = attrs.getAttributeIntValue(NAMESPACE_ANDROID,"numColumns",2);
    mHeaderView = new HeaderView(context, mMaxHeight);
    addHeaderView(mHeaderView.getView());
    setOnScrollListener(this);

    setOnItemClickListener(this);
  }

  @Override
  public void setHaveHeaderView(boolean flag) {
    isHaveHeaderView = flag;
  }

  @Override
  public void setHaveFooterView(boolean flag) {
    isHaveFooterView = flag;
  }

  public void addHeaderView(View v) {
    if (isHaveHeaderView) {
    }
  }


  public void addFooterView(View v) {
    if (isHaveFooterView) {
    }
  }

  @Override
  public void setHeaderView(View view) {

  }

  @Override
  public void setHeaderView(int layout) {

  }

  @Override
  public int getNumColumns() {
    return mNumColumns;
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

  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    mCurrentFirstVisibleItem = firstVisibleItem;
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
    if (getLastVisiblePosition() != INVALID_POSITION && getLastVisiblePosition() == (getCount() - 1)) {
      final View bottomChildView = getChildAt(getLastVisiblePosition() - getFirstVisiblePosition());
      return getHeight() >= bottomChildView.getBottom();
    }
    return false;
  }

  @Override
  public void onMove(float offsetX, float offsetY) {
    boolean flag = offsetY > 0 || isHeaderNeedScroll();
    if (mScroller == 0 && flag && !isScrollBottom()) {
      mHeaderView.onMove(offsetX, offsetY);
    } else {
      selfScrollerMove(offsetY);
    }
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    if (mScroller == 0 && isHeaderNeedScroll()) { // 如果是滚到到底部了则滚动listview
      mHeaderView.onUp(bottom2Up, isFling);
    } else {
      selfScrollerUp(bottom2Up, isFling);
    }
  }

  private void selfScrollerMove(float offsetY) {
    int translateY = (int) (getTranslationY() + offsetY + 0.5);
    setTranslationY(translateY);
  }

  private void selfScrollerUp(boolean bottom2Up, boolean isFling) {
    int tranlationY = (int) getTranslationY();
    goonMove(200);
  }

  private void goonMove(long duration) {
    ValueAnimator translate = ValueAnimator.ofFloat(1f, 0f);
    translate.setDuration(duration);
    translate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animValue = animation.getAnimatedFraction();
        float fraction = 1f - animValue;
        setTranslationY(fraction * getTranslationY());
      }
    });
    translate.start();
  }

  @Override
  public int getHeaderHeight() {
    return mHeaderView.getHeaderHeight();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (mItemClickListener != null) {
      mItemClickListener.onItemClick(parent, view, position);
    }
  }

  @Override
  public boolean isHeaderNeedScroll() {
    return mHeaderView.isNeedScroll();
  }

  @Override
  public void setItemClickListener(IItemClickListener listener) {
    mItemClickListener = listener;
  }

  @Override
  public void setPullCallback(IPullCallback listener) {

  }
}

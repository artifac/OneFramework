package com.one.framework.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.one.framework.R;

/**
 * 滑动星星控件
 */
public class StarView extends LinearLayout {

  private int mLevel;
  private OnStarChangeListener mListener;

  private Context mContext;

  private boolean mEntranceMode;

  private static final int STAR_NUM_DEFAULT = 5;
  private static final int sDarkStarDrawableId = R.drawable.one_star_view_checked;
  private static final int sBrightStarDrawableId = R.drawable.one_star_view_unchecked;
  private boolean mStarChangeEnable = true;

  private int mStarViewWidth, mStarViewHeight, mStarViewRightMargin;

  private int mStarViewCount;
  private boolean isSliding = true;

  public StarView(Context context) {
    this(context, null);
  }
  public StarView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public StarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context;
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StarView);
    mStarViewCount = a.getInteger(R.styleable.StarView_starViewNumber, STAR_NUM_DEFAULT);
    isSliding = a.getBoolean(R.styleable.StarView_starView_isSlide, true);
    mStarViewWidth = a.getDimensionPixelOffset(R.styleable.StarView_starViewWidth, 0);
    mStarViewHeight = a.getDimensionPixelOffset(R.styleable.StarView_starViewHeight, 0);
    mStarViewRightMargin = a.getDimensionPixelOffset(R.styleable.StarView_starViewRightMargin, 0);
    a.recycle();
    initView();
  }

  public void setEntranceMode(boolean flag) {
    mEntranceMode = flag;
  }

  private void initView() {
    setOrientation(LinearLayout.HORIZONTAL);
    createStars(mStarViewCount);
    setTouchEnable(isSliding);
  }

  private void createStars(int starNum) {
    removeAllViews();
    for (int i = 0; i < starNum; i++) {
      View oneStarLayout = LayoutInflater.from(mContext).inflate(R.layout.one_star_view_layout, this, false);
      StarItemView starIv = (StarItemView) oneStarLayout.findViewById(R.id.one_star_view);
      starIv.setFocusableInTouchMode(true);
      starIv.setFocusable(true);
      starIv.setImageResource(sDarkStarDrawableId);
      starIv.setChecked(false);
      if (mStarViewHeight != 0 && mStarViewWidth != 0) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)starIv.getLayoutParams();
        params.width = mStarViewWidth;
        params.height = mStarViewHeight;
        params.rightMargin = mStarViewRightMargin;
        starIv.setLayoutParams(params);
      }
      addView(oneStarLayout);
    }
  }

  /**
   * 获取当前星级
   */
  public int getLevel() {
    return mLevel;
  }

  /**
   * 设置能否进行评星级
   */
  public void setTouchEnable(boolean flag) {
    if (flag) {
      setOnTouchListener(changeListener);
    } else {
      setOnTouchListener(null);
    }
  }

  public void setOnStarChangeListener(OnStarChangeListener listener) {
    mListener = listener;
  }

  private OnTouchListener changeListener = new OnTouchListener() {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          if (!mEntranceMode || mStarChangeEnable) {
            onStarTouch(event);
          } else if (!mStarChangeEnable) {
            if (mListener != null) {
              mListener.onStarChange(mLevel);
            }
          }
          break;
        case MotionEvent.ACTION_MOVE:
          if (!mEntranceMode) {
            onStarTouch(event);
          }
          break;
      }
      return true;
    }
  };

  private void onStarTouch(MotionEvent event) {
    int newLevel = calculateLevel(event.getX(), mLevel);
    if (newLevel != 0 && newLevel != mLevel) {
      setLevel(getChildCount(), newLevel);
      mLevel = newLevel;
      if (mListener != null) {
        mListener.onStarChange(mLevel);
      }
    }
  }

  private int calculateLevel(float x, int oldLevel) {
    int childCount = getChildCount();
    int newLevel = getLevelByPosition(x, childCount);
    if (newLevel == oldLevel || newLevel <= 0) {
      return oldLevel;
    }
    return newLevel;
  }

  private void setLevel(int childCount, int level) {
    for (int i = 0; i < childCount; i++) {
      View child = getChildAt(i);
      StarItemView starIv = (StarItemView) child.findViewById(R.id.one_star_view);
      if (i < level) {
        starIv.setChecked(true);
        starIv.setImageResource(sDarkStarDrawableId);
      } else {
        starIv.setChecked(false);
        starIv.setImageResource(sBrightStarDrawableId);
      }
    }
  }

  /**
   * 设置星级
   */
  public void setLevel(int level) {
    setLevel(getChildCount(), level);
    mLevel = level;
  }

  private int getLevelByPosition(float x, int childCount) {
    int level = 0;
    for (int i = 0; i < childCount - 1; i++) {
      View child = getChildAt(i);
      View nextChild = getChildAt(i + 1);
      if (x >= child.getLeft() && x < nextChild.getLeft()) {
        level = i + 1;
      }
    }
    //最后一个星星
    View lastView = getChildAt(childCount - 1);
    if (x >= lastView.getLeft()) {
      level = childCount;
    }
    return level;
  }

  public void setStarChangeEnable(boolean b) {
    mStarChangeEnable = b;
  }

  public interface OnStarChangeListener {
    void onStarChange(int level);
  }
}

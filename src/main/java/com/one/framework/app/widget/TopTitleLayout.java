package com.one.framework.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.base.ITopTitleView;
import com.one.framework.log.Logger;
import com.one.map.location.LocationProvider;
import com.one.map.model.Address;
import java.util.Stack;

/**
 * Created by ludexiang on 2018/4/17.
 */

public class TopTitleLayout extends RelativeLayout implements ITopTitleView, OnClickListener {

  private TextView mTitle;
  private ImageView mLeft;
  private ImageView mClose;
  private TextView mRight;
  private ImageView mRightIcon;

  private TextView mLeftTitle;
  private int mLeftDefaultResId;
  private int mRightDefaultResId;

  private int mLeftResId;
  private int mRightResId;

  /**
   * Listener 通过栈 FILO
   */
  private Stack<ITopTitleListener> mClickListenerStack = new Stack<>();

  private final int sTitleSize = 17;


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
    mRight = (TextView) findViewById(R.id.one_top_right);
    mClose = (ImageView) findViewById(R.id.one_top_close);
    mRightIcon = findViewById(R.id.one_top_right_icon);
    mLeftTitle = findViewById(R.id.one_top_left_title);

    mLeftResId = mLeftDefaultResId = R.drawable.one_top_bar_my_center;
    mRightDefaultResId = R.drawable.one_top_bar_message;

    mTitle.setOnClickListener(this);
    mLeft.setOnClickListener(this);
    mRight.setOnClickListener(this);
    mLeft.setImageResource(mLeftDefaultResId);
    mRightIcon.setImageResource(mRightDefaultResId);
  }

  @Override
  public int getViewHeight() {
    return getMeasuredHeight();
  }

  @Override
  public void setTitle(String title) {
    setTitle(title, sTitleSize, ITopbarFragment.CENTER);
  }

  @Override
  public void setTitle(int resId) {
    setTitle(getContext().getString(resId), sTitleSize);
  }

  @Override
  public void setTitle(String title, int sizeSp) {
    setTitle(title, sizeSp, ITopbarFragment.CENTER);
  }

  @Override
  public void setTitle(String title, int sizeSp, Typeface typeface) {
    mTitle.setText(title);
    mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
    mTitle.setTypeface(typeface);
    setBackgroundResource(R.drawable.one_top_bar_gradient_bg);
  }

  @Override
  public void setTitleWithPosition(String title, int position) {
    switch (position) {
      case ITopbarFragment.LEFT: {
        mLeftTitle.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.GONE);
        mLeftTitle.setText(title);
        setBackgroundResource(R.drawable.one_top_bar_gradient_bg);
        break;
      }
      case ITopbarFragment.CENTER: {
        mTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTitle.setVisibility(View.VISIBLE);
        mLeftTitle.setVisibility(View.GONE);
        mTitle.setText(title);
        setBackgroundColor(Color.WHITE);
        break;
      }
    }
  }

  private void setTitle(String title, int sizeSp, int position) {
    mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
    setTitleWithPosition(title, position);
  }

  @Override
  public void setLeftImage(int resId) {
    if (resId != 0) {
      mLeftResId = resId;
      mLeft.setImageResource(resId);
      mLeft.setVisibility(View.VISIBLE);
    } else {
      mLeft.setVisibility(View.GONE);
    }
  }

  @Override
  public void setRightImage(int resId) {
    if (resId != 0) {
      mRightResId = resId;
      mRightIcon.setImageResource(resId);
      mRightIcon.setVisibility(View.VISIBLE);
    } else {
      mRightIcon.setVisibility(View.GONE);
    }
  }

  @Override
  public void hideRightImage(boolean hide) {
    mRightIcon.setVisibility(!hide ? View.VISIBLE : View.GONE);
  }

  /**
   * 左上角返回listener
   * @param listener
   */
  public void setLeftClickListener(View.OnClickListener listener) {
    mLeft.setOnClickListener(listener);
  }

  @Override
  public void setCloseVisible(boolean visible) {
    mClose.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  public void setCloseClickListener(View.OnClickListener listener) {
    mClose.setOnClickListener(listener);
  }

  public void setRightClickListener(View.OnClickListener listener) {
    mRight.setOnClickListener(listener);
  }

  public void setRightVisible(boolean visible) {
    mRight.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  @Override
  public void setTopTitleListener(ITopTitleListener listener) {
    Logger.e("ldx", "titleListener >>>> " + listener + " stack " + mClickListenerStack);
    if (mClickListenerStack.contains(listener)) {
      return;
    }
    mClickListenerStack.push(listener);
  }

  @Override
  public void setRightText(String txtBtn) {
    if (!TextUtils.isEmpty(txtBtn)) {
      mRight.setVisibility(View.VISIBLE);
      mRight.setText(txtBtn);
      mRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    } else {
      mRight.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void setRightResId(int txtResId) {
    setRightResId(txtResId, Color.parseColor("#999999"));
  }

  @Override
  public void setRightResId(int txtResId, int color) {
    if (txtResId > 0) {
      mRight.setVisibility(View.VISIBLE);
      setRightText(getContext().getString(txtResId));
      mRight.setTextColor(color);
    } else {
      mRight.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void setRightCompoundDrawableBounds(int left, int top, int right, int bottom) {
    mRight.setText("");
    mRight.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
  }

  @Override
  public void titleReset() {
    // 在首页但是处理不同的逻辑
    setLeftImage(mLeftDefaultResId);
    setRightImage(mRightDefaultResId);
    setRightResId(0);
    mLeftTitle.setVisibility(View.GONE);
    mTitle.setVisibility(View.VISIBLE);
    Address curLoc = LocationProvider.getInstance().getLocation();
    if (curLoc != null) {
      setTitle(curLoc.mCity, 14, Typeface.defaultFromStyle(Typeface.BOLD));
    }
    setBackgroundResource(R.drawable.one_top_bar_gradient_bg);
  }

  @Override
  public void setTitleBarBackground(int color) {
    setBackgroundColor(color);
  }

  @Override
  public void setTitleBarBackgroundResources(int res) {
    setBackgroundResource(res);
  }

  @Override
  public void onClick(View v) {
    if (mClickListenerStack.size() == 0) {
      return;
    }
    boolean onlyOne = mClickListenerStack.size() == 1;
    // 若是root Fragment 直接获取 Listener 不弹出栈 左上角默认🔙故直接pop
    ITopTitleListener listener;
    int id = v.getId();
    if (id == R.id.one_top_left) {
      listener = onlyOne ? mClickListenerStack.peek() : mClickListenerStack.pop();
      if (listener != null) {
        listener.onTitleItemClick(ClickPosition.LEFT);
      }
    } else if (id == R.id.one_top_title) {
      listener = mClickListenerStack.peek();
      if (listener != null) {
        listener.onTitleItemClick(ClickPosition.TITLE);
      }
    } else if (id == R.id.one_top_right) {
      listener = mClickListenerStack.peek();
      if (listener != null) {
        listener.onTitleItemClick(ClickPosition.RIGHT);
      }
    }
  }

  @Override
  public void popBackListener() {
    // peek 返回栈顶元素 不移除
    // pop 返回栈顶元素 移除
    boolean onlyOne = mClickListenerStack.size() == 1;
    if (onlyOne) {
      mClickListenerStack.peek();
    } else {
      mClickListenerStack.pop();
    }
  }

  @Override
  public View getRightView() {
    return mRight;
  }

  @Override
  public View getView() {
    return this;
  }
}

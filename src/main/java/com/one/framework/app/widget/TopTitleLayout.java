package com.one.framework.app.widget;

import android.content.Context;
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
import com.one.framework.app.widget.base.ITopTitleView;
import com.one.framework.log.Logger;
import com.one.map.location.LocationProvider;
import java.util.Stack;

/**
 * Created by ludexiang on 2018/4/17.
 */

public class TopTitleLayout extends RelativeLayout implements ITopTitleView, OnClickListener {

  private TextView mTitle;
  private ImageView mLeft;
  private TextView mRight;

  private int mLeftDefaultResId;

  private int mLeftResId;

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

    mLeftResId = mLeftDefaultResId = R.drawable.one_top_bar_my_center;


    mTitle.setOnClickListener(this);
    mLeft.setOnClickListener(this);
    mRight.setOnClickListener(this);
    mLeft.setImageResource(mLeftDefaultResId);
  }

  @Override
  public int getViewHeight() {
    return getMeasuredHeight();
  }

  @Override
  public void setTitle(String title) {
    setTitle(title, sTitleSize);
  }

  @Override
  public void setTitle(int resId) {
    setTitle(getContext().getString(resId), sTitleSize);
  }

  @Override
  public void setTitle(String title, int sizeSp) {
    mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
    mTitle.setText(title);
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
      mRight.setOnClickListener(this);
    } else {
      mRight.setVisibility(View.INVISIBLE);
      mRight.setOnClickListener(null);
    }
  }

  @Override
  public void setRightResId(int txtResId) {
    if (txtResId != 0) {
      mRight.setVisibility(View.VISIBLE);
      setRightText(getContext().getString(txtResId));
    } else {
      mRight.setVisibility(View.INVISIBLE);
      mRight.setOnClickListener(null);
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
    setRightResId(0);
    setTitle(LocationProvider.getInstance().getLocation().mCity, 14);
  }

  @Override
  public void onClick(View v) {
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
    Logger.e("ldx", "Topbar LeftBackStack >>>> " + mClickListenerStack);
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

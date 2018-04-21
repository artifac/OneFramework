package com.one.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.widget.base.ITopTitleView;
import com.one.framework.log.Logger;
import java.util.Stack;

/**
 * Created by ludexiang on 2018/4/17.
 */

public class TopTitleLayout extends RelativeLayout implements ITopTitleView, OnClickListener {

  private TextView mTitle;
  private ImageView mLeft;
  private TextView mRight;

  /**
   * Listener 通过栈 FILO
   */
  private Stack<ITopTitleListener> mLeftListenerStack = new Stack<>();


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

    mTitle.setOnClickListener(this);
    mLeft.setOnClickListener(this);
    mRight.setOnClickListener(this);
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

  @Override
  public void setLeftImage(int resId) {
    mLeft.setImageResource(resId);
  }

  @Override
  public void setTopTitleListener(ITopTitleListener listener) {
    if (mLeftListenerStack.contains(listener)) {
      return;
    }
    mLeftListenerStack.push(listener);
  }

  @Override
  public void setRight(String txtBtn) {

  }

  @Override
  public void onClick(View v) {
    boolean onlyOne = mLeftListenerStack.size() == 1;
    // 若是root Fragment 直接获取 Listener 不弹出栈 左上角默认🔙故直接pop
    ITopTitleListener listener;
    int id = v.getId();
    if (id == R.id.one_top_left) {
      listener = onlyOne ? mLeftListenerStack.peek() : mLeftListenerStack.pop();
      if (listener != null) {
        listener.onTitleItemClick(ClickPosition.LEFT);
      }
    } else if (id == R.id.one_top_title) {
      listener = mLeftListenerStack.peek();
      if (listener != null) {
        listener.onTitleItemClick(ClickPosition.TITLE);
      }
    } else if (id == R.id.one_top_right) {
      listener = mLeftListenerStack.peek();
      if (listener != null) {
        listener.onTitleItemClick(ClickPosition.RIGHT);
      }
    }
  }
}

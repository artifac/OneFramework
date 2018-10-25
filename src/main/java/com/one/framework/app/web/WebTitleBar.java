package com.one.framework.app.web;

import android.content.Context;
import android.util.AttributeSet;
import com.one.framework.app.widget.TopTitleLayout;

/**
 * {@link WebActivity} 的标题栏
 */
public class WebTitleBar extends TopTitleLayout {

  public WebTitleBar(Context context) {
    super(context);
  }

  public WebTitleBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public WebTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * 设置标题文字
   *
   * @param titleName 标题文字
   */
  public void setTitleName(String titleName) {
    setTitle(titleName);
  }

  /**
   * 设置返回按钮的点击监听
   *
   * @param listener 返回按钮的点击监听
   */
  public void setOnBackClickListener(OnClickListener listener) {
    setLeftClickListener(listener);
  }

  /**
   * 设置关闭按钮的点击监听
   *
   * @param listener 关闭按钮的点击监听
   */
  public void setOnCloseClickListener(OnClickListener listener) {
    setCloseClickListener(listener);
  }

  /**
   * 设置更多按钮的点击监听
   *
   * @param listener 更多按钮的点击监听
   */
  public void setOnMoreClickListener(OnClickListener listener) {
    setRightClickListener(listener);
  }
}

package com.one.framework.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import com.one.framework.R;

/**
 * 自定义PopWindow类，封装了PopWindow的一些常用属性，用Builder模式支持链式调用
 * 如果想让PopUpWindow所包含的view做动画需通过PopupWindow.getContentView()获取对应的View在startAnimation
 * popupwindow style 动画时间为300ms
 */
public class PopWindow {

  private Context mContext;
  private int mWidth;
  private int mHeight;
  private boolean mIsFocusable = true;
  private boolean mIsOutside = true;
  private int mResLayoutId = -1;
  private View mContentView;
  private PopupWindow mPopupWindow;
  private int mAnimationStyle = -1;

  private boolean mClipEnable = true;//default is true
  private boolean mIgnoreCheekPress = false;
  private int mInputMode = -1;
  private PopupWindow.OnDismissListener mOnDismissListener;
  private int mSoftInputMode = -1;
  private boolean mTouchable = true;//default is ture
  private View.OnTouchListener mOnTouchListener;

  private Drawable popWindowDrawable;

  private PopWindow(Context context) {
    mContext = context;
  }

  public int getWidth() {
    return mWidth;
  }

  public int getHeight() {
    return mHeight;
  }

  /**
   *
   * @param anchor
   * @param xOff
   * @param yOff
   * @return
   */
  public PopWindow showAsDropDown(View anchor, int xOff, int yOff) {
    if (mPopupWindow != null) {
      mPopupWindow.showAsDropDown(anchor, xOff, yOff);
    }
    return this;
  }

  public PopWindow showAsDropDown(View anchor) {
    if (mPopupWindow != null) {
      mPopupWindow.showAsDropDown(anchor);
    }
    return this;
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  public PopWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity) {
    if (mPopupWindow != null) {
      mPopupWindow.showAsDropDown(anchor, xOff, yOff, gravity);
    }
    return this;
  }


  /**
   * 相对于父控件的位置（通过设置Gravity.CENTER，下方Gravity.BOTTOM等 ），可以设置具体位置坐标
   *
   * @param x the popup's x location offset
   * @param y the popup's y location offset
   */
  public PopWindow showAtLocation(View parent, int gravity, int x, int y) {
    if (mPopupWindow != null) {
      mPopupWindow.showAtLocation(parent, gravity, x, y);
    }
    return this;
  }

  /**
   * 添加一些属性设置
   */
  private void apply(PopupWindow popupWindow) {
    popupWindow.setClippingEnabled(mClipEnable);
    if (mIgnoreCheekPress) {
      popupWindow.setIgnoreCheekPress();
    }
    if (mInputMode != -1) {
      popupWindow.setInputMethodMode(mInputMode);
    }
    if (mSoftInputMode != -1) {
      popupWindow.setSoftInputMode(mSoftInputMode);
    }
    if (mOnDismissListener != null) {
      popupWindow.setOnDismissListener(mOnDismissListener);
    }
    if (mOnTouchListener != null) {
      popupWindow.setTouchInterceptor(mOnTouchListener);
    }
    popupWindow.setTouchable(mTouchable);
  }

  private PopupWindow build() {
    if (mContentView == null) {
      mContentView = LayoutInflater.from(mContext).inflate(mResLayoutId, null);
    }
    if (mWidth != 0 && mHeight != 0) {
      mPopupWindow = new PopupWindow(mContentView, mWidth, mHeight);
    } else {
      mPopupWindow = new PopupWindow(mContentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
    mAnimationStyle = mAnimationStyle != -1 ? mAnimationStyle : R.style.PopDefaultAnimation;
    mPopupWindow.setAnimationStyle(mAnimationStyle);

    apply(mPopupWindow);//设置一些属性

    mPopupWindow.setFocusable(mIsFocusable);
    mPopupWindow.setBackgroundDrawable(popWindowDrawable != null ? popWindowDrawable : new ColorDrawable(Color.parseColor("#20000000")));
    mPopupWindow.setOutsideTouchable(mIsOutside);

    if (mWidth == 0 || mHeight == 0) {
      mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
      //如果外面没有设置宽高的情况下，计算宽高并赋值
      mWidth = mPopupWindow.getContentView().getMeasuredWidth();
      mHeight = mPopupWindow.getContentView().getMeasuredHeight();
    }

    mPopupWindow.update();
    return mPopupWindow;
  }

  public View getView() {
    if (mPopupWindow != null) {
      return mPopupWindow.getContentView();
    }
    return mContentView;
  }

  /**
   * 关闭popWindow
   */
  public void dissmiss() {
    if (mPopupWindow != null) {
      mPopupWindow.dismiss();
    }
  }

  public boolean isShowing() {
    if (mPopupWindow != null) {
      return mPopupWindow.isShowing();
    }
    return false;
  }

  public static class PopupWindowBuilder {

    private PopWindow popWindow;

    public PopupWindowBuilder(Context context) {
      popWindow = new PopWindow(context);
    }

    public PopupWindowBuilder size(int width, int height) {
      popWindow.mWidth = width;
      popWindow.mHeight = height;
      return this;
    }


    public PopupWindowBuilder setFocusable(boolean focusable) {
      popWindow.mIsFocusable = focusable;
      return this;
    }

    public PopupWindowBuilder setView(int resLayoutId) {
      popWindow.mResLayoutId = resLayoutId;
      popWindow.mContentView = null;
      return this;
    }

    public PopupWindowBuilder setView(View view) {
      popWindow.mContentView = view;
      popWindow.mResLayoutId = -1;
      return this;
    }

    public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable) {
      popWindow.mIsOutside = outsideTouchable;
      return this;
    }

    public PopupWindowBuilder setBackgroundDrawable(Drawable drawable) {
      popWindow.popWindowDrawable = drawable;
      return this;
    }

    /**
     * 设置弹窗动画
     */
    public PopupWindowBuilder setAnimationStyle(int animationStyle) {
      popWindow.mAnimationStyle = animationStyle;
      return this;
    }

    public PopupWindowBuilder setClippingEnable(boolean enable) {
      popWindow.mClipEnable = enable;
      return this;
    }

    public PopupWindowBuilder setIgnoreCheekPress(boolean ignoreCheekPress) {
      popWindow.mIgnoreCheekPress = ignoreCheekPress;
      return this;
    }

    public PopupWindowBuilder setInputMethodMode(int mode) {
      popWindow.mInputMode = mode;
      return this;
    }

    public PopupWindowBuilder setOnDissmissListener(
        PopupWindow.OnDismissListener onDissmissListener) {
      popWindow.mOnDismissListener = onDissmissListener;
      return this;
    }


    public PopupWindowBuilder setSoftInputMode(int softInputMode) {
      popWindow.mSoftInputMode = softInputMode;
      return this;
    }

    public PopupWindowBuilder setTouchable(boolean touchable) {
      popWindow.mTouchable = touchable;
      return this;
    }

    public PopupWindowBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter) {
      popWindow.mOnTouchListener = touchIntercepter;
      return this;
    }

    public PopWindow create() {
      //构建PopWindow
      popWindow.build();
      return popWindow;
    }

  }
}
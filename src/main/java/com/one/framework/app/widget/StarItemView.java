package com.one.framework.app.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

public class StarItemView extends ImageView {

  private boolean mChecked;
  private CharSequence mTextOn;
  private CharSequence mTextOff;

  public StarItemView(Context context) {
    this(context, null);
  }

  public StarItemView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public StarItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mChecked = false;
    mTextOn = "已选中";
    mTextOff = "未选中";
  }

  private CharSequence getText() {
    return mChecked ? mTextOn : mTextOff;
  }

  public void setChecked(boolean isChecked) {
    mChecked = isChecked;
  }

  @Override
  public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
    super.onInitializeAccessibilityEvent(event);
    event.setChecked(mChecked);
  }

  @Override
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
    super.onInitializeAccessibilityNodeInfo(info);
    info.setCheckable(true);
    info.setChecked(mChecked);
    // Very often you will need to add only the text on the custom view.
    CharSequence text = getText();
    if (!TextUtils.isEmpty(text)) {
      info.setText(text);
    }
  }

  @Override
  public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
    super.onPopulateAccessibilityEvent(event);
    CharSequence text = getText();
    if (!TextUtils.isEmpty(text)) {
      event.getText().add(text);
    }
  }
}

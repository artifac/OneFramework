package com.one.framework.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.one.framework.app.widget.base.IMovePublishListener;

/**
 * Created by ludexiang on 2018/4/21.
 */

public class ScrollerLayout extends RelativeLayout {
  private IMovePublishListener mMoveListener;

  public ScrollerLayout(Context context) {
    super(context);
  }

  public ScrollerLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ScrollerLayout(Context context, AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setMoveListener(IMovePublishListener listener) {
    mMoveListener = listener;
  }


}

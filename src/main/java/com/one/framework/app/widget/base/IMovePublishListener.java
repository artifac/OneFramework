package com.one.framework.app.widget.base;

import android.support.annotation.Keep;

/**
 * Created by ludexiang on 2018/4/3.
 */

@Keep
public interface IMovePublishListener {
  void onMove(float offsetX, float offsetY);
  void onUp(boolean bottom2Up, boolean isFling);
}

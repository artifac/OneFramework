package com.one.framework.app.widget.base;

/**
 * Created by ludexiang on 2018/4/3.
 */

public interface IMovePublishListener {
  void onMove(float offsetX, float offsetY);
  void onUp(boolean bottom2Up, boolean isFling);
}

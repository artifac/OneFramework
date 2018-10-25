package com.one.framework.app.widget.base;

import android.support.annotation.Keep;
import android.view.View;

@Keep
public interface IOptionView {
  int NOW = 1;
  int BOOKING = 2;
  
  // default NOW
  int getState();

  void setState(int state);

  @Keep
  interface IOptionChange {
    void onChange(String key, int position);
  }
  
  void setOptionChange(IOptionChange l);

  int getSavePosition();

  View getView();

  int getPosition();
}

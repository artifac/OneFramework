package com.one.framework.app.widget.base;

import android.graphics.Color;
import android.support.annotation.StringRes;
import android.widget.ImageView.ScaleType;

/**
 * Created by ludexiang on 2017/12/6.
 */

public interface IListItemView {

  int DEFAULT_COLOR = Color.parseColor("#373c43");
  int DEFAULT_SIZE = 14;

  void setLeftImgVisible(boolean visible);

  void setImgUrl(String url, int defaultRes);

  void setImgRes(int resId);

  void setImgRes(int resId, ScaleType scaleType);

  void setItemTitle(CharSequence title);

  void setItemTitle(@StringRes int strRes);

  void setItemTitle(@StringRes int strRes, int color);

  void setItemTitle(CharSequence title, int size);

  void setItemColor(int color);

  void setItemTitle(CharSequence title, int size, int color);

  void setItemTitle(CharSequence title, int size, int color, boolean useUnit);

  void setItemTitle(@StringRes int title, int size, int color, boolean useUnit);

  void setItemTitle(CharSequence title, int size, boolean bold, int color);

  void setRightTxt(@StringRes int resId);

  void setRightColor(int color);

  void setRightTxt(CharSequence rightTxt);

  void setRightTxt(CharSequence rightTxt, int color);

  void setRightTxt(CharSequence rightTxt, int size, int color);

  void setScriptTxt(CharSequence scriptTxt, int color, int size);

  void setClickCallback(IClickCallback callback);

  void setArrowVisible(boolean visible);

  void setSeparatorColor(int color);

  void setLRMargin(int margin);

  interface IClickCallback {

    void callback(int id);
  }
}

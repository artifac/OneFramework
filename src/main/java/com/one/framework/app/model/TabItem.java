package com.one.framework.app.model;

/**
 * Created by ludexiang on 2018/3/28.
 */

public class TabItem {

  /**
   * tab business
   */
  public String tabBiz;

  /**
   * item tab
   */
  public String tab;
  /**
   * tab icon maybe null
   */
  public String tabIcon;

  /**
   * tab icon id
   */
  public int tabIconResId;

  /**
   * position of viewgroup
   */
  public int position;

  /**
   * red point
   */
  public boolean isRedPoint;

  /**
   * current tab is selected
   */
  public boolean isSelected;

  /**
   * 是否可点击
   */
  public boolean isClickable = true;

  /**
   * tab bizType
   */
  public int tabBizType;
}

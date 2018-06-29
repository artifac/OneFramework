package com.one.framework.model;

import android.support.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ludexiang on 2018/4/21.
 */

public class NavigatorModel {
  public static final int MY_TRAVEL = 1;
  public static final int SETTING = MY_TRAVEL << 2;

  public String optionsIconUrl; // 小 icon
  public int optionsIconId;
  public String optionsInfo;
  public String optionsExtra;

  @OptionsType
  public int optionsType;

  @Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @IntDef({MY_TRAVEL, SETTING})
  @interface OptionsType {}
}

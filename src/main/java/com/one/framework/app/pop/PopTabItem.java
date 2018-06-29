package com.one.framework.app.pop;

import android.support.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ludexiang on 2017/12/25.
 */

public class PopTabItem {
  public static final int CANCEL_ORDER = 1;
  public static final int CONNECT_SERVICE = 2;
  public static final int EMERGENCY_CONTACT = 3;
  public PopTabItem(String tab) {
    this.tab = tab;
  }

  public int itemIcon;

  public @PopTabItemType int itemType;

  public String tab;

  @Target({ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @IntDef({CANCEL_ORDER, CONNECT_SERVICE, EMERGENCY_CONTACT})
  public @interface PopTabItemType {

  }
}

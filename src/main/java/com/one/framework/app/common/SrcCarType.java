package com.one.framework.app.common;

import android.support.annotation.NonNull;

/**
 * Created by ludexiang on 2018/6/22.
 */

public enum SrcCarType {
  UNKNOW("未知领域", -1), TAXI("出租车", 3);

  public int bizCode;
  String bizName;

  SrcCarType(String name, int value) {
    bizName = name;
    bizCode = value;
  }

  @NonNull
  public static SrcCarType getCarType(int code) {
    for (SrcCarType state : SrcCarType.values()) {
      if (state.bizCode == code) {
        return state;
      }
    }
    return UNKNOW;
  }

  public static String getBizName(int code) {
    for (SrcCarType state : SrcCarType.values()) {
      if (state.bizCode == code) {
        return state.bizName;
      }
    }
    return UNKNOW.bizName;
  }

}

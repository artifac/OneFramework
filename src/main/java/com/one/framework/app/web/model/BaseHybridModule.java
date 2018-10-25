package com.one.framework.app.web.model;

import com.one.framework.app.web.hybird.IHybridActivity;

public class BaseHybridModule {
  public IHybridActivity mHybridActivity;

  public BaseHybridModule(IHybridActivity hybridActivity) {
    mHybridActivity = hybridActivity;
  }
}

package com.one.framework.app.web.hybird;

import android.app.Activity;

public interface IHybridActivity {
  Activity getActivity();
  FusionWebView getWebView();
  Object getExportModuleInstance(Class clazz);
}

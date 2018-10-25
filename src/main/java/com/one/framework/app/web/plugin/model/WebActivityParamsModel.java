package com.one.framework.app.web.plugin.model;

import android.content.Intent;
import android.os.Bundle;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.jsbridge.JavascriptBridge;

public class WebActivityParamsModel {

  private JavascriptBridge jsBridge;
  private WebActivity activity;
  private Intent getIntent;
  private Bundle saveInstanceState;

  public WebActivityParamsModel(JavascriptBridge jsBridge, WebActivity activity, Intent getIntent,
      Bundle saveInstanceState) {
    this.jsBridge = jsBridge;
    this.activity = activity;
    this.getIntent = getIntent;
    this.saveInstanceState = saveInstanceState;
  }

  public JavascriptBridge getJsBridge() {
    return jsBridge;
  }

  public void setJsBridge(JavascriptBridge jsBridge) {
    this.jsBridge = jsBridge;
  }

  public WebActivity getActivity() {
    return activity;
  }

  public void setActivity(WebActivity activity) {
    this.activity = activity;
  }

  public Intent getGetIntent() {
    return getIntent;
  }

  public void setGetIntent(Intent getIntent) {
    this.getIntent = getIntent;
  }

  public Bundle getSaveInstanceState() {
    return saveInstanceState;
  }

  public void setSaveInstanceState(Bundle saveInstanceState) {
    this.saveInstanceState = saveInstanceState;
  }
}
package com.one.framework.app.web.js;

public class DefaultCallbackFunction implements CallbackFunction {
  private WebViewJsBridge bridge;
  private String callbackId;

  public DefaultCallbackFunction(WebViewJsBridge bridge, String callbackId) {
    this.callbackId = callbackId;
    this.bridge = bridge;
  }

  @Override
  public void onCallBack(Object... obj) {
    CallbackMessage callbackMessage = new CallbackMessage();
    callbackMessage.setCallbackId(this.callbackId);
    callbackMessage.setCallbackArguments(obj);
    this.bridge.callbackJS(callbackMessage);
  }
}

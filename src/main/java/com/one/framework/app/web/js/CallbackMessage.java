package com.one.framework.app.web.js;

import org.json.JSONArray;

public class CallbackMessage {
  private String callbackId;
  private Object[] callbackArguments;

  public CallbackMessage() {
  }

  public void setCallbackId(String callbackId) {
    this.callbackId = callbackId;
  }

  public void setCallbackArguments(Object[] arguments) {
    this.callbackArguments = arguments;
  }

  public String getCallbackId() {
    return this.callbackId;
  }

  public JSONArray getArgumentsAsJSONArray() {
    JSONArray jsonArray = new JSONArray();
    if (this.callbackArguments != null) {
      Object[] var2 = this.callbackArguments;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Object object = var2[var4];
        jsonArray.put(object);
      }
    }

    return jsonArray;
  }
}

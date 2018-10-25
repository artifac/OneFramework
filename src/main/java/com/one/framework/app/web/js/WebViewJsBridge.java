package com.one.framework.app.web.js;

public interface WebViewJsBridge {

  void executeJs(String js);

  void callbackJS(CallbackMessage msg);

  void invokeJSMethod(String arg1, String arg2, String arg3);

  void invokeJSMethod(String arg1, String arg2, String arg3, CallbackFunction function);
}

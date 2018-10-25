package com.one.framework.app.web.hybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.one.framework.app.web.js.BridgeHelper;
import com.one.framework.app.web.js.CallbackFunction;
import com.one.framework.app.web.js.CallbackMessage;
import com.one.framework.app.web.js.DefaultCallbackFunction;
import com.one.framework.app.web.js.ExportNamespace;
import com.one.framework.app.web.js.InvokeMessage;
import com.one.framework.app.web.js.WebViewJsBridge;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class FusionWebViewClient extends WebViewClient implements WebViewJsBridge {

  private static final String TAG = "FusionWebViewClient";
  public static final Map<String, ExportNamespace> namespaceMap = new HashMap();
  private Context mContext;
  private IHybridActivity mHybridActivity;
  private FusionWebView mWebView;
  private long callbackId = 0L;
  private Map<String, CallbackFunction> responseCallbacks = new HashMap();

  public static void export(String exportName, Class exportClass) {
    namespaceMap.put(exportName, new ExportNamespace(exportName, exportClass));
  }

  public FusionWebViewClient(FusionWebView webView) {
    this.mContext = webView.getContext();
    this.mHybridActivity = webView.mHybridContainer;
    this.mWebView = webView;
  }

  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (url.startsWith("fusion://invokeNative")) {
      this.handleInvokeFromJs(url);
      return true;
    } else if (url.startsWith("fusion://return")) {
      this.handleResponseFromJS(url);
      return true;
    } else {
      return super.shouldOverrideUrlLoading(view, url);
    }
  }

  public void onPageStarted(WebView view, String url, Bitmap favicon) {
    super.onPageStarted(view, url, favicon);
    BridgeHelper.webViewLoadLocalJs(view);
  }

  public void onPageFinished(WebView view, String url) {
    super.onPageFinished(view, url);
  }

  public void handleInvokeFromJs(String url) {
    InvokeMessage invokeMessage = BridgeHelper.parseInvokeMessage(url);
    ExportNamespace targetNamespace = namespaceMap.get(invokeMessage.getModuleName());
    if (targetNamespace != null) {
      Class targetClass = targetNamespace.getExportClass();
      Method targetMethod = targetNamespace.getTargetMethod(invokeMessage.getFunctionName());
      if (targetMethod != null) {
        executeTargetMethod(targetClass, targetMethod, invokeMessage.getArgsForNative());
      }
    }
  }

  private void executeTargetMethod(Class targetClass, Method targetMethod, Object[] argsData) {
    Class<?>[] parameterTypes = targetMethod.getParameterTypes();
    int length = parameterTypes.length;

    for (int i = 0; i < length; ++i) {
      Class type = parameterTypes[i];
      if (type.isInterface() && type == CallbackFunction.class) {
        String callbackId = (String) argsData[i];
        argsData[i] = new DefaultCallbackFunction(this, callbackId);
      }
    }

    boolean isStatic = Modifier.isStatic(targetMethod.getModifiers());

    try {
      if (isStatic) {
        targetMethod.invoke(targetClass, argsData);
      } else {
        targetMethod.invoke(this.mHybridActivity.getExportModuleInstance(targetClass), argsData);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void executeJs(String js) {

  }

  public void callbackJS(CallbackMessage callbackMessage) {
    String callbackId = callbackMessage.getCallbackId();
    String arguments = callbackMessage.getArgumentsAsJSONArray().toString();
    final String javascriptCommand = String
        .format("javascript:Fusion.callbackJS('%s', %s);", callbackId, arguments);
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      this.mWebView.loadUrl(javascriptCommand);
    } else {
      this.mHybridActivity.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          FusionWebViewClient.this.mWebView.loadUrl(javascriptCommand);
        }
      });
    }

  }

  public void invokeJSMethod(String module, String function, String data) {
    this.invokeJSMethod(module, function, data, (CallbackFunction) null);
  }

  public void invokeJSMethod(String module, String function, String data,
      CallbackFunction responseCallback) {
    InvokeMessage invokeMessage = new InvokeMessage();
    invokeMessage.setModuleName(module);
    invokeMessage.setFunctionName(function);
    invokeMessage.setArgs(data);
    if (responseCallback != null) {
      String callbackName = String.format("JAVA_CB_%s", String.valueOf(++this.callbackId));
      this.responseCallbacks.put(callbackName, responseCallback);
      invokeMessage.setReturnFunction(callbackName);
    }

    this.executeCallJS(invokeMessage, "javascript:Fusion.invokeJSMethod('%s');");
  }

  public void handleResponseFromJS(String url) {
    InvokeMessage invokeMessage = BridgeHelper.parseInvokeMessage(url);
    CallbackFunction callbackFunction = (CallbackFunction) this.responseCallbacks
        .get(invokeMessage.getFunctionName());
    if (callbackFunction != null) {
      callbackFunction.onCallBack(new Object[]{invokeMessage.getArgsForNativeCallback()});
      this.responseCallbacks.remove(invokeMessage.getFunctionName());
    }

  }

  private void executeCallJS(InvokeMessage message, String jsCommond) {
    String messageJson = message.toJson();
    String javascriptCommand = String.format(jsCommond, messageJson);
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      this.mWebView.loadUrl(javascriptCommand);
    }

  }
}

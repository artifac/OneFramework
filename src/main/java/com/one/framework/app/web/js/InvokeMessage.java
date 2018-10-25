package com.one.framework.app.web.js;

import android.text.TextUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

public class InvokeMessage {
  public static final String KEY_NAMESPACE = "module";
  public static final String KEY_FUNCTION_NAME = "method";
  public static final String KEY_ARGS = "arguments";
  public static final String KEY_RETURN_FUNCTION = "result";
  private String moduleName;
  private String functionName;
  private String args;
  private String returnFunction;

  public InvokeMessage() {
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getModuleName() {
    return this.moduleName;
  }

  public void setFunctionName(String functionName) {
    this.functionName = functionName;
  }

  public String getFunctionName() {
    return this.functionName;
  }

  public void setArgs(String args) {
    this.args = args;
  }

  public Object[] getArgsForNative() {
    ArrayList argsList = new ArrayList();

    try {
      JSONArray argsJsonArray = new JSONArray(this.args);
      int len = argsJsonArray.length();

      for(int i = 0; i < len; ++i) {
        argsList.add(argsJsonArray.get(i));
      }
    } catch (JSONException var5) {
      var5.printStackTrace();
    }

    return argsList.toArray();
  }

  public String getArgsForNativeCallback() {
    return this.args;
  }

  public void setReturnFunction(String callbackId) {
    this.returnFunction = callbackId;
  }

  public boolean hasReturnFunction() {
    return !TextUtils.isEmpty(this.returnFunction);
  }

  public String toJson() {
    return null;
  }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.one.framework.net.base;

import android.text.TextUtils;
import com.one.framework.net.NetConstant;
import java.io.Serializable;
import org.json.JSONObject;

public class BaseObject implements Serializable, Cloneable {

  public int code = NetConstant.NO_DATA;
  public String message;
  public int timeoffset;
  public String data;
  private Throwable throwable;

  public static boolean isAvailable(BaseObject obj) {
    return obj != null && obj.isAvailable();
  }

  public static BaseObject valueOf(Object obj) {
    if (obj == null) {
      return null;
    } else {
      BaseObject result = null;

      try {
        result = (BaseObject) obj;
      } catch (ClassCastException var3) {
        var3.printStackTrace();
      }

      return result;
    }
  }

  public Throwable getThrowable() {
    return this.throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

  public int getErrorCode() {
    return this.code;
  }

  public void setErrorCode(int error) {
    this.code = error;
  }

  public int getTimeOffset() {
    return this.timeoffset;
  }

  public void setTimeOffset(int timeoffset) {
    this.timeoffset = timeoffset;
  }

  public boolean isAvailable() {
    return this.code == 0;
  }

  public void resetState() {
    this.code = NetConstant.NO_DATA;
  }

  public String getErrorMsg() {
    return this.message;
  }

  public void setErrorMsg(String msg) {
    this.message = msg;
  }

  public void parse(String json) {
    if (TextUtils.isEmpty(json)) {
      this.setErrorCode(NetConstant.ERROR_INVALID_DATA_FORMAT);
    } else {
      JSONObject obj;
      try {
        obj = new JSONObject(json);
      } catch (Exception var4) {
        this.setErrorCode(NetConstant.ERROR_INVALID_DATA_FORMAT);
        return;
      }

      if (obj.has(NetConstant.ERROR_CODE)) {
        this.setErrorCode(obj.optInt(NetConstant.ERROR_CODE));
      }

      if (obj.has(NetConstant.ERROR_MSG)) {
        this.setErrorMsg(obj.optString(NetConstant.ERROR_MSG));
      } else if (obj.has(NetConstant.ERROR)) {
        this.setErrorMsg(obj.optString(NetConstant.ERROR));
      }

      if (TextUtils.isEmpty(this.message) || TextUtils.isDigitsOnly(this.message)) {
        this.message = null;
      }

      if (obj.has(NetConstant.TIME_OFFSET)) {
        this.setTimeOffset(obj.optInt(NetConstant.TIME_OFFSET));
      }

      if (obj.has(NetConstant.DATA)) {
        setParseData(obj.optJSONObject(NetConstant.DATA).toString());
      }
    }
  }

  public void setParseData(String dataString) {
    data = dataString;
  }

  public String toString() {
    return "BaseObject [code=" + this.code + ", message=" + this.message +  " data >>> " + data +"]";
  }

  public BaseObject clone() {
    BaseObject obj = null;

    try {
      obj = (BaseObject) super.clone();
    } catch (CloneNotSupportedException var3) {
      var3.printStackTrace();
    }

    return obj;
  }
}

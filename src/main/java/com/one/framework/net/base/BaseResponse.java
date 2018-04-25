package com.one.framework.net.base;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ludexiang on 2018/4/25.
 */

public abstract class BaseResponse implements Serializable {

  /**
   * response 返回码
   */
  public int errno;

  /**
   * response
   */
  public String errMsg;

  public void parse(String json) {
    try {
      JSONObject jsonObject = new JSONObject(json);
      if (jsonObject.has("errno")) {
        errno = jsonObject.optInt("errno");
      }
      if (jsonObject.has("errmsg")) {
        errMsg = jsonObject.optString("errmsg");
      }
      if (jsonObject.has("data")) {
        parse(jsonObject.optJSONObject("data"));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public abstract void parse(JSONObject obj);
}

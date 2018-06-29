package com.one.framework.net;

import java.util.HashMap;

/**
 * Created by ludexiang on 2018/6/8.
 */

public interface IHeaderParams {
  void setLoginPhone(String mobilePhone);
  HashMap<String, Object> getParams();
}

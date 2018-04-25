package com.one.framework.net.model;

import com.one.framework.app.model.TabItem;
import com.one.framework.net.base.BaseResponse;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * Created by ludexiang on 2018/4/25.
 */

public class ReqConfigResponse extends BaseResponse {

  private List<TabItem> tabs = new ArrayList<TabItem>();

  @Override
  public void parse(JSONObject obj) {
//    obj.optJSONArray()
  }
}

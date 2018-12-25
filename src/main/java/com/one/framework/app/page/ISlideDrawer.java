package com.one.framework.app.page;

import com.one.framework.model.NavigatorModel;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/23.
 */

public interface ISlideDrawer extends IComponent {
  void setListOptions(List<NavigatorModel> listOptions);
  void setGridOptions(List<NavigatorModel> gridOptions);
  void refreshHeader();
  void recoveryDefault(); // 恢复默认
}

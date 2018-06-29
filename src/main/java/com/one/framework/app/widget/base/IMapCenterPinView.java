package com.one.framework.app.widget.base;

import android.view.View;
import com.one.map.IMap;
import com.one.map.model.LatLng;

/**
 * Created by ludexiang on 2018/6/12.
 */

public interface IMapCenterPinView {
  void setMap(IMap map);
  void stop();
  void reverseGeoAddress(LatLng latLng);
  void hide(boolean isHide);

  /**
   * 拖动地图是否可以地址解析
   * @param reverse
   */
  void isToggleLoading(boolean reverse);

  View getView();
}

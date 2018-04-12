//package com.one.framework.app.map;
//
//import com.one.map.map.MarkerOption;
//import com.one.map.map.PolylineOption;
//import com.one.map.map.element.IMarker;
//import com.one.map.map.element.Marker;
//import com.one.map.map.element.Polyline;
//import com.one.map.model.Address;
//import com.one.map.model.BestViewModel;
//import com.one.map.model.LatLng;
//import java.util.List;
//
///**
// * Created by mobike on 2017/11/27.
// */
//
//public interface IMapFragment {
//
//  Marker addMarker(MarkerOption option);
//
//  void displayMyLocation();
//
//  void hideMyLocation();
//
//  /**
//   * geo to address
//   */
//  Address geo2Address(LatLng latLng);
//
//  /**
//   * reverse geo
//   */
//  LatLng reverseGeo(Address address);
//
//  /**
//   * 路线规划
//   */
//  void drivingRoutePlan(Address from, Address to);
//
//  /**
//   * show info window
//   */
//  boolean showInfoWindow(IMarker marker, CharSequence msg);
//
//  /**
//   * update info window msg
//   */
//  void updateInfoWindowMsg(CharSequence msg);
//
//  /**
//   * remove info window
//   */
//  void removeInfoWindow();
//
//  /**
//   * draw line
//   * @param option
//   */
//  Polyline addPolyline(PolylineOption option);
//
//  /**
//   * 路径规划返回的消息
//   * @return
//   */
//  interface IRoutePlanMsgCallback {
//    void routePlanMsg(String msg, List<LatLng> points);
//  }
//
//  void setRoutePlanCallback(IRoutePlanMsgCallback callback);
//
//  /**
//   * 清空所有地图元素
//   */
//  void clearElements();
//
//  /**
//   * 刷新最佳view
//   * 根据BesetViewModel 来判断是回到定位点还是框view
//   * 1.回到定位点 设置zoomCenter 并清空bounds
//   * 2.框元素 通过设置bounds
//   */
//  void doBestView(BestViewModel model);
//}

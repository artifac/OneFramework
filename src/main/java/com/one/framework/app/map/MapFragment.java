package com.one.framework.app.map;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.map.location.LocationProvider;
import com.one.map.map.MarkerOption;
import com.one.map.map.PolylineOption;
import com.one.map.map.element.IMarker;
import com.one.map.map.element.Marker;
import com.one.map.map.element.Polyline;
import com.one.map.model.Address;
import com.one.map.model.BestViewModel;
import com.one.map.model.LatLng;
import com.one.map.view.IMapView;

/**
 * Created by mobike on 2017/11/14.
 */

public class MapFragment extends Fragment implements IMapFragment  {

  private FrameLayout mMapViewContainer;
//  private MapPresenter mMapPresenter;
  private IMapView mMapView;

  /**
   * 屏幕中心定位点大头针
   */
  private ImageView mLocPin;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
//    mMapView = MapFactory.newInstance().getMapView(activity, IMapView.TENCENT);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.map_fragment_layout, container);
    mMapViewContainer = (FrameLayout) view.findViewById(R.id.map_view_container);
    mLocPin = (ImageView) view.findViewById(R.id.map_fragment_loc_pin);
    mMapView.attachToRootView(mMapViewContainer);
//    mMapPresenter = new MapPresenter(getContext(), this, mMapPoi);

    initLocation();
    return view;
  }

  private void initLocation() {
    Address location = getCurrentLocation();
    if (location != null) {
      LatLng loc = location.mAdrLatLng;
//      Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_map_my_location);
//      BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
//      mMapView.myLocationConfig(bitmapDescriptor, loc);

      BestViewModel model = new BestViewModel();
      model.zoomCenter = loc;
      doBestView(model);
//      displayMyLocation();
    }
  }

  @Override
  public Marker addMarker(MarkerOption option) {
    return mMapView.addMarker(option);
  }

  @Override
  public Address geo2Address(LatLng latLng) {
    return null;
  }

  @Override
  public LatLng reverseGeo(Address address) {
    return null;
  }

  @Override
  public void drivingRoutePlan(Address from, Address to) {
//    mMapPresenter.drivingRoutePlan(from, to);
  }

  @Override
  public Polyline addPolyline(PolylineOption option) {
    return mMapView.addPolyline(option);
  }

  @Override
  public void doBestView(BestViewModel model) {
    mMapView.doBestView(model);
  }

  @Override
  public void displayMyLocation() {
    mMapView.setMyLocationEnable(true);
  }

  @Override
  public void hideMyLocation() {
    mMapView.setMyLocationEnable(false);
  }

  @Override
  public void clearElements() {
    mMapView.clearElements();
  }

  @Override
  public boolean showInfoWindow(IMarker marker, CharSequence msg) {
    return mMapView.showInfoWindow(marker, msg);
  }

  @Override
  public void updateInfoWindowMsg(CharSequence msg) {
    mMapView.updateInfoWindowMsg(msg);
  }

  @Override
  public void removeInfoWindow() {
    mMapView.removeInfoWindow();
  }

  @Override
  public void setRoutePlanCallback(IRoutePlanMsgCallback callback) {
//    mMapPresenter.setRoutePlanCallback(callback);
  }

  private Address getCurrentLocation() {
    return LocationProvider.getInstance().getLocation();
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.onResume();
  }

  @Override
  public void onStart() {
    super.onStart();
    mMapView.onStart();
  }

  @Override
  public void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    mMapView.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mMapView.onDestroy();
  }
}

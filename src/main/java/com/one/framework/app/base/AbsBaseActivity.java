package com.one.framework.app.base;

import com.one.map.model.Address;
import com.one.map.view.IMapDelegate.CenterLatLngParams;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/29.
 */

public class AbsBaseActivity extends BaseActivity {

  @Override
  public void onMapMoveFinish(CenterLatLngParams params) {

  }

  @Override
  public void onMapGeo2Address(Address address) {

  }

  @Override
  public void onMapPoiAddresses(int type, List<Address> addresses) {

  }
}

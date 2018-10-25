package com.one.framework.app.web.jsbridge.functions;

import android.content.Context;
import com.one.framework.app.web.jsbridge.JavascriptBridge.Function;
import com.one.map.location.LocationProvider;
import com.one.map.model.Address;
import org.json.JSONException;
import org.json.JSONObject;

public class FuncGetLocationInfo extends Function {

  public static final String P_LAT = "lat";
  public static final String P_LNG = "lng";
  public static final String P_CITY_ID = "city_id";
  public static final String P_AREA = "area";
  private Context mContext;

  public FuncGetLocationInfo(Context context) {
    mContext = context;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    JSONObject object = new JSONObject();
    try {
      String lng = "";
      String lat = "";
      String cid = "";
      String area = "";
      Address address = LocationProvider.getInstance().getLocation();
      if (address != null) {
        lng = address.mAdrLatLng.longitude + "";
        lat = address.mAdrLatLng.latitude + "";
        cid = address.mCityCode;
        area = address.mCity;
      }
      object.put(P_LNG, lng);
      object.put(P_LAT, lat);
      object.put(P_CITY_ID, cid);
      object.put(P_AREA, area);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return object;
  }
}

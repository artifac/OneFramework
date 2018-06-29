package com.one.framework.provider;

import android.util.SparseArray;
import com.one.framework.net.model.OrderDetail;
import com.one.map.model.Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class HomeDataProvider {

  private Address sCurAddress;
  private Map<String, Address> mAdrMap = new HashMap<String, Address>();
  private SparseArray<List<Address>> mSearchResult = new SparseArray<>();
//  private List<Address> mPoiAddress = new ArrayList<Address>();

  private OrderDetail mOrderDetail;

  private HomeDataProvider() {

  }

  private final static class HomeProviderFactory {

    private static HomeDataProvider sProvider;

    public static HomeDataProvider getInstance() {
      if (sProvider == null) {
        sProvider = new HomeDataProvider();
      }
      return sProvider;
    }
  }

  public static HomeDataProvider getInstance() {
    return HomeProviderFactory.getInstance();
  }

  public void savePoiAddress(String key, Address address) {
    mAdrMap.put(key, address);
  }

  /**
   * 根据当前位置获取周边Building
   * @param pois
   */
  public void savePoiAddresses(int type, List<Address> pois) {
    mSearchResult.put(type, pois);
  }

  public List<Address> obtainPoiAddress(int type) {
    return mSearchResult.get(type);
  }

  public Address obtionPoiAddress(String key) {
    return mAdrMap.get(key);
  }

  public void saveLocAddress(Address address) {
    sCurAddress = address;
  }

  public Address obtainCurAddress() {
    return sCurAddress;
  }

  public void saveOrderDetail(OrderDetail detail) {
    mOrderDetail = detail;
  }

  public OrderDetail obtainOrderDetail() {
    return mOrderDetail;
  }
}

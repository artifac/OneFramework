package com.one.framework.provider;

import android.util.SparseArray;
import com.one.framework.model.ContactModel;
import com.one.framework.net.model.CarType;
import com.one.framework.net.model.Entrance;
import com.one.framework.net.model.OrderDetail;
import com.one.map.model.Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ludexiang on 2018/6/7.
 */

public class HomeDataProvider {

  private Address sCurAddress;
  private Map<String, Address> mAdrMap = new HashMap<String, Address>();
  private SparseArray<List<Address>> mSearchResult = new SparseArray<>();
  private List<CarType> mCarTypes = new ArrayList<>();
//  private List<Address> mPoiAddress = new ArrayList<Address>();

  private List<ContactModel> mCurContactModels;
  private List<OrderDetail> mOrderDetail = new ArrayList<>();

  private List<Entrance> mEntrances = new ArrayList<>();

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

  public void saveCarType(List<CarType> carTypes) {
    if (!mCarTypes.isEmpty()) {
      mCarTypes.clear();
    }
    if (carTypes != null && !carTypes.isEmpty()) {
      mCarTypes.addAll(carTypes);
    }
  }

  /**
   * 获取车型
   * @return
   */
  public List<CarType> obtainCarTypes() {
    return mCarTypes;
  }

  /**
   * 服务费
   * @param entrances
   */
  public void saveEntrances(List<Entrance> entrances) {
    if (!mEntrances.isEmpty()) {
      mEntrances.clear();
    }
    mEntrances.addAll(entrances);
  }

  public List<Entrance> obtainEntrance() {
    return mEntrances;
  }

  public Entrance obtainEntranceByTab(int tabType) {
    if (!mEntrances.isEmpty()) {
      for (Entrance entrance : mEntrances) {
        if (entrance.getEntranceTabBizType() == tabType) {
          return entrance;
        }
      }
    }
    return null;
  }

  /**
   * 保存紧急联系人
   * @param models
   */
  public void saveContact(List<ContactModel> models) {
    mCurContactModels = models;
  }

  /**
   * 获取当前紧急联系人
   * @return
   */
  public List<ContactModel> getContactModels() {
    return mCurContactModels;
  }

  /**
   * 保存当前上车点位置
   * @param address
   */
  public void saveCurAddress(Address address) {
    sCurAddress = address;
  }

  public Address obtainCurAddress() {
    return sCurAddress;
  }

  public void saveOrderDetail(OrderDetail detail) {
    if (detail == null) {
      return;
    }
    boolean contain = false;
    for (OrderDetail details : mOrderDetail) {
      if (details.getOrderId().equals(detail.getOrderId())) {
        contain = true;
      }
    }
    if (contain) {
      return;
    }
    mOrderDetail.add(detail);
  }

  public void saveOrderDetails(List<OrderDetail> details) {
    mOrderDetail.clear();
    if (details != null) {
      mOrderDetail.addAll(details);
    }
  }

  /**
   * 获取即时订单
   * @return
   */
  public OrderDetail obtainOrderDetail() {
    if (mOrderDetail.isEmpty()) {
      return null;
    }
    if (mOrderDetail.size() == 1) {
      return mOrderDetail.get(0);
    }
    for (OrderDetail detail: mOrderDetail) {
      if (detail != null && detail.getType() == 1) {
        return detail;
      } else {
        continue;
      }
    }
    return null;
  }

  /**
   * 司机或乘客取消订单之后要remove 当前的订单
   * @param detail
   */
  public void removeOrder(OrderDetail detail) {
    if (mOrderDetail.isEmpty()) {
      return;
    }
    for (Iterator<OrderDetail> iterator = mOrderDetail.iterator(); iterator.hasNext();) {
      OrderDetail od = iterator.next();
      if (od.getOrderId().equals(detail.getOrderId())) {
        iterator.remove();
      }
    }
  }

  /**
   * 获取当前有多少个未完成订单
   * @return
   */
  public int obtainOrdersCount() {
    return mOrderDetail.size();
  }

  /**
   * 清空所有数据
   */
  public void clearOrderDetails() {
    mOrderDetail.clear();
  }
}

package com.one.framework.net;

import android.text.TextUtils;
import com.one.framework.app.login.UserProfile;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.base.INetworkConfig;
import com.one.framework.net.model.MyTripsModel;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.model.OrderTravelling;
import com.one.framework.net.model.UserInfo;
import com.one.framework.net.request.RequestHelper;
import com.one.framework.net.response.IResponseListener;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/4/25.
 */

public class Api {
  public static String BASE_URL_HOST = "http://app.taxi.com";
  public static String BASE_URL_HOSTS = "https://app.taxi.com";
  private static String LOGIN_SEND_SMS = "/api/v2/usermgr/getverifycode.do";
  private static String LOGIN_DO_LOGIN = "/api/user/usermgr/login.do";
  private static String TRIP_TRAVELLING = "/api/chariot/trip/travelling";
  private static final String TRIP_MY_TRIPS_LIST = "/api/chariot/trip/list";
  private static final String TRIP_ORDER_DETAIL = "/api/chariot/trip/detail";
  private static String sApiUrl = BASE_URL_HOSTS;
  private static INetworkConfig sConfig;

  public static void initNetworkConfig(INetworkConfig config) {
    sConfig = config;
  }

  public static void apiUrl(String apiHost) {
    if (TextUtils.isEmpty(apiHost)) {
      sApiUrl = BASE_URL_HOSTS;
      return;
    }
    sApiUrl = apiHost;
  }

  public static int sendSms(String phone, IResponseListener<BaseObject> listener) {
    sConfig.setUserPhone(phone);
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("mobileNo", phone);
    urlParams.put("bizType", 1);
    urlParams.put("userid", "");
    return request(LOGIN_SEND_SMS, urlParams, listener, BaseObject.class);
  }

  public static int doLogin(String mobileNo, String verificode, IResponseListener<UserInfo> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("abroadVersion", 1);
    urlParams.put("mobileNo", mobileNo);
    urlParams.put("capt", verificode);
    return request(LOGIN_DO_LOGIN, urlParams, listener, UserInfo.class);
  }

  /**
   * 获取行程中订单 all projects
   * @return
   */
  public static int allTravelling(IResponseListener<OrderTravelling> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("bizType", 3); // 此处应该去掉
    return request(TRIP_TRAVELLING, urlParams, listener, OrderTravelling.class);
  }

  /**
   * 获取我的行程
   * offset 偏移量 后期改成 pageIndex pageSize 方式
   */
  public static int myTrips(String userId, int offset, int pageLimit, IResponseListener<MyTripsModel> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("userId", userId);
    urlParams.put("offset", offset);
    urlParams.put("limit", pageLimit);
    return requestGet(TRIP_MY_TRIPS_LIST, urlParams, listener, MyTripsModel.class);
  }

  /**
   * 获取订单详情
   */
  public static int tripDetail(int bizType, String oid, IResponseListener<OrderDetail> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("bizType", bizType); // 此处应该去掉
    urlParams.put("orderId", oid); // 此处应该去掉
    return request(TRIP_ORDER_DETAIL, urlParams, listener, OrderDetail.class);
  }

  /**
   * post 请求时返回请求 hashcode()
   * @param url
   * @param urlParams
   * @param <T> BaseObject 子类
   * @return
   */
  public static <T extends BaseObject> int request(String url, HashMap<String, Object> urlParams, IResponseListener<T> listener, Class<T> t) {
    StringBuilder builder = new StringBuilder(sApiUrl);
    return RequestHelper.getRequest(sConfig).requestPost(builder.append(url).toString(), urlParams, listener, t);
  }

  /**
   * Get 请求时返回请求 hashcode()
   * @param url
   * @param urlParams
   * @param <T> BaseObject 子类
   * @return
   */
  public static <T extends BaseObject> int requestGet(String url, HashMap<String, Object> urlParams, IResponseListener<T> listener, Class<T> t) {
    StringBuilder builder = new StringBuilder(sApiUrl);
    return RequestHelper.getRequest(sConfig).requestGet(builder.append(url).toString(), urlParams, listener, t);
  }

  public static void cancelRequest(int requestCode) {
    RequestHelper.cancelRequest(requestCode);
  }
}

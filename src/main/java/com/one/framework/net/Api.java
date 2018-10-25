package com.one.framework.net;

import android.text.TextUtils;
import com.one.framework.app.ads.AdsModel;
import com.one.framework.model.AutoShareModel;
import com.one.framework.model.ContactLists;
import com.one.framework.model.ContactModel;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.base.INetworkConfig;
import com.one.framework.net.model.AppConfig;
import com.one.framework.net.model.IMLoginInfo;
import com.one.framework.net.model.MyTripsModel;
import com.one.framework.net.model.OrderDetail;
import com.one.framework.net.model.OrderTravelling;
import com.one.framework.net.model.UserInfo;
import com.one.framework.net.model.WxSecretFreeModel;
import com.one.framework.net.request.RequestHelper;
import com.one.framework.net.response.IResponseListener;
import java.util.HashMap;

/**
 * Created by ludexiang on 2018/4/25.
 */

public class Api {
  public static String BASE_URL_HOST = "http://app.taxi.com";
  public static String BASE_URL_HOSTS = "https://furion-app.com";
  private static String LOGIN_SEND_SMS = "/api/user/usermgr/getverifycode.do";
  private static String LOGIN_DO_LOGIN = "/api/user/usermgr/login.do";
  private static String LOGIN_DO_LOGOUT = "/api/user/usermgr/logout.do";
  private static String TRIP_TRAVELLING = "/api/taxi/trip/travelling";
  private static String BIZ_ADS_POP = "/api/biz/ads";
  private static final String TRIP_MY_TRIPS_LIST = "/api/taxi/trip/list";
  private static final String TRIP_ORDER_DETAIL = "/api/taxi/trip/detail";

  private static final String URGENT_CONTACT = "/api/taxi/contact/add";
  private static final String UPDATE_CONTACT = "/api/taxi/contact/update";
  private static final String DELETE_CONTACT = "/api/taxi/contact/delete";
  private static final String QUERY_CONTACT = "/api/taxi/contact/select";
  private static final String APP_CONFIG = "/api/taxi/config/passenger";
  private static final String WX_OPEN_SECRET_FREE = "/api/taxi/noauthpay/enableNoauthProtoWx";
  private static final String WX_CLOSE_SECRET_FREE = "/api/taxi/noauthpay/cancelNoauthProtoWx";
  private static final String IM_LOGIN = "/api/taxi/im/loginInfo";
  private static final String AUTO_SHARE_ENABLE = "/api/taxi/autoshare/enable";
  private static final String AUTO_SHARE_DISABLE = "/api/taxi/autoshare/disable";
  private static final String AUTO_SHARE_USER_INFO = "/api/taxi/autoshare/info";
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

  public static int imLogin(IResponseListener<IMLoginInfo> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    return request(IM_LOGIN, urlParams, listener, IMLoginInfo.class);
  }

  public static int sendSms(String phone, IResponseListener<BaseObject> listener) {
    sConfig.setUserPhone(phone);
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("mobileNo", phone);
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

  public static int doLogout(String userId, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("userid", userId);
    return request(LOGIN_DO_LOGOUT, urlParams, listener, BaseObject.class);
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

  public static int bizAds(IResponseListener<AdsModel> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("bizType", 3); // 此处应该去掉
    return request(BIZ_ADS_POP, urlParams, listener, AdsModel.class);
  }

  public static int addContact(String name, String tel, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("contactName", name);
    urlParams.put("contactPhoneNo", tel);
    return request(URGENT_CONTACT, urlParams, listener, BaseObject.class);
  }

  public static int updateContact(String name, String tel, long contactId, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("contactName", name);
    urlParams.put("contactPhoneNo", tel);
    urlParams.put("contactId", contactId);
    return request(UPDATE_CONTACT, urlParams, listener, BaseObject.class);
  }

  public static int deleteContact(long uid, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("contactId", uid);
    return request(DELETE_CONTACT, urlParams, listener, BaseObject.class);
  }

  public static int queryContact(IResponseListener<ContactLists> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    return requestGet(QUERY_CONTACT, urlParams, listener, ContactLists.class);
  }

  /**
   * 自动分享紧急联系人
   */
  public static int autoShareDisable(IResponseListener<BaseObject> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    return request(AUTO_SHARE_DISABLE, urlParams, listener, BaseObject.class);
  }

  public static int autoShareEnable(String from, String to, IResponseListener<BaseObject> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("timeBegin", from + ":00");
    urlParams.put("timeEnd", to + ":00");
    return request(AUTO_SHARE_ENABLE, urlParams, listener, BaseObject.class);
  }

  public static int autoShareUserInfo(IResponseListener<AutoShareModel> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    return requestGet(AUTO_SHARE_USER_INFO, urlParams, listener, AutoShareModel.class);
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
   * app config
   * @param listener
   * @return
   */
  public static int appConfig(IResponseListener<AppConfig> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    return request(APP_CONFIG, urlParams, listener, AppConfig.class);
  }

  /**
   * 开通微信免密支付
   */
  public static int wxOpenSecretFree(IResponseListener<WxSecretFreeModel> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("client", 0); // 0 表示android
    return request(WX_OPEN_SECRET_FREE, urlParams, listener, WxSecretFreeModel.class);
  }

  /**
   * 关闭微信免密支付
   */
  public static int wxCloseSecretFree(IResponseListener<BaseObject> listener) {
    HashMap<String, Object> urlParams = new HashMap<>();
    urlParams.put("client", 0); // 0 表示android
    return request(WX_CLOSE_SECRET_FREE, urlParams, listener, BaseObject.class);
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

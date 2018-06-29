package com.one.framework.net;

public class NetConstant {
  /** 与url组合，作为文件大小的key */
  public static final String KEY_1 = "-1";
  /** 与url组合，作为文件名称的key */
  public static final String KEY_2 = "-2";

  /** 下载失败或网络异常 */
  public static final int MSG_ERROR = -1;
  /** 表示服务器无响应 */
  public static final int SOCKE_TTIMEOUT = -2;
  /** 表示链接服务器超时 */
  public static final int CONNECT_TIMEOUT = -3;
  /** 表示访问地址不存在或者网已断 */
  public static final int HTTP_HOST_CONNECT = -4;
  /** 网络链接失败 */
  public static final int ERROR_NETWORK_FAIL = -5;
  /** 用户暂停下载 */
  public static final int MSG_USR_CANCELLED = 0;

  /** 下载进度更新 */
  public static final int MSG_PROGRESS_UPDATE = 1;

  /** 下载完成 */
  public static final int MSG_DOWN_COMPLETE = 2;

  /** 多线程断点线程下载标志 */
  public static final int MSG_WHAT_MULTI_THREADS = 3;
  /** 下载完成 */
  public static final int MSG_WHAT_FINISH = 4;
  /** 连接网络超时时间 */
  public static final int CONNECT_TIME_OUT = 10 * 1000;
  /** 开启的线程数 */
  public static final int THREAD_NUM = 2;

  /*--------------服务端返回错误码---------------------*/
  /** 登录Token验证失败 */
  public static final int INVALID_TOKEN = 101;
  /** 只能文本叫车错误 */
  public static final int ONLY_TEXT_ERROR = 2008;

  /*--------------服务端数组字段---------------------*/
  /** 错误码 */
  public static String ERROR_CODE = "code";
  /** 错误描述 */
  public static String ERROR_MSG = "message";
  /** 腾讯服务器下发的errmsg */
  public static String ERROR = "error";
  /** 时间差 */
  public static String TIME_OFFSET = "timeoffset";
  /** 后端返回数据*/
  public static String DATA = "data";
  public static String OBJECT = "object";
  /** 数据返回正常 */
  public final static int OK = 0;
  /** 无数据 */
  public final static int NO_DATA = -800;
  /** 数据格式错误 */
  public static final int ERROR_INVALID_DATA_FORMAT = -900;
  /** 未完成订单*/
  public static final int ORDER_UNFINISHED = 200021;
  /** 订单已被抢 */
  public static final int ORDER_STRIVED = 1002;
  /** 订单已取消 */
  public static final int ORDER_CANCELED = 1030;
  /** token失效 */
  public static final int TOKEN_FAILED = 1011;
  public static final int ACCESS_TOKEN_INVALID=101;
  /** 异地登陆 */
  public static final int REMOTE_LOGIN = -200;
  /** 系统繁忙*/
  public static final int SYSTEM_BUSY = 10006;
}

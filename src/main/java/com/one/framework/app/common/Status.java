package com.one.framework.app.common;

import android.support.annotation.NonNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ludexiang on 2018/1/8.
 * 包含行程状态及支付状态
 */

public class Status {

  // Implementing a fromString method on an enum type
  private static final Map<String, OrderStatus> orderToEnum = new HashMap<String, OrderStatus>();

  static {
    // Initialize map from constant name to enum constant
    for (OrderStatus status : OrderStatus.values()) {
      orderToEnum.put(status.toString(), status);
    }
  }

  // Returns Status for string, or null if string is invalid
  public static OrderStatus getOrderStatus(String symbol) {
    if (orderToEnum.containsKey(symbol)) {
      return orderToEnum.get(symbol);
    }
    return OrderStatus.UNKNOW;
  }

  /**
   * 订单状态
   */
  public enum OrderStatus implements Serializable {
    UNKNOW("未知状态", 100),  // 返回的状态 OrderStatus 不包含则不做处理
    CREATE("等待应答", 0),  //新创建的订单，司机尚未接单
    RECEIVED("司机已接单", 1), //司机接单
    SET_OFF("司机已出发", 2), //司机出发，前往乘客上车地点
    READY("司机已到达", 3), //司机到达乘客上车地点
    START("行程中", 4), // 接到乘客，行程开始
    ARRIVED("待支付", 5), //到达终点
    AUTO_PAID("待支付", 6), // 已自动支付，费用未结清，待手动支付
    PAID("已支付", 7),  // 支付完成（终态)
    COMPLAINT("订单疑义", 8),
    CANCELED("已取消", 9), //订单取消（终态）
    FINISH("已完成", 10), //未支付（到达终点或者取消需要支付取消费用）？？
    REASSIGN("重新派单", 11),  // 重新派单
    CANCELED_UNPAID("未支付", 12), // 取消未支付
    CANCELED_AUTOPAID("待支付", 13), // 取消待支付
    CANCELED_PAID("已支付", 14), // 乘客取消已支付
    REFUNDING("退款中", 15), // 退款中
    REFUNDED("已退款", 16),  // 退款完成（终态）
    REFUND_FAILED("退款失败", 17),  //交易关闭（终态），如取消不需要支付费用
    CONFIRM("已完成", 18), // 顺风车确认搭乘
    DISCOUNT_REFUNDING("拼车退款", 19), // 顺风车拼车退款
    DISCOUNT_REFUNDED("拼车退款完成", 20), // 顺风车拼车退款完毕
    AUTO_PAYING("支付中", 21),//支付中，乘客支付
    CONFIRMED_PRICE("待支付", 22); //确认价格

    int mValue;
    String mStatus;

    OrderStatus(String status, int value) {
      mStatus = status;
      mValue = value;
    }

    public int getValue() {
      return mValue;
    }

    @NonNull
    public static OrderStatus fromStateCode(int code) {
      for (OrderStatus state : OrderStatus.values()) {
        if (state.mValue == code) {
          return state;
        }
      }
      return UNKNOW;
    }

    @NonNull
    public static String getStatusName(int code) {
      for (OrderStatus state : OrderStatus.values()) {
        if (state.mValue == code) {
          return state.mStatus;
        }
      }
      return UNKNOW.mStatus;
    }

    public static boolean isValid(OrderStatus state) {
      return state != null && state != OrderStatus.UNKNOW;
    }
  }
}



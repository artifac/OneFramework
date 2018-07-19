package com.one.framework.net.model

/**
 * Created by ludexiang on 2018/6/20.
 */
import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class OrderTravelling(
        @field:SerializedName("orderDetail")
        val orderDetail: List<OrderDetail>,
        @field:SerializedName("lastCancelOrder")
        val lastCancelOrder: OrderDetail
) : BaseObject()

data class OrderDetail(
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("planStartPlace")
        val startPlaceName: String,
        @field:SerializedName("planEndPlace")
        val endPlaceName: String,
        @field:SerializedName("planStartLat")
        val startLat: Double,
        @field:SerializedName("planStartLng")
        val startLng: Double,
        @field:SerializedName("planEndLat")
        val endLat: Double,
        @field:SerializedName("planEndLng")
        val endLng: Double,
        @field:SerializedName("cityCode")
        val cityCode: String,
        @field:SerializedName("driverInfo")
        val driver: OrderDriverInfo? = null,
        @field:SerializedName("taxiInfo")
        val carInfo: CarInfo? = null,
        @field:SerializedName("feeInfo")
        val feeInfo: FeeInfo,
        @field:SerializedName("status")
        val orderStatus: Int,
        @field:SerializedName("carType")
        val carType: Int,
        @field:SerializedName("vendorId")
        val vendorId: Int,
        @field:SerializedName("payType")
        val payType: Int,
        @field:SerializedName("type")
        val type: Int,
        @field:SerializedName("bizType")
        val bizType: Int,
        @field:SerializedName("orderCreateTime")
        val orderCreateTime: Long,
        @field:SerializedName("waitConfigTime")
        val waitConfigTime: Int,
        @field:SerializedName("currentServerTime")
        val currentServerTime: Long
) : BaseObject()

data class OrderDriverInfo(
        @field:SerializedName("driverId")
        val driverId: String,
        @field:SerializedName("name")
        val driverName: String,
        @field:SerializedName("driverIcon")
        val driverIcon: String,
        @field:SerializedName("driverReceivedCount")
        val driverReceiveOrderCount: Long,
        @field:SerializedName("driverRate")
        val driverStar: Float? = null,
        @field:SerializedName("phoneNo")
        val driverTel: String,
        @field:SerializedName("driverCar")
        val driverCar: String,
        @field:SerializedName("driverCarColor")
        val driverCarColor: String,
        @field:SerializedName("driverCompany")
        val driverCompany: String
)

data class FeeInfo(
        @field:SerializedName("actualTime")
        val actualTime: Long,
        @field:SerializedName("actualDistance")
        val actualDistance: Long,
        @field:SerializedName("totalMoney")
        val totalMoney: Int,// 分
        @field:SerializedName("actualPayMoney")
        val actualPayMoney: Int,
        @field:SerializedName("unPayMoney")
        val unPayMoney: Int,
        @field:SerializedName("discountMoney")
        val discountMoney: Int,// 券
        @field:SerializedName("refundMoney")
        val refundMoney: Int
)

data class CarInfo(
        @field:SerializedName("payForPickUp")
        val pay4PickUp : Int,
        @field:SerializedName("riderTags")
        val marks: List<String>,
        @field:SerializedName("dispatchFee")
        val tip : Int,
        @field:SerializedName("feedback")
        val feedback: Int,
        @field:SerializedName("driverComment")
        val evaluate: Evaluate
)

data class Evaluate(
        @field:SerializedName("userId")
        val userId : String,
        @field:SerializedName("bizType")
        val bizType : Int,
        @field:SerializedName("driverId")
        val driverId : String,
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("content")
        val content: String? = "",
        @field:SerializedName("tags")
        val tags: String?,
        @field:SerializedName("star")
        val star : Int
)
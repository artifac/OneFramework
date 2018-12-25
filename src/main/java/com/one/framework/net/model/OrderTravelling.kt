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
        val cityCode: String ? = null,
        @field:SerializedName("driverInfo")
        val driver: OrderDriverInfo? = null,
        @field:SerializedName("taxiInfo")
        val carInfo: CarInfo? = null,
        @field:SerializedName("feeInfo")
        val feeInfo: FeeInfo? = null,
        @field:SerializedName("status")
        var orderStatus: Int,
        @field:SerializedName("carType")
        val carType: Int,
        @field:SerializedName("vendorId")
        val vendorId: Int,
        @field:SerializedName("payType")
        val payType: Int,
        @field:SerializedName("type")
        val type: Int = 1, // 1 --> now 2 --> book
        @field:SerializedName("bizType")
        val bizType: Int,
        @field:SerializedName("orderCreateTime")
        val orderCreateTime: Long,
        @field:SerializedName("bookTime")
        val bookTime: Long,
        @field:SerializedName("waitConfigTime")
        val waitConfigTime: Int,
        @field:SerializedName("currentServerTime")
        val currentServerTime: Long,
        @field:SerializedName("cancelType")
        val cancelType: Int = -1,
        @field:SerializedName("cancelReason")
        val cancelReason: String? = null,
        @field:SerializedName("driverImAccount")
        val driverIMUserName: String? = null,
        @field:SerializedName("passengerImAccount")
        val passengerIMUserName: String? = null
) : BaseObject()

data class OrderDriverInfo(
        @field:SerializedName("driverId")
        val driverId: String? = null,
        @field:SerializedName("name")
        val driverName: String? = null,
        @field:SerializedName("driverIcon")
        val driverIcon: String? = null,
        @field:SerializedName("carpoolTimes") // 司机服务次数
        val driverReceiveOrderCount: Int? = 0,
        @field:SerializedName("driverRate")
        val driverStar: Float = 4f,
        @field:SerializedName("phoneNo")
        val driverTel: String? = null,
        @field:SerializedName("modelName")
        val driverCar: String? = null,
        @field:SerializedName("driverCarColor")
        val driverCarColor: String? = null,
        @field:SerializedName("company")
        val driverCompany: String? = null,
        @field:SerializedName("licenseNo")
        val driverCarNo: String? = null,
        @field:SerializedName("realDriveAge") // 驾龄
        val driverDrivingAge: Int = 0,
        @field:SerializedName("carType")
        val driverCarType: String? = null
) : BaseObject()

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
) : BaseObject()

data class CarInfo(
        @field:SerializedName("payForPickUp")
        val pay4PickUp: Int,
        @field:SerializedName("riderTags")
        val marks: List<String>,
        @field:SerializedName("dispatchFee")
        val tip: Int,
        @field:SerializedName("feedback")
        val feedback: Int,
        @field:SerializedName("driverComment")
        val evaluate: Evaluate?
) : BaseObject()

data class Evaluate(
        @field:SerializedName("userId")
        val userId: String,
        @field:SerializedName("bizType")
        val bizType: Int,
        @field:SerializedName("driverId")
        val driverId: String,
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("content")
        val content: String? = "",
        @field:SerializedName("tags")
        val tags: String?,
        @field:SerializedName("star")
        val star: Int
) : BaseObject()
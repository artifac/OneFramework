package com.one.framework.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class AppConfig(
        @field:SerializedName("taxiTypeInfo")
        val carTypes: List<CarType>,
        @field:SerializedName("entranceConfigInfos")
        val entrance: List<Entrance>
) : BaseObject()

data class CarType(
        @field:SerializedName("id")
        val carId: Int,
        @field:SerializedName("name")
        val carName: String,
        @field:SerializedName("startPrice")
        val carStartPrice: Int,
        @field:SerializedName("normalIcon3X")
        val carNormalIcon: String,
        @field:SerializedName("selectedIcon3X")
        val carSelectIcon: String,
        @field:SerializedName("isSelected")
        var isSelected: Boolean
) {
        override fun toString(): String {
                return "carId $carId carName $carName startPrice $carStartPrice"
        }
}

data class Entrance(
        @field:SerializedName("tab")
        val entranceTab: String,
        @field:SerializedName("tabPosition")
        val entranceTabPosition: Int,
        @field:SerializedName("tabBiz")
        val entranceTabBiz: String,
        @field:SerializedName("isRedPoint")
        val isRedPoint: Boolean,
        @field:SerializedName("tabBizType")
        val entranceTabBizType: Int,
        @field:SerializedName("tabIsSelected")
        val entranceTabSelected: Boolean,
        @field:SerializedName("isDispatchFeeEnable")
        val isDispatchFeeEnable: Boolean,
        @field:SerializedName("dispatchFee")
        val dispatchFee: List<Int>

)
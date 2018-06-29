package com.one.framework.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

/**
 * Created by ludexiang on 2018/6/22.
 */
data class MyTripsModel(
        @field:SerializedName("tripList")
        val tripList: MutableList<Trip>
) : BaseObject()

data class Trip(
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("status")
        val tripStatus: Int,
        @field:SerializedName("startAddress")
        val tripStart: String,
        @field:SerializedName("endAddress")
        val tripEnd: String,
        @field:SerializedName("startLat")
        val tripStartLat: Double,
        @field:SerializedName("startLng")
        val tripStartLng: Double,
        @field:SerializedName("endLat")
        val tripEndLat: Double,
        @field:SerializedName("endLng")
        val tripEndLng: Double,
        @field:SerializedName("createTime")
        val tripTime: Long,
        @field:SerializedName("bizType")
        val tripType: Int
)
package com.one.pay.model

import com.google.gson.annotations.SerializedName

data class PayInfo(
        @field:SerializedName("tradetype")
        val type: String,
        @field:SerializedName("mchid")
        val mchid: String,
        @field:SerializedName("package")
        val packageName: String,
        @field:SerializedName("appid")
        val appId: String,
        @field:SerializedName("sign")
        val sign: String,
        @field:SerializedName("partnerId")
        val partnerId: String,
        @field:SerializedName("prepayid")
        val prePayId: String,
        @field:SerializedName("deviceinfo")
        val deviceInfo: String,
        @field:SerializedName("mwebUrl")
        val payUrl: String? = null,
        @field:SerializedName("noncestr")
        val noncestr: String,
        @field:SerializedName("timestamp")
        val timeStamp: Long,
        @field:SerializedName("payChannel")
        val payChannel : Int
) {
}
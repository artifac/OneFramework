package com.one.framework.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class MyWalletModel(
    @field:SerializedName("balanceInfo")
    val balanceInfo : BalanceInfo,
    @field:SerializedName("userCouponInfo")
    val userCouponInfo: CouponInfo,
    @field:SerializedName("couponDetailViewUrl")
    val couponDetailViewUrl : String,
    @field:SerializedName("userWalletDetailViewUrl")
    val userWalletDetailViewUrl: String
) : BaseObject()

data class BalanceInfo(
        @field:SerializedName("balance")
        val balance: Int,
        @field:SerializedName("userId")
        val userId: String
) : BaseObject()

data class CouponInfo(
        @field:SerializedName("validCountCount")
        val validCount: Int,
        @field:SerializedName("inValidCountCount")
        val invalidCount: Int
) : BaseObject()

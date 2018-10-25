package com.one.pay.model

import com.google.gson.annotations.SerializedName

/**
 * Created by ludexiang on 2018/6/21.
 */
data class PayModel(
        @field:SerializedName("totalFee")
        val totalFee: Float,
        @field:SerializedName("feeDetailUrl")
        val feeDetail: String,
        @field:SerializedName("voucherUrl")
        val voucherUrl: String,
        @field:SerializedName("payList")
        val payList: List<PayList>
)

data class PayList(
        @field:SerializedName("payItemIcon")
        val payItemIcon: String?,
        @field:SerializedName("payItemTitle")
        val payItemTitle: String,
        @field:SerializedName("payItemSelected")
        var payItemSelected: Boolean = false,
        @field:SerializedName("payItemIconId")
        val payItemIconRes: Int = 0,
        @field:SerializedName("payItemType")
        val payItemType: Int = 0
) {
    fun setSelected(select: Boolean) {
        payItemSelected = select
    }

}
package com.one.framework.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.api.annotation.ServiceProvider
import com.one.framework.net.base.BaseObject

/**
 * Created by ludexiang on 2018/6/17.
 */
data class UserInfo(
        @field:SerializedName("userid")
        val userId: String,
        @field:SerializedName("username")
        val userName: String? = null,
        @field:SerializedName("idCode")
        val idCode: String? = null,
        @field:SerializedName("mobileno")
        val mobileNo: String,
        @field:SerializedName("userimage")
        val userImage: String? = null,
        @field:SerializedName("pushkey")
        val pushKey: String,
        @field:SerializedName("authtoken")
        val token: String,
        @field:SerializedName("rsacode")
        val rsaCode: String,
        @field:SerializedName("nickname")
        val nickName: String? = null,
        @field:SerializedName("tag")
        val tag: Int = 0,
        @field:SerializedName("progress")
        val progress: Int = 0,
        @field:SerializedName("credits")
        val credits: Int = 0,
        @field:SerializedName("img")
        val img: String? = null,
        @field:SerializedName("nation")
        val nation: String? = null,
        @field:SerializedName("special_progress")
        val specialProgress: Int = 0,
        @field:SerializedName("comment")
        val comment: String? = null,
        @field:SerializedName("isfirstshare")
        val isFirstShare: String? = null,
        @field:SerializedName("deposit")
        val deposit: Int = 0,
        @field:SerializedName("requestedDeposit")
        val requestedDeposit: Int = 0,
        @field:SerializedName("country")
        val country: Int = 0,
        @field:SerializedName("currency")
        val currency: Int = 0,
        @field:SerializedName("h5Url")
        val h5Url: String? = null,
        @field:SerializedName("gender")
        val gender: Int = 0,
        @field:SerializedName("level")
        val level: Int = 0,
        @field:SerializedName("needCollectConsent")
        val needCollectConsent: Boolean = false,
        @field:SerializedName("isNew")
        val isNew: Boolean = false,
        @field:SerializedName("email")
        val email: String? = null,
        @field:SerializedName("passwordState")
        val passwordState : Int = 0
) : BaseObject()
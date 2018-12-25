package com.one.framework.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class InviteModel(
        @field:SerializedName("allCount")
        val allCount: Int = 0,
        @field:SerializedName("remainCount")
        val remainCount: Int = 0,
        @field:SerializedName("inviteCode")
        val inviteCode: String,
        @field:SerializedName("url")
        val inviteUrl: String,
        @field:SerializedName("content")
        val inviteContent: String,
        @field:SerializedName("title")
        val inviteTitle: String
): BaseObject()
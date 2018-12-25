package com.one.framework.push

import com.google.gson.annotations.SerializedName

data class PushMsgModel(
        @field:SerializedName("ds") // description
        val description: String,
        @field:SerializedName("tl") // title
        val title: String,
        @field:SerializedName("ty") // typeId
        val msgId: Int,
        @field:SerializedName("ob") // deeplink
        val deepLink: String?
)
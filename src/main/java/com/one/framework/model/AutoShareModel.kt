package com.one.framework.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class AutoShareModel(
        @field:SerializedName("uid")
        val ui: String,
        @field:SerializedName("enable")
        val enable: Int,
        @field:SerializedName("timeBegin")
        val timeBegin: String,
        @field:SerializedName("timeEnd")
        val timeEnd: String
) : BaseObject()
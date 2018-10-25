package com.one.framework.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class IMLoginInfo(
        @field:SerializedName("userName")
        val userName: String,
        @field:SerializedName("password")
        val password : String
): BaseObject()
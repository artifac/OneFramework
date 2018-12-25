package com.one.framework.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class LoginModel(
        @field:SerializedName("isNew")
        val isNewUser: Boolean
): BaseObject()
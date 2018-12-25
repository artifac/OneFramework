package com.one.framework.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class DownloadModel(
        @field:SerializedName("version")
        val version: String,
        @field:SerializedName("lowestVersion")
        val lowestVersion: String,
        @field:SerializedName("forceUpgrade")
        val forceUpgrade: Boolean,
        @field:SerializedName("url")
        val downloadUrl : String,
        @field:SerializedName("type")
        val type: String,
        @field:SerializedName("flag")
        val flag: Boolean,
        @field:SerializedName("description")
        val description: String
) : BaseObject()
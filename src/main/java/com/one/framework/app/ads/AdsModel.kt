package com.one.framework.app.ads

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class AdsModel(
    @field:SerializedName("popupMarketing")
    var ads: List<AdsBean>,
    @field:SerializedName("shareInfo")
    val shareInfo: ShareInfo? = null
) : BaseObject() {
    override fun toString(): String {
        return super.toString()
    }
}

data class ShareInfo(
    @field:SerializedName("shareIcon")
    val shareIcon: String? = null,
    @field:SerializedName("shareTitle")
    val shareTitle: String? = null,
    @field:SerializedName("share2ChatContent")
    val shareChatContent: String? = null,
    @field:SerializedName("share2MomentsContent")
    val shareMomentsContent: String? = null
)

data class AdsBean(
    /**
     * type -- 1 不展示分享入口
     * type -- 2 展示分享入口
     */
    @field:SerializedName("type")
    var type: Int,
    @field:SerializedName("marketingId")
    var marketingId: String?,
    @field:SerializedName("backImg")
    val imageUrl: String? = null,
    @field:SerializedName("redirectUrl")
    val redirectUrl: String? = null,
    @field:SerializedName("showStyle")
    val style: Int = 1, // 1 弹窗 2 气泡
    @field:SerializedName("bubbleIcon")
    val bubbleIcon: String? = null,
    @field:SerializedName("bubbleStartColor")
    val bubbleStartColor: String? = null,
    @field:SerializedName("bubbleTimeOutSeconds")
    val bubbleTimeOut: Int,// 单位秒
    @field:SerializedName("bubbleWord")
    val bubbleWord: String? = null,
    @field:SerializedName("bubbleWordColor")
    val bubbleWordColor: String? = null,
    @field:SerializedName("bubbleEndColor")
    val bubbleEndColor: String? = null,
    @field:SerializedName("bizType")
    val bizType: Int
)
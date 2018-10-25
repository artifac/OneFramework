package com.one.framework.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class ContactLists(
        @field:SerializedName("list")
        val contactLists: List<ContactModel>
): BaseObject()

data class ContactModel(
        @field:SerializedName("contactId") // 手机联系人中id
        val id: Int? = 0,
        @field:SerializedName("contactName")
        var name: String? = null,
        @field:SerializedName("contactPhoneNo")
        var phoneNumber: String? = null,
        @field:SerializedName("id")
        var contactId: Long? = 0
) : BaseObject()
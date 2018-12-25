package com.trip.taxi.utils

import android.content.Context
import com.one.framework.app.login.UserProfile
import com.one.framework.utils.WebpageQueryHelper

object H5Page {
    var ENV = ""
//    var MESSAGE_DETAIL = String.format(HOST + "/message/detail", ENV)
//    var HELP = String.format(HOST + "/help", ENV)
//    var TRIP_CANCEL = String.format(HOST + "/cancel", ENV)


    private var host = "https://%staxi.com"
    private var m2host = "https://%sm2.com"
    fun initEnv(env: String) {
        ENV = env + "-"
    }

    public fun login(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/login", ENV), null)
    }

    public fun register(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/register", ENV), null)
    }

    public fun mine(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/mine", ENV), null)
    }

    public fun withdrawHelp(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/help", ENV), null)
    }

    public fun cannotWithDrawReason(): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("key", "4")
        return WebpageQueryHelper.append(String.format(host + "/taxi/help/detail", ENV), map)
    }

    public fun messageDetail(id: String, messageType: String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("id", id)

        map.put("msgType", messageType)
        return WebpageQueryHelper.append(String.format(host + "/taxi/message/detail", ENV), map)
    }

    public fun protocol(): String {
        return WebpageQueryHelper.append(String.format(m2host + "/app/help/zh/taxiDriverProtocol.html", ENV), null)
    }

    fun lawAndPrivatePolicy(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/help/protocol", ENV), null)
    }

    public fun orderCancel(orderId: String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("orderId", orderId)
        map.put("bizeType", "3")
        return WebpageQueryHelper.append(String.format(host + "/taxi/cancel", ENV), map)
    }

    public fun bindCard(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/register/bankcard", ENV), null)
    }

    public fun certification(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/register/bankcard/2", ENV), null)
    }

    public fun accountBalance(accessToken: String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("accesstoken", accessToken)
        return WebpageQueryHelper.append(String.format(host + "/api/passenger/taxi/wallet/remain", ENV), map)
    }

    public fun accountBalanceDetail(accessToken: String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("accesstoken", accessToken)
        return WebpageQueryHelper.append(String.format(host + "/api/passenger/taxi/wallet/remain_detail", ENV), map)
    }

    public fun changeAvator(accessToken: String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("accesstoken", accessToken)
        return WebpageQueryHelper.append(String.format(host + "/api/passenger/taxi/mine/profile", ENV), map)
    }

    public fun feedback(uid : String, token : String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("userId", uid)
        map.put("accesstoken", token)
        return WebpageQueryHelper.append(String.format(host + "/api/passenger/taxi/feedback/feedback", ENV), map)
    }

    public fun serviceInfo(uid : String, token : String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("userId", uid)
        map.put("accesstoken", token)
        return WebpageQueryHelper.append(String.format(host + "/api/passenger/taxi/help/service", ENV), map)
    }

    /**
     * deeplink 默认加上 accesstoken and userId
     */
    fun createDeeplink(context: Context, deeplink: String): String {
        var map: MutableMap<String, String> = java.util.HashMap()
        map.put("accesstoken", UserProfile.getInstance(context).tokenValue)
        map.put("userId", UserProfile.getInstance(context).userId)
        return WebpageQueryHelper.append(deeplink, map)
    }
}
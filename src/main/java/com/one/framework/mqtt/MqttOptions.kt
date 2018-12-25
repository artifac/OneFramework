package com.one.framework.mqtt

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import com.one.framework.app.login.UserProfile
import com.one.framework.mqtt.IMqttOptions

/**
 * MQTT：登录：
 * username:uid
 * password:token
 * topic 格式： passenger/carTrip/_*_ _*_可替换
 */

class MqttOptions(var context: Context) : IMqttOptions {
    private val product = "passenger"
    override fun buildPassWord(): String {
        return UserProfile.getInstance(context).tokenValue
    }

    override fun buildClientId(): String {
        val ANDROID_ID = Settings.System.getString(context.contentResolver, Settings.System.ANDROID_ID)
        return (if (isDebug()) "internal" else product) + "-" + ANDROID_ID
    }

    override fun buildTopic(topic: String?): String {
        return if (TextUtils.isEmpty(topic)) "passenger/carTrip/status" else "passenger/carTrip/$topic"
    }

    override fun buildUrl(environment: String?): String {
        return if (TextUtils.isEmpty(environment)) "tcp://mqtt.furion.io:1883" else "tcp://mqtt.$environment.io:1883"
    }

    override fun buildUserName(): String {
        return UserProfile.getInstance(context).userId
    }

    override fun isDebug(): Boolean {
        return false
    }

}
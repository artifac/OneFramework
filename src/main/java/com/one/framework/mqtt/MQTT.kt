package com.one.framework.mqtt

import android.content.Context
import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject

data class MqttConfig(val mqttOptions: IMqttOptions) {
    val qos = 1
}

data class Message(val topic: String?, val message: ByteArray?)

typealias Callback = (topic: String?, msg: ByteArray?) -> Unit

interface IMqttOptions {
    fun buildUserName(): String
    fun buildPassWord(): String
    fun buildClientId(): String
    fun buildTopic(topic: String?): String
    fun buildUrl(environment: String?): String
    fun isDebug(): Boolean
}

interface ILongLink {

    fun init(context: Context, config: MqttConfig)

    fun subscribe(topics: String?)

    fun unSubscribe(topics: String?)

    fun listSubscribe(): List<String>

    fun start()

    fun stop()

    fun addCallback(callback: Callback)

    fun removeCallback(callback: Callback)

    fun getMessageSubject(): PublishSubject<Message>

    fun publish(message: Message, retained: Boolean = false)

    fun isConnected(): Boolean
}


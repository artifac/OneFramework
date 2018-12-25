package com.one.framework.mqtt

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.one.framework.log.Logger
import io.reactivex.subjects.PublishSubject
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

object MqttLink : ILongLink {

    private val TAG = MqttLink::class.java.simpleName
    private val callbacks = HashSet<Callback>()
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var config: MqttConfig

    private val subscriptions = HashSet<String>()
    @SuppressLint("StaticFieldLeak")
    private lateinit var client: MqttClient

    private val subject = PublishSubject.create<Message>()

    override fun listSubscribe(): List<String> = subscriptions.toList()

    override fun publish(message: Message, retained: Boolean) {
        if (client != null) {
            client.publish(message.topic, message.message, config.qos, retained)
        }
    }

    override fun isConnected(): Boolean = client.isConnected

    override fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    override fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    override fun getMessageSubject(): PublishSubject<Message> = subject

    override fun init(context: Context, config: MqttConfig) {
        this.config = config
        client = MqttClient(config.mqttOptions.buildUrl(""), config.mqttOptions.buildClientId(), MemoryPersistence())
        client.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Logger.d("MQTT", "messageArrived $topic ${message}")
                handler.post {
                    subject.onNext(Message(topic, message?.payload)) // 发布消息
                    callbacks.forEach { it(topic, message?.payload) }
                }
            }

            override fun connectionLost(cause: Throwable?) {
                Logger.d("MQTT", "connectionLost $cause ")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })
        if (!client.isConnected) {
            client.connect(buildOption())
        }
    }

    override fun subscribe(topics: String?) {
        if (client != null && client.isConnected) {
            client.subscribe(config.mqttOptions.buildTopic(topics), config.qos)
            subscriptions.add(config.mqttOptions.buildTopic(topics))
        }
    }

    override fun start() {
    }

    override fun unSubscribe(topics: String?) {
        if (client.isConnected) {
            client.unsubscribe(config.mqttOptions.buildTopic(topics))
            subscriptions.remove(config.mqttOptions.buildTopic(topics))
        }
    }

    override fun stop() {
        if (client.isConnected) {
            client.disconnect()
        } else {
        }
    }

    private fun buildOption(): MqttConnectOptions {
        var option = MqttConnectOptions()
        option.isAutomaticReconnect = true // 是否重连
        option.isCleanSession = false      // 是否清除缓存
        option.userName = if (TextUtils.isEmpty(config.mqttOptions.buildUserName())) "userName" else config.mqttOptions.buildUserName()
        option.password = config.mqttOptions.buildPassWord().toCharArray()
        option.keepAliveInterval = 60
        option.connectionTimeout = 10      // 超时时间
        Logger.d(TAG, "buildOption ${option.isAutomaticReconnect} ${option.userName} ${option.password} ${option.isAutomaticReconnect}")
        return option
    }
}
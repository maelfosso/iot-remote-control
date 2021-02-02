package com.maelfosso.bleck.iotremotecontrol.bluetooth

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.reflect.KFunction1

class WrappedArduinoBluetoothDevice(private val device: ArduinoBluetoothDevice) {
    private val compositeDisposable = CompositeDisposable()

    private var messageReceivedListener: OnMessageReceivedListener? = null
    private var messageSentListener: OnMessageSentListener? = null
    private var errorListener: OnErrorListener? = null

    init {
        compositeDisposable.add(
            device.openMessageStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { messageReceivedListener?.onMessageReceived(it)},
                    { errorListener?.onError(it) }
                )
        )
    }

    fun sendMessage(message: String) {
        device.checkNotClosed()

        compositeDisposable.add(
            device.send(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { messageSentListener?.onMessageSent(message) },
                    { errorListener?.onError(it) }
                )
        )
    }

    fun close() {
        compositeDisposable.dispose()
    }

    fun setListeners(
        messageReceivedListener: OnMessageReceivedListener,
        messageSentListener: OnMessageSentListener?,
        errorListener: OnErrorListener?
    ) {
        this.messageReceivedListener = messageReceivedListener
        this.messageSentListener = messageSentListener
        this.errorListener = errorListener
    }

    interface  OnMessageReceivedListener {
        fun onMessageReceived(message: String)
    }

    interface OnMessageSentListener {
        fun onMessageSent(message: String)
    }

    interface OnErrorListener {
        fun onError(error: Throwable)
    }
}
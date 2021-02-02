package com.maelfosso.bleck.iotremotecontrol.bluetooth

import android.bluetooth.BluetoothSocket
import android.text.TextUtils
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicBoolean

class ArduinoBluetoothDevice constructor(
    private val macAddress: String,
    private val socket: BluetoothSocket,
//    private val charset: Charset
){
    val closed = AtomicBoolean(false)
    val outputStream: OutputStream = socket.outputStream
    val inputStream: InputStream = socket.inputStream

    private var wrapper: WrappedArduinoBluetoothDevice? = null

    fun openMessageStream(): Flowable<String> {
        checkNotClosed()

        return Flowable.create({ emitter ->
            val reader = BufferedReader(InputStreamReader(inputStream)) //, charset))
            while (!emitter.isCancelled && !closed.get()) {
                synchronized(inputStream) {
                    try {
                        val receivedString = reader.readLine()
                        if (!TextUtils.isEmpty(receivedString)) {
                            emitter.onNext(receivedString)
                        }
                    } catch (e: Exception) {
                        if (!emitter.isCancelled && !closed.get()) {
                            emitter.onError(e)
                        }
                    }
                }
            }
            emitter.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

    fun send(message: String): Completable {
        checkNotClosed()

        return Completable.fromAction {
            synchronized(outputStream) {
                if (!closed.get()) outputStream.write(message.toByteArray()) // toByteArray(charset))
            }
        }
    }

    fun close() {
        if (!closed.get()) {
            closed.set(true)
            inputStream.close()
            outputStream.close()
            socket.close()
        }

        wrapper?.close()
        wrapper = null
    }

    fun checkNotClosed() {
        check(!closed.get()) { "Device connection closed" }
    }

    fun toWrapped() : WrappedArduinoBluetoothDevice {
        checkNotClosed()

        wrapper?.let { return it }
        val newWrapper = WrappedArduinoBluetoothDevice(this)
        wrapper = newWrapper
        return newWrapper
    }
}
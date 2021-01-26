package com.maelfosso.bleck.iotremotecontrol.bluetooth

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WrappedArduinoBluetoothDevice(val device: ArduinoBluetoothDevice) {
    private val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable.add(
            device.openMessageStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun sendMessage(message: String) {
        device.checkNotClosed()

        compositeDisposable.add(
            device.send(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun close() {
        compositeDisposable.dispose()
    }
}
package com.maelfosso.bleck.iotremotecontrol

import android.app.Application
import android.bluetooth.BluetoothAdapter
import com.maelfosso.bleck.iotremotecontrol.bluetooth.ArduinoBluetoothDevice
import io.reactivex.rxjava3.core.Single
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

class IOTApplication: Application() {

    companion object {
        val BTD_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        //    val charset: Charset = StandardCharsets.UTF_8
        var arduinoDevice: ArduinoBluetoothDevice? = null

        fun openConnectionToArduinoDevice(
            bluetoothAdapter: BluetoothAdapter,
            mac: String): Single<ArduinoBluetoothDevice> {

            return if (arduinoDevice != null) {
                Single.just(arduinoDevice)
            } else {
                Single.fromCallable {
                    try {
                        val device = bluetoothAdapter.getRemoteDevice(mac)
                        val socket = device.createInsecureRfcommSocketToServiceRecord(BTD_UUID)
                        bluetoothAdapter.cancelDiscovery()
                        socket.connect()

                        arduinoDevice = ArduinoBluetoothDevice(mac, socket) //, charset)

                        return@fromCallable arduinoDevice
                    } catch (e: Exception) {
                        throw  e
                    }
                }
            }
        }

        fun close() {
            arduinoDevice?.close()
        }
    }

}
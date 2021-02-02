package com.maelfosso.bleck.iotremotecontrol

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.util.Log
import android.widget.Toast
import com.maelfosso.bleck.iotremotecontrol.bluetooth.ArduinoBluetoothDevice
import com.maelfosso.bleck.iotremotecontrol.bluetooth.WrappedArduinoBluetoothDevice
import io.reactivex.rxjava3.core.Single
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.coroutines.coroutineContext

class IOTApplication: Application() {

    companion object {
        val TAG: String = javaClass.name

        val BTD_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        //    val charset: Charset = StandardCharsets.UTF_8
        lateinit var arduinoDevice: ArduinoBluetoothDevice
        var wrappedArduinoDevice: WrappedArduinoBluetoothDevice? = null
//            get() = arduinoDevice.toWrapped()

        private var _bluetoothAdapter: BluetoothAdapter? = null // BluetoothAdapter.getDefaultAdapter()
//            get() {
//            if (field == null) {
//                _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//            }
//            return field
//        }
//        val bluetoothAdapter = _bluetoothAdapter!!

        fun getBluetoothAdapter(): BluetoothAdapter {
            if (_bluetoothAdapter == null) {
                _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                Log.d(TAG, "BAdapter was null ${_bluetoothAdapter == null}")
            }

            return _bluetoothAdapter!!
        }

        fun openConnectionToArduinoDevice(
//            bluetoothAdapter: BluetoothAdapter,
            mac: String): Single<ArduinoBluetoothDevice> {

            return Single.fromCallable {
                try {
                    val device = getBluetoothAdapter().getRemoteDevice(mac)
                    val socket = device.createInsecureRfcommSocketToServiceRecord(BTD_UUID)
                    getBluetoothAdapter().cancelDiscovery()
                    socket.connect()

                    arduinoDevice = ArduinoBluetoothDevice(mac, socket) //, charset)
                    wrappedArduinoDevice = arduinoDevice.toWrapped()

                    return@fromCallable arduinoDevice
                } catch (e: Exception) {
                    throw  e
                }
            }

//            return if (arduinoDevice !== null) {
//                Single.just(arduinoDevice)
//            } else {
//                Single.fromCallable {
//                    try {
//                        val device = getBluetoothAdapter().getRemoteDevice(mac)
//                        val socket = device.createInsecureRfcommSocketToServiceRecord(BTD_UUID)
//                        getBluetoothAdapter().cancelDiscovery()
//                        socket.connect()
//
//                        arduinoDevice = ArduinoBluetoothDevice(mac, socket) //, charset)
//                        wrappedArduinoDevice = arduinoDevice.toWrapped()
//
//                        return@fromCallable arduinoDevice
//                    } catch (e: Exception) {
//                        throw  e
//                    }
//                }
//            }
        }



        fun close() {
            arduinoDevice?.close()
        }
    }

}
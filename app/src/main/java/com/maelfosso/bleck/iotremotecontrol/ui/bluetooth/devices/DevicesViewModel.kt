package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DevicesViewModel (
            bluetoothAdapter: BluetoothAdapter,
            application: Application
        ) : ViewModel() {


    val devices = bluetoothAdapter.bondedDevices.filterNotNull().toList()

//    lateinit var devices: List<BluetoothDevice>  // emptyList() // listOf<BluetoothDevice>()
//    var devices = mutableListOf<BluetoothDevice>()
//    var devices = bluetoothAdapter.bondedDevices.map {
//        it?.let {
//            return@let it
//        }
//    }.toList()

    init {
//        initializeDevices()
        Log.d("DEVICESVIEWMODEL", devices.toString());
//        bluetoothAdapter.bondedDevices.forEach {
//            it?.let {
//                devices.add(it)
//            }
//        }
//        for( dev in bluetoothAdapter.bondedDevices) {
//            devices. = dev
//        }
    }

    private fun initializeDevices() {
        viewModelScope.launch {

        }
    }
}
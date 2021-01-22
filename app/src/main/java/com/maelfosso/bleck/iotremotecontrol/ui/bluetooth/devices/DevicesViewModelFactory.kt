package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices

import android.app.Application
import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class DevicesViewModelFactory(
        private val bluetoothAdapter: BluetoothAdapter,
        private val application: Application
    ): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevicesViewModel::class.java)) {
            return DevicesViewModel(bluetoothAdapter, application) as T
        }

        throw  IllegalArgumentException("Unknown ViewModel Class")
    }

}
package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices

import android.app.Application
import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maelfosso.bleck.iotremotecontrol.IOTApplication
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DevicesViewModel () : ViewModel() {


    private val bluetoothAdapter = IOTApplication.getBluetoothAdapter()
    val pairedDevices = MutableLiveData<MutableList<Device>>()
    val discoveredDevices = MutableLiveData<MutableList<Device>>()

    init {
        pairedDevices.postValue(
            bluetoothAdapter.bondedDevices
                .filterNotNull()
                .toMutableList()
                .map { Device(it.name, it.address, false) }.toMutableList()
        )
//            .map { Device(it.name, it.address, false) }
    }

    fun onClick(address: String, onComplete: () -> Unit, onError: (it: Throwable) -> Unit) {
        IOTApplication.openConnectionToArduinoDevice(address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    pairedDevices.value?.forEachIndexed { index, device ->
                        pairedDevices.value!![index].isConnectedTo = device.address === address
//                        devices.value?.get(index).isConnectedTo [index].isConnectedTo = device.address === address
                    }

                    onComplete()
                },
                {
                    onError(it)
                }
            )


//        val device = _bluetoothAdapter.getRemoteDevice(address)
//
//        try {
//            bluetoothSocket = device.createRfcommSocketToServiceRecord(DevicesListFragment.MY_UUID)
//            bluetoothSocket.connect()
//            Log.d(DevicesListFragment.TAG, "Connected to device - ${device.name} - ${device.address}")
//        } catch (e: IOException) {
//            Log.e(DevicesListFragment.TAG, "Error when creating socket . $e")
//            bluetoothSocket.close()
//        }

//        val mHandler = Handler(Looper.getMainLooper()) {
//            if (it.what == ConnectedThread.RESPONSE_MESSAGE) {
//                val txt = it.obj as String
//                if
//            }
//        }
    }

    class Device constructor(val name: String, val address: String, var isConnectedTo: Boolean = false) {

    }
}
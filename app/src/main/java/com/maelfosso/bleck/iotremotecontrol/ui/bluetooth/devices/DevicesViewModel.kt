package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maelfosso.bleck.iotremotecontrol.IOTApplication
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class DevicesViewModel (
            bluetoothAdapter: BluetoothAdapter,
            application: Application
        ) : ViewModel() {


    private val _bluetoothAdapter = bluetoothAdapter
    val devices = MutableLiveData<MutableList<Device>>()

    init {
        devices.postValue(
            bluetoothAdapter.bondedDevices
                .filterNotNull()
                .toMutableList()
                .map { Device(it.name, it.address, false) }.toMutableList()
        )
//            .map { Device(it.name, it.address, false) }
    }

    fun onClick(address: String, onComplete: () -> Unit, onError: () -> Unit) {
        IOTApplication.openConnectionToArduinoDevice(_bluetoothAdapter, address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    devices.value?.forEachIndexed {index, device ->
                        devices.value!![index].isConnectedTo = device.address === address
//                        devices.value?.get(index).isConnectedTo [index].isConnectedTo = device.address === address
                    }

                    onComplete()
                },
                {
                    onError()
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
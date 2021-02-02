package com.maelfosso.bleck.iotremotecontrol.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices.DevicesViewModel
import java.lang.IllegalArgumentException

class BoardViewModelFactory(): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BoardViewModel::class.java)) {
            return BoardViewModel() as T
        }

        throw  IllegalArgumentException("Unknown ViewModel Class")
    }

}
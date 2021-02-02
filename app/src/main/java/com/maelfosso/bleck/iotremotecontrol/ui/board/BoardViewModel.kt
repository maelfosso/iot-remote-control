package com.maelfosso.bleck.iotremotecontrol.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maelfosso.bleck.iotremotecontrol.IOTApplication

class BoardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun onKeyClicked(key: String) {
        IOTApplication.wrappedArduinoDevice?.sendMessage(key)
    }
}
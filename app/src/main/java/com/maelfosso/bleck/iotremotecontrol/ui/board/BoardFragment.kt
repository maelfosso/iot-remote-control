package com.maelfosso.bleck.iotremotecontrol.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maelfosso.bleck.iotremotecontrol.IOTApplication
import com.maelfosso.bleck.iotremotecontrol.R
import com.maelfosso.bleck.iotremotecontrol.bluetooth.WrappedArduinoBluetoothDevice
import com.maelfosso.bleck.iotremotecontrol.databinding.FragmentBoardBinding

class BoardFragment : Fragment(),
        WrappedArduinoBluetoothDevice.OnMessageReceivedListener,
        WrappedArduinoBluetoothDevice.OnMessageSentListener,
        WrappedArduinoBluetoothDevice.OnErrorListener {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var boardViewModel: BoardViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_board,
            container, false)
        val viewModelFactory = BoardViewModelFactory()
        boardViewModel = ViewModelProvider(this, viewModelFactory).get(BoardViewModel::class.java)
        binding.boardViewModel = boardViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        IOTApplication.wrappedArduinoDevice?.setListeners(
            this,
            this,
            this
        )
    }

    override fun onMessageSent(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onMessageReceived(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onError(error: Throwable) {
        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
    }
}
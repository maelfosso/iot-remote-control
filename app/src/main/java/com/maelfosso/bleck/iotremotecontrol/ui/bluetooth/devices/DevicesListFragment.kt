package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maelfosso.bleck.iotremotecontrol.BluetoothConnectionActivity
import com.maelfosso.bleck.iotremotecontrol.R
import com.maelfosso.bleck.iotremotecontrol.databinding.FragmentDevicesListBinding
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.DevicesAdapter
import kotlinx.android.synthetic.main.fragment_devices_list.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DevicesListFragment : Fragment() {

    companion object {
        var TAG: String = javaClass.name
    }

    private var _binding: FragmentDevicesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_devices_list,
            container, false)

        val application = requireNotNull(activity).application
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val viewModelFactory = DevicesViewModelFactory(bluetoothAdapter, application)
        val devicesViewModel = ViewModelProvider(this, viewModelFactory).get(DevicesViewModel::class.java)
        binding.devicesViewModel = devicesViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.devicesViewModel?.devices?.forEach {
            Log.d(TAG, "VMODEL - Devics : ${it.name} - ${it.address}")
        }
        val adapter = DevicesAdapter()

        binding.rvPairedDevices.adapter = adapter
        adapter.devices = binding.devicesViewModel!!.devices!!

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
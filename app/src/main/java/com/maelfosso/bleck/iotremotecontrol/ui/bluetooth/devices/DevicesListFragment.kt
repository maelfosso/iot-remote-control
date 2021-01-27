package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.maelfosso.bleck.iotremotecontrol.R
import com.maelfosso.bleck.iotremotecontrol.databinding.FragmentDevicesListBinding
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.DevicesAdapter
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DevicesListFragment : Fragment() {

    companion object {
        var TAG: String = javaClass.name
        val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    }

    private var _binding: FragmentDevicesListBinding? = null
    private val binding get() = _binding!!
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var devicesViewModel: DevicesViewModel
    private lateinit var pairedDevicesAdapter: DevicesAdapter
    private lateinit var discoveredDevicesAdapter: DevicesAdapter

    val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceive")

            when(intent.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d(TAG, "ACTION DISCOVERY STARTED")
                    binding.discoveringProgress.visibility = ProgressBar.VISIBLE
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d(TAG, "ACTION DISCOVERY FINISHED")
                    binding.discoveringProgress.visibility = ProgressBar.INVISIBLE
                }
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address

                    Log.d(TAG, "\t DISCOVERED - Devices : $deviceName - $deviceHardwareAddress")
                    devicesViewModel
                        .discoveredDevices
                        .value?.add(
                            DevicesViewModel.Device(
                                deviceName,
                                deviceHardwareAddress,
                                false)
                        )
                }
            }
        }
    }

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
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val viewModelFactory = DevicesViewModelFactory(bluetoothAdapter, application)
        devicesViewModel = ViewModelProvider(this, viewModelFactory).get(DevicesViewModel::class.java)
        binding.devicesViewModel = devicesViewModel
        binding.lifecycleOwner = this

        devicesViewModel.pairedDevices.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                pairedDevicesAdapter.devices = it
            }
        })
        devicesViewModel.discoveredDevices.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                discoveredDevicesAdapter.devices = it
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context?.let { LocalBroadcastManager.getInstance(it).registerReceiver(mReceiver, filter) }

        binding.devicesViewModel?.pairedDevices?.value?.forEach { it ->
            Log.d(TAG, "VMODEL - Devics : ${it.name} - ${it.address}")
        }
        pairedDevicesAdapter = DevicesAdapter(DevicesAdapter.DevicesListener { device ->
            Toast.makeText(context, "${device}", Toast.LENGTH_SHORT).show()

            devicesViewModel.onClick(
                device,
                {
                    Toast.makeText(context, "CONNECTED TO ${device}", Toast.LENGTH_LONG).show()
                },
                {
                    Toast.makeText(context, "ERROR when connecting to ${device}", Toast.LENGTH_LONG).show()
                }
            )

        })
        discoveredDevicesAdapter = DevicesAdapter(DevicesAdapter.DevicesListener { device ->
            Toast.makeText(context, "${device}", Toast.LENGTH_SHORT).show()

            devicesViewModel.onClick(
                device,
                {
                    Toast.makeText(context, "CONNECTED TO ${device}", Toast.LENGTH_LONG).show()
                },
                {
                    Toast.makeText(context, "ERROR when connecting to ${device}", Toast.LENGTH_LONG).show()
                }
            )

        })

        binding.rvPairedDevices.adapter = pairedDevicesAdapter
        binding.rvDiscoveredDevices.adapter = discoveredDevicesAdapter
        bluetoothAdapter.startDiscovery()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(mReceiver) }
        _binding = null
    }
}
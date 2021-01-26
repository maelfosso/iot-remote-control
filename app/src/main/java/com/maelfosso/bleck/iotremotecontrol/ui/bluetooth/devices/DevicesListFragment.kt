package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maelfosso.bleck.iotremotecontrol.BluetoothConnectionActivity
import com.maelfosso.bleck.iotremotecontrol.R
import com.maelfosso.bleck.iotremotecontrol.databinding.FragmentDevicesListBinding
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.DevicesAdapter
import kotlinx.android.synthetic.main.bluetooth_devices_item.*
import kotlinx.android.synthetic.main.fragment_devices_list.*
import java.io.IOException
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
    private lateinit var adapter: DevicesAdapter

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

        devicesViewModel.devices.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                adapter.devices = it
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.devicesViewModel?.devices?.value?.forEach { it ->
            Log.d(TAG, "VMODEL - Devics : ${it.name} - ${it.address}")
        }
        adapter = DevicesAdapter(DevicesAdapter.DevicesListener { device ->
            Toast.makeText(context, "${device}", Toast.LENGTH_SHORT).show()

            devicesViewModel.onClick(
                device,
                {
                    Toast.makeText(context, "CONNECTED TO ${device}", Toast.LENGTH_LONG).show()
//                    adapter.
                },
                {
                    Toast.makeText(context, "ERROR when connecting to ${device}", Toast.LENGTH_LONG).show()
                }
            )

//            val conn = new CreateConnected
//            val device = bluetoothAdapter.getRemoteDevice(address)
//            try {
//                val tmp = device.createRfconnSocketToServiceRecord(MY_UUID)
//                bluetootSocket = tmp
//                bluetootSocket.connect()
//                Log.d(TAG, "Connected to device - ${device.name} - ${device.address}")
//            } catch (e: IOException) {
//                Log.e(TAG, "Error when creating socket . $e")
//                bluetootSocket.close()
//            }
//
//            val mHandler = Handler(Looper.getMainLooper()) {
//                if (it.what == ConnectedThread.RESPONSE_MESSAGE) {
//                    val txt = it.obj as String
//                    if
//                }
//            }
        })

        binding.rvPairedDevices.adapter = adapter
//        adapter.devices = binding.devicesViewModel!!.devices!!

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
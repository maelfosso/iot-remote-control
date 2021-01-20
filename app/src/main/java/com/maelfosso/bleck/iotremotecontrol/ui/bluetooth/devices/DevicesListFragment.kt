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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maelfosso.bleck.iotremotecontrol.BluetoothConnectionActivity
import com.maelfosso.bleck.iotremotecontrol.R
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.DevicesAdapter
import kotlinx.android.synthetic.main.fragment_devices_list.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DevicesListFragment : Fragment() {

    companion object {
        var TAG: String = javaClass.name
    }

    lateinit var bluetoothAdapter: BluetoothAdapter

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_devices_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            Log.d(TAG, "Devices : ${device.name} - ${device.address}")
        }
        rv_paired_devices.apply {
            layoutManager  = LinearLayoutManager(activity)
            adapter = DevicesAdapter(pairedDevices?.toList())
        }

//        view.findViewById<Button>(R.id.button_second).setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }
    }
}
package com.maelfosso.bleck.iotremotecontrol

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.maelfosso.bleck.iotremotecontrol.databinding.ActivityBluetoothConnectionBinding
import com.maelfosso.bleck.iotremotecontrol.databinding.ContentBluetoothConnectionBinding
import com.maelfosso.bleck.iotremotecontrol.databinding.FragmentDevicesListBinding
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.DevicesAdapter
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices.DevicesListFragment
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices.DevicesViewModel
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices.DevicesViewModelFactory
import com.maelfosso.bleck.iotremotecontrol.ui.settings.SettingsFragment

class BluetoothConnectionActivity : AppCompatActivity() {

    companion object {
        
        const val PERMISSION_CODE: Int = 201
        const val REQUEST_ENABLE_LOCATION: Int = 301
        const val REQUEST_ENABLE_BLUETOOTH: Int = 202
        const val REQUEST_BLUETOOTH_DISCOVERY: Int = 203
        const val REQUEST_BLUETOOTH_DISCOVERY_DURATION: Int = 2000

        var TAG: String = javaClass.name

        fun start(context: Context) {
            val intent = Intent(context, BluetoothConnectionActivity::class.java).apply {}
            context.startActivity(intent)
        }
    }

    lateinit var bluetoothAdapter: BluetoothAdapter

    private var _binding: ActivityBluetoothConnectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var devicesViewModel: DevicesViewModel
    private lateinit var pairedDevicesAdapter: DevicesAdapter
    private lateinit var discoveredDevicesAdapter: DevicesAdapter


    val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceive")

            when(intent.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d(TAG, "ACTION DISCOVERY STARTED")
                    binding.progressBar.visibility = ProgressBar.VISIBLE
                    Snackbar.make(
                        binding.root,
                        "Starting Discovering devices bluetooth",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d(TAG, "ACTION DISCOVERY FINISHED")
                    binding.progressBar.visibility = ProgressBar.INVISIBLE
                    Snackbar.make(
                        binding.root,
                        "Discovering devices bluetooth finished",
                        Snackbar.LENGTH_LONG
                    ).show()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_bluetooth_connection)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_bluetooth_connection)
        binding.lifecycleOwner = this

        val viewModelFactory = DevicesViewModelFactory()
        devicesViewModel = ViewModelProvider(this, viewModelFactory).get(DevicesViewModel::class.java)
        binding.devicesViewModel = devicesViewModel
        binding.lifecycleOwner = this

        devicesViewModel.pairedDevices.observe(this, androidx.lifecycle.Observer {
            it?.let {
                pairedDevicesAdapter.devices = it
            }
        })
        devicesViewModel.discoveredDevices.observe(this, androidx.lifecycle.Observer {
            it?.let {
                discoveredDevicesAdapter.devices = it
            }
        })

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter)

        setSupportActionBar(binding.toolbar) // findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.devicesViewModel?.pairedDevices?.value?.forEach { it ->
            Log.d(TAG, "VMODEL - Devics : ${it.name} - ${it.address}")
        }
        pairedDevicesAdapter = DevicesAdapter(DevicesAdapter.DevicesListener { device ->
            onDeviceClick(device)

        })
        discoveredDevicesAdapter = DevicesAdapter(DevicesAdapter.DevicesListener { device ->

            onDeviceClick(device)
        })

        binding.rvPairedDevices.adapter = pairedDevicesAdapter
        binding.rvDiscoveredDevices.adapter = discoveredDevicesAdapter


        val res = IOTApplication.getBluetoothAdapter().startDiscovery()
        Log.d(TAG, "BLUETOOTH starting discovery : $res")
    }

    private fun onDeviceClick(device: String) {
        Toast.makeText(this, "Connecting to ... $device", Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = ProgressBar.VISIBLE

        devicesViewModel.onClick(
            device,
            {
                binding.progressBar.visibility = ProgressBar.INVISIBLE
                Toast.makeText(this, "CONNECTED TO $device", Toast.LENGTH_LONG).show()
            },
            {
                binding.progressBar.visibility = ProgressBar.INVISIBLE
                Log.d(TAG, it.message, it)
                Toast.makeText(this, "ERROR when connecting to $device", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun handleScan() {
        IOTApplication.getBluetoothAdapter().startDiscovery()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.bluetooth_connection, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        invalidateOptionsMenu()

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_scan -> {
                handleScan()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter)

        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

}

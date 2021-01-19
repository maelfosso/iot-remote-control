package com.maelfosso.bleck.iotremotecontrol

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BluetoothConnectionActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_CODE: Int = 201
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_connection)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(baseContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE
                )
            }
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        setupBluetooth();
    }

    private fun setupBluetooth(): Boolean {
        Log.d(TAG, "setupBluetooth()")

        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                "Sorry but you device does'nt support Bluetooth",
                Toast.LENGTH_LONG
            ).show()

            return false
        }

        if (!bluetoothAdapter?.isEnabled) {
            Log.d(TAG, "Bluetooth is not enabled")

            Toast.makeText(this,
                "Please enable Bluetooth",
                Toast.LENGTH_LONG
            ).show()

            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)

            return false
        }

        Log.d(TAG, "Great! Your bluetooth is enabled")

        Toast.makeText(this,
            "Great! Your bluetooth is enabled",
            Toast.LENGTH_SHORT
        ).show()

        return makeDeviceDiscoverable()
    }

    private fun makeDeviceDiscoverable(): Boolean {
        Log.d(TAG, "makeDeviceDiscoverable()")

        if (bluetoothAdapter.scanMode === BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.d(TAG, "Scan mode NOT connectable discoverable")

            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, REQUEST_BLUETOOTH_DISCOVERY_DURATION)
            startActivityForResult(discoverableIntent, REQUEST_BLUETOOTH_DISCOVERY)

            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")

        when (requestCode) {
            REQUEST_ENABLE_BLUETOOTH -> {
                if (resultCode === Activity.RESULT_OK) {
                    Log.d(TAG, "Great! Bluetooth enabled")

                    Toast.makeText(this,
                        "Great! Bluetooth enabled",
                        Toast.LENGTH_LONG
                    ).show()

                    makeDeviceDiscoverable()
                } else {
                    Log.d(TAG, "Sorry but you need to enable your Bluetooth to move on")

                    Toast.makeText(this,
                        "Sorry but you need to enable your Bluetooth to move on",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            REQUEST_BLUETOOTH_DISCOVERY -> {
                if (resultCode === Activity.RESULT_OK) {
                    Log.d(TAG, "Great! Bluetooth Made Discoverable")

                    Toast.makeText(this,
                        "Great! Bluetooth Made Discoverable",
                        Toast.LENGTH_LONG
                    ).show()

                    makeDeviceDiscoverable()
                } else {
                    Log.d(TAG, "Sorry! Discoverability rejected")

                    Toast.makeText(this,
                        "Sorry! Discoverability rejected",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }
}
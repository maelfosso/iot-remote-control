package com.maelfosso.bleck.iotremotecontrol

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {
    
    companion object {
        var TAG: String = javaClass.name
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        checkPermission()
    }
    
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(baseContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    BluetoothConnectionActivity.PERMISSION_CODE
                )
            }
        }
    }

    private fun makeDeviceDiscoverable(): Boolean {
        Log.d(TAG, "makeDeviceDiscoverable()")

        if (IOTApplication.bluetoothAdapter.scanMode === BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.d(TAG, "Scan mode NOT connectable discoverable")

            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

            discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                BluetoothConnectionActivity.REQUEST_BLUETOOTH_DISCOVERY_DURATION
            )
            startActivityForResult(discoverableIntent,
                BluetoothConnectionActivity.REQUEST_BLUETOOTH_DISCOVERY
            )

            return false
        }


        return true
    }

    private fun launch() {
        StartActivity.start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")

        when (requestCode) {
            BluetoothConnectionActivity.REQUEST_ENABLE_BLUETOOTH -> {
                if (resultCode === Activity.RESULT_OK) {
                    Log.d(TAG, "Great! Bluetooth enabled")

                    Toast.makeText(this,
                        "Great! Bluetooth enabled",
                        Toast.LENGTH_LONG
                    ).show()

                    if (makeDeviceDiscoverable()) {
                        launch()
                    }
                } else {
                    Log.d(TAG, "Sorry but you need to enable your Bluetooth to move on")

                    Toast.makeText(this,
                        "Sorry but you need to enable your Bluetooth to move on",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            BluetoothConnectionActivity.REQUEST_BLUETOOTH_DISCOVERY -> {
                if (resultCode === Activity.RESULT_OK) {
                    Log.d(TAG, "Great! Bluetooth Made Discoverable")

                    Toast.makeText(this,
                        "Great! Bluetooth Made Discoverable",
                        Toast.LENGTH_LONG
                    ).show()

                    if (makeDeviceDiscoverable()) {
                        launch()
                    }
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
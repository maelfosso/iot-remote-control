package com.maelfosso.bleck.iotremotecontrol

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class SplashActivity : AppCompatActivity() {
    
    companion object {
        var TAG: String = javaClass.name
    }

    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()
        handler.postDelayed(Runnable {
            checkPermission()
            if (setupBluetooth()) {
                launch()
            }
        }, 3000)
    }
    
    private fun checkPermission() {
        Log.d(TAG, "checkPermission()")

        if (!hasPermissions(*PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                BluetoothConnectionActivity.PERMISSION_CODE
            );
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )) {
                showExplanation("Permission Needed", "Rationale");
            } else {

                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    BluetoothConnectionActivity.PERMISSION_CODE
                )
                Log.d(TAG, "ActivityCompat.RequestPeermissions")

            }
        }
    }

    private fun hasPermissions(vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showExplanation(
        title: String,
        message: String,
//        permission: String,
//        permissionRequestCode: Int
    ) {
        val builder = android.app.AlertDialog.Builder(this);
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                android.R.string.ok
            ) { p0, p1 ->
                ActivityCompat.requestPermissions(
                    this@SplashActivity,
                    PERMISSIONS,
                    BluetoothConnectionActivity.PERMISSION_CODE
                )
                Log.d(TAG, "ActivityCompat.RequestPeermissions")
            }
        builder.create().show()
    }

    private fun setupBluetooth(): Boolean {
        Log.d(TAG, "setupBluetooth() ${IOTApplication.getBluetoothAdapter().isEnabled}")

        if (!IOTApplication.getBluetoothAdapter().isEnabled) {
            Log.d(TAG, "Bluetooth is not enabled")

            Toast.makeText(
                this,
                "Please enable Bluetooth",
                Toast.LENGTH_LONG
            ).show()

            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(
                enableBluetoothIntent,
                BluetoothConnectionActivity.REQUEST_ENABLE_BLUETOOTH
            )

            return false
        }

        Log.d(TAG, "Great! Your bluetooth is enabled")
        Toast.makeText(
            this,
            "Great! Your bluetooth is enabled",
            Toast.LENGTH_SHORT
        ).show()

        return makeDeviceDiscoverable() && enableLocation()
    }

//    @RequiresApi(Build.VERSION_CODES.M)
    private fun enableLocation() : Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                BluetoothConnectionActivity.REQUEST_ENABLE_LOCATION
            )

            return false
        }

        return true
    }

    private fun makeDeviceDiscoverable(): Boolean {
        Log.d(TAG, "makeDeviceDiscoverable()  ")
        Log.d(TAG, IOTApplication.getBluetoothAdapter().toString())
        Log.d(TAG, IOTApplication.getBluetoothAdapter().scanMode.toString())
        Log.d(
            TAG,
            (IOTApplication.getBluetoothAdapter().scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE).toString()
        )

        if (IOTApplication.getBluetoothAdapter().scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.d(TAG, "Scan mode NOT connectable discoverable")

            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

            discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                BluetoothConnectionActivity.REQUEST_BLUETOOTH_DISCOVERY_DURATION
            )
            startActivityForResult(
                discoverableIntent,
                BluetoothConnectionActivity.REQUEST_BLUETOOTH_DISCOVERY
            )

            return false
        }


        return true
    }

    private fun launch() {
        Log.d(TAG, "launching starting act")

        StartActivity.start(this)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        Log.d(TAG, "onRequestPermissionResult : $requestCode -- ${grantResults.size} -- ${grantResults[0]} -- ${PackageManager.PERMISSION_DENIED}")
        when(requestCode) {
            BluetoothConnectionActivity.PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    if (setupBluetooth()) {
                        launch()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Permission Denied !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")

        when (requestCode) {

            BluetoothConnectionActivity.REQUEST_ENABLE_BLUETOOTH,
            BluetoothConnectionActivity.REQUEST_ENABLE_LOCATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Great! Bluetooth enabled")

                    Toast.makeText(
                        this,
                        "Great!  enabled",
                        Toast.LENGTH_LONG
                    ).show()

                    if (makeDeviceDiscoverable() && enableLocation()) {
                        launch()
                    }
                } else {
                    Log.d(TAG, "Sorry but you need to enable your Bluetooth to move on")

                    Toast.makeText(
                        this,
                        "Sorry but you need to enable your Bluetooth to move on",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            BluetoothConnectionActivity.REQUEST_BLUETOOTH_DISCOVERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Great! Bluetooth Made Discoverable")

                    Toast.makeText(
                        this,
                        "Great! Bluetooth Made Discoverable",
                        Toast.LENGTH_LONG
                    ).show()

                    if (makeDeviceDiscoverable() && enableLocation()) {
                        launch()
                    }
                } else {
                    Log.d(TAG, "Sorry! Discoverability rejected")

                    Toast.makeText(
                        this,
                        "Sorry! Discoverability rejected",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }
}
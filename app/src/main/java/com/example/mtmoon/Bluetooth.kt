package com.example.mtmoon

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelUuid
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import kotlinx.android.synthetic.main.activity_bluetooth.*
import kotlinx.coroutines.delay
import java.util.*
import java.util.jar.Manifest

class Bluetooth : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 27)
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC Address
            val bluetoothText = TextView(this)
            bluetoothText.marginTop + 50
            val parameters = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            bluetoothText.layoutParams = parameters
            bluetoothText.textAlignment = ViewGroup.TEXT_ALIGNMENT_CENTER
            bluetoothText.text = "$deviceName:      $deviceHardwareAddress"
            PairedDevices.addView(bluetoothText)

        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)


        val permissionVerify: Int = 1
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                android.Manifest.permission
                    .ACCESS_FINE_LOCATION), permissionVerify)
        }


        bluetoothAdapter?.startDiscovery()
        Toast.makeText(this, "Looking for Bluetooth Devices...", Toast.LENGTH_SHORT).show()
        println("Starting Bluetooth Query...")


    }

    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            println("We have Bluetooth Action Detected ")
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //Disc. has found a device; get object & info from Intent
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var deviceName = device?.name
                    if (deviceName == null) {
                        deviceName = "Unknown"
                    }
                    val deviceHardwareAddress = device?.address // MAC
                    val bluetoothText = TextView(this@Bluetooth)
                    bluetoothText.marginTop + 50
                    bluetoothText.marginBottom + 100 // n0t doing anything really I think idk
                    val parameters = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    bluetoothText.isClickable = true
                    bluetoothText.layoutParams = parameters
                    bluetoothText.textAlignment = ViewGroup.TEXT_ALIGNMENT_CENTER
                    bluetoothText.setOnClickListener(View.OnClickListener {
                        Toast.makeText(applicationContext,"Attempting to connect to $deviceName...", Toast.LENGTH_SHORT).show()
                        attemptConnection(device)
                    })
                    bluetoothText.text = "$deviceName:      $deviceHardwareAddress"
                    AvailableDevices.addView(bluetoothText)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
        bluetoothAdapter?.cancelDiscovery()
        println("Bluetooth Query Killed.")
    }

    override fun onPause() {
        super.onPause()
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun onResume() {
        super.onResume()
        bluetoothAdapter?.startDiscovery()
    }


    /** 3426b296-85b5-4963-bfc5-c9bc545d7c4a */ // Sample UUID

    fun attemptConnection(device: BluetoothDevice?) {
        bluetoothAdapter?.cancelDiscovery()
        device?.fetchUuidsWithSdp()
        val dummy = BluetoothDevice.EXTRA_UUID
        println("They Might be Giants...!  Here!    $dummy")
        val deviceUUID : UUID = device!!.uuids[0].uuid  // This is now NOT showing as null
        println("Uh oh!  Here!  $deviceUUID")
//
        Thread(Runnable { // Thread so socket.connect() does not block main thread -- blocking call
            val socket = device?.createRfcommSocketToServiceRecord(deviceUUID)
            socket?.connect()
//            socket?.close()
        }).start()

    }
}
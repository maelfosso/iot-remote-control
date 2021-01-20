package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maelfosso.bleck.iotremotecontrol.BluetoothConnectionActivity
import com.maelfosso.bleck.iotremotecontrol.R

class DevicesAdapter(devicesList: List<BluetoothDevice>?) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    companion object {
        var TAG: String = javaClass.name
    }

    var devices: List<BluetoothDevice>? = devicesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.bluetooth_devices_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = devices!![position].name
        holder.address.text = devices!![position].address
        holder.status.text = "paired"
    }

    override fun getItemCount(): Int {
        return devices!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var status: TextView
        var address: TextView

        init {
            name = itemView.findViewById(R.id.name)
            address = itemView.findViewById(R.id.address)
            status = itemView.findViewById(R.id.status)

            itemView.setOnClickListener {
                val position: Int = adapterPosition
                val context = itemView.context
//                val intent = Intent(context,)

                Log.d(TAG, "item clicked - ${position}")

            }
        }
    }
}
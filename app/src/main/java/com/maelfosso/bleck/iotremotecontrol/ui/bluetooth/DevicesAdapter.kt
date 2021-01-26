package com.maelfosso.bleck.iotremotecontrol.ui.bluetooth

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maelfosso.bleck.iotremotecontrol.R
import com.maelfosso.bleck.iotremotecontrol.databinding.BluetoothDevicesItemBinding
import com.maelfosso.bleck.iotremotecontrol.ui.bluetooth.devices.DevicesViewModel

class DevicesAdapter(val clickListener: DevicesListener) :
//        ListAdapter<BluetoothDevice, DevicesAdapter.ViewHolder>()
        RecyclerView.Adapter<DevicesAdapter.ViewHolder>()
    {

    companion object {
        var TAG: String = javaClass.name
    }

    var devices = listOf<DevicesViewModel.Device>() // List<BluetoothDevice>? = devicesList
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BluetoothDevicesItemBinding
            .inflate(layoutInflater, parent, false)


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = devices[position]

        holder.bind(item, clickListener)
    }

    override fun getItemCount(): Int {
        return devices!!.size
    }

    private fun ViewHolder.bind(
        item: DevicesViewModel.Device,
        onClickListener: DevicesListener
    ) {
        with (binding) {
            name.text = item.name
            address.text = item.address
            status.setImageDrawable(
                if (item.isConnectedTo)
                    this.status.context.resources.getDrawable(R.drawable.ic_check_circle_24px) //, this.status.context.theme)
                    else null
            )

            device = item
            clickListener = onClickListener

            executePendingBindings()
        }

    }

    inner class ViewHolder(val binding: BluetoothDevicesItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }


    class DevicesListener(val clickListener: (deviceAddress: String) -> Unit) {
        fun onClick(device: DevicesViewModel.Device) = clickListener(device.address)
    }

}

//        companion object {
//            fun from(parent: ViewGroup): ViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
////                val v = layoutInflater
////                    .inflate(R.layout.bluetooth_devices_item, parent, false)
//
//                val binding = BluetoothDevicesItemBinding
//                    .inflate(layoutInflater, parent, false)
//
//
//                return ViewHolder(binding)
//            }
//        }

//        fun bind(item: BluetoothDevice) {
//            with(binding) {
//
//            }
//        }
//        var name: TextView = itemView.findViewById(R.id.name)
//        var status: TextView = itemView.findViewById(R.id.status)
//        var address: TextView = itemView.findViewById(R.id.address)

//        init {
//
//            itemView.setOnClickListener {
//                val position: Int = adapterPosition
//                val context = itemView.context
////                val intent = Intent(context,)
//
//                Log.d(TAG, "item clicked - ${position}")
//
//            }
//        }
////    }
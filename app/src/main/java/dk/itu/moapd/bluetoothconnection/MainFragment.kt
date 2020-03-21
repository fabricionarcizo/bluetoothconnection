package dk.itu.moapd.bluetoothconnection

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private var devices: ArrayList<String> = ArrayList()
    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deviceAdapter = DeviceAdapter()
        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.adapter = deviceAdapter

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null ) {
            toggle_button.setOnClickListener {
                val status = toggle_button.isChecked

                if (status) {
                    if (!bluetoothAdapter!!.isEnabled) {
                        val intent = Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivity(intent)
                    }
                }
                else if (bluetoothAdapter!!.isEnabled)
                    bluetoothAdapter!!.disable()

                visible_button.isEnabled = status
                devices_button.isEnabled = status
            }

            visible_button.setOnClickListener {
                val intent = Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                startActivity(intent)
            }

            devices_button.setOnClickListener {
                devices.clear()
                val pairedDevices = bluetoothAdapter!!.bondedDevices
                for (device in pairedDevices)
                    devices.add(device.name + " (" +
                                device.address + ")")
                deviceAdapter.setDevices(devices)
            }

            bluetoothAdapter!!.disable()

        } else {
            toggle_button.isEnabled = false
            visible_button.isEnabled = false
            devices_button.isEnabled = false
            devices.add("This device does NOT support Bluetooth")
            deviceAdapter.setDevices(devices)
        }
    }

    private inner class DeviceHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val mDevice: TextView = view.findViewById(R.id.device)

    }

    private inner class DeviceAdapter :
        RecyclerView.Adapter<DeviceHolder>() {

        private var devices: List<String> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
            val layout = layoutInflater.inflate(R.layout.list_paired_devices, parent, false)
            return DeviceHolder(layout)
        }

        override fun getItemCount() = devices.size

        override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
            if (devices.size > position) {
                val device = devices[position]
                holder.apply {
                    mDevice.text = device
                }
            } else
                holder.apply {
                    mDevice.text = getString(R.string.unavailable_device)
                }
        }

        fun setDevices(devices: List<String>) {
            this.devices = devices
            notifyDataSetChanged()
        }
    }

}

package dk.itu.moapd.bluetoothconnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainFragment extends Fragment {

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private List<String> mDevices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDevices = new ArrayList<>();

        final DeviceAdapter deviceAdapter = new DeviceAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(deviceAdapter);

        final ToggleButton toggle_button =
                view.findViewById(R.id.toggle_button);
        final Button visible_button =
                view.findViewById(R.id.visible_button);
        final Button devices_button =
                view.findViewById(R.id.devices_button);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            toggle_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean status = toggle_button.isChecked();

                    if (status) {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent intent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivity(intent);
                        }
                    }
                    else if (mBluetoothAdapter.isEnabled())
                        mBluetoothAdapter.disable();

                    visible_button.setEnabled(status);
                    devices_button.setEnabled(status);
                }
            });

            visible_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivity(intent);
                }
            });

            devices_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDevices.clear();
                    mPairedDevices = mBluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : mPairedDevices)
                        mDevices.add(device.getName() + " (" +
                                     device.getAddress() + ")");
                    deviceAdapter.setDevices(mDevices);
                }
            });

            mBluetoothAdapter.disable();

        } else {
            toggle_button.setEnabled(false);
            visible_button.setEnabled(false);
            devices_button.setEnabled(false);
            mDevices.add("This device does NOT support Bluetooth");
            deviceAdapter.setDevices(mDevices);
        }
    }

    private class DeviceHolder extends RecyclerView.ViewHolder {

        TextView mDevice;

        DeviceHolder(View view) {
            super(view);
            mDevice = view.findViewById(R.id.device);
        }

    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {

        private List<String> mDevices = new ArrayList<>();

        @NonNull
        @Override
        public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {
            View layout = getLayoutInflater()
                    .inflate(R.layout.list_paired_devices, parent, false);
            return new DeviceHolder(layout);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceHolder holder,
                                     int position) {
            if (mDevices.size() > position) {
                String device = mDevices.get(position);
                holder.mDevice.setText(device);
            } else
                holder.mDevice.setText(
                        getString(R.string.unavailable_device));
        }

        @Override
        public int getItemCount() {
            return mDevices.size();
        }

        void setDevices(List<String> devices) {
            mDevices = devices;
            notifyDataSetChanged();
        }

    }

}

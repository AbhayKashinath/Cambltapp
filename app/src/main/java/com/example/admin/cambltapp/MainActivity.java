package com.example.admin.cambltapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;

import java.util.ArrayList;
import java.util.Set;

public abstract class MainActivity extends AppCompatActivity implements OnItemClickListener {

    ArrayAdapter<String> listAdapter;
    ArrayList<String> pairedDevices;
    Button button;
    ListView listView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    IntentFilter filter;
    BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not detected", 0).show();
            finish();

        } else {
            if (!btAdapter.isEnabled()) {
                turnonBluetooth();

            }
        }
        getPairedDevices();
        startDiscovery();
    }

    private void startDiscovery() {
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    private void turnonBluetooth() {

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    private void getPairedDevices() {
        devicesArray = btAdapter.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray) {
                pairedDevices.add(device.getName() + "\n" + device.getAddress());

            }
        }
    }

    private void init() {

        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(listAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listAdapter.add(device.getName() + "\n" + device.getAddress());
                    {

                        for (int i = 0; i < listAdapter.getCount(); i++) {
                            for (int a = 0; a < pairedDevices.size(); a++) {
                                if (listAdapter.getItem(i).equals(pairedDevices.get(a))) {

                                    String s = listAdapter.getItem(i);
                                    s = s + "Device Paired";

                                    break;
                                }
                            }
                        }
                        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                            if (listAdapter.getCount() > 0) {

                            }

                        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                            if (btAdapter.getState() == btAdapter.STATE_OFF) {
                                turnonBluetooth();

                            }

                        }
                    }
                }
                ;
                registerReceiver(receiver, filter);
                filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

                registerReceiver(receiver, filter);
                filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                registerReceiver(receiver, filter);
                filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);


            }


           /* public void onPause()  {
               onPause();
                unregisterReceiver(receiver);
            }*/


            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "turn on bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }


            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listAdapter.getItem(position).contains("Device Paired")) {
                    Toast.makeText(getApplicationContext(), "The Device is Paired", 0).show();
                } else {
                    Toast.makeText(getApplicationContext(), "The Device is not Paired", 0).show();
                }
            }

        };
    }
}
package com.example.jslocal.fraunenhofer_pong;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private String LOG_TAG; // Just for logging purposes. Could be anything. Set to app_name
    private int REQUEST_ENABLE_BT = 99; // Any positive integer should work.
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LOG_TAG = getResources().getString(R.string.app_name);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        enableBluetoothOnDevice();


    }

    private void enableBluetoothOnDevice()
    {
        if (mBluetoothAdapter == null)
        {
            Log.e(LOG_TAG, "This device does not have a bluetooth adapter");
            finish();
            // If the android device does not have bluetooth, just return and get out.
            // There's nothing the app can do in this case. Closing app.
        }

        // Check to see if bluetooth is enabled. Prompt to enable it
        if( !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == 0)
            {
                // If the resultCode is 0, the user selected "No" when prompt to
                // allow the app to enable bluetooth.
                // You may want to display a dialog explaining what would happen if
                // the user doesn't enable bluetooth.
                Toast.makeText(this, "The user decided to deny bluetooth access", Toast.LENGTH_LONG).show();
            }
            else
                Log.i(LOG_TAG, "User allowed bluetooth access!");
        }
    }

    private ArrayList getArrayOfAlreadyPairedBluetoothDevices()
    {
        ArrayList <BluetoothObject> arrayOfAlreadyPairedBTDevices = null;

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0)
        {
            arrayOfAlreadyPairedBTDevices = new ArrayList<BluetoothObject>();

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices)
            {
                // Create the device object and add it to the arrayList of devices
                BluetoothObject bluetoothObject = new BluetoothObject();
                bluetoothObject.setName(device.getName());
                bluetoothObject.setAddress(device.getAddress());
                bluetoothObject.setState(device.getBondState());
                bluetoothObject.setType(device.getType());    // requires API 18 or higher
                bluetoothObject.setUuids(device.getUuids().toString());

                arrayOfAlreadyPairedBTDevices.add(bluetoothObject);
            }
        }

        return arrayOfAlreadyPairedBTDevices;
    }

    private void displayListOfFoundDevices()
    {
        List<BluetoothObject> arrayOfFoundBTDevices = new ArrayList<BluetoothObject>();

        // start looking for bluetooth devices
        mBluetoothAdapter.startDiscovery();

        // Discover new devices
        // Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the "RSSI" to get the signal strength as integer,
                    // but should be displayed in "dBm" units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    // Create the device object and add it to the arrayList of devices
                    BluetoothObject bluetoothObject = new BluetoothObject();
                    bluetoothObject.setName(device.getName());
                    bluetoothObject.setAddress(device.getAddress());
                    bluetoothObject.setState(device.getBondState());
                    bluetoothObject.setType(device.getType());    // requires API 18 or higher
                    bluetoothObject.setUuids(device.getUuids().toString());
                    bluetoothObject.setRssi(rssi);

                    arrayOfFoundBTDevices.add(bluetoothObject);

                    // 1. Pass context and data to the custom adapter
                    FoundBTDevicesAdapter adapter = new FoundBTDevicesAdapter(getApplicationContext(), arrayOfFoundBTDevices);

                    // 2. setListAdapter
                    setListAdapter(adapter);
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    //TODO Bluetooth: http://toomanytutorials.blogspot.com/2015/03/scanning-for-bluetooth-devices-in.html

}


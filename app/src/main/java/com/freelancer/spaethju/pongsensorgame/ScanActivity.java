package com.freelancer.spaethju.pongsensorgame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ScanActivity extends Activity {

    private ListView listView;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private ArrayList<String> deviceNameList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private static UUID MY_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final String TAG = "BT_PONG";
    private ConnectedThread mConnectThread;
    private Handler handler;
    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //listView = (ListView) findViewById(R.id.listView);
        listView.setHeaderDividersEnabled(true);
        listView.setBackgroundColor(Color.WHITE);
        // Set an item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);
                for (BluetoothDevice device : mDeviceList) {
                    if (selectedItem.split(" - ")[0].equals(device.getName()) &&
                            selectedItem.split(" - ")[1].equals(device.getAddress())) {
                        connectDevice(device);
                        mConnectThread = new ConnectedThread(mSocket);
                        mConnectThread.start();
                        mConnectThread.run();
                        mBluetoothAdapter.cancelDiscovery();
                        System.out.println("DONE");
                    }
                }
            }
        });

        init();
    }

    public void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                try {
                    Method m = device.getClass()
                            .getMethod("removeBond", (Class[]) null);
                    m.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    Log.e("Remove has been failed.", e.getMessage());
                }
            }
        }
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device does not support BT");
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(btOnReceiver, filter);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);
                if (device.getName() != null) {
                    deviceNameList.add(device.getName() + " - " + device.getAddress());
                } else {
                    deviceNameList.add("Unkown" + " - " + device.getAddress());
                }
                Log.i("BT", device.getName() + " - " + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, deviceNameList));
            }
        }
    };

    private final BroadcastReceiver btOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        init();
                }
            }
        }
    };


    public boolean createBond(BluetoothDevice btDevice) throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public void connectDevice(BluetoothDevice device) {
        try {
            createBond(device);
            Log.i("BT", "Bond created.");
            mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            Log.e(TAG, "socket not created", e1);
        } catch (Exception e) {
            Log.e(TAG, "Bond failed", e);
        }

        try {
            mSocket.connect();
        } catch (IOException e) {
            try {

                mSocket.close();
                Log.e(TAG,"Cannot connect", e);
            } catch (IOException e1) {
                Log.d(TAG,"Socket not closed", e1);
            }
        }

    }



    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                    System.out.println(readMsg.toString());
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                } catch (Exception e) {
                    Log.d(TAG, "No sensor device");
                    Toast errorToast = Toast.makeText(ScanActivity.this, "Error, Select another device please!", Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }


}
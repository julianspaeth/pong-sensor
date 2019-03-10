package com.freelancer.spaethju.pongsensorgame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.UUID;

public class ConnectActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private static UUID MY_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final String TAG = "BT";
    private final static int REQUEST_ENABLE_BT = 1;
    private String mac;
    private BluetoothDevice mDevice ;
    private BluetoothManager bluetoothManager;
    private BluetoothGatt gatt;
    private boolean mScanning;
    private Handler handler = new Handler();
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    boolean enabled;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    private int connectionState = STATE_DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mac = getIntent().getStringExtra("MAC");
//
//        mDevice = null;
//
//        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent =
//                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            Log.e(TAG, "BT not enabled");
//        }
//
//        scanLeDevice(true);


    }

//    private static final long SCAN_PERIOD = 10000;
//
//    private void scanLeDevice (final boolean enable) {
//        Log.i(TAG, "Scan for BLE device");
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(leScanCallback);
//                    if (mDevice == null) {
//                        Log.e(TAG, "No BLE device found");
//                        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
//                        startActivity(startIntent);
//                    }
//
//                }
//            }, SCAN_PERIOD);
//
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(leScanCallback);
//        } else {
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(leScanCallback);
//            Intent startIntent = new Intent(this, StartActivity.class);
//            startActivity(startIntent);
//        }
//    }
//
//
//    // Device scan callback.
//    private BluetoothAdapter.LeScanCallback leScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi,
//                                     byte[] scanRecord) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(device.getAddress().equals(mac)){
//                                Log.i(TAG, "BLE device found");
//                                mDevice = device;
//                                mBluetoothAdapter.stopLeScan(leScanCallback);
//                                setContentView(R.layout.activity_connect);
//                                TextView mac_tv = (TextView) findViewById(R.id.mac_address);
//                                mac_tv.setText(mac);
//                                gatt = mDevice.connectGatt(getApplicationContext(), true, gattCallback);
//                            }
//                        }
//                    });
//                }
//    };
//
//
//    // Various callback methods defined by the BLE API.
//    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            super.onConnectionStateChange(gatt, status, newState);
//            if (newState == STATE_CONNECTED) {
//                gatt.discoverServices();
//            }
//
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            super.onServicesDiscovered(gatt, status);
//            ArrayList<BluetoothGattService> services = (ArrayList<BluetoothGattService>) gatt.getServices();
//            for (BluetoothGattService service : services) {
//                ArrayList<BluetoothGattCharacteristic> characteristics = (ArrayList<BluetoothGattCharacteristic>) service.getCharacteristics();
//                for (BluetoothGattCharacteristic characteristic : characteristics) {
//                    //gatt.readCharacteristic(characteristic);
//                    try {
//                        gatt.setCharacteristicNotification(characteristic, true);
//                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                                UUID.fromString(MY_UUID.toString()));
//                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                        gatt.writeDescriptor(descriptor);
//                    } catch (NullPointerException e) {
//                        // do something
//                    }
//                }
//            }
//
//        }
//
//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
//        }
//
//        @Override
//        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            super.onCharacteristicChanged(gatt, characteristic);
//            System.out.println(new String(characteristic.getValue()));
//        }
//    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gatt != null) {
            gatt.disconnect();
        }

    }

    /** Called when the user taps the Play button */
    public void play(View view) {
        Log.i(TAG, "Play button pressed");
        Intent playIntent = new Intent(this, PlayActivity.class);
        startActivity(playIntent);
    }

}
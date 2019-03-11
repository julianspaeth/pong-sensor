package com.freelancer.spaethju.pongsensorgame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.UUID;

public class ConnectActivity extends Activity {

//    private BluetoothAdapter mBluetoothAdapter;
//    private BluetoothSocket mSocket;
//    private static UUID MY_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
//    private static final String TAG = "BT";
//    private final static int REQUEST_ENABLE_BT = 1;
//    private String mac;
//    private BluetoothDevice mDevice ;
//    private BluetoothManager bluetoothManager;
//    private BluetoothGatt gatt;
//    private boolean mScanning;
//    private Handler handler = new Handler();
//    private static final int STATE_DISCONNECTED = 0;
//    private static final int STATE_CONNECTING = 1;
//    private static final int STATE_CONNECTED = 2;
//    boolean enabled;
//
//    public final static String ACTION_GATT_CONNECTED =
//            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
//    public final static String ACTION_GATT_DISCONNECTED =
//            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
//    public final static String ACTION_GATT_SERVICES_DISCOVERED =
//            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
//    public final static String ACTION_DATA_AVAILABLE =
//            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
//    public final static String EXTRA_DATA =
//            "com.example.bluetooth.le.EXTRA_DATA";
//    private int connectionState = STATE_DISCONNECTED;

    private String mac = "";
    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler handler = new Handler();
    private String TAG = "BLE";
    private boolean mScanning;
    public static UUID UART_UUID   = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID TX_UUID   = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID RX_UUID   = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    // UUID for the UART BTLE client characteristic which is necessary for notifications.
    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    // UUIDs for the Device Information service and associated characeristics.
    public static UUID DIS_UUID       = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static UUID DIS_MANUF_UUID = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    public static UUID DIS_MODEL_UUID = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    public static UUID DIS_HWREV_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static UUID DIS_SWREV_UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");
    private UUID[] uuids = new UUID[1];
    private BluetoothGatt bluetoothGatt;
    private int connectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private Boolean connected;

    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        mac = getIntent().getStringExtra("MAC");
        uuids[0] = UART_UUID;
        setUpBle();
        findBleDevices();

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

    private void setUpBle() {
        Log.i(TAG, "Set up BLE");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private static final long SCAN_PERIOD = 5000;
    private void findBleDevices() {
        Log.i(TAG, "Find BLE devices");
        connected = false;
        if (true) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    if (!connected) {
                        Log.i(TAG, "Device not found");
                        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(startIntent);
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(uuids, leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            if (!connected) {
                Log.i(TAG, "Device not found");
                Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(startIntent);
            }
        }

    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device.getAddress().equals(mac)) {
                                connected = true;
                                Log.i(TAG, "Connect to " + device.getName() + " with MAC: " + device.getAddress());
                                bluetoothGatt = device.connectGatt(getApplicationContext(), true, gattCallback);
                            }

                        }
                    });
                }
            };


    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        connectionState = STATE_CONNECTED;
                        Log.i(TAG, "Connected to GATT server.");
                        gatt.discoverServices();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        connectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                        closeConnection();
                        Intent startIntent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(startIntent);

                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    Log.i(TAG, "Discover Services");
                    // Save reference to each UART characteristic.
                    tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
                    rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);

                    // Setup notifications on RX characteristic changes (i.e. data received).
                    // First call setCharacteristicNotification to enable notification.
                    if (!gatt.setCharacteristicNotification(rx, true)) {
                        // Stop if the characteristic notification setup failed.
                        Log.e(TAG, "RX characteristic notificiation setup failed.");
                    }
                    // Next update the RX characteristic's client descriptor to enable notifications.
                    BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
                    if (desc == null) {
                        // Stop if the RX characteristic has no client descriptor.
                        Log.e(TAG, "RX descriptorsetup failed.");
                    }
                    assert desc != null;
                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    if (!gatt.writeDescriptor(desc)) {
                        // Stop if the client descriptor could not be written.
                        Log.e(TAG, "Enable notification descriptor write failed.");
                    }

                }


                @Override
                // Characteristic notification
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    byte[] byte_array = characteristic.getValue();
                    double[] data = toDouble(toInt(byte_array));


                    double x = data[0];
                    double y = data[2];
                    double z = data[4];

//                    double pitch = (atan2(-z, y));
//                    double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
//                    double roll = atan2(-x, Math.sqrt(z * z + y * y)));
//                    System.out.println(pitch);
//                    System.out.println(roll);
                }
            };


    public int[] toInt(byte[] byte_array) {
        int[] int_array = new int[byte_array.length];
        for(int i = 0; i < int_array.length; i++){
            int_array[i] = (int) byte_array[i];
        }
        return int_array;
    }

    public double[] toDouble(int[] int_array) {
        double[] double_array = new double[int_array.length];
        for(int i = 0; i < int_array.length; i++){
            double_array[i] = (double) int_array[i];
        }
        return double_array;
    }


    public void closeConnection() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeConnection();
    }

    /** Called when the user taps the Play button */
    public void play(View view) {
        Log.i(TAG, "Play button pressed");
        Intent playIntent = new Intent(this, PlayActivity.class);
        startActivity(playIntent);
    }


}
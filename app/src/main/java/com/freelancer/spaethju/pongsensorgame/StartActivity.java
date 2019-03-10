package com.freelancer.spaethju.pongsensorgame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

public class StartActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private static final String TAG = "START";
    private final static int REQUEST_ENABLE_BT = 1;
    private TextInputEditText mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device does not support BT");
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "Bluetooth not enabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    /** Called when the user taps the Connect button */
    public void connect(View view) {
        Log.i(TAG, "Connect button pressed");
        Intent connectIntent = new Intent(this, ConnectActivity.class);
        TextInputEditText textEdit = (TextInputEditText) this.findViewById(R.id.macInput);
        String mac =  Objects.requireNonNull(textEdit.getText()).toString();
        if (mac.trim().length() != 17) {
            Log.e(TAG, "MAC address not valid");
            Toast.makeText(this, "No valid Mac adress", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "Establish connection");
            connectIntent.putExtra("MAC", mac.trim());
            startActivity(connectIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
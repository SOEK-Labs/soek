package com.soek.ponnex.angelhack;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jimbo Alvarez on 4/24/2016.
 */
public class BluetoothReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
            Log.e("BluetoothLEParser", "Bluetooth On"); //for debugging
            context.startService(new Intent(context, EntranceBeaconService.class));
        }

        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
            Log.e("BluetoothLEParser", "Bluetooth Off"); //for debugging
        }
    }

}
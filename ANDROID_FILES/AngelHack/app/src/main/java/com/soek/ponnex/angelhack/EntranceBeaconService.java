package com.soek.ponnex.angelhack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Jimbo Alvarez on 4/23/2016.
 */
public class EntranceBeaconService extends Service implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    private Identifier entrance_beacon_uuid;
    private Identifier entrance_beacon_maj;
    private Identifier entrance_beacon_min;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        entrance_beacon_uuid = Identifier.fromUuid(UUID.fromString("74278bda-b644-4520-8f0c-720eaf059935"));
        entrance_beacon_maj = Identifier.fromInt(1);
        entrance_beacon_min = Identifier.fromInt(1);

        setmBeaconManager();
        Log.e("EntranceBeacon", "CREATED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    private void setmBeaconManager() {
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24")); //working
        mBeaconManager.setRangeNotifier(this);

        mBeaconManager.bind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
        Log.e("EntranceBeacon", "STOP");
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("entrance_beacon", entrance_beacon_uuid, entrance_beacon_maj, entrance_beacon_min));
        } catch (RemoteException e) {
            Log.e("EntranceBeacon", "Stop scan beacon problem", e);
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        if (beacons.size() > 0) {
            Beacon inRangeBeacon = beacons.iterator().next();
            Log.e("EntranceBeacon", "didRangeBeaconsInRegion");

            if(inRangeBeacon.getId1().equals(entrance_beacon_uuid)) {
                Log.e("EntranceBeacon", "Entrance Beacon Detected");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("isLaunchedFromDetection", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                this.stopSelf();
            }
        }
    }

}

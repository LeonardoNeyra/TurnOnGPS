package com.neyra.leonardo.gpsexample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.provider.Settings;
import android.service.carrier.CarrierMessagingService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.Console;

import static com.google.firebase.crash.FirebaseCrash.log;

public class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    AlertDialog alert = null;
    LocationManager locationManager;
    public LocationManager handle;
    private String provider;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EnableGPSAutoMatically();
        //StartService();

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this,"GPS Desactivado",Toast.LENGTH_LONG).show();
            buildAlertMessageNoGps();
        }
        else{
            Toast.makeText(this,"GPS Activado",Toast.LENGTH_LONG).show();
        }*/

        /*if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); // this is the key ingredient

            BroadcastReceiver.PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new CarrierMessagingService.ResultCallback() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final WifiConfiguration.Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(MainActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;

                    }
                }
            });
            googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        }*/
    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            toast("Success");
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            toast("GPS is not on");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                toast("Error");
            }
        }
    }

    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            log("Window has been closed");
        }
    }

    @SuppressLint("MissingPermission")
    public void StartService(){
        handle = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = handle.getBestProvider(criteria, true);
        handle.requestLocationUpdates(provider, 10000, 1, this);
    }

    private boolean buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
        return true;
    }

    /*private void turnGpsOn (Context context) {
        String beforeEnable = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String newSet = String.format ("%s,%s", beforeEnable, LocationManager.GPS_PROVIDER);
        try {
            Settings.Secure.putString (context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, newSet);
        } catch(Exception e) {}
    }

    private void turnGPSOn2(){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);
        }
    }*/



    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null){
            alert.dismiss();
        }
    }*/

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /////////////////////////////////////////////////

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }
}

/*
*
* Links *
*
 */

//https://gist.github.com/irfaan008/f977c043ff7dfe3f71ce979a2ee52ebe
//https://www.youtube.com/watch?v=nenaJb66JL8
//https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API
//https://www.instructables.com/id/Turn-on-GPS-Programmatically-in-Android-44-or-High/
//https://www.youtube.com/watch?v=EIGSIxJP6Zs
//https://stackoverflow.com/questions/25175522/how-to-enable-location-access-programmatically-in-android

//https://code.i-harness.com/es/q/480b29
//https://stackoverflow.com/questions/4721449/how-can-i-enable-or-disable-the-gps-programmatically-on-android
//https://es.stackoverflow.com/questions/110566/c%C3%B3mo-puedo-activar-el-gps-autom%C3%A1ticamente-en-android
//https://stackoverflow.com/questions/9928256/how-to-turn-on-the-gps-on-android
//https://stackoverflow.com/questions/15426144/how-can-i-enable-disable-gps-programmatically-in-android
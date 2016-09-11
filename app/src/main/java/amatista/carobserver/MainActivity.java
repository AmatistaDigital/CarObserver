package amatista.carobserver;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {
    private GoogleApiClient mGoogleApiClient;
    private int MY_PERMISSIONS_ACCESS_FINE_LOCATION;
    private int REQUEST_CHECK_SETTINGS;
    private LocationRequest mLocationRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("onCreate", "123");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.w("onLocationChanged", location.getLatitude() + " " + location.getLongitude() + " " + location.getSpeed());
        TextView textView = (TextView)findViewById(R.id.id_textLocation);
        textView.setText(location.getLatitude() + " " + location.getLongitude() + " " + location.getSpeed());
    }

    protected void onStart() {
        Log.w("onStart", "123");
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        Log.w("onStop", "123");
        // mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnectionSuspended(int reason) {
        Log.w("onConnectionSuspended", reason + "");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.w("onConnected", "onConnected");
        try {

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);

        } catch(SecurityException e) {
            Log.w("onConnected", e.getMessage());
        }

    }

    @Override
    public void onResult(LocationSettingsResult result) {
        final Status status = result.getStatus();
        Log.w("onResult", status.getStatusCode() + "");
        Log.w("onResult", LocationSettingsStatusCodes.SUCCESS + "");
        Log.w("onResult", LocationSettingsStatusCodes.RESOLUTION_REQUIRED + "");
        Log.w("onResult", LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE + "");
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                try {
                    Log.w("onRequestPermissionsRes", "w00t");
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
                } catch(SecurityException e) {
                    Log.w("onRequestPermissionsRes", e.getMessage());
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequest);
                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                                builder.build());
                result.setResultCallback(this);
            } else {
                Log.w("onRequestPermissionsRes", grantResults.toString());
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w("onConnectionFailed", result.toString());
    }
}

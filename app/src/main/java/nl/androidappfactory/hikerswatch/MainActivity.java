package nl.androidappfactory.hikerswatch;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {

    LocationManager locationManager;
    Location location;
    String provider;

    TextView latTV;
    TextView lngTV;
    TextView accuracyTV;
    TextView speedTV;
    TextView bearingTV;
    TextView altitudeTV;
    TextView providerTV;
    TextView addressTV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTV = (TextView) findViewById(R.id.lat);
        lngTV = (TextView) findViewById(R.id.lng);
        accuracyTV = (TextView) findViewById(R.id.accuracy);
        speedTV = (TextView) findViewById(R.id.speed);
        bearingTV = (TextView) findViewById(R.id.bearing);
        altitudeTV = (TextView) findViewById(R.id.altitude);
        providerTV = (TextView) findViewById(R.id.provider);
        addressTV = (TextView) findViewById(R.id.address);

        getLocation();
        onLocationChanged(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        checkPermission();
        locationManager.removeUpdates(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermission();
        locationManager.requestLocationUpdates(provider, 2000, 3, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        Double alt = location.getAltitude();
        Float bearing = location.getBearing();
        Float speed = location.getSpeed() * 3.6f;
        Float accuracy = location.getAccuracy();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> listAddresses = geocoder.getFromLocation(lat, lng, 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                Log.i("PlaceInfo", listAddresses.get(0).toString());

                String addressHolder = "";

                for (int i = 0; i <= listAddresses.get(0).getMaxAddressLineIndex(); i++) {

                    addressHolder += listAddresses.get(0).getAddressLine(i) + "\n";

                }

                addressTV.setText("Address:\n" + addressHolder);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        latTV.setText("Latitude: " + lat.toString());
        lngTV.setText("Longitude: " + lng.toString());
        altitudeTV.setText("Altitude: " + alt.toString() + "m");
        bearingTV.setText("Bearing: " + bearing.toString());
        speedTV.setText("Speed: " + speed.toString() + "km/h");
        accuracyTV.setText("Accuracy: " + accuracy.toString() + "m");
        providerTV.setText("Location provider: " + provider);


        Log.i("Latitude", String.valueOf(lat));
        Log.i("Longitude", String.valueOf(lng));
        Log.i("altitude", String.valueOf(alt));
        Log.i("bearing", String.valueOf(bearing));
        Log.i("speed", String.valueOf(speed));
        Log.i("accuracy", String.valueOf(accuracy));


    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // location service disabled
            } else {
                // First try location from GPS Provider
                checkThePermission();
                if (isGPSEnabled) {
                    provider = LocationManager.GPS_PROVIDER;
                    Log.d("Log.d", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(provider);
                    }
                }

                if (isNetworkEnabled) {
                    if (location == null) {
                        provider = LocationManager.NETWORK_PROVIDER;
                        Log.d("Log.d", "Network Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(provider);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error : Location",
                    "Impossible to connect to LocationManager", e);
        }
    }


    private boolean checkThePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return true;

            }
        }
        return false;
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

    private boolean checkPermission() {


        return Build.VERSION.SDK_INT >= 23 && !(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);

    }


}

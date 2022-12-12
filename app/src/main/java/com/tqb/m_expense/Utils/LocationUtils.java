package com.tqb.m_expense.Utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    private LocationManager locationManager;
    private Context context;

    private LocationUtils(Context context) {
        this.context = context;
    }

    public LocationUtils setManager(LocationManager locationManager) {
        this.locationManager = locationManager;
        return this;
    }

    public static LocationUtils init(Context context, LocationManager locationManager) {
        LocationUtils instance = new LocationUtils(context);
        instance.setManager(locationManager);

        return instance;
    }

    public boolean isLocationEnabled() {
        return isGpsEnabled() || isNetworkEnabled();
    }

    public boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isNetworkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // get last known location
    public Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            Log.d("LocationUtils", "Need permission to get location.");
            return null;
        }
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);


            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }

        Log.d("Location", "found best location:  " + bestLocation);
        return bestLocation;
    }

    public void updateLocation(LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            Log.d("LocationUtils", "Need permission to get location.");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, locationListener);
    }

    public String getAddress() {
        Location location = getLastKnownLocation();
        if (location == null) return null;
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                Log.d("Location", "get address: " + location.getLatitude() + ", " + location.getLongitude());
                List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    // full address
                    sb.append(address.getAddressLine(0));

                }
                if (addresses.size() == 0) {
                    return "No address found";
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Location", "Geocoder not present");
            return null;
        }
        return null;
    }

    public float[] getLatLong() {
        Location location = getLastKnownLocation();
        if (location == null) return null;
        return new float[]{(float) location.getLatitude(), (float) location.getLongitude()};
    }




}


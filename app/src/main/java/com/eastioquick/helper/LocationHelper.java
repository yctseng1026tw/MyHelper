package com.eastioquick.helper;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


public class LocationHelper {
    Context context;
    public LocationHelper(Context context){
        this.context=context;
    }
    public Location getLocation() throws SecurityException {
        LocationManager locMan = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Location location = locMan
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locMan
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }
    public Location getLocation2() throws SecurityException{
        Location location=null;
        LocationManager locationManager=null;
        long MIN_TIME_BW_UPDATES=0l;
        float MIN_DISTANCE_CHANGE_FOR_UPDATES=0f;
        android.location.LocationListener listener=new
                android.location.LocationListener(){
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
                };
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            //
            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                //this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,listener );

                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            /*
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                            */
                        }
                    }
                }
            }
            locationManager.removeUpdates(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public GeoPoint getGeoByLocation(Location location) {
        GeoPoint gp = null;
        try {
            if (location != null) {
                double geoLatitude = location.getLatitude() * 1E6;
                double geoLongitude = location.getLongitude() * 1E6;
                gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gp;
    }

    public Address getAddressbyGeoPoint( GeoPoint gp) {
        Address result = null;
        try {
            if (gp != null) {
                Geocoder gc = new Geocoder(context, Locale.TRADITIONAL_CHINESE);

                double geoLatitude = (int) gp.getLatitudeE6() / 1E6;
                double geoLongitude = (int) gp.getLongitudeE6() / 1E6;

                List<Address> lstAddress = gc.getFromLocation(geoLatitude,
                        geoLongitude, 1);
                if (lstAddress.size() > 0) {
                    result = lstAddress.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public boolean isGPSEnable(){
        LocationManager locMan = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public void enableGPS(boolean isEnable){
        if(isEnable){
            turnGPSOn();
        }else{
            turnGPSOff();
        }
    }
    @Deprecated
    private void turnGPSOn(){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);
        }
    }
    @Deprecated
    private void turnGPSOff(){
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);
        }
    }
}

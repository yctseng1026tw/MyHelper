package productivity.eastioquick.com.myhelper;

import android.app.Activity;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.eastioquick.helper.LocationHelper;
import com.google.android.maps.GeoPoint;
/*
compileSdkVersion 16 to
compileSdkVersion "Google Inc.:Google APIs:16"
*/
public class LocationActivity extends Activity {

    private TextView longitude;
    private TextView latitude;
    private TextView address;
    private Switch switchGPS;
    LocationHelper helper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        helper=new LocationHelper(this);

        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);
        address = (TextView) findViewById(R.id.address);
        switchGPS=(Switch)findViewById(R.id.switchGPS);
        switchGPS.setChecked(helper.isGPSEnable());
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                helper.enableGPS(isChecked);
            }
        });


        Location mLocation = helper.getLocation();
        GeoPoint gp = helper.getGeoByLocation(mLocation);
        Address mAddress = helper.getAddressbyGeoPoint(gp);

        longitude.setText("Longitude: " + mLocation.getLongitude());
        latitude.setText("Latitude: " + mLocation.getLatitude());
        address.setText("Address: " + mAddress.getCountryName() + "," + mAddress.getLocality());

    }
}
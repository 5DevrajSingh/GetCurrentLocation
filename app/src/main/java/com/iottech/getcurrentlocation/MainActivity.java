package com.iottech.getcurrentlocation;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.skydoves.elasticviews.ElasticButton;
import com.skydoves.elasticviews.ElasticImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean isPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocation;
    private int GPS_REQUEST_CODE = 9001;
    EditText searchText;
    ElasticImageView searchButton;
    FloatingActionButton myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchText = findViewById(R.id.search_location_editText);
        searchButton = findViewById(R.id.search_button);
        myLocation = findViewById(R.id.my_location);

        checkPermissionLocation();

        initiMap();

        mFusedLocation = new FusedLocationProviderClient(this);

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
                viewAddress();
            }
        });


        if (isPermissionGranted) {

            searchButton.setOnClickListener(this::geoLocate);
//            viewAddress();
        }

    }

    private void viewAddress() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocation.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    if (location!=null) {
                        List<Address> addressesList = geocoder.getFromLocation(location.getLatitude()
                                , location.getLongitude(), 1);

//                        pinCode.setText(addressesList.get(0).getPostalCode());
//                        city.setText(addressesList.get(0).getSubLocality());
//                        address.setText(addressesList.get(0).getAddressLine(0));
//
//                        name.setText(GroceryConst.sharedPreferences.getString(GroceryConst.KEY.USER_NAME,""));
//                        contact.setText(GroceryConst.sharedPreferences.getString(GroceryConst.KEY.USER_MOBILE,""));
//                        email.setText(GroceryConst.sharedPreferences.getString(GroceryConst.KEY.USER_EMAIL,""));
//
//
//
//                        GroceryConst.currentLattitude = location.getLatitude();
//                        GroceryConst.currentLongitude = location.getLongitude();
//
//                        Log.d("checkDistance......", "current longitude : " + currentLongitude.toString() +
//                                "\n current Latitude : " + currentLattitude);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void geoLocate(View view) {

        String locationName = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                System.out.println("checkMyLocation....."+location);
//                try {
//                    List<Address> addressesList = geocoder.getFromLocationName(locationName, 1);
//
//                    if (addressesList.size() > 0) {
//                        getLocation(location.getLatitude(), location.getLongitude());
//
//                        System.out.println("checkMyLocation....."+addressesList.get(0).getAddressLine(0));
//                    }
//
//
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }
            }
        });


        try {
            List<Address> addressesList = geocoder.getFromLocationName(locationName, 1);

            if (addressesList.size() > 0) {
                Address address1 = addressesList.get(0);
                getLocation(address1.getLatitude(), address1.getLongitude());
                mMap.addMarker(new MarkerOptions().position(new LatLng(address1.getLatitude(), address1.getLongitude())));
//                pinCode.setText(addressesList.get(0).getPostalCode());
//                city.setText(addressesList.get(0).getSubLocality());
//                address.setText(addressesList.get(0).getAddressLine(0));

                Toast.makeText(MainActivity.this, address1.getLocality(), Toast.LENGTH_SHORT).show();
            }


        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void initiMap() {

        if (isPermissionGranted) {

            if (gpsIsEnable()) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(this);
            }
        }
    }

    private boolean gpsIsEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Gps Permission")
                    .setMessage("Gps is required, please enable the gps")
                    .setPositiveButton("yes", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }).setCancelable(false)
                    .show();
        }

        return false;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        mFusedLocation.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {

                    getLocation(location.getLatitude(), location.getLongitude());
                }
            }
        });

    }

    private void getLocation(double latitude, double longitude) {

        LatLng latLong = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions().position(latLong).title("You are here");

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLong, 14);
        mMap.moveCamera(cameraUpdate);
        mMap.addMarker(markerOptions);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    //// this is the permission dialog

    public void checkPermissionLocation() {

        Dexter.withContext(this).withPermission
                (Manifest.permission.ACCESS_FINE_LOCATION).withListener
                (new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Permission is Granted", Toast.LENGTH_SHORT).show();
                        isPermissionGranted = true;

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), "");
                        intent.setData(uri);
                        startActivity(intent);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();

                    }
                }).check();
    }

    ///////////////

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
//        mMap.setMyLocationEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnabled) {
                Toast.makeText(MainActivity.this, "Gps is enable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gps is not enable", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
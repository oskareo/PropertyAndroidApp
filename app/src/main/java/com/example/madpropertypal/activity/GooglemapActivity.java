package com.example.madpropertypal.activity;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.madpropertypal.R;
import com.example.madpropertypal.databinding.ActivityGooglemapBinding;
import com.example.madpropertypal.model.LocationModel;
import com.example.madpropertypal.util.Constants;
import com.example.madpropertypal.util.Utils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GooglemapActivity extends AppCompatActivity {
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    private List<Address> addresses;
    private boolean Camera_Moved = false;
    private Marker marker;
    private FusedLocationProviderClient fusedLocationClient;
    private android.location.Location mLastLocation;
    private ActivityGooglemapBinding binding;
    private LocationModel locationModel;
    private static final int LOCATION_SETTINGS_REQUEST = 2124;
    private static final int LOCATION_PERMISSIONS = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGooglemapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        listeners();
    }

    private void listeners() {
        binding.btnCurrentLocation.setOnClickListener(v -> {
            getPermissions();
            getLocation();
        });
        binding.btnSearch.setOnClickListener(v -> startSearchActivity());

        binding.btnProceed.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(Constants.LOCATION_DATA, (Serializable) locationModel);
            setResult(Constants.GET_LOCATION, intent);
            finish();
        });

    }

    private void startSearchActivity() {
        // Create a new Places client instance
        //Start AutoComplete by Intent
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(GooglemapActivity.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void init() {
        binding.btnProceed.setClickable(false);
        binding.btnProceed.setEnabled(false);
        getPermissions();
        Places.initialize(getApplicationContext(), "AIzaSyALXaA5LSPJTExiJBW18EMumOolBg0LjVM");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getPermissions() {
        if (isGpsEnabled()) {
            if (!checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(GooglemapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(GooglemapActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(GooglemapActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSIONS);
                }
            } else {
                initiateMap();
            }
        } else {
            enableLocation();
        }

    }

    private void initiateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(google_map -> {
            googleMap = google_map;
            animateCamera();
        });
    }

    private void animateCamera() {
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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mLastLocation = location;
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        try {
                            displayMarker(currentLocation.latitude, currentLocation.longitude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                });
    }

    private void displayMarker(double lat, double lon) throws IOException {
        LatLng loc = new LatLng(lat, lon);
        Log.v("TestingNewLoc", "Entered in displayMarker");
        if (!Camera_Moved) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));
            Camera_Moved = true;
        }
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions().position(loc).title("").snippet("").draggable(true));
        marker.setIcon(bitmapFromVector());
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        getMoreValues(lat, lon);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.i("asdad", "Start");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.i("asdad", "Start");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.i("asdafsad", "Start");
                LatLng latLng = marker.getPosition();
                android.location.Location location = new android.location.Location("");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                getMoreValues(latLng.latitude, latLng.longitude);
            }
        });
    }

    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private void getMoreValues(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 5); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            for (Address address1 : addresses) {
                if (address1 != null) {
                    address = address1.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    Log.v("TestingNewLoc", "address: " + address);
                    String city = addresses.get(0).getLocality();
                    Log.v("TestingNewLoc", "city: " + city);
                    String state = addresses.get(0).getAdminArea();
                    Log.v("TestingNewLoc", "state: " + state);
                    String country = addresses.get(0).getCountryName();
                    Log.v("TestingNewLoc", "country: " + country);
                    String postalCode = addresses.get(0).getPostalCode();
                    Log.v("TestingNewLoc", "postalCode: " + postalCode);
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    Log.v("TestingNewLoc", "knownName: " + knownName);
                    if (city == null) {
                        city = state;
                    }
                    if (address == null) {
                        address = knownName + ", " + city + ", " + country;
                    }
                    locationModel = new LocationModel(latitude, longitude, address, city, country);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            address = Utils.getCompleteAddressString(this, latitude, longitude);
            locationModel = new LocationModel(latitude, longitude, address, "city", "country");
        }


        binding.btnProceed.setClickable(true);
        binding.btnProceed.setEnabled(true);

    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkLocationPermission()) {
                getDeviceLocation();
            } else {
                getPermissions();
            }
        } else {
            getDeviceLocation();
        }
    }

    private void getDeviceLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        assert locationManager != null;
        boolean isGPSLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkLocation = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSLocation && !isNetworkLocation) {
            Log.v("TestingCurrentLoc", "if (!isGPSLocation && !isNetworkLocation)");
            requestNewLocationData();
        } else {
            Log.v("TestingCurrentLoc", "else get final Location");
            requestNewLocationData();
        }
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLastLocation = locationResult.getLastLocation();
            Log.v("TestingNewLoc", "Latitude: " + mLastLocation.getLatitude());
            Log.v("TestingNewLoc", "Longitude: " + mLastLocation.getLongitude());
            getMoreValues(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            try {
                displayMarker(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private boolean isGpsEnabled() {
        LocationManager lm = (LocationManager) GooglemapActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            assert lm != null;
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }
        return gps_enabled;
    }

    private void enableLocation() {
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(GooglemapActivity.this)
                .checkLocationSettings(settingsBuilder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException ex) {
                switch (ex.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException =
                                    (ResolvableApiException) ex;
                            resolvableApiException
                                    .startResolutionForResult(GooglemapActivity.this,
                                            LOCATION_SETTINGS_REQUEST);
                        } catch (IntentSender.SendIntentException ignored) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            String TAG = "GoogleMapsLog";
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.v("TestingNewLoc", "Place: " + place.getName() + ", " + place.getId());
                if (marker != null) {
                    marker.remove();
                }
                Log.i("SelectedLocation", "" + place.getLatLng());

                try {
                    displayMarker(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mLastLocation = new android.location.Location("");
                mLastLocation.setLatitude(place.getLatLng().latitude);
                mLastLocation.setLongitude(place.getLatLng().longitude);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
                assert status.getStatusMessage() != null;
                Log.v(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == LOCATION_SETTINGS_REQUEST) {
            if (resultCode == RESULT_OK) {
                Utils.showShortToast(GooglemapActivity.this, "Location Enabled");
                init();
            } else {
                Utils.showShortToast(GooglemapActivity.this, "Location Not Enabled");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(GooglemapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(GooglemapActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    initiateMap();
                }
            } else {
                Toast.makeText(GooglemapActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(GooglemapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return ContextCompat.checkSelfPermission(GooglemapActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        } else {
            return false;
        }
    }

    private BitmapDescriptor bitmapFromVector() {
        Drawable vector = ContextCompat.getDrawable(GooglemapActivity.this, R.drawable.ic_location);
        vector.setBounds(0, 0, vector.getIntrinsicWidth(), vector.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                vector.getIntrinsicWidth(),
                vector.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        vector.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
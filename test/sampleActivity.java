package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.annotation.SuppressLint;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class sampleActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location lastLocation;
    private LocationManager locationManager;
    public View cardView;
    public TextView rName, rAddr, rMenu, rTel, rBusinessHours, rExplanation;
    // 사용할 이미지 뷰
    private ImageView food;
    private static final String jsonFile = "jsons/busanRestaurant.json";


    private static String[] PERMISSIONS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    final LatLng CITY_HALL = new LatLng(35.17914523506671, 129.07492106277513);
    private GoogleMap gMap;
    ArrayList<MyItem> restaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapG);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;
        if (getLocationPermission()) {
            initMap(gMap);
        }
        else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);
        }
        cardView = findViewById(R.id.cardView_Rest);

        rName = findViewById(R.id.RestNametxt);
        rBusinessHours = findViewById(R.id.businessHourstxt);
        rAddr = findViewById(R.id.RestAddrtxt);
        rMenu = findViewById(R.id.RestMenutxt);
        rTel = findViewById(R.id.RestTeltxt);
        rExplanation = findViewById(R.id.RestExptxt);

        // 이미지뷰랑 연결한 부분
        food = findViewById(R.id.foodImage);

        gMap.setOnMyLocationButtonClickListener(this);
        gMap.setOnMyLocationClickListener(this);
        Toast.makeText(getApplicationContext(), "count" + restaurants.size(), Toast.LENGTH_LONG).show();

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                cardView.setVisibility(View.INVISIBLE);
            }
        });

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                MyItem item = null;
                for (int i = 0; i < restaurants.size(); i++) {
                    if(restaurants.get(i).findIndex(marker.getTitle()) != -1) {
                        item = restaurants.get(i);
                    }
                }
                // 이미지 주소 받은 부분
                String imageUrl = item.getImageurl();
                rName.setText(item.getTitle());
                rBusinessHours.setText("영업시간: " + item.getBusinessHours());
                rAddr.setText("주소: " + item.getAddr());
                rMenu.setText("메뉴: " + item.getMenu());
                rTel.setText("전화번호: " + item.getTelNum());
                rExplanation.setText("가게 설명: " + item.getExplanation());
                //이거 통해서 연결하시면 되요.
                Glide.with(sampleActivity.this).load(imageUrl).into(food);
                cardView.setVisibility(View.VISIBLE);

            }
        });

    }

    // 퍼미션 확인부분
    private Boolean getLocationPermission() {
        for (int i = 0; i < 2; i++) {
            String permission = PERMISSIONS[i];
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    public void initMap(GoogleMap googleMap) {
        if (getLocationPermission()) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            addMarker(googleMap);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(), 15));
        }
        else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, 15));
        }


    }

    @SuppressLint("MissingPermission")
    public LatLng getMyLocation() {
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lastLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastLocation != null) {
            return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        else {
            return CITY_HALL;
        }
    }

    public void searchMyLocation() {
        if (getLocationPermission()) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(), 15));
        }
        else {
            Toast.makeText(getApplicationContext()
                    , "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    public JSONArray loadJson(String jsonFile) {
        String bf = "";

        JSONArray restaurant = null;
        try {
            InputStream is = getAssets().open(jsonFile);

            int FileSize = is.available();
            byte[] buffer = new byte[FileSize];
            is.read(buffer);
            is.close();

            bf = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(bf);

            restaurant = jsonObject.getJSONArray("item");

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurant;
    }

    public void addMarker(GoogleMap googleMap) {
        JSONArray restaurant = loadJson(jsonFile);
        try {
            for (int i = 0; i < restaurant.length(); i++) {
                JSONObject rest = restaurant.getJSONObject(i);
                MyItem item = new MyItem(rest.getInt("UC_SEQ"), rest.getString("TITLE"), rest.getString("ADDR1"), rest.getString("CNTCT_TEL")
                        , rest.getString("RPRSNTV_MENU"), rest.getString("USAGE_DAY_WEEK_AND_TIME"), rest.getString("ITEMCNTNTS")
                        , rest.getDouble("LAT"), rest.getDouble("LNG"), rest.getString("MAIN_IMG_NORMAL"));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(item.getLat(), item.getLng()));
                markerOptions.title(item.getTitle());
                markerOptions.snippet(item.getMenu());

                googleMap.addMarker(markerOptions);
                restaurants.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
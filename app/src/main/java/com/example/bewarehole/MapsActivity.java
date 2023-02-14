package com.example.bewarehole;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bewarehole.Model.PlaceHoleModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bewarehole.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener
        , GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapLongClickListener
        , LocationListener {
     private GoogleMap mMap;
     private ActivityMapsBinding binding;
     final private boolean COARSELocationPermission = true;


    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation=null;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 19;
    LocationManager locationManager ;
    boolean GpsStatus=false;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private Marker m;
    private String userid;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceplacehole = database.getReference().child("datebase").child("PlaceHole");
    BottomsheetViewModel viewModel;
    bottomsheetFragment sheetFragment;
    Double distance;
    ArrayList<PlaceHoleModel> listzoom = new ArrayList<>();
    ArrayList<LatLng> arrnearsthole = new ArrayList<>();
    double curTime=0;
    double Totaldistance = 0;
    MediaPlayer mp;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 2 sec */
    Marker nearst;
    long holenumber=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setLayoutDirection(getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userid = extras.getString("userid");
        }




        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        placesClient = Places.createClient(this);


        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = getFusedLocationProviderClient(this);


        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.yourlocationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if(GpsStatus)
                {
                    getDeviceLocation();
                }
                else
                {
                     enableGps();
                }

            }
        });

        binding.addhole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m!= null) {
                    new AlertDialog.Builder(mapFragment.getContext())
                            .setTitle("اضافة مكان الحفرة")
                            .setMessage("هذا يعتبر تعهد منك بان مكان الحفرة الذي ادخلته صحيح وعدم صحة هذا المعلومات يغتبر مخالفة للقواعد وشروط البرنامج وقد يعرضك لغرامة مالية كبيرة وان كنت متاكد من معلوماتك اضغط  علي  زر الاضافة غير ذلك اضغط ع زر الغاء")
                            .setPositiveButton(
                                    "اضافة",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            sheetFragment = new bottomsheetFragment(false, userid, m.getPosition().latitude + "," + m.getPosition().longitude);
                                            sheetFragment.show(getSupportFragmentManager(), "h");

                                        }
                                    })
                            .setNegativeButton(
                                    "الغاء",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }).show();


                }
                else {
                    Toast.makeText(getApplicationContext(), "من فضلك اختار  مكان وجود الحفرة اولا  ثم الضعط علي زر اضافة", Toast.LENGTH_SHORT).show();

                }

            }
        });
        binding.addroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m != null) {
                  /*    ResponseDirectionServiceApi directionServiceApi=new Retrofit.Builder()
                             .baseUrl("https://maps.googleapis.com/maps/")
                             .addConverterFactory(GsonConverterFactory.create())
                             .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                             .build()
                             .create(ResponseDirectionServiceApi.class);
                   */

                    if(lastKnownLocation!=null) {
                        LatLng d = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        // distance = SphericalUtil.computeDistanceBetween(d, m.getPosition());
                        distance = CalculationByDistance(d, m.getPosition());
                        Toast.makeText(getApplicationContext(), "" + (distance), Toast.LENGTH_SHORT).show();
                    }else{

                        Toast.makeText(getApplicationContext(),"لا نستطيع تحديد موقعك الحالي من فضلك تاكد من اذونات الموقع وفتح الGPS" ,
                                Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "من فضلك اختار  وجهتك اولا  ثم قم بالضعط علي  هذا الزر ", Toast.LENGTH_SHORT).show();

                }
            }
        });
        viewModel = new ViewModelProvider(this).get(BottomsheetViewModel.class);
        viewModel.getcompleteaddhole().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
              getlistmarker();
              getlistholezoom();
            }
        });

    }
    public void enableGps(){

        LocationRequest mLocationRequestGps = new LocationRequest();
        mLocationRequestGps.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestGps.setInterval(UPDATE_INTERVAL);
        mLocationRequestGps.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequestGps).setAlwaysShow(true);
        Task<LocationSettingsResponse>locationSettingsResponseTask=LocationServices.
                getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response=task.getResult(ApiException.class);
                } catch (ApiException e) {
                    e.printStackTrace();
                    if(e.getStatusCode()== LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                        ResolvableApiException resolvableApiException= (ResolvableApiException) e;

                        try {
                            resolvableApiException.startResolutionForResult(MapsActivity.this,55);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }

                    }
                    if(e.getStatusCode()==LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE){

                        Toast.makeText(getApplicationContext(),"faileddd",Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==55){
            if(resultCode==RESULT_OK){

                getDeviceLocation();

            }

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getlistmarker();
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(GpsStatus) {
            getDeviceLocation();
            getlistholezoom();
        }else{
            enableGps();
        }

        //draw line
        /*        ArrayList<LatLng>points=new ArrayList<>();
        points.add(new LatLng(29.081548, 31.089986));
        points.add(new LatLng(29.082019, 31.090112));
        points.add(new LatLng(29.082079, 31.090126));
        points.add(new LatLng(29.082110, 31.089955));
        points.add(new LatLng(29.082185, 31.089499));
        PolylineOptions polylineOptions=new PolylineOptions();
        polylineOptions.addAll(points);
        polylineOptions.width(12);
        polylineOptions.color(Color.RED);
        polylineOptions.geodesic(true);
        mMap.addPolyline(polylineOptions);
   */


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
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                defaultLocation, 15));

        //mMap.setMyLocationEnabled(true);
        //mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    public void getlistholezoom() {
        listzoom=new ArrayList<>();
        referenceplacehole.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null&&lastKnownLocation!=null){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        PlaceHoleModel model = snapshot1.getValue(PlaceHoleModel.class);
                        LatLng l = new LatLng(model.getLatitudelocation(), model.getLongitudelocation());
                        LatLng d = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        // distance = SphericalUtil.computeDistanceBetween(d, m.getPosition());
                        double dist = CalculationByDistance(d, l);
                        if (dist <= 8000) {
                            listzoom.add(model);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void getlistmarker() {

        referenceplacehole.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null){
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    PlaceHoleModel model = snapshot1.getValue(PlaceHoleModel.class);
                    LatLng l = new LatLng(model.getLatitudelocation(), model.getLongitudelocation());
                    mMap.addMarker(new MarkerOptions().position(l).title(model.getTitlekind())
                            .snippet(model.getSnippetdesciption()).icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.blackhole)).flat(true));
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getcount(){
        referenceplacehole.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holenumber=snapshot.getChildrenCount();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (COARSELocationPermission) {

                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            curTime = System.currentTimeMillis();
                            if (lastKnownLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                Totaldistance=0;
                                getlistholezoom();
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        if (m != null) {
            m.remove();
        }
        m = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker").snippet("hello my friend").flat(true));

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        startLocationUpdates();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest).setAlwaysShow(true);


        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)

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
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                        // do work here

                if(locationResult.getLastLocation()!=null){

                    if (lastKnownLocation == null) {
                        lastKnownLocation = locationResult.getLastLocation();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    }

                    if(lastKnownLocation!=null) {

                        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if(!GpsStatus){
                            enableGps();
                        }
                        if(GpsStatus){
                                onLocationChanged(locationResult.getLastLocation());


                        }

                    }
                }
            }
            },
                Looper.myLooper());


    }

    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        double di=CalculationByDistance(new LatLng(location.getLatitude(),location.getLongitude()),new LatLng(
                lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()
        ));
        if(di>=7){
            Totaldistance=Totaldistance+di;
            lastKnownLocation=location;
            getwarm();
        }

    }

    public void getwarm(){

        arrnearsthole=new ArrayList<>();

        if (listzoom.size() >= 1) {
             double min=40;
             LatLng latLng=null;
             if(nearst!=null){
             nearst.remove();
             }
             for ( int i = 0 ; i < listzoom.size(); i++) {

                double p = CalculationByDistance(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                        new LatLng(listzoom.get(i).getLatitudelocation()
                                ,listzoom.get(i).getLongitudelocation()
                        ));
                if (p<= 35) {
                    if(p<min){
                        min=p;
                        latLng=new LatLng(listzoom.get(i).getLatitudelocation()
                                ,listzoom.get(i).getLongitudelocation());
                    }
                    arrnearsthole.add(new LatLng(listzoom.get(i).getLatitudelocation()
                            , listzoom.get(i).getLongitudelocation()
                    ));

                }
            }
            if (arrnearsthole.size() >= 1) {
                binding.txtWarm.setVisibility(View.VISIBLE);
                nearst = mMap.addMarker(new MarkerOptions().position(latLng).title("هذه اقرب حفرة منك").flat(true));

                Toast.makeText(getApplicationContext(), arrnearsthole.size() + "عدد الحفر", Toast.LENGTH_SHORT).show();
                mp = MediaPlayer.create(this, R.raw.morningalarm);
                mp.start();

            }
                else{

                    binding.txtWarm.setVisibility(View.GONE);
                }


        }
        else {
            if(nearst!=null) {
                nearst.remove();
            }
            binding.txtWarm.setVisibility(View.GONE);

        }

        if (Totaldistance >= 4000) {

            listzoom=new ArrayList<>();
            getlistholezoom();
            listzoom = new ArrayList<>();
            arrnearsthole = new ArrayList<>();
            curTime = System.currentTimeMillis();
            Totaldistance = 0;

        }

    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;

        return Radius * c * 1000;
    }
/*
    private void getspeed(Location location) {
        double newTime = System.currentTimeMillis();
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        LatLng n = new LatLng(newLat, newLon);
        if (location.hasSpeed()) {
            float speed = location.getSpeed();
            Totaldistance = Totaldistance + speed;
            Toast.makeText(getApplication(), "SPEED : " + String.valueOf(speed) + "m/s", Toast.LENGTH_SHORT).show();
        } else {
            double distanc = CalculationByDistance(n, new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            double timeDifferent = newTime - curTime;
            double speed = distance / timeDifferent;
            Totaldistance = Totaldistance + speed;
            Toast.makeText(getApplication(), "SPEED 2 : " + String.valueOf(speed) + "m/s", Toast.LENGTH_SHORT).show();

        }
        curTime = newTime;
        lastKnownLocation = location;

    }
*/


}


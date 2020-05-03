package com.aspectsense.pharmacyguidecy;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aspectsense.greektools.Greeklish;
import com.aspectsense.pharmacyguidecy.ui.PlaceRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class ActivityPharmacy extends AppCompatActivity {

    public static final String VIEW_NAME_HEADER_NAME = "VIEW_NAME_HEADER_NAME";
    public static final String VIEW_NAME_HEADER_ADDRESS = "VIEW_NAME_HEADER_ADDRESS";
    public static final String VIEW_NAME_HEADER_ADDRESS_DETAILS = "VIEW_NAME_HEADER_ADDRESS_DETAILS";
    public static final String VIEW_NAME_HEADER_DISTANCE = "VIEW_NAME_HEADER_DISTANCE";
    public static final String VIEW_NAME_HEADER_DISTANCE_UNIT = "VIEW_NAME_HEADER_DISTANCE_UNIT";

    private TextView nameTextView;
    private TextView addressTextView;
    private TextView addressDetailsTextView;
    private TextView distanceTextView;
    private TextView distanceUnitTextView;

    private ImageButton imageButtonDirections;
    private Button buttonDialPharmacy;
    private Button buttonDialHome;

    private GoogleMap mGoogleMap = null;

    private AdView adView;

    private SharedPreferences sharedPreferences;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        this.nameTextView = findViewById(R.id.nameTextView);
        this.addressTextView = findViewById(R.id.addressTextView);
        this.addressDetailsTextView = findViewById(R.id.addressDetailsTextView);
        this.distanceTextView = findViewById(R.id.distanceTextView);
        this.distanceUnitTextView = findViewById(R.id.distanceUnitTextView);

        ViewCompat.setTransitionName(nameTextView, VIEW_NAME_HEADER_NAME);
        ViewCompat.setTransitionName(addressTextView, VIEW_NAME_HEADER_ADDRESS);
        ViewCompat.setTransitionName(addressDetailsTextView, VIEW_NAME_HEADER_ADDRESS_DETAILS);
        ViewCompat.setTransitionName(distanceTextView, VIEW_NAME_HEADER_DISTANCE);
        ViewCompat.setTransitionName(distanceUnitTextView, VIEW_NAME_HEADER_DISTANCE_UNIT);

        imageButtonDirections = findViewById(R.id.imageButtonDirections);
        imageButtonDirections.setEnabled(false); // initially disabled - until pharmacy is loaded

        buttonDialPharmacy = findViewById(R.id.buttonDialPharmacy);
        buttonDialPharmacy.setEnabled(false);

        buttonDialHome = findViewById(R.id.buttonDialHome);
        buttonDialHome.setEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null) mapFragment.getMapAsync(googleMap -> {
            this.mGoogleMap = googleMap;
            updateUI();
        }); // callback for map ready event

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        this.adView = findViewById(R.id.adView);

        // ads API
        MobileAds.initialize(this);
        final AdRequest adRequest = new AdRequest.Builder().build();
        this.adView = findViewById(R.id.adView);
        this.adView.loadAd(adRequest);
    }

    private String pharmacyNameEn;
    private String pharmacyNameEl;
    private String pharmacyPhoneBusiness;
    private String pharmacyPhoneHome;
    private String pharmacyAddress;
    private String pharmacyAddressDetails;
    private String localityEn;
    private String localityEl;
    private String cityEn;
    private String cityEl;
    private float pharmacyLat;
    private float pharmacyLng;

    private Location passedLocation = null;
    private Location detectedUserLocation = null;

    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = getIntent();
        assert intent != null;

        handleIntent(intent);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(final Intent intent) {

        // final int pharmacyId = intent.getIntExtra(PlaceRecyclerAdapter.PHARMACY_ID, 0);
        pharmacyNameEn = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_NAME_EN);
        pharmacyNameEl = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_NAME_EL);
        pharmacyPhoneBusiness = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_PHONE_BUSINESS);
        pharmacyPhoneHome = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_PHONE_HOME);
        pharmacyAddress = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_ADDRESS);
        pharmacyAddressDetails = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_ADDRESS_DETAILS);
        localityEn = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_LOCALITY_EN);
        localityEl = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_LOCALITY_EL);
        cityEn = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_CITY_EN);
        cityEl = intent.getStringExtra(PlaceRecyclerAdapter.PHARMACY_CITY_EL);
        pharmacyLat = intent.getFloatExtra(PlaceRecyclerAdapter.PHARMACY_LAT, (float) ActivityHome.DEFAULT_LOCATION.getLatitude());
        pharmacyLng = intent.getFloatExtra(PlaceRecyclerAdapter.PHARMACY_LNG, (float) ActivityHome.DEFAULT_LOCATION.getLongitude());

        this.passedLocation = intent.getParcelableExtra(PlaceRecyclerAdapter.LOCATION);

        if (adView != null) { adView.resume(); }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (adView != null) { adView.pause(); }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) { adView.destroy(); }
        super.onDestroy();
    }

    private void updateUI() {

        // create intent to get directions to pharmacy
        final String directionsUri = "http://maps.google.com/maps?daddr=" + pharmacyLat + "," + pharmacyLng;
        final Intent directionsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionsUri));
        imageButtonDirections.setEnabled(Utils.isIntentAvailable(this, directionsIntent));
        imageButtonDirections.setOnClickListener(v -> startActivity(directionsIntent));

        // create intent for dialing pharmacy
        final Intent dialPharmacyIntent = new Intent(Intent.ACTION_DIAL);
        dialPharmacyIntent.setData(Uri.parse("tel: +357" + pharmacyPhoneBusiness));
        buttonDialPharmacy.setEnabled(Utils.isIntentAvailable(this, dialPharmacyIntent));
        buttonDialPharmacy.setText(pharmacyPhoneBusiness);
        buttonDialPharmacy.setOnClickListener(v -> startActivity(dialPharmacyIntent));

        // create intent for dialing home
        final Intent dialHomeIntent = new Intent(Intent.ACTION_DIAL);
        dialHomeIntent.setData(Uri.parse("tel: +357" + pharmacyPhoneHome));
        buttonDialHome.setEnabled(Utils.isIntentAvailable(this, dialHomeIntent));
        buttonDialHome.setText(pharmacyPhoneHome);
        buttonDialHome.setOnClickListener(v -> startActivity(dialHomeIntent));

        final Locale locale = getResources().getConfiguration().getLocales().get(0);
        final boolean useGreekCharacters = sharedPreferences.getBoolean(ActivityHome.PREF_USE_GREEK_CHARACTERS, true);
        final boolean isGreek = Utils.isGreek(locale) || useGreekCharacters;

        nameTextView.setText(isGreek ? pharmacyNameEl : pharmacyNameEn);

        final String greekAddress = pharmacyAddress;

        final boolean localityCoincidesWithCity = localityEl.equals(cityEl);
        final String fullGreekAddress = localityCoincidesWithCity ?
                String.format("%s, %s", greekAddress, localityEl) :
                String.format("%s, %s, %s", greekAddress, localityEl, cityEl);
        final String fullEnglishAddress = localityCoincidesWithCity ?
                String.format("%s, %s", Greeklish.toGreeklish(greekAddress), localityEn) :
                String.format("%s, %s, %s", Greeklish.toGreeklish(greekAddress), localityEn, cityEn);

        final String localizedAddress = isGreek ? fullGreekAddress : fullEnglishAddress;

        addressTextView.setText(localizedAddress);
        final String localizedAddressDetails = isGreek ? pharmacyAddressDetails : Greeklish.toGreeklish(pharmacyAddressDetails);
        addressDetailsTextView.setText(localizedAddressDetails);
        final float distance = Utils.distanceBetween(passedLocation, pharmacyLat, pharmacyLng);
        if (distance < 1000) {
            distanceUnitTextView.setText(R.string.m);
            distanceTextView.setText(String.format(locale, "%d", ((int) (distance / 10)) * 10));
        } else {
            distanceUnitTextView.setText(R.string.Km);
            final String formattedDistance = distance <= 99999 ?
                    String.format(locale, "%.1f", distance / 1000) : ">99";
            distanceTextView.setText(formattedDistance);
        }

        if(mGoogleMap != null) {
            final LatLng pharmacyLatLng = new LatLng(pharmacyLat, pharmacyLng);

            final String localizedName = isGreek ? pharmacyNameEl : pharmacyNameEn;
            final Marker pharmacyMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .title(localizedName)
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_place_black_24dp))
                    .snippet(localizedAddress)
                    .position(pharmacyLatLng));
            pharmacyMarker.showInfoWindow();


            // compute upper left and lower right corners
            final double offset = 0.01d;
            double maxLatitude = pharmacyLat + offset;
            double minLatitude = pharmacyLat - offset;
            double maxLongitude = pharmacyLng + offset;
            double minLongitude = pharmacyLng - offset;

            if(detectedUserLocation != null) {
                mGoogleMap.setMyLocationEnabled(true);

                final double lat = detectedUserLocation.getLatitude();
                final double lng = detectedUserLocation.getLongitude();
                if(maxLatitude < lat) maxLatitude = lat;
                if(minLatitude > lat) minLatitude = lat;
                if(maxLongitude < lng) maxLongitude = lng;
                if(minLongitude > lng) minLongitude = lng;
            }

            final LatLng southWestBound = new LatLng(minLatitude, minLongitude);
            final LatLng northEastBound = new LatLng(maxLatitude, maxLongitude);

            int width = getResources().getDisplayMetrics().widthPixels / 2;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southWestBound, northEastBound), width, height, padding));
        }
    }

    /**
     * https://stackoverflow.com/a/48356646
     */
    private BitmapDescriptor bitmapDescriptorFromVector(@DrawableRes int vectorDrawableResourceId) {
        final Drawable background = ContextCompat.getDrawable(this, vectorDrawableResourceId);
        assert background != null;
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        final Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorDrawableResourceId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        vectorDrawable.setTint(getColor(R.color.pharmacy_green));
        final Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pharmacy_menu, menu);
        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        menu.findItem(R.id.app_bar_menu_location).setEnabled(permissionAccessFineLocationApproved);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // close this activity and return to previous one (if any)
            case R.id.app_bar_menu_location:
                // App has permission to access location in the foreground. Start your foreground service that has a foreground service type of "location".
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            this.detectedUserLocation = location;
                            updateUI();
                        })
                        .addOnFailureListener(this, e -> Log.e(ActivityHome.TAG, "location error: " + e.getMessage()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.aspectsense.pharmacyguidecy;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aspectsense.pharmacyguidecy.data.City;
import com.aspectsense.pharmacyguidecy.data.DateToPharmacy;
import com.aspectsense.pharmacyguidecy.data.Locality;
import com.aspectsense.pharmacyguidecy.data.OnCall;
import com.aspectsense.pharmacyguidecy.data.UpdateResponse;
import com.aspectsense.pharmacyguidecy.ui.GreekCharactersViewModel;
import com.aspectsense.pharmacyguidecy.ui.LocationViewModel;
import com.aspectsense.pharmacyguidecy.ui.SearchViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.aspectsense.pharmacyguidecy.ui.SectionsPagerAdapter;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivityHome extends AppCompatActivity {

    public static final String TAG = "cpg";

    private static final String PREF_LAST_CHECKED_FOR_UPDATES    = "PREF_LAST_CHECKED_FOR_UPDATES";
    private static final String PREF_LAST_LOGGED_NEARBY_SEARCH   = "PREF_LAST_LOGGED_NEARBY_SEARCH";
    private static final String PREF_LATEST_TIMESTAMP            = "PREF_LATEST_TIMESTAMP";
    public static final String PREF_USE_GREEK_CHARACTERS         = "PREF_USE_GREEK_CHARACTERS";

    private final Executor executor = Executors.newSingleThreadExecutor();

    private ViewPager viewPager;
    private AdView adView;

    private RequestQueue queue;
    private final Gson gson = new Gson();
    private CPGDao cpgDao;
    private SharedPreferences sharedPreferences;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private LocationViewModel locationViewModel;
    private GreekCharactersViewModel greekCharactersViewModel;
    private SearchViewModel searchViewModel;

    private Snackbar snackbarLocationDenied;
    private Snackbar snackbarLocationDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        queue = Volley.newRequestQueue(this);
        cpgDao = CPGDatabase.getDatabase(this).cpgDao();
        cpgDao.getAllCitiesLiveData().observe(this, this::setCities);
        cpgDao.getAllLocalitiesLiveData().observe(this, this::setLocalities);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);

        snackbarLocationDenied = Snackbar
                .make(coordinatorLayout, R.string.Location_denied, BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction(R.string.Allow, v -> requestLocation());

        snackbarLocationDisabled = Snackbar
                .make(coordinatorLayout, R.string.Location_disabled, BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction(R.string.Enable, v -> requestEnableLocation());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    handleLocationUpdate(location);
                }
            }
        };

        this.locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        this.greekCharactersViewModel = new ViewModelProvider(this).get(GreekCharactersViewModel.class);
        this.searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // ads API
        MobileAds.initialize(this);
        final AdRequest adRequest = new AdRequest.Builder().build();
        this.adView = findViewById(R.id.adView);
        this.adView.loadAd(adRequest);
    }

    public LocationViewModel getLocationViewModel() {
        return locationViewModel;
    }

    public GreekCharactersViewModel getGreekCharactersViewModel() {
        return greekCharactersViewModel;
    }

    public SearchViewModel getSearchViewModel() {
        return searchViewModel;
    }

    static final Location DEFAULT_LOCATION = new Location(""); // Nicosia
    static {
        DEFAULT_LOCATION.setLatitude(35.1856d); // Nicosia 35.1856° N, 33.3823° E
        DEFAULT_LOCATION.setLongitude(33.3823d);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfDataNeedUpdate();
        requestLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationIsEnabledAndStartUpdates();

        if (adView != null) { adView.resume(); }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback); // stop location updates

        if (adView != null) { adView.pause(); }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) { adView.destroy(); }
        super.onDestroy();
    }

    // location-specific code
    private static final int LOCATION_REQUEST_CODE = 1042;
    private static final int REQUEST_CHECK_SETTINGS_CODE = 1043;

    private void requestLocation() {
        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            // App has permission to access location in the foreground. Start your foreground service that has a foreground service type of "location".
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, this::handleLocationUpdate)
                    .addOnFailureListener(this, e -> Log.e(TAG, "location error: " + e));
        } else {
            // Make a request for foreground-only location access.
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        if(requestCode == LOCATION_REQUEST_CODE) {
            if(!permissionAccessFineLocationApproved) {
                snackbarLocationDenied.show();
                handleLocationUpdate(DEFAULT_LOCATION);
            } else {
                snackbarLocationDenied.dismiss();
            }
        }
    }

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent than this
     * value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final LocationRequest locationRequest = new LocationRequest();
    static {
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setSmallestDisplacement(10f); // 10 meters
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private List<City> cities = null;

    private void setCities(final List<City> cities) {
        this.cities = cities;
        if(cities != null && localities != null && latestLocation != null) {
            locationViewModel.setLocation(new LocationViewModel.LocationAndCitiesAndLocalities(latestLocation, cities, localities));
        }
    }

    private List<Locality> localities = null;

    private void setLocalities(final List<Locality> localities) {
        this.localities = localities;
        if(cities != null && localities != null && latestLocation != null) {
            locationViewModel.setLocation(new LocationViewModel.LocationAndCitiesAndLocalities(latestLocation, cities, localities));
        }
    }

    private Location latestLocation = null;

    private void handleLocationUpdate(final Location location) {
        this.latestLocation = location;
        if(cities != null && localities != null && latestLocation != null) {
            locationViewModel.setLocation(new LocationViewModel.LocationAndCitiesAndLocalities(latestLocation, cities, localities));
            checkIfNearbySearchMustBeLogged(location.getLatitude(), location.getLongitude());
        }
    }

    private void checkLocationIsEnabledAndStartUpdates() {
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest);

        final Task<LocationSettingsResponse> locationSettingsResponseTask =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(task -> {
            try {
                // final LocationSettingsResponse response =
                        task.getResult(ApiException.class);
                // All location settings are satisfied - the client initializes location requests...
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()); // start location updates
                snackbarLocationDisabled.dismiss();
            } catch (ApiException exception) {
                handleLocationUpdate(DEFAULT_LOCATION);
                snackbarLocationDisabled.show();
            }
        });
    }

    private void requestEnableLocation() {
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest);

        final Task<LocationSettingsResponse> locationSettingsResponseTask =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(task -> {
            try {
                // final LocationSettingsResponse response =
                        task.getResult(ApiException.class);
                // All location settings are satisfied - the client initializes location requests...
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()); // start location updates
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            final ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            resolvable.startResolutionForResult(ActivityHome.this, REQUEST_CHECK_SETTINGS_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CHECK_SETTINGS_CODE) {
            snackbarLocationDisabled.dismiss();
        }
    }

    // end of location-specific code

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // configure search
        {
            final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_menu_search).getActionView();
            searchView.setOnQueryTextListener(queryTextListener);
        }
        // configure use Greek checkable
        {
            final Locale locale = getResources().getConfiguration().getLocales().get(0);
            final boolean localeIsGreek = Utils.isGreek(locale);
            final MenuItem menuItem = menu.findItem(R.id.app_bar_menu_force_greek);
            menuItem.setEnabled(!localeIsGreek);
            final boolean useGreekCharacters = sharedPreferences.getBoolean(PREF_USE_GREEK_CHARACTERS, true);
            menuItem.setChecked(localeIsGreek || useGreekCharacters);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_menu_force_greek:
                final boolean useGreekCharacters = !item.isChecked();
                item.setChecked(useGreekCharacters);
                sharedPreferences.edit().putBoolean(PREF_USE_GREEK_CHARACTERS, useGreekCharacters).apply();
                greekCharactersViewModel.setUseGreekCharacters(useGreekCharacters);
                return true;
            case R.id.app_bar_menu_synchronize:
                update();
                return true;
            case R.id.app_bar_menu_information:
                startActivity(new Intent(this, ActivityInformation.class));
                return true;
            case R.id.app_bar_menu_about:
                //do something
                startActivity(new Intent(this, ActivityAbout.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final long ONE_HOUR = 60L * 60 * 1000;

    private void checkIfDataNeedUpdate() {
        final long lastCheckedForUpdates = sharedPreferences.getLong(PREF_LAST_CHECKED_FOR_UPDATES, 0L);
        if(System.currentTimeMillis() - lastCheckedForUpdates > ONE_HOUR) {
            update(); // only check for updates if needed
        }
    }

    private static final String GET_UPDATES_URL = "https://cypruspharmacyguide.appspot.com/json/update-all?from=%d&magic=%s";

    private void update() {
        Snackbar.make(viewPager, R.string.Fetching_updates, BaseTransientBottomBar.LENGTH_SHORT).show();

        // form and encode URL (only parameters need encoding)
        final long timestamp = sharedPreferences.getLong(PREF_LATEST_TIMESTAMP, 0L); // latest timestamp - default is zero
        final String encodedURL = String.format(Locale.US, GET_UPDATES_URL, timestamp, getString(R.string.magic));
        Log.d(TAG, "encodedURL: " + encodedURL);

        queue.add(new StringRequest(
                Request.Method.GET,
                encodedURL,
                this::handleUpdateResponse,
                this::handleUpdateError
        ));
    }

    private void handleUpdateResponse(final String response) {
        sharedPreferences.edit().putLong(PREF_LAST_CHECKED_FOR_UPDATES, System.currentTimeMillis()).apply(); // when we connected last time

        final UpdateResponse updateResponse = gson.fromJson(response, UpdateResponse.class);
        final int numOfUpdates = updateResponse.getUpdateSize();
        if(numOfUpdates == 0) {
            Snackbar.make(viewPager, R.string.Already_up_to_date, BaseTransientBottomBar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(viewPager, getResources().getQuantityText(R.plurals.Updates_fetched, numOfUpdates), BaseTransientBottomBar.LENGTH_SHORT).show();
        }

        // update DB
        executor.execute(() -> {
            cpgDao.insert(updateResponse.getCities());
            cpgDao.insert(updateResponse.getLocalities());
            cpgDao.insert(updateResponse.getPharmacies());
            final OnCall [] onCalls = updateResponse.getOnCalls();
            for(final OnCall onCall : onCalls) {
                cpgDao.deleteDatesToPharmacies(onCall.getDate());
                final DateToPharmacy [] dateToPharmacies = onCall.toDatesToPharmacies();
                cpgDao.insert(dateToPharmacies);
            }
            // delete old entries
            cpgDao.deleteDatesToPharmaciesOlderThan(Utils.getDayBeforeYesterdayAsString());
            // update preferences
            sharedPreferences.edit().putLong(PREF_LATEST_TIMESTAMP, updateResponse.getLastUpdated()).apply(); // timestamp of data currently in DB
        });
    }

    private void handleUpdateError(final VolleyError volleyError) {
        Log.e(TAG, "Volley error: " + volleyError);
        Snackbar.make(viewPager, R.string.Network_error, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    private static final long A_QUARTER_OF_THE_HOUR = 15L * 60 * 1000;

    private void checkIfNearbySearchMustBeLogged(final double lat, final double lng) {
        final long lastLoggedNearbySearch = sharedPreferences.getLong(PREF_LAST_LOGGED_NEARBY_SEARCH, 0L);
        if(System.currentTimeMillis() - lastLoggedNearbySearch > A_QUARTER_OF_THE_HOUR) {
            logNearbySearch(lat, lng); // only log nearby searches if needed
        }
    }

    /**
     * The URL for submitting nearby search events - note how the coordinates are 'approximated' to 2 decimal points only
     */
    private static final String LOG_NEARBY_SEARCH_URL = "https://cypruspharmacyguide.appspot.com/services/log-nearby-search?installation_id=%s&lat=%.2f&lng=%.2f";

    private void logNearbySearch(final double lat, final double lng) {
        // form and encode URL (only parameters need encoding)
        final String installation_id = Installation.id(this);
        final String encodedURL = String.format(Locale.US, LOG_NEARBY_SEARCH_URL, installation_id, lat, lng);

        queue.add(new StringRequest(
                Request.Method.POST,
                encodedURL,
                this::handleLogNearbySearchResponse,
                this::handleLogNearbySearchError
        ));
    }

    private void handleLogNearbySearchResponse(final String response) {
        sharedPreferences.edit().putLong(PREF_LAST_LOGGED_NEARBY_SEARCH, System.currentTimeMillis()).apply(); // when we connected last time
    }

    private void handleLogNearbySearchError(final VolleyError volleyError) {
        Log.e(TAG, "Volley error: " + volleyError);
    }

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // empty
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            searchViewModel.setQuery(query);
            return false;
        }
    };

    // todo i18n - add Greek
}
package com.aspectsense.pharmacyguidecy.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.aspectsense.pharmacyguidecy.ActivityPharmacy;
import com.aspectsense.greektools.Greeklish;
import com.aspectsense.pharmacyguidecy.ActivityHome;
import com.aspectsense.pharmacyguidecy.R;
import com.aspectsense.pharmacyguidecy.Utils;
import com.aspectsense.pharmacyguidecy.data.City;
import com.aspectsense.pharmacyguidecy.data.FlatPharmacy;
import com.aspectsense.pharmacyguidecy.data.Locality;
import com.aspectsense.pharmacyguidecy.data.Pharmacy;
import com.aspectsense.pharmacyguidecy.data.Place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.aspectsense.pharmacyguidecy.Utils.distanceBetween;

/**
 * Used as an adapter for lists of {@link com.aspectsense.pharmacyguidecy.data.Place}s, i.e.
 * lists of Pharmacies, Localities and Cities.
 *
 * @author Nearchos
 * Created: 28-Mar-20
 */
public class PlaceRecyclerAdapter
        extends RecyclerView.Adapter<PlaceRecyclerAdapter.PlaceViewHolder> {

    static class PlaceViewHolder extends RecyclerView.ViewHolder {

        private final CardView rootCardView;
        private final TextView nameTextView;
        private final TextView addressTextView;
        private final TextView addressDetailsTextView;
        private final TextView distanceTextView;
        private final TextView distanceUnitTextView;
        private PlaceViewHolder(View itemView) {
            super(itemView);
            rootCardView = itemView.findViewById(R.id.rootCardView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            addressDetailsTextView = itemView.findViewById(R.id.addressDetailsTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            distanceUnitTextView = itemView.findViewById(R.id.distanceUnitTextView);
        }
    }

    private final Activity activity;

    private final SharedPreferences sharedPreferences;
    private final LayoutInflater layoutInflater;
    private final List<Place> places = new ArrayList<>(); // Cached copy of pharmacies

    PlaceRecyclerAdapter(final Activity activity) {
        this.activity = activity;

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        this.layoutInflater = LayoutInflater.from(activity);
    }

    private static final int TYPE_CITY                      = 0x01;
    private static final int TYPE_PHARMACY                  = 0x02;
    private static final int TYPE_LOADING                   = 0x03;
    private static final int TYPE_NO_MATCHING_PHARMACIES    = 0x04;

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView;
        switch (viewType) {
            case TYPE_CITY:
            {
                itemView = layoutInflater.inflate(R.layout.item_city, parent, false);
                break;
            }
            case TYPE_PHARMACY:
            {
                itemView = layoutInflater.inflate(R.layout.item_pharmacy, parent, false);
                break;
            }
            case TYPE_LOADING:
            {
                itemView = layoutInflater.inflate(R.layout.item_loading, parent, false);
                break;
            }
            case TYPE_NO_MATCHING_PHARMACIES:
            default:
            {
                itemView = layoutInflater.inflate(R.layout.item_no_matching_pharmacies, parent, false);
                break;
            }
        }
        return new PlaceViewHolder(itemView);
    }

    @Override
    public int getItemViewType(final int position) {
        if (places.get(position) instanceof Pharmacy) {
            return TYPE_PHARMACY;
        } else if (places.get(position) instanceof City) {
            return TYPE_CITY;
        } else if (places.get(position) instanceof PlaceholderLoading) {
            return TYPE_LOADING;
        } else { // assume PlaceholderNoMatchingPharmacies
            return TYPE_NO_MATCHING_PHARMACIES;
        }
    }

    private final Map<String,Locality> uuidsToLocalities = new HashMap<>();
    private final Map<String,City> uuidsToCities = new HashMap<>();

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        final Locale locale = activity.getResources().getConfiguration().getLocales().get(0);
        final Place place = places.get(position);
        final boolean useGreekCharacters = sharedPreferences.getBoolean(ActivityHome.PREF_USE_GREEK_CHARACTERS, true);
        final boolean isGreek = Utils.isGreek(locale) || useGreekCharacters;
        if(place instanceof PlaceholderNoMatchingPharmacies || place instanceof PlaceholderLoading) {
            // nothing to do - no binding necessary
        } else if(place instanceof City) {
            final City city = (City) place;
            holder.nameTextView.setText(isGreek ? city.getNameEl() : city.getNameEn());
        } else if(place instanceof Pharmacy) {
            Pharmacy pharmacy = (Pharmacy) place;
            holder.rootCardView.setOnClickListener(v -> showPharmacy(pharmacy, latestLocationAndCitiesAndLocalities.getLocation(), holder));
            holder.nameTextView.setText(isGreek ? pharmacy.getName() : pharmacy.getNameEn());
            if(latestLocationAndCitiesAndLocalities != null && uuidsToLocalities.isEmpty()) {
                final List<Locality> localities = latestLocationAndCitiesAndLocalities.getLocalities();
                for(final Locality locality : localities) {
                    uuidsToLocalities.put(locality.getUuid(), locality);
                }
                final List<City> cities = latestLocationAndCitiesAndLocalities.getCities();
                for(final City city : cities) {
                    uuidsToCities.put(city.getUuid(), city);
                }
            }
            final String greekAddress = pharmacy.getAddress();
            final Locality locality = uuidsToLocalities.get(pharmacy.getLocalityUuid());
            final String localizedAddress = locality == null ?
                    isGreek ? greekAddress : Greeklish.toGreeklish(greekAddress) :
                    String.format("%s, %s",
                            isGreek ? greekAddress : Greeklish.toGreeklish(greekAddress),
                            isGreek ? locality.getNameEl() : locality.getNameEn());
            holder.addressTextView.setText(localizedAddress);
            final Location latestLocation = latestLocationAndCitiesAndLocalities.getLocation();
            float lat = place.getLat();
            float lng = place.getLng();
            if(lat == 0f || lng == 0f) {
                if(locality != null) {
                    lat = locality.getLat();
                    lng = locality.getLng();
                }
            }
            final float distance = distanceBetween(latestLocation, lat, lng);
            if (distance < 1000) {
                holder.distanceUnitTextView.setText(R.string.m);
                holder.distanceTextView.setText(String.format(locale, "%d", ((int) (distance / 10)) * 10));
            } else {
                holder.distanceUnitTextView.setText(R.string.Km);
                final String formattedDistance = distance <= 99999 ?
                        String.format(locale, "%.1f", distance / 1000) : ">99";
                holder.distanceTextView.setText(formattedDistance);
            }
        }
    }

    public static final String FLAT_PHARMACY = "FLAT_PHARMACY";
    public static final String LOCATION = "LOCATION";

    private void showPharmacy(@NonNull final Pharmacy pharmacy, @NonNull final Location location, @NonNull PlaceViewHolder holder) {
        final Intent intent = new Intent(activity, ActivityPharmacy.class);
        final Locality locality = uuidsToLocalities.get(pharmacy.getLocalityUuid());
        assert locality != null;
        final City city = uuidsToCities.get(locality.getCityUuid());
        assert city != null;

        intent.putExtra(FLAT_PHARMACY, new FlatPharmacy(pharmacy.getId(), pharmacy.getName(), pharmacy.getNameEn(),
                pharmacy.getAddress(), pharmacy.getAddressPostalCode(), pharmacy.getAddressDetails(),
                pharmacy.getLat(), pharmacy.getLng(), locality.getNameEl(), locality.getNameEn(),
                city.getNameEl(), city.getNameEn(), pharmacy.getPhoneBusiness(), pharmacy.getPhoneHome()));

        intent.putExtra(LOCATION, location);

        /*
         * Now create an {@link android.app.ActivityOptions} instance using the
         * {@link ActivityOptionsCompat#makeSceneTransitionAnimation(Activity, Pair[])} factory
         * method.
         */
        final ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                // Now we provide a list of Pair items which contain the view we are transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                new Pair<>(holder.nameTextView, ActivityPharmacy.VIEW_NAME_HEADER_NAME),
                new Pair<>(holder.addressTextView, ActivityPharmacy.VIEW_NAME_HEADER_ADDRESS),
                new Pair<>(holder.addressDetailsTextView, ActivityPharmacy.VIEW_NAME_HEADER_ADDRESS_DETAILS),
                new Pair<>(holder.distanceTextView, ActivityPharmacy.VIEW_NAME_HEADER_DISTANCE),
                new Pair<>(holder.distanceUnitTextView, ActivityPharmacy.VIEW_NAME_HEADER_DISTANCE_UNIT)
        );

        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(activity, intent, activityOptions.toBundle());
    }

    private List<Pharmacy> latestPharmacies = new ArrayList<>();

    void setPharmacies(@NonNull List<Pharmacy> pharmacies) {
        this.latestPharmacies = pharmacies;
        if(latestLocationAndCitiesAndLocalities != null) {
            sortByLocation();
        }
    }

    private LocationViewModel.LocationAndCitiesAndLocalities latestLocationAndCitiesAndLocalities = null;

    void setLocationAndCitiesAndLocalities(final LocationViewModel.LocationAndCitiesAndLocalities locationAndCitiesAndLocalities) {
        this.latestLocationAndCitiesAndLocalities = locationAndCitiesAndLocalities;
        if(latestPharmacies != null) {
            sortByLocation();
        }
    }

    private void sortByLocation() {
        if(latestLocationAndCitiesAndLocalities == null) return;
        final List<City> cities = latestLocationAndCitiesAndLocalities.getCities();
        final Map<String,City> cityUuidToCity = new HashMap<>();
        for(final City city : cities) {
            cityUuidToCity.put(city.getUuid(), city);
        }

        final List<Locality> localities = latestLocationAndCitiesAndLocalities.getLocalities();
        final Map<String, City> localityUuidToCity = new HashMap<>();
        for(final Locality locality : localities) {
            localityUuidToCity.put(locality.getUuid(), cityUuidToCity.get(locality.getCityUuid()));
        }
        final Location location = latestLocationAndCitiesAndLocalities.getLocation();
        final double lat = location.getLatitude(), lng = location.getLongitude();

        Collections.sort(cities, (city1, city2) -> Float.compare(
                distanceBetween(lat, lng, city1.getLat(), city1.getLng()),
                distanceBetween(lat, lng, city2.getLat(), city2.getLng())
        ));

        this.places.clear();
        this.places.addAll(filter(cities, latestPharmacies));

        // sort pharmacies, first by city, then by distance to current location
        Collections.sort(places, (place1, place2) -> {
            if(place1 instanceof City && place2 instanceof City) {
                return Float.compare(
                        distanceBetween(lat, lng, place1.getLat(), place1.getLng()),
                        distanceBetween(lat, lng, place2.getLat(), place2.getLng())
                );
            } else if(place1 instanceof City && place2 instanceof Pharmacy) {
                final City city2 = getCityFromPlace(place2, localityUuidToCity);
                return Float.compare(
                        distanceBetween(lat, lng, place1.getLat(), place1.getLng()),
                        distanceBetween(lat, lng, city2.getLat(), city2.getLng())
                );
            } else if(place1 instanceof Pharmacy && place2 instanceof City) {
                final City city1 = getCityFromPlace(place1, localityUuidToCity);
                return Float.compare(
                        distanceBetween(lat, lng, city1.getLat(), city1.getLng()),
                        distanceBetween(lat, lng, place2.getLat(), place2.getLng())
                );
            } else if(place1 instanceof Pharmacy && place2 instanceof Pharmacy) {
                final City city1 = getCityFromPlace(place1, localityUuidToCity);
                final City city2 = getCityFromPlace(place2, localityUuidToCity);
                if(city1 == null || city2 == null) {
                    return 0;
                } else if(city1.equals(city2)) {
                    // in case the coordinates of either pharmacy are zero, use those of the corresponding locality
                    float lat1 = place1.getLat();
                    float lng1 = place1.getLng();
                    if(lat1 == 0f || lng1 == 0) {
                        final Locality locality1 = uuidsToLocalities.get(((Pharmacy) place1).getLocalityUuid());
                        if(locality1 != null) {
                            lat1 = locality1.getLat();
                            lng1 = locality1.getLng();
                        }
                    }
                    float lat2 = place2.getLat();
                    float lng2 = place2.getLng();
                    if(lat2 == 0f || lng2 == 0) {
                        final Locality locality2 = uuidsToLocalities.get(((Pharmacy) place2).getLocalityUuid());
                        if(locality2 != null) {
                            lat2 = locality2.getLat();
                            lng2 = locality2.getLng();
                        }
                    }
                    return Float.compare(
                            distanceBetween(lat, lng, lat1, lng1),
                            distanceBetween(lat, lng, lat2, lng2)
                    );
                } else {
                    return Float.compare(
                            distanceBetween(lat, lng, city1.getLat(), city1.getLng()),
                            distanceBetween(lat, lng, city2.getLat(), city2.getLng()));
                }
            } else {
                return 0; // this should never happen
            }
        });
        notifyDataSetChanged();
    }

    private City getCityFromPlace(@NonNull final Place place, @NonNull final Map<String, City> localityUuidToCity) {
        final Pharmacy pharmacy = (Pharmacy) place;
        return localityUuidToCity.get(pharmacy.getLocalityUuid());
    }

    @Override
    public int getItemCount() {
        return places == null ? 0 : places.size();
    }

    private String query = null;

    void setFilter(final String query) {
        if(query == null || query.trim().isEmpty()) {
            this.query = null;
        } else {
            this.query = Greeklish.removeAccents(query.toLowerCase());
        }

        sortByLocation();
    }

    private List<Place> filter(final List<City> cities, final List<Pharmacy> pharmacies) {
        final List<Place> filteredPlaces = new ArrayList<>();

        if(query == null) {
            if(pharmacies.isEmpty()) {
                filteredPlaces.add(new PlaceholderLoading());
            } else {
                filteredPlaces.addAll(cities);
                filteredPlaces.addAll(pharmacies);
            }
        } else {
            final Set<String> flaggedCityUuids = new HashSet<>();
            for(final Pharmacy pharmacy : pharmacies) {
                if(pharmacy.getSearchableName().contains(query)) {
                    final Locality locality = uuidsToLocalities.get(pharmacy.getLocalityUuid());
                    if(locality != null) {
                        flaggedCityUuids.add(locality.getCityUuid());
                    }
                    filteredPlaces.add(pharmacy);
                }
            }
            for(final City city : cities) {
                if(flaggedCityUuids.contains(city.getUuid())) {
                    filteredPlaces.add(0, city);
                }
            }
        }

        if(filteredPlaces.isEmpty()) {
            filteredPlaces.add(new PlaceholderNoMatchingPharmacies());
        }

        return filteredPlaces;
    }

    private static class PlaceholderNoMatchingPharmacies implements Place {
        @Override
        public float getLat() {
            return 0f;
        }

        @Override
        public float getLng() {
            return 0f;
        }
    }

    private static class PlaceholderLoading implements Place {
        @Override
        public float getLat() {
            return 0f;
        }

        @Override
        public float getLng() {
            return 0f;
        }
    }
}
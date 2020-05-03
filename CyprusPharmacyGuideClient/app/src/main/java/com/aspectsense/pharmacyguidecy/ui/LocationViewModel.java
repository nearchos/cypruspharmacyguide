package com.aspectsense.pharmacyguidecy.ui;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aspectsense.pharmacyguidecy.data.City;
import com.aspectsense.pharmacyguidecy.data.Locality;

import java.util.List;

/**
 * @author Nearchos
 * Created: 30-Mar-20
 */
public class LocationViewModel extends ViewModel {

    private final MutableLiveData<LocationAndCitiesAndLocalities> locationMutableLiveData = new MutableLiveData<>();

    public void setLocation(final LocationAndCitiesAndLocalities locationAndCitiesAndLocalities) {
        locationMutableLiveData.postValue(locationAndCitiesAndLocalities);
    }

    public LiveData<LocationAndCitiesAndLocalities> getLocationLiveData() {
        return locationMutableLiveData;
    }

    public static class LocationAndCitiesAndLocalities {
        private Location location;
        private List<City> cities;
        private List<Locality> localities;

        public LocationAndCitiesAndLocalities(Location location, final List<City> cities, final List<Locality> localities) {
            this.location = location;
            this.cities = cities;
            this.localities = localities;
        }

        public Location getLocation() {
            return location;
        }

        public List<City> getCities() {
            return cities;
        }

        public List<Locality> getLocalities() {
            return localities;
        }
    }
}
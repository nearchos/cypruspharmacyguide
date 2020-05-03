package com.aspectsense.pharmacyguidecy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aspectsense.pharmacyguidecy.CPGDatabase;
import com.aspectsense.pharmacyguidecy.data.Pharmacy;

import java.util.List;

public class AllPharmaciesViewModel extends AndroidViewModel {

    private final LiveData<List<Pharmacy>> pharmaciesLiveData;

    public AllPharmaciesViewModel(final Application application) {
        super(application);
        this.pharmaciesLiveData = CPGDatabase.getDatabase(application).cpgDao().getAllPharmacies();
    }

    LiveData<List<Pharmacy>> getPharmaciesLiveData() {
        return pharmaciesLiveData;
    }
}
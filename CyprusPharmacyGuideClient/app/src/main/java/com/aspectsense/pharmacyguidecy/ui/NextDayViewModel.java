package com.aspectsense.pharmacyguidecy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aspectsense.pharmacyguidecy.CPGDatabase;
import com.aspectsense.pharmacyguidecy.Utils;
import com.aspectsense.pharmacyguidecy.data.OnCall;
import com.aspectsense.pharmacyguidecy.data.Pharmacy;

import java.util.List;

public class NextDayViewModel extends AndroidViewModel {

    private final LiveData<List<Pharmacy>> pharmaciesLiveData;

    public NextDayViewModel(final Application application) {
        super(application);

        final String selectedDateAsString = OnCall.SIMPLE_DATE_FORMAT.format(Utils.isEarlyHours() ?
                Utils.getToday() : // current day if still early hours
                Utils.getTomorrow() // next day if 8:00 or later
        );

        this.pharmaciesLiveData = CPGDatabase.getDatabase(application).cpgDao().getPharmaciesOnCall(selectedDateAsString);
    }

    LiveData<List<Pharmacy>> getPharmaciesLiveData() {
        return pharmaciesLiveData;
    }
}
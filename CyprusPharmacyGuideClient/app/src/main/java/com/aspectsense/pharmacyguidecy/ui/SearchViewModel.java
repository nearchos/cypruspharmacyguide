package com.aspectsense.pharmacyguidecy.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Nearchos
 * Created: 08-Apr-20
 */
public class SearchViewModel extends ViewModel {

    private final MutableLiveData<String> queryMutableLiveData = new MutableLiveData<>();

    public void setQuery(final String query) {
        queryMutableLiveData.postValue(query);
    }

    LiveData<String> getQueryMutableLiveData() {
        return queryMutableLiveData;
    }
}
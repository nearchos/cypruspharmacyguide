package com.aspectsense.pharmacyguidecy.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Nearchos
 * Created: 08-Apr-20
 */
public class GreekCharactersViewModel extends ViewModel {

    private final MutableLiveData<Boolean> useGreekCharactersMutableLiveData = new MutableLiveData<>();

    public void setUseGreekCharacters(final Boolean useGreekCharacters) {
        useGreekCharactersMutableLiveData.postValue(useGreekCharacters);
    }

    LiveData<Boolean> getUseGreekCharactersLiveData() {
        return useGreekCharactersMutableLiveData;
    }
}
package com.aspectsense.pharmacyguidecy;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aspectsense.pharmacyguidecy.data.City;
import com.aspectsense.pharmacyguidecy.data.DateToPharmacy;
import com.aspectsense.pharmacyguidecy.data.Locality;
import com.aspectsense.pharmacyguidecy.data.Pharmacy;

import java.util.List;

/**
 * @author Nearchos Paspallis
 * Created: 28-Mar-20
 */
@Dao
public interface CPGDao  {

    @Query("SELECT * FROM room_pharmacies")
    LiveData<List<Pharmacy>> getAllPharmacies();

//    @Query("SELECT * FROM room_pharmacies WHERE id=:id")
//    LiveData<Pharmacy> getPharmacy(final int id);

    @Query("SELECT * FROM room_localities")
    LiveData<List<Locality>> getAllLocalitiesLiveData();

    @Query("SELECT * FROM room_cities")
    LiveData<List<City>> getAllCitiesLiveData();

    @Query("SELECT * FROM room_pharmacies WHERE id IN (SELECT pharmacy_id FROM room_dates_to_pharmacies WHERE date=:dateAsString)")
    LiveData<List<Pharmacy>> getPharmaciesOnCall(final String dateAsString);

//    @Query("SELECT * FROM dates_to_pharmacies WHERE pharmacy_id=:pharmacyId")
//    LiveData<List<DateToPharmacy>> getDatesToPharmacy(final int pharmacyId);

    @Insert
    void insert(final City... cities);

    @Insert
    void insert(final Locality... localities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final Pharmacy... pharmacies);

    @Insert
    void insert(final DateToPharmacy... dateToPharmacies);

    @Query("DELETE FROM room_dates_to_pharmacies WHERE date = :dateAsString")
    void deleteDatesToPharmacies(final String dateAsString);

    @Query("DELETE FROM room_dates_to_pharmacies WHERE date <= :dateAsString")
    void deleteDatesToPharmaciesOlderThan(final String dateAsString);
}
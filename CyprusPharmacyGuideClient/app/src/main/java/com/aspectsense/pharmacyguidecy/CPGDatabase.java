package com.aspectsense.pharmacyguidecy;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aspectsense.pharmacyguidecy.data.City;
import com.aspectsense.pharmacyguidecy.data.DateToPharmacy;
import com.aspectsense.pharmacyguidecy.data.Locality;
import com.aspectsense.pharmacyguidecy.data.Pharmacy;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
@Database(entities = {City.class, Locality.class, Pharmacy.class, DateToPharmacy.class}, version = 20042101)
public abstract class CPGDatabase extends RoomDatabase {

    private static final String DATABASE_FILE_NAME = "room.db"; // explicitly use a different name as we use a completely new schema

    public abstract CPGDao cpgDao();

    private static volatile CPGDatabase INSTANCE;

    public static CPGDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CPGDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CPGDatabase.class, DATABASE_FILE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
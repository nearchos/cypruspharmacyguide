package com.aspectsense.pharmacyguidecy.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
@Entity(tableName = "room_dates_to_pharmacies",
        foreignKeys = { @ForeignKey(entity = Pharmacy.class, parentColumns = "id", childColumns = "pharmacy_id") },
        indices = { @Index("date"), @Index("pharmacy_id") })
public class DateToPharmacy {

    @PrimaryKey(autoGenerate = true) private final long _id;
    @NonNull private final String date;
    @ColumnInfo(name = "pharmacy_id") private final int pharmacyId;

    DateToPharmacy(final long _id, final @NonNull String date, final int pharmacyId) {
        this._id = _id;
        this.date = date;
        this.pharmacyId = pharmacyId;
    }

    public long get_id() {
        return _id;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    private static final SimpleDateFormat JSON_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private Date getAsDate() {
        try {
            return JSON_DATE_FORMAT.parse(date);
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }

    public int getPharmacyId() {
        return pharmacyId;
    }

    @Override
    @NonNull
    public String toString() {
        return new SimpleDateFormat("E, MMM dd, yyyy", Locale.getDefault()).format(getAsDate());
    }
}
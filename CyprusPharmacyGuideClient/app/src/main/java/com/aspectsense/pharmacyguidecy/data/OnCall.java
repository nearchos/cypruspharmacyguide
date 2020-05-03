package com.aspectsense.pharmacyguidecy.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.aspectsense.pharmacyguidecy.ActivityHome;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
@Entity(tableName = "room_on_calls")
public class OnCall {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NonNull private String date;
    private String pharmacies;

    public OnCall(@NonNull String date, @NonNull String pharmacies) {
        this.date = date;
        this.pharmacies = pharmacies;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public String getPharmacies() {
        return pharmacies;
    }

    public Date getParsedDate() {
        try {
            return SIMPLE_DATE_FORMAT.parse(date);
        } catch (ParseException pe) {
            Log.e(ActivityHome.TAG, "Error while parsing date: " + date, pe);
            return null;
        }
    }

    private static List<Integer> parsePharmacyIDsList(final String pharmacyIdsS) {
        final List<Integer> pharmacyIds = new ArrayList<>();

        final StringTokenizer stringTokenizer = new StringTokenizer(pharmacyIdsS, ",");
        while(stringTokenizer.hasMoreTokens()) {
            final String token = stringTokenizer.nextToken().trim();
            pharmacyIds.add(Integer.parseInt(token));
        }

        return pharmacyIds;
    }

    private static final long DUMMY_ID = 0L;

    public DateToPharmacy [] toDatesToPharmacies() {
        final List<Integer> pharmacyIds = parsePharmacyIDsList(pharmacies);
        final DateToPharmacy [] dateToPharmacies = new DateToPharmacy[pharmacyIds.size()];
        for(int i = 0; i < dateToPharmacies.length; i++) {
            dateToPharmacies[i] = new DateToPharmacy(DUMMY_ID, date, pharmacyIds.get(i));
        }
        return dateToPharmacies;
    }
}
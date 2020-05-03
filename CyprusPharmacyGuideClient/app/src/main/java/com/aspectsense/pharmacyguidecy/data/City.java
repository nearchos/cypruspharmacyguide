package com.aspectsense.pharmacyguidecy.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
@Entity(tableName = "room_cities")
public class City implements Place {

    @SerializedName("UUID") @PrimaryKey  @NonNull private String uuid;
    private String nameEn;
    private String nameEl;
    private float lat;
    private float lng;

    public City(@NonNull String uuid, @NonNull String nameEl, @NonNull String nameEn, float lat, float lng) {
        this.uuid = uuid;
        this.nameEl = nameEl;
        this.nameEn = nameEn;
        this.lat = lat;
        this.lng = lng;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public String getNameEl() {
        return nameEl;
    }

    public String getNameEn() {
        return nameEn;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final City city = (City) other;
        return uuid.equals(city.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    @NonNull
    public String toString() {
        return nameEn;
    }
}
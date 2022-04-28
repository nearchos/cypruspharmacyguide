package com.aspectsense.pharmacyguidecy.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aspectsense.greektools.Greeklish;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
@Entity(tableName = "room_pharmacies")
public class Pharmacy implements Place {

    @SerializedName("ID") @PrimaryKey private final int id;
    private final String name;
    private final String address;
    private final String addressPostalCode;
    private final String addressDetails;
    private final float lat;
    private final float lng;
    @SerializedName("localityUUID") @ColumnInfo(name = "locality_uuid") private final String localityUuid;
    private final String phoneBusiness;
    private final String phoneHome;
    private final boolean active;

    @Ignore private final transient String searchableName;

    public Pharmacy(int id, @NonNull String name, @NonNull String address, @NonNull String addressPostalCode, @NonNull String addressDetails, float lat, float lng, @NonNull String localityUuid, @NonNull String phoneBusiness, @NonNull String phoneHome, boolean active) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.addressPostalCode = addressPostalCode;
        this.addressDetails = addressDetails;
        this.lat = lat;
        this.lng = lng;
        this.localityUuid = localityUuid;
        this.phoneBusiness = phoneBusiness;
        this.phoneHome = phoneHome;
        this.active = active;

        this.searchableName = Greeklish.removeAccents(name.toLowerCase()) + Greeklish.toGreeklish(name.toLowerCase());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameEn() {
        return Greeklish.toGreeklish(name);
    }

    public String getAddress() {
        return address;
    }

    public String getAddressEn() {
        return Greeklish.toGreeklish(address);
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public String getAddressDetails() {
        return addressDetails;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public String getLocalityUuid() {
        return localityUuid;
    }

    public String getPhoneBusiness() {
        return phoneBusiness;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public boolean isActive() {
        return active;
    }

    public String getSearchableName() {
        return searchableName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Pharmacy pharmacy = (Pharmacy) other;
        return id == pharmacy.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    @NonNull
    public String toString() {
        return "Pharmacy{" +
                "id=" + id +
                ", name='" + name + '}';
    }
}
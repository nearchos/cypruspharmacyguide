package com.aspectsense.pharmacyguidecy.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author Nearchos
 * Created: 04-Jun-20
 */
public class Geo {
    @SerializedName("@type")
    private final String type = "GeoCoordinates";
    private final float latitude;
    private final float longitude;

    Geo(final FlatPharmacy flatPharmacy) {
        this.latitude = flatPharmacy.getLat();
        this.longitude = flatPharmacy.getLng();
    }

    public String getType() {
        return type;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}

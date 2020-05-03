package com.aspectsense.pharmacyguidecy.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
@Entity(tableName = "room_localities")
public class Locality implements Place {

    @SerializedName("UUID") @PrimaryKey @NonNull private String uuid;
    private String nameEl;
    private String nameEn;
    private float lat;
    private float lng;
    @SerializedName("cityUUID") private String cityUuid;

    public Locality(@NonNull String uuid, @NonNull String nameEl, @NonNull String nameEn, float lat, float lng, @NonNull String cityUuid) {
        this.uuid = uuid;
        this.nameEl = nameEl;
        this.nameEn = nameEn;
        this.lat = lat;
        this.lng = lng;
        this.cityUuid = cityUuid;
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

    public String getCityUuid() {
        return cityUuid;
    }

    @Override
    @NonNull
    public String toString() {
        return "Locality{" +
                "uuid='" + uuid + '\'' +
                ", nameEl='" + nameEl + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", cityUuid='" + cityUuid + '\'' +
                '}';
    }
}
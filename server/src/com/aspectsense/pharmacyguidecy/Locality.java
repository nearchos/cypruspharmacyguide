package com.aspectsense.pharmacyguidecy;

import java.io.Serializable;

/**
 * Date: 8/14/12
 * Time: 1:11 PM
 */
public class Locality implements Serializable
{
    private final String uuid;
    private final long lastUpdated;

    private final String name_el;
    private final String name_en;
    private final String cityUUID;
    private final double lat;
    private final double lng;

    public Locality(final String uuid, final long lastUpdated, final String name_el, final String name_en, final String cityUUID, final double lat, final double lng)
    {
        this.uuid = uuid;
        this.lastUpdated = lastUpdated;

        this.name_el = name_el;
        this.name_en = name_en;
        this.cityUUID = cityUUID;
        this.lat = lat;
        this.lng = lng;
    }

    public String getUUID()
    {
        return uuid;
    }

    public long getLastUpdated()
    {
        return lastUpdated;
    }

    public String getNameEl()
    {
        return name_el;
    }

    public String getNameEn()
    {
        return name_en;
    }

    public String getCityUUID()
    {
        return cityUUID;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }
}
package com.aspectsense.pharmacyguidecy;

import java.io.Serializable;
import java.util.Locale;

/**
 * Date: 8/14/12
 * Time: 1:11 PM
 */
public class City implements Serializable
{
    private final String uuid;
    private final long lastUpdated;

    private final String name_en;
    private final String name_el;
    private final double lat;
    private final double lng;

    public City(final String uuid, final long lastUpdated, final String name_en, final String name_el, final double lat, final double lng)
    {
        this.uuid = uuid;
        this.lastUpdated = lastUpdated;

        this.name_en = name_en;
        this.name_el = name_el;
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

    public String getNameEn()
    {
        return name_en;
    }

    public String getNameEl()
    {
        return name_el;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }

    public String getLocalizedString()
    {
        if("el".equals(Locale.getDefault().getLanguage()))
        {
            return name_el;
        }
        else
        {
            return name_en;
        }
    }
}
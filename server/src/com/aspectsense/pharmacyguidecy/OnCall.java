package com.aspectsense.pharmacyguidecy;

import java.io.Serializable;

/**
 * Date: 8/14/12
 * Time: 1:11 PM
 */
public class OnCall implements Serializable
{
    private final String uuid;
    private final long lastUpdated;

    private final String date; // format: yyyy-mm-dd
    private final String pharmacies;

    public OnCall(final String uuid, final long lastUpdated, final String date, final String pharmacies)
    {
        this.uuid = uuid;
        this.lastUpdated = lastUpdated;

        this.date = date;
        this.pharmacies = pharmacies;
    }

    public String getUUID()
    {
        return uuid;
    }

    public long getLastUpdated()
    {
        return lastUpdated;
    }

    public String getDate()
    {
        return date;
    }

    public String getPharmacies()
    {
        return pharmacies;
    }

    @Override
    public String toString()
    {
        return uuid + "(" + lastUpdated + "): " + date + " --> " + pharmacies;
    }
}
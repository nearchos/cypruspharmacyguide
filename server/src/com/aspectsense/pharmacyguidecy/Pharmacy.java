package com.aspectsense.pharmacyguidecy;

import java.io.Serializable;

/**
 * Date: 8/14/12
 * Time: 1:11 PM
 */
public class Pharmacy implements Serializable
{
    private final String uuid;
    private final long lastUpdated;

    private final String ID;
    private final String name;
    private final String address;
    private final String address_postal_code;
    private final String address_details;
    private final String localityUUID;
    private final double lat;
    private final double lng;
    private final String phone_business;
    private final String phone_home;

    public Pharmacy(final String uuid, final long lastUpdated, final String ID, final String name, final String address,
                    final String address_postal_code,
                    final String address_details, final String localityUUID, final double lat, final double lng,
                    final String phone_business, final String phone_home)
    {
        this.uuid = uuid;
        this.lastUpdated = lastUpdated;

        this.ID = ID;
        this.name = name;
        this.address = address;
        this.address_postal_code = address_postal_code;
        this.address_details = address_details;
        this.localityUUID = localityUUID;
        this.lat = lat;
        this.lng = lng;
        this.phone_business = phone_business;
        this.phone_home = phone_home;
    }

    public String getUUID()
    {
        return uuid;
    }

    public long getLastUpdated()
    {
        return lastUpdated;
    }

    public String getID()
    {
        return ID;
    }

    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return address;
    }

    public String getAddressPostalCode()
    {
        return address_postal_code;
    }

    public String getAddressDetails()
    {
        return address_details;
    }

    public String getLocalityUUID()
    {
        return localityUUID;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }

    public String getPhoneBusiness()
    {
        return phone_business;
    }

    public String getPhoneHome()
    {
        return phone_home;
    }
}
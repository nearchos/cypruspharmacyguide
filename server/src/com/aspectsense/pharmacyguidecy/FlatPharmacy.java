package com.aspectsense.pharmacyguidecy;

public class FlatPharmacy {

    private final String uuid;
    private final long lastUpdated;

    private final String ID;
    private final String name;
    private final String address;
    private final String address_postal_code;
    private final String address_details;
    private final String localityNameEn;
    private final String localityNameEl;
    private final String cityNameEn;
    private final String cityNameEl;
    private final double lat;
    private final double lng;
    private final String phone_business;
    private final String phone_home;
    private final boolean active;

    public FlatPharmacy(final String uuid, final long lastUpdated, final String ID, final String name, final String address,
                        final String address_postal_code, final String address_details,
                        final String localityNameEn, final String localityNameEl,
                        final String cityNameEn, final String cityNameEl,
                        final double lat, final double lng,
                        final String phone_business, final String phone_home)
    {
        this(uuid, lastUpdated, ID, name, address, address_postal_code, address_details, localityNameEn, localityNameEl, cityNameEn, cityNameEl, lat, lng, phone_business, phone_home, true);
    }

    public FlatPharmacy(final String uuid, final long lastUpdated, final String ID, final String name, final String address,
                        final String address_postal_code, final String address_details,
                        final String localityNameEn, final String localityNameEl,
                        final String cityNameEn, final String cityNameEl,
                        final double lat, final double lng,
                        final String phone_business, final String phone_home, final boolean active)
    {
        this.uuid = uuid;
        this.lastUpdated = lastUpdated;

        this.ID = ID;
        this.name = name;
        this.address = address;
        this.address_postal_code = address_postal_code;
        this.address_details = address_details;
        this.localityNameEn = localityNameEn;
        this.localityNameEl = localityNameEl;
        this.cityNameEn = cityNameEn;
        this.cityNameEl = cityNameEl;
        this.lat = lat;
        this.lng = lng;
        this.phone_business = phone_business;
        this.phone_home = phone_home;
        this.active = active;
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

    public String getLocalityNameEn() {
        return localityNameEn;
    }

    public String getLocalityNameEl() {
        return localityNameEl;
    }

    public String getCityNameEn() {
        return cityNameEn;
    }

    public String getCityNameEl() {
        return cityNameEl;
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

    public boolean isActive() {
        return active;
    }
}
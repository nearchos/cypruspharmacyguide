package com.aspectsense.pharmacyguidecy.data;

import com.aspectsense.greektools.Greeklish;

import java.io.Serializable;

/**
 * @author Nearchos
 * Created: 18-May-20
 */
public class FlatPharmacy implements Serializable {

    private int id;
    private String name;
    private String nameEn;
    private String address;
    private String addressPostalCode;
    private String addressDetails;
    private float lat;
    private float lng;
    private String localityNameEl;
    private String localityNameEn;
    private String cityNameEl;
    private String cityNameEn;
    private String phoneBusiness;
    private String phoneHome;

    public FlatPharmacy(int id, String name, String nameEn, String address, String addressPostalCode, String addressDetails, float lat, float lng, String localityNameEl, String localityNameEn, String cityNameEl, String cityNameEn, String phoneBusiness, String phoneHome) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.address = address;
        this.addressPostalCode = addressPostalCode;
        this.addressDetails = addressDetails;
        this.lat = lat;
        this.lng = lng;
        this.localityNameEl = localityNameEl;
        this.localityNameEn = localityNameEn;
        this.cityNameEl = cityNameEl;
        this.cityNameEn = cityNameEn;
        this.phoneBusiness = phoneBusiness;
        this.phoneHome = phoneHome;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getAddress() {
        return address;
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

    public String getLocalityNameEl() {
        return localityNameEl;
    }

    public String getLocalityNameEn() {
        return localityNameEn;
    }

    public String getCityNameEl() {
        return cityNameEl;
    }

    public String getCityNameEn() {
        return cityNameEn;
    }

    public String getFullAddressEl() {
        return address + ", CY-" + addressPostalCode + (localityNameEl.equals(cityNameEl) ? ", " + localityNameEl : "") + ", " + cityNameEl;
    }

    public String getFullAddressEn() {
        return Greeklish.toGreeklish(address) + ", CY-" + addressPostalCode + (localityNameEn.equals(cityNameEn) ? ", " + localityNameEn : "") + ", " + cityNameEn;
    }

    public String getPhoneBusiness() {
        return phoneBusiness;
    }

    public String getPhoneHome() {
        return phoneHome;
    }
}
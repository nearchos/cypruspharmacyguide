package com.aspectsense.pharmacyguidecy.data;

import com.aspectsense.greektools.Greeklish;
import com.google.gson.annotations.SerializedName;

/**
 * @author Nearchos
 * Created: 04-Jun-20
 */
public class Address {
    @SerializedName("@type")
    private final String type = "postalAddress";
    private final String addressCountry = "Cyprus";
    private final String addressDistrict;
    private final String addressLocality;
    private final String addressPostalCode;
    private final String streetAddress;

    Address(final FlatPharmacy flatPharmacy) {
        this.addressDistrict = flatPharmacy.getCityNameEn();
        this.addressLocality = flatPharmacy.getLocalityNameEn();
        this.addressPostalCode = flatPharmacy.getAddressPostalCode();
        this.streetAddress = Greeklish.toGreeklish(flatPharmacy.getAddress());
    }

    public String getType() {
        return type;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }
}
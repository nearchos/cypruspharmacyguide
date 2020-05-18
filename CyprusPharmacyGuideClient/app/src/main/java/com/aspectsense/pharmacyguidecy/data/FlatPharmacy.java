package com.aspectsense.pharmacyguidecy.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.aspectsense.greektools.Greeklish;

/**
 * @author Nearchos
 * Created: 18-May-20
 */
public class FlatPharmacy implements Parcelable {

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

    public FlatPharmacy() {
        // empty
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(addressPostalCode);
        parcel.writeString(addressDetails);
        parcel.writeFloat(lat);
        parcel.writeFloat(lng);
        parcel.writeString(localityNameEl);
        parcel.writeString(localityNameEn);
        parcel.writeString(cityNameEl);
        parcel.writeString(cityNameEn);
        parcel.writeString(phoneBusiness);
        parcel.writeString(phoneHome);
    }

    private FlatPharmacy(final Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        address = parcel.readString();
        addressPostalCode = parcel.readString();
        addressDetails = parcel.readString();
        lat = parcel.readFloat();
        lng = parcel.readFloat();
        localityNameEl = parcel.readString();
        localityNameEn = parcel.readString();
        cityNameEl = parcel.readString();
        cityNameEn = parcel.readString();
        phoneBusiness = parcel.readString();
        phoneHome = parcel.readString();
    }

    public static final Parcelable.Creator<FlatPharmacy> CREATOR = new Creator<FlatPharmacy>() {
        @Override
        public FlatPharmacy createFromParcel(Parcel sourceParcel) {
            return new FlatPharmacy(sourceParcel);
        }

        @Override
        public FlatPharmacy[] newArray(int size) {
            return new FlatPharmacy[size];
        }
    };

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
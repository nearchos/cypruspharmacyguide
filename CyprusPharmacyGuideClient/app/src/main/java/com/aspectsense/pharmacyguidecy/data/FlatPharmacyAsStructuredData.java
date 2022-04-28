package com.aspectsense.pharmacyguidecy.data;

import com.aspectsense.greektools.Greeklish;
import com.aspectsense.pharmacyguidecy.ActivityPharmacy;
import com.google.gson.annotations.SerializedName;

/**
 * @author Nearchos
 * Created: 04-Jun-20
 */
public class FlatPharmacyAsStructuredData {
    @SerializedName("@context")
    private final String dataContext = "https://schema.org";
    @SerializedName("@type")
    private final String type = "pharmacy";
    @SerializedName("@id")
    private final String id;
    private final String name;
    private final Address address;
    private final String telephone;
    private final Geo geo;

    public FlatPharmacyAsStructuredData(final FlatPharmacy flatPharmacy) {
        this.id = "http://cypruspharmacyguide.com/pharmacy/" + flatPharmacy.getId();
        this.name = Greeklish.toGreeklish(flatPharmacy.getName());
        this.address = new Address(flatPharmacy);
        this.telephone = "+357" + flatPharmacy.getPhoneBusiness();
        this.geo = new Geo(flatPharmacy);
    }

    public String getDataContext() {
        return dataContext;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public Geo getGeo() {
        return geo;
    }
}
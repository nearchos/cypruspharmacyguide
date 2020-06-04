package com.aspectsense.pharmacyguidecy;

import com.aspectsense.greektools.Greeklish;
import com.aspectsense.pharmacyguidecy.data.FlatPharmacy;
import com.aspectsense.pharmacyguidecy.data.FlatPharmacyAsStructuredData;

import org.junit.Test;

/**
 * @author Nearchos
 * Created: 18-May-20
 */
public class StructuredDataTest {

    @Test
    public void testStructuredData() {
        final FlatPharmacy flatPharmacy = new FlatPharmacy(
                1024,
                "Θρασυβουλίδου Ραφαέλα",
                Greeklish.toGreeklish("Θρασυβουλίδου Ραφαέλα"),
                "Λεωφόρος Αποστόλου Παύλου 60",
                "8046",
                "200μ πριν το MALL έναντι COSMIC BOWLING",
                34.766888f,
                32.416064f,
                "Πάφος",
                "Paphos",
                "Πάφος",
                "Paphos",
                "26600888",
                "99071469");

        System.out.println("flatPharmacy: " + flatPharmacy);
        System.out.println("structured data: " + new FlatPharmacyAsStructuredData(flatPharmacy));

        assert true;
    }
}

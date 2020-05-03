package com.aspectsense.pharmacyguidecy;

import org.junit.Test;

import java.util.Locale;

import static java.lang.String.format;

/**
 */
public class UrlUnitTest {

    @Test
    public void urlFormatting() {

        final float lat = 35.1234f;
        final float lng = 33.4567f;
        final String installation_id = "1a2b3c4d5e";

        System.out.println("lat: " + lat + ", lng: " + lng);
        final String encodedURL = format(Locale.US, "https://cypruspharmacyguide.appspot.com/services/log-nearby-search?installation_id=%s&lat=%.2f&lng=%.2f", installation_id, lat, lng);
        System.out.println("encoded URL: >" + encodedURL + "<");

        assert(true);
    }
}
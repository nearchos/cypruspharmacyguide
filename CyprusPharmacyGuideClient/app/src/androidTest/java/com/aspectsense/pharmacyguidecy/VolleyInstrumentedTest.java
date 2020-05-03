package com.aspectsense.pharmacyguidecy;

import android.content.Context;
import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class VolleyInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final RequestQueue queue = Volley.newRequestQueue(appContext);

        final String LOG_NEARBY_SEARCH_URL = "https://cypruspharmacyguide.appspot.com/services/log-nearby-search?installation_id=%s&lat=%.2f&lng=%.2f";

            // form and encode URL (only parameters need encoding)
        final float lat = 35.123f, lng = 33.234f;
        final String installation_id = Installation.id(appContext);
        final String encodedURL = String.format(Locale.US, LOG_NEARBY_SEARCH_URL, installation_id, lat, lng);

        queue.add(new StringRequest(
                Request.Method.POST,
                encodedURL,
                this::handleLogNearbySearchResponse,
                this::handleLogNearbySearchError
        ));

        assert true;
    }

    private void handleLogNearbySearchResponse(final String response) {
        Log.i(ActivityHome.TAG, "Volley response: " + response);
    }

    private void handleLogNearbySearchError(final VolleyError volleyError) {
        Log.e(ActivityHome.TAG, "Volley error: " + volleyError);
    }
}

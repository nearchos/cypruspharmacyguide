package com.aspectsense.pharmacyguidecy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ActivityInformation extends AppCompatActivity {

    private ImageView summerCheckImageView;
    private ImageView winterCheckImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        summerCheckImageView = findViewById(R.id.activity_info_summer_check_image_view);
        winterCheckImageView = findViewById(R.id.activity_info_winter_check_image_view);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final boolean isSummerPeriod = Utils.isSummerTime();
        summerCheckImageView.setVisibility(isSummerPeriod ? View.VISIBLE : View.GONE);
        winterCheckImageView.setVisibility(!isSummerPeriod ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to previous one (if any)
        }
        return super.onOptionsItemSelected(item);
    }
}
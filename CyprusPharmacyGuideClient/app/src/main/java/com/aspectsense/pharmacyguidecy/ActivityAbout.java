package com.aspectsense.pharmacyguidecy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ActivityAbout extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        this.coordinatorLayout = findViewById(R.id.activity_about_coordinator_layout);

        final TextView versionTextView = findViewById(R.id.activity_about_version_details);
        try {
            final String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionTextView.setText(getString(R.string.Version_with_name, versionName));
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(ActivityHome.TAG, "Could not get version name", nnfe);
        }

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to previous one (if any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void cardApp(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.application_url))));
    }

    public void cardDeveloper(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.aspectsense_url))));
    }

    public void cardVersion(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.project_github_url))));
    }

    public void cardRate(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=" + getApplication().getPackageName())));
    }

    public void cardShare(View view) {
        if(!Utils.shareApp(this)) {
            Snackbar.make(coordinatorLayout, R.string.No_apps_available_for_sharing, BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }
}
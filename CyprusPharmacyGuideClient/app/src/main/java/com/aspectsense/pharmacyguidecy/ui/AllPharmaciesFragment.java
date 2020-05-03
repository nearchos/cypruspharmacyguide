package com.aspectsense.pharmacyguidecy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aspectsense.pharmacyguidecy.ActivityHome;
import com.aspectsense.pharmacyguidecy.ActivityInformation;
import com.aspectsense.pharmacyguidecy.R;
import com.aspectsense.pharmacyguidecy.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllPharmaciesFragment extends Fragment {

    private PlaceRecyclerAdapter placeRecyclerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.placeRecyclerAdapter = new PlaceRecyclerAdapter(getActivity());
    }

    static AllPharmaciesFragment newInstance() {
        return new AllPharmaciesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_all_pharmacies, container, false);

        final TextView allPharmaciesTextView = root.findViewById(R.id.allPharmaciesTextView);
        final String label;
        if(Utils.arePharmaciesOpen()) {
            final String closingTime = Utils.getNextClosingTime();
            label = getString(R.string.All_pharmacies_currently_open, closingTime);
        } else {
            label = getString(R.string.All_pharmacies);
        }
        allPharmaciesTextView.setText(label);
        allPharmaciesTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ActivityInformation.class)));

        final AllPharmaciesViewModel allPharmaciesViewModel = new ViewModelProvider(this).get(AllPharmaciesViewModel.class);
        allPharmaciesViewModel.getPharmaciesLiveData().observe(
                getViewLifecycleOwner(),
                pharmacies -> placeRecyclerAdapter.setPharmacies(pharmacies));

        final ActivityHome activityHome = ((ActivityHome) getActivity());
        if(activityHome != null) {
            final LocationViewModel locationViewModel = ((ActivityHome) getActivity()).getLocationViewModel();
            locationViewModel.getLocationLiveData().observe(
                    getViewLifecycleOwner(),
                    location -> placeRecyclerAdapter.setLocationAndCitiesAndLocalities(location)
            );
            final GreekCharactersViewModel greekCharactersViewModel = ((ActivityHome) getActivity()).getGreekCharactersViewModel();
            greekCharactersViewModel.getUseGreekCharactersLiveData().observe(
                    getViewLifecycleOwner(),
                    useGreekCharacters -> placeRecyclerAdapter.notifyDataSetChanged()
            );
            final SearchViewModel searchViewModel = ((ActivityHome) getActivity()).getSearchViewModel();
            searchViewModel.getQueryMutableLiveData().observe(
                    getViewLifecycleOwner(),
                    query -> placeRecyclerAdapter.setFilter(query)
            );
        }

        final RecyclerView recyclerView = root.findViewById(R.id.allPharmaciesRecyclerView);
        recyclerView.setAdapter(placeRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}
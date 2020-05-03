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
public class OnCallNowFragment extends Fragment {

    private PlaceRecyclerAdapter placeRecyclerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.placeRecyclerAdapter = new PlaceRecyclerAdapter(getActivity());
    }

    static OnCallNowFragment newInstance() {
        return new OnCallNowFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_on_call_now, container, false);

        final TextView onCallNowTextView = root.findViewById(R.id.onCallNowTextView);
        final String label;
        if(Utils.isEvening()) {
            final String tonight = Utils.formatAsDay(Utils.getToday());
            label = String.format("%s, %s (%s)", getString(R.string.On_call_tonight), tonight, getString(R.string.until_8am_next_morning));
        } else if(Utils.isEarlyHours()) {
            final String tonight = Utils.formatAsDay(Utils.getYesterday());
            label = String.format("%s, %s (%s)", getString(R.string.On_call_tonight), tonight, getString(R.string.until_8am_next_morning));
        } else {
            final String today = Utils.formatAsDay(Utils.getToday());
            label = String.format("%s, %s (%s)", getString(R.string.On_call_today), today, getString(R.string.until_8am_next_morning));
        }
        onCallNowTextView.setText(label);
        onCallNowTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ActivityInformation.class)));


        final OnCallNowViewModel onCallNowViewModel = new ViewModelProvider(this).get(OnCallNowViewModel.class);
        onCallNowViewModel.getPharmaciesLiveData().observe(
                getViewLifecycleOwner(),
                pharmacies -> placeRecyclerAdapter.setPharmacies(pharmacies)
        );

        final ActivityHome activityHome = ((ActivityHome) getActivity());
        if(activityHome != null) {
            final LocationViewModel locationViewModel = ((ActivityHome) getActivity()).getLocationViewModel();
            locationViewModel.getLocationLiveData().observe(
                    getViewLifecycleOwner(),
                    locationAndCitiesAndLocalities -> placeRecyclerAdapter.setLocationAndCitiesAndLocalities(locationAndCitiesAndLocalities)
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

        final RecyclerView recyclerView = root.findViewById(R.id.onCallNowRecyclerView);
        recyclerView.setAdapter(placeRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}
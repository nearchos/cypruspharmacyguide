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
public class NextDayFragment extends Fragment {

    private PlaceRecyclerAdapter placeRecyclerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.placeRecyclerAdapter = new PlaceRecyclerAdapter(getActivity());
    }

    static NextDayFragment newInstance() {
        return new NextDayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_next_day, container, false);

        final TextView nextDayTextView = root.findViewById(R.id.nextDayTextView);
        final String label;
        if(Utils.isEvening()) {
            final String tomorrow = Utils.formatAsDay(Utils.getTomorrow());
            label = String.format("%s, %s", getString(R.string.On_call_tomorrow), tomorrow);
        } else if(Utils.isEarlyHours()) {
            final String today = Utils.formatAsDay(Utils.getToday());
            label = String.format("%s, %s", getString(R.string.On_call_tomorrow), today);
        } else {
            final String tomorrow = Utils.formatAsDay(Utils.getTomorrow());
            label = String.format("%s, %s", getString(R.string.On_call_tomorrow), tomorrow);
        }
        nextDayTextView.setText(label);
        nextDayTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ActivityInformation.class)));

        final NextDayViewModel nextDayViewModel = new ViewModelProvider(this).get(NextDayViewModel.class);
        nextDayViewModel.getPharmaciesLiveData().observe(
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

        final RecyclerView recyclerView = root.findViewById(R.id.nextDayRecyclerView);
        recyclerView.setAdapter(placeRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}
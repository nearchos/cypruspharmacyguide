package com.aspectsense.pharmacyguidecy.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
public class UpdateResponse {

    private String status;
    private City [] cities;
    private Locality [] localities;
    private Pharmacy [] pharmacies;
    @SerializedName("on-calls") private OnCall [] onCalls;
    private long lastUpdated;

    public UpdateResponse(String status, City[] cities, Locality[] localities, Pharmacy[] pharmacies, OnCall [] onCalls, long lastUpdated) {
        this.status = status;
        this.cities = cities;
        this.localities = localities;
        this.pharmacies = pharmacies;
        this.onCalls = onCalls;
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public City [] getCities() {
        return cities;
    }

    public Locality [] getLocalities() {
        return localities;
    }

    public Pharmacy [] getPharmacies() {
        return pharmacies;
    }

    public OnCall [] getOnCalls() {
        return onCalls;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public int getUpdateSize() {
        return cities.length  + localities.length + pharmacies.length + onCalls.length;
    }

    public boolean isEmpty() {
        return getUpdateSize() == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdateResponse{" +
                "status='" + status + '\'' +
                ", cities=" + (cities == null ? null : cities.length) +
                ", localities=" + (localities == null ? null : localities.length) +
                ", pharmacies=" + (pharmacies == null ? null : pharmacies.length) +
                ", onCalls=" + (onCalls == null ? null : onCalls.length) +
                ", lastUpdated=" + (lastUpdated) +
                '}';
    }
}
package com.aspectsense.pharmacyguidecy.data;

import java.io.Serializable;

/**
 * @author Nearchos
 * 05-Jun-16.
 */
public class Parameter implements Serializable {
    private String uuid;
    private long lastUpdated;
    private String name;
    private String value;

    public Parameter(String uuid, long lastUpdated, String name, String value) {
        this.uuid = uuid;
        this.lastUpdated = lastUpdated;
        this.name = name;
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean getValueAsBoolean() {
        return "true".equalsIgnoreCase(value);
    }
}
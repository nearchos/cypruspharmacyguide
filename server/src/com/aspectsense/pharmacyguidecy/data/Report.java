package com.aspectsense.pharmacyguidecy.data;

import com.google.appengine.api.datastore.Text;

/**
 * Created on 16/07/2014 / 18:15.
 * @author Nearchos Paspallis
 */
public class Report
{
    private final String uuid;
    private final String createdBy;
    private final long requestedOn;
    private final long generatedOn;
    private final long fromTimestamp;
    private final long toTimestamp;
    private final Text jsonText;

    Report(final String uuid,
           final String createdBy,
           final long requestedOn,
           final long generatedOn,
           final long fromTimestamp,
           final long toTimestamp,
           final Text jsonText)
    {
        this.uuid = uuid;
        this.createdBy = createdBy;
        this.requestedOn = requestedOn;
        this.generatedOn = generatedOn;
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.jsonText = jsonText;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public long getRequestedOn()
    {
        return requestedOn;
    }

    public long getGeneratedOn()
    {
        return generatedOn;
    }

    public long getFromTimestamp()
    {
        return fromTimestamp;
    }

    public long getToTimestamp()
    {
        return toTimestamp;
    }

    public Text getJsonText()
    {
        return jsonText;
    }

    @Override public String toString()
    {
        return uuid + "->{" + createdBy + "," + fromTimestamp + "," + toTimestamp + "}";
    }
}
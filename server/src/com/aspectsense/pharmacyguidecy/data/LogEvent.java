package com.aspectsense.pharmacyguidecy.data;

import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 8/16/12
 * Time: 11:40 AM
 */
public class LogEvent
{
    public static final Logger log = Logger.getLogger(LogEvent.class.getCanonicalName());

    public static final int TYPE_UNKNOWN        = 0x01;
    public static final int TYPE_LAUNCH_APP     = 0x01;
    public static final int TYPE_NEARBY_SEARCH  = 0x02;

    private final String installationId;
    private final double lat;
    private final double lng;
    private final long   timestamp;
    private final long   type;

    public LogEvent(final String installationId,
                    final double lat,
                    final double lng,
                    final long timestamp,
                    final String typeS)
    {
        this(
                installationId,
                lat,
                lng,
                timestamp,
                // could be 'L' for 'launch' or 'N' for 'nearby search', or 'U" for 'unknown'
                "L".equals(typeS) ? TYPE_LAUNCH_APP : "N".equals(typeS) ? TYPE_NEARBY_SEARCH : TYPE_UNKNOWN);
    }

    public LogEvent(final String installationId,
                    final double lat,
                    final double lng,
                    final long timestamp,
                    final long type)
    {
        this.installationId = installationId;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getInstallationId()
    {
        return installationId;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lng;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public long getType()
    {
        return type;
    }
}
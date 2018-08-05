package com.aspectsense.pharmacyguidecy.data;

import com.google.appengine.api.datastore.*;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created on 16/07/2014 / 19:35.
 * @author Nearchos Paspallis
 */
public class LogEventFactory
{
    public static final Logger log = Logger.getLogger(LogEventFactory.class.getCanonicalName());

    public static final String KEY  = "key"; // uuid
    public static final String KIND = "LogEvent";

    public static final String PROPERTY_LOG_EVENT_INSTALLATION_ID   = "installation_id";
    public static final String PROPERTY_LOG_EVENT_LAT               = "lat";
    public static final String PROPERTY_LOG_EVENT_LNG               = "lng";
    public static final String PROPERTY_LOG_EVENT_TIMESTAMP         = "timestamp";
    public static final String PROPERTY_LOG_EVENT_TYPE              = "type";

    /**
     * Returns all {@link com.aspectsense.pharmacyguidecy.data.LogEvent}s with a timestamp greater or equal than
     * 'fromTimestamp' and less than 'toTimestamp'.
     * @param fromTimestamp the lower bound (inclusive)
     * @param toTimestamp the upper bound (exclusive)
     * @return all log events in [fromTimestamp,toTimestamp), sorted by timestamp ascending
     */
    static public Vector<LogEvent> getLogEvents(final long fromTimestamp, final long toTimestamp)
    {
        final Vector<LogEvent> logEvents = new Vector<LogEvent>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query.Filter filterTimestampFrom = new Query.FilterPredicate(
                PROPERTY_LOG_EVENT_TIMESTAMP,
                Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                fromTimestamp);
        final Query.Filter filterTimestampTo = new Query.FilterPredicate(
                PROPERTY_LOG_EVENT_TIMESTAMP,
                Query.FilterOperator.LESS_THAN,
                toTimestamp);
        final Query query = new Query(KIND)
                .addSort(PROPERTY_LOG_EVENT_TIMESTAMP, Query.SortDirection.ASCENDING)
                .setFilter(Query.CompositeFilterOperator.and(filterTimestampFrom, filterTimestampTo));
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable())
        {
            logEvents.add(getFromEntity(entity));
        }

        return logEvents;
    }

    static public Key addLogEvent(final LogEvent logEvent)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity logEventEntity = new Entity(KIND);
        logEventEntity.setProperty(PROPERTY_LOG_EVENT_INSTALLATION_ID, logEvent.getInstallationId());
        logEventEntity.setProperty(PROPERTY_LOG_EVENT_LAT, logEvent.getLat());
        logEventEntity.setProperty(PROPERTY_LOG_EVENT_LNG, logEvent.getLng());
        logEventEntity.setProperty(PROPERTY_LOG_EVENT_TIMESTAMP, logEvent.getTimestamp());
        logEventEntity.setProperty(PROPERTY_LOG_EVENT_TYPE, logEvent.getType());

        return datastoreService.put(logEventEntity);
    }

    static public LogEvent getFromEntity(final Entity entity)
    {
        return new LogEvent(
                (String) entity.getProperty(PROPERTY_LOG_EVENT_INSTALLATION_ID),
                (Double) entity.getProperty(PROPERTY_LOG_EVENT_LAT),
                (Double) entity.getProperty(PROPERTY_LOG_EVENT_LNG),
                (Long) entity.getProperty(PROPERTY_LOG_EVENT_TIMESTAMP),
                (Long) entity.getProperty(PROPERTY_LOG_EVENT_TYPE));
    }
}

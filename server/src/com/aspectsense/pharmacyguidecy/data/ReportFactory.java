package com.aspectsense.pharmacyguidecy.data;

import com.google.appengine.api.datastore.*;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/3/12
 * Time: 8:37 PM
 */
public class ReportFactory
{
    public static final Logger log = Logger.getLogger(ReportFactory.class.getCanonicalName());

    public static final String KIND = "Report";

    public static final String PROPERTY_REPORT_CREATED_BY       = "report_created_by";
    public static final String PROPERTY_REPORT_REQUESTED_ON     = "report_requested_on";
    public static final String PROPERTY_REPORT_GENERATED_ON     = "report_generated_on";
    public static final String PROPERTY_REPORT_FROM_TIMESTAMP   = "report_from_day";
    public static final String PROPERTY_REPORT_TO_TIMESTAMP     = "report_to_day";
    public static final String PROPERTY_REPORT_JSON_TEXT        = "report_json_text";

    static private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public Report getReport(final String keyAsString)
    {
        try
        {
            final Entity reportEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

            return getFromEntity(reportEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + keyAsString);

            return null;
        }
    }

    static public Vector<Report> getAllReports()
    {
        final Vector<Report> reports = new Vector<Report>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable())
        {
            reports.add(getFromEntity(entity));
        }

        return reports;
    }

    static public Key addReport(final String createdBy, final long requestedOn,
                                final long fromTimestamp, final long toTimestamp)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity reportEntity = new Entity(KIND);
        reportEntity.setProperty(PROPERTY_REPORT_CREATED_BY, createdBy);
        reportEntity.setProperty(PROPERTY_REPORT_REQUESTED_ON, requestedOn);
        reportEntity.setProperty(PROPERTY_REPORT_GENERATED_ON, 0);
        reportEntity.setProperty(PROPERTY_REPORT_FROM_TIMESTAMP, fromTimestamp);
        reportEntity.setProperty(PROPERTY_REPORT_TO_TIMESTAMP, toTimestamp);
        reportEntity.setProperty(PROPERTY_REPORT_JSON_TEXT, null);

        return datastoreService.put(reportEntity);
    }

    static public Key editReport(final String uuid, final String createdBy, final long requestedOn,
                                 final long generatedOn, final long fromTimestamp, final long toTimestamp,
                                 final Text jsonText)
            throws EntityNotFoundException
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity reportEntity = datastoreService.get(KeyFactory.stringToKey(uuid));

        reportEntity.setProperty(PROPERTY_REPORT_CREATED_BY, createdBy);
        reportEntity.setProperty(PROPERTY_REPORT_REQUESTED_ON, requestedOn);
        reportEntity.setProperty(PROPERTY_REPORT_GENERATED_ON, generatedOn);
        reportEntity.setProperty(PROPERTY_REPORT_FROM_TIMESTAMP, fromTimestamp);
        reportEntity.setProperty(PROPERTY_REPORT_TO_TIMESTAMP, toTimestamp);
        reportEntity.setProperty(PROPERTY_REPORT_JSON_TEXT, jsonText);

        return datastoreService.put(reportEntity);
    }

    static public Report getFromEntity(final Entity entity)
    {
        return new Report(
                KeyFactory.keyToString(entity.getKey()),
                (String) entity.getProperty(PROPERTY_REPORT_CREATED_BY),
                (Long) entity.getProperty(PROPERTY_REPORT_REQUESTED_ON),
                (Long) entity.getProperty(PROPERTY_REPORT_GENERATED_ON),
                (Long) entity.getProperty(PROPERTY_REPORT_FROM_TIMESTAMP),
                (Long) entity.getProperty(PROPERTY_REPORT_TO_TIMESTAMP),
                (Text) entity.getProperty(PROPERTY_REPORT_JSON_TEXT));
    }
}
package com.aspectsense.pharmacyguidecy.data;

import com.aspectsense.pharmacyguidecy.OnCall;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 8/16/12
 * Time: 11:40 AM
 */
public class OnCallFactory
{
    public static final Logger log = Logger.getLogger(OnCallFactory.class.getCanonicalName());

    public static final String KEY  = "key"; // uuid
    public static final String KIND = "OnCall";

    public static final String PROPERTY_LAST_UPDATED = "last_updated";
    public static final String PROPERTY_ON_CALL_DATE = "on_call_date";
    public static final String PROPERTY_ON_CALL_PHARMACIES = "on_call_pharmacies";

    static public OnCall getOnCallFromDate(final String dateS)
    {
        // utilize MemcacheService
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

        OnCall onCall = (OnCall) memcacheService.get("on-call-" + dateS);

        if(onCall == null) {
            final Query.Filter filterDate = new Query.FilterPredicate(
                    OnCallFactory.PROPERTY_ON_CALL_DATE,
                    Query.FilterOperator.EQUAL,
                    dateS);

            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            final Query query = new Query(OnCallFactory.KIND);
            query.setFilter(filterDate);
            final PreparedQuery preparedQuery = datastore.prepare(query);
            final Iterable<Entity> iterable = preparedQuery.asIterable();
            final Iterator<Entity> iterator = iterable.iterator();
            Entity entity;
            if((entity = iterator.next()) != null)
            {
                onCall = OnCallFactory.getFromEntity(entity);
                memcacheService.put("on-call-" + dateS, onCall);
            }
            else
            {
                throw new IllegalArgumentException("Could not find an entity for date: " + dateS);
            }
        }

        return onCall;
    }

    static public OnCall getOnCall(final String keyAsString)
    {
        if(keyAsString == null) throw new IllegalArgumentException("Argument keyAsString cannot be null!");

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity onCallEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

            return getFromEntity(onCallEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + keyAsString);

            return null;
        }
    }

    public static final String ALL_ON_CALLS = "all-on-calls";

    static public Vector<OnCall> getAllOnCalls()
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

        Vector<OnCall> onCalls = (Vector<OnCall>) memcacheService.get(ALL_ON_CALLS);

        if(onCalls == null) {

            onCalls = new Vector<>();

            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query query = new Query(KIND).addSort(PROPERTY_ON_CALL_DATE);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            for(final Entity entity : preparedQuery.asIterable())
            {
                onCalls.add(getFromEntity(entity));
            }

            memcacheService.put(ALL_ON_CALLS, onCalls);
        }

        return onCalls;
    }

    static public Key addOnCall(final String date, final String pharmacies)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        memcacheService.delete(ALL_ON_CALLS);

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity cityEntity = new Entity(KIND);
        cityEntity.setProperty(PROPERTY_LAST_UPDATED, System.currentTimeMillis());
        cityEntity.setProperty(PROPERTY_ON_CALL_DATE, date);
        cityEntity.setProperty(PROPERTY_ON_CALL_PHARMACIES, pharmacies);

        return datastoreService.put(cityEntity);
    }

    static public Key editOnCall(final String uuid, final String date, final String pharmacies)
            throws EntityNotFoundException
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        memcacheService.delete(ALL_ON_CALLS);

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity onCallEntity = datastoreService.get(KeyFactory.stringToKey(uuid));

        onCallEntity.setProperty(PROPERTY_ON_CALL_DATE, date);
        onCallEntity.setProperty(PROPERTY_ON_CALL_PHARMACIES, pharmacies);
        final long lastUpdated = System.currentTimeMillis();
        onCallEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        return datastoreService.put(onCallEntity);
    }

    static public OnCall getFromEntity(final Entity entity)
    {
        return new OnCall(
                KeyFactory.keyToString(entity.getKey()),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED),
                (String) entity.getProperty(PROPERTY_ON_CALL_DATE),
                (String) entity.getProperty(PROPERTY_ON_CALL_PHARMACIES));
    }

    static public void deleteEntriesBefore(final String cutoffDate)
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        memcacheService.delete(ALL_ON_CALLS);

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        final Query.Filter filter = new Query.FilterPredicate(PROPERTY_ON_CALL_DATE, Query.FilterOperator.LESS_THAN, cutoffDate);
        final Query query = new Query(KIND).addSort(PROPERTY_ON_CALL_DATE).setFilter(filter);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);

        final Vector<Key> keys = new Vector<Key>();
        for(final Entity entity : preparedQuery.asIterable())
        {
            keys.add(entity.getKey());
        }

        datastoreService.delete(keys);
    }
}

package com.aspectsense.pharmacyguidecy.data;

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
public class ParameterFactory
{
    public static final Logger log = Logger.getLogger(ParameterFactory.class.getCanonicalName());

    public static final String KIND = "Parameter";

    public static final String PROPERTY_LAST_UPDATED = "last_updated";
    public static final String PROPERTY_PARAMETER_NAME = "name";
    public static final String PROPERTY_PARAMETER_VALUE = "value";

    private static final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
    private static final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    public static final String ALL_PARAMETERS = "ALL_PARAMETERS";

    public static Vector<Parameter> getAllParameters() {
        if(memcacheService.contains(ALL_PARAMETERS)) {
            return (Vector<Parameter>) memcacheService.get(ALL_PARAMETERS);
        } else {
            final Query query = new Query(KIND).addSort(PROPERTY_PARAMETER_NAME);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Vector<Parameter> parameters = new Vector<>();
            for(final Entity entity : preparedQuery.asIterable()) {
                parameters.add(getFromEntity(entity));
            }

            memcacheService.put(ALL_PARAMETERS, parameters);

            return parameters;
        }
    }

//    static public Parameter getParameter(final String keyAsString) {
//        try {
//            final Entity parameterEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));
//            return getFromEntity(parameterEntity);
//        } catch (EntityNotFoundException enfe) {
//            log.severe("Could not find " + KIND + " with key: " + keyAsString);
//            return null;
//        }
//    }

    public static Parameter getParameterByName(final String name) {

        if(!kindExists(KIND)) return null;

        final Query.Filter filterName = new Query.FilterPredicate(
                PROPERTY_PARAMETER_NAME,
                Query.FilterOperator.EQUAL,
                name);

        final Query query = new Query(KIND);
        // filter pharmacies last updated after 'from'
        query.setFilter(filterName);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        Entity entity;
        if(iterator.hasNext() && (entity = iterator.next()) != null) {
            return getFromEntity(entity);
        } else {
            return null;
        }
    }

    static public Key addOrUpdateParameter(final String name, final String value) {
        final Query.Filter filterName = new Query.FilterPredicate(
                PROPERTY_PARAMETER_NAME,
                Query.FilterOperator.EQUAL,
                name);

        final Query query = new Query(KIND);
        // filter pharmacies last updated after 'from'
        query.setFilter(filterName);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        Entity entity;
        if(iterator.hasNext() && (entity = iterator.next()) != null) {
            entity.setProperty(PROPERTY_LAST_UPDATED, System.currentTimeMillis());
            entity.setProperty(PROPERTY_PARAMETER_VALUE, value);
            return datastoreService.put(entity);
        } else {
            final Entity parameterEntity = new Entity(KIND);
            parameterEntity.setProperty(PROPERTY_LAST_UPDATED, System.currentTimeMillis());
            parameterEntity.setProperty(PROPERTY_PARAMETER_NAME, name);
            parameterEntity.setProperty(PROPERTY_PARAMETER_VALUE, value);
            return datastoreService.put(parameterEntity);
        }
    }

    static public Parameter getFromEntity(final Entity entity) {
        return new Parameter(
                KeyFactory.keyToString(entity.getKey()),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED),
                (String) entity.getProperty(PROPERTY_PARAMETER_NAME),
                (String) entity.getProperty(PROPERTY_PARAMETER_VALUE));
    }

    private static boolean kindExists (String kind) {
        final Query query = new Query(kind).setKeysOnly();
        final PreparedQuery preparedQuery = DatastoreServiceFactory.getDatastoreService().prepare(query);
        return !preparedQuery.asList(FetchOptions.Builder.withLimit(1)).isEmpty();
    }
}
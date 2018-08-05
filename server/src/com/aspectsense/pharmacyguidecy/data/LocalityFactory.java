package com.aspectsense.pharmacyguidecy.data;

import com.aspectsense.pharmacyguidecy.Locality;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/3/12
 * Time: 8:37 PM
 */
public class LocalityFactory
{

    public static final Logger log = Logger.getLogger(LocalityFactory.class.getCanonicalName());

    public static final String KIND = "Locality";

    public static final String PROPERTY_LOCALITY_NAME_EL    = "locality_name_el";
    public static final String PROPERTY_LOCALITY_NAME_EN    = "locality_name_en";
    public static final String PROPERTY_CITY_UUID           = "cityUUID";
    public static final String PROPERTY_LAT                 = "lat";
    public static final String PROPERTY_LNG                 = "lng";
    public static final String PROPERTY_LAST_UPDATED        = "last_updated";

    static private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    static private MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

    static public Locality getLocality(final String keyAsString)
    {
        Locality locality = (Locality) memcacheService.get(keyAsString);

        if(locality == null)
        {
            try
            {
                final Entity localityEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

                locality = getFromEntity(localityEntity);
            }
            catch (EntityNotFoundException enfe)
            {
                log.severe("Could not find " + KIND + " with key: " + keyAsString);

                locality = null;
            }

            memcacheService.put(keyAsString, locality);
        }

        return locality;
    }

    static public Vector<Locality> getAllLocalities()
    {
        final Vector<Locality> localities = new Vector<Locality>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_LOCALITY_NAME_EL);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable())
        {
            localities.add(getFromEntity(entity));
        }

        return localities;
    }

    static public Key addLocality(final String nameEl, final String nameEn, final String cityUUID, final double lat, final double lng)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity localityEntity = new Entity(KIND);
        localityEntity.setProperty(PROPERTY_LOCALITY_NAME_EL, nameEl);
        localityEntity.setProperty(PROPERTY_LOCALITY_NAME_EN, nameEn);
        localityEntity.setProperty(PROPERTY_CITY_UUID, cityUUID);
        localityEntity.setProperty(PROPERTY_LAT, lat);
        localityEntity.setProperty(PROPERTY_LNG, lng);
        final long lastUpdated = System.currentTimeMillis();
        localityEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        return datastoreService.put(localityEntity);
    }

    static public Locality getFromEntity(final Entity entity)
    {
        return new Locality(
                KeyFactory.keyToString(entity.getKey()),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED),
                (String) entity.getProperty(PROPERTY_LOCALITY_NAME_EL),
                (String) entity.getProperty(PROPERTY_LOCALITY_NAME_EN),
                (String) entity.getProperty(PROPERTY_CITY_UUID),
                (Double) entity.getProperty(PROPERTY_LAT),
                (Double) entity.getProperty(PROPERTY_LNG));
    }
}
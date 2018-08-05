package com.aspectsense.pharmacyguidecy.data;

import com.aspectsense.pharmacyguidecy.City;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 8/16/12
 * Time: 11:40 AM
 */
public class CityFactory
{
    public static final Logger log = Logger.getLogger(CityFactory.class.getCanonicalName());

    public static final String KIND = "City";

    public static final String PROPERTY_LAST_UPDATED = "last_updated";
    public static final String PROPERTY_CITY_NAME_EN = "city_name_en";
    public static final String PROPERTY_CITY_NAME_EL = "city_name_el";
    public static final String PROPERTY_CITY_LAT = "city_lat";
    public static final String PROPERTY_CITY_LNG = "city_lng";

    static public City getCity(final String keyAsString)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try
        {
            final Entity cityEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

            return getFromEntity(cityEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + keyAsString);

            return null;
        }
    }

    public static final String ALL_CITIES = "all-cities";

    static public Vector<City> getAllCities()
    {
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        if(memcacheService.contains(ALL_CITIES))
        {
            return (Vector<City>) memcacheService.get(ALL_CITIES);
        }
        else
        {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query query = new Query(KIND).addSort(PROPERTY_CITY_NAME_EN);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Vector<City> cities = new Vector<City>();
            for(final Entity entity : preparedQuery.asIterable())
            {
                cities.add(getFromEntity(entity));
            }

            memcacheService.put(ALL_CITIES, cities);

            return cities;
        }
    }

    static public Key addCity(final String nameEn, final String nameEl, final double lat, final double lng)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity cityEntity = new Entity(KIND);
        cityEntity.setProperty(PROPERTY_LAST_UPDATED, System.currentTimeMillis());
        cityEntity.setProperty(PROPERTY_CITY_NAME_EN, nameEn);
        cityEntity.setProperty(PROPERTY_CITY_NAME_EL, nameEl);
        cityEntity.setProperty(PROPERTY_CITY_LAT, lat);
        cityEntity.setProperty(PROPERTY_CITY_LNG, lng);

        MemcacheServiceFactory.getMemcacheService().delete(ALL_CITIES); // invalidate cache

        return datastoreService.put(cityEntity);
    }

    static public City getFromEntity(final Entity entity)
    {
        return new City(
                KeyFactory.keyToString(entity.getKey()),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED),
                (String) entity.getProperty(PROPERTY_CITY_NAME_EN),
                (String) entity.getProperty(PROPERTY_CITY_NAME_EL),
                (Double) entity.getProperty(PROPERTY_CITY_LAT),
                (Double) entity.getProperty(PROPERTY_CITY_LNG));
    }
}
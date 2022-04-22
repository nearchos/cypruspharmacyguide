package com.aspectsense.pharmacyguidecy.data;

import com.aspectsense.pharmacyguidecy.Pharmacy;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/3/12
 * Time: 8:37 PM
 */
public class PharmacyFactory
{

    public static final Logger log = Logger.getLogger(PharmacyFactory.class.getCanonicalName());

    public static final String KEY  = "key"; // uuid
    public static final String KIND = "Pharmacy";

    public static final String PROPERTY_PHARMACY_ID                     = "pharmacy_id";
    public static final String PROPERTY_PHARMACY_NAME_EL                = "pharmacy_name_el";
    public static final String PROPERTY_PHARMACY_ADDRESS                = "pharmacy_address";
    public static final String PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE    = "pharmacy_address_postal_code";
    public static final String PROPERTY_PHARMACY_ADDRESS_DETAILS        = "pharmacy_address_details";
    public static final String PROPERTY_PHARMACY_LAT                    = "pharmacy_lat";
    public static final String PROPERTY_PHARMACY_LNG                    = "pharmacy_lng";
    public static final String PROPERTY_PHARMACY_LOCALITY_UUID          = "pharmacy_locality_UUID";
    public static final String PROPERTY_PHARMACY_PHONE_BUSINESS         = "pharmacy_phone_business";
    public static final String PROPERTY_PHARMACY_PHONE_HOME             = "pharmacy_phone_home";
    public static final String PROPERTY_PHARMACY_ACTIVE                 = "pharmacy_active";

    public static final String PROPERTY_LAST_UPDATED        = "last_updated";

    static private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    static public Pharmacy getPharmacy(final String keyAsString)
    {
        try
        {
            final Entity pharmacyEntity = datastoreService.get(KeyFactory.stringToKey(keyAsString));

            return getFromEntity(pharmacyEntity);
        }
        catch (EntityNotFoundException enfe)
        {
            log.severe("Could not find " + KIND + " with key: " + keyAsString);

            return null;
        }
    }

    public static final String MEMCACHE_PREFIX = "PHARMACY_ID:";

    static public Pharmacy getPharmacyByID(final String id)
    {
        // Using the synchronous cache
        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
        memcacheService.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        Pharmacy pharmacy = (Pharmacy) memcacheService.get(MEMCACHE_PREFIX + id); // read from cache

        if(pharmacy == null) {
            final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            final Query query = new Query(KIND).setFilter(new Query.FilterPredicate(
                    PharmacyFactory.PROPERTY_PHARMACY_ID,
                    Query.FilterOperator.EQUAL,
                    id));
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Iterable<Entity> iterable = preparedQuery.asIterable();
            final Iterator<Entity> iterator = iterable.iterator();
            if(iterator.hasNext()) {
                pharmacy = getFromEntity(iterator.next());
                memcacheService.put(MEMCACHE_PREFIX + id, pharmacy);
            }
        }

        return pharmacy;
    }

    static public Vector<Pharmacy> getAllPharmacies()
    {
        final Vector<Pharmacy> pharmacies = new Vector<Pharmacy>();

        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND).addSort(PROPERTY_PHARMACY_ID);
        final PreparedQuery preparedQuery = datastoreService.prepare(query);
        for(final Entity entity : preparedQuery.asIterable())
        {
            pharmacies.add(getFromEntity(entity));
        }

        return pharmacies;
    }

    static public Key addPharmacy(final String ID, final String nameEl, final String address, final String address_postal_code,
                                  final String address_details, final double lat, final double lng,
                                  final String localityUUID, final String phoneBusiness, final String phoneHome)
    {
        return addPharmacy(ID, nameEl, address, address_postal_code, address_details, lat, lng, localityUUID, phoneBusiness, phoneHome, true);
    }

    static public Key addPharmacy(final String ID, final String nameEl, final String address, final String address_postal_code,
                                  final String address_details, final double lat, final double lng,
                                  final String localityUUID, final String phoneBusiness, final String phoneHome, final boolean active)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity pharmacyEntity = new Entity(KIND);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ID, ID);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_NAME_EL, nameEl);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ADDRESS, address);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE, address_postal_code);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ADDRESS_DETAILS, address_details);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_LAT, lat);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_LNG, lng);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_LOCALITY_UUID, localityUUID);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_PHONE_BUSINESS, phoneBusiness);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_PHONE_HOME, phoneHome);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ACTIVE, active);
        final long lastUpdated = System.currentTimeMillis();
        pharmacyEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        return datastoreService.put(pharmacyEntity);
    }

    static public List<Key> addPharmacies(Vector<Entity> pharmacyEntities)
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        return datastoreService.put(pharmacyEntities);
    }

    static public Key editPharmacy(final String uuid, final String ID, final String nameEl, final String address,
                                   final String address_postal_code,
                                  final String address_details, final double lat, final double lng,
                                  final String localityUUID, final String phoneBusiness, final String phoneHome)
            throws EntityNotFoundException
    {
        return editPharmacy(uuid, ID, nameEl, address, address_postal_code, address_details, lat, lng, localityUUID,
                phoneBusiness, phoneHome, true);
    }

    static public Key editPharmacy(final String uuid, final String ID, final String nameEl, final String address,
                                   final String address_postal_code,
                                  final String address_details, final double lat, final double lng,
                                  final String localityUUID, final String phoneBusiness, final String phoneHome, final boolean active)
            throws EntityNotFoundException
    {
        final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        final Entity pharmacyEntity = datastoreService.get(KeyFactory.stringToKey(uuid));

        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ID, ID);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_NAME_EL, nameEl);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ADDRESS, address);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE, address_postal_code);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ADDRESS_DETAILS, address_details);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_LAT, lat);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_LNG, lng);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_LOCALITY_UUID, localityUUID);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_PHONE_BUSINESS, phoneBusiness);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_PHONE_HOME, phoneHome);
        pharmacyEntity.setProperty(PROPERTY_PHARMACY_ACTIVE, active);
        final long lastUpdated = System.currentTimeMillis();
        pharmacyEntity.setProperty(PROPERTY_LAST_UPDATED, lastUpdated);

        return datastoreService.put(pharmacyEntity);
    }

    static public Pharmacy getFromEntity(final Entity entity)
    {
        return new Pharmacy(
                KeyFactory.keyToString(entity.getKey()),
                (Long) entity.getProperty(PROPERTY_LAST_UPDATED),
                (String) entity.getProperty(PROPERTY_PHARMACY_ID),
                (String) entity.getProperty(PROPERTY_PHARMACY_NAME_EL),
                (String) entity.getProperty(PROPERTY_PHARMACY_ADDRESS),
                (String) entity.getProperty(PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE),
                (String) entity.getProperty(PROPERTY_PHARMACY_ADDRESS_DETAILS),
                (String) entity.getProperty(PROPERTY_PHARMACY_LOCALITY_UUID),
                (Double) entity.getProperty(PROPERTY_PHARMACY_LAT),
                (Double) entity.getProperty(PROPERTY_PHARMACY_LNG),
                (String) entity.getProperty(PROPERTY_PHARMACY_PHONE_BUSINESS),
                (String) entity.getProperty(PROPERTY_PHARMACY_PHONE_HOME),
                entity.hasProperty(PROPERTY_PHARMACY_ACTIVE) ? (Boolean) entity.getProperty(PROPERTY_PHARMACY_ACTIVE) : true);
    }
}
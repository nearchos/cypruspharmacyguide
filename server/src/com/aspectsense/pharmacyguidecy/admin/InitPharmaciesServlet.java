package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.Locality;
import com.aspectsense.pharmacyguidecy.Pharmacy;
import com.aspectsense.pharmacyguidecy.data.LocalityFactory;
import com.aspectsense.pharmacyguidecy.data.PharmacyFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * User: Nearchos Paspallis
 * Date: 11/21/12
 * Time: 10:06 AM
 */
public class InitPharmaciesServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/html");

        if(user == null)
        {
            response.getWriter().print("You must sign in first");
        }
        else
        {
            final UserEntity userEntity = UserEntity.getUserEntity(user.getEmail());
            if(userEntity == null || !userEntity.isAdmin())
            {
                response.getWriter().print("User '" + user.getEmail() + "' is not an admin");
            }
            else
            {
                // delete existing entries...
                final Vector<Pharmacy> pharmacies = PharmacyFactory.getAllPharmacies();
                final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
                for(final Pharmacy pharmacy : pharmacies)
                {
                    datastoreService.delete(KeyFactory.stringToKey(pharmacy.getUUID()));
                }

                final Map<String,String> localityNameElToUUID = new HashMap<String, String>();
                final Vector<Locality> allLocalities = LocalityFactory.getAllLocalities();
                for(final Locality locality : allLocalities)
                {
                    localityNameElToUUID.put(locality.getNameEl(), locality.getUUID());
                }

                final InputStream inputStream = getServletContext().getResourceAsStream("/WEB-INF/pharmacies.txt");
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String line;
                final Vector<Entity> pharmacyEntities = new Vector<Entity>();
                while((line = bufferedReader.readLine()) != null)
                {
                    try
                    {
                        StringTokenizer stringTokenizer = new StringTokenizer(line, "|");
                        final String ID = stringTokenizer.nextToken().trim();
                        final String nameEl = stringTokenizer.nextToken().trim();
                        final String address = stringTokenizer.nextToken().trim();
                        final String addressPostalCode = stringTokenizer.nextToken().trim();
                        final String addressDetails = stringTokenizer.nextToken().trim();
                        final String localityNameEl = stringTokenizer.nextToken().trim();
                        final String latS = stringTokenizer.nextToken();
                        final double lat = latS == null ? 0d : Double.parseDouble(latS);
                        final String lngS = stringTokenizer.nextToken();
                        final double lng = lngS == null ? 0d : Double.parseDouble(lngS);
                        final String phoneBusiness = stringTokenizer.nextToken().trim();
                        final String phoneHome = stringTokenizer.nextToken().trim();

                        final Entity pharmacyEntity = new Entity(PharmacyFactory.KIND);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_ID, ID);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_NAME_EL, nameEl);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_ADDRESS, address);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE, addressPostalCode);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_DETAILS, addressDetails);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_LAT, lat);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_LNG, lng);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_LOCALITY_UUID, localityNameElToUUID.get(localityNameEl));
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_PHONE_BUSINESS, phoneBusiness);
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_PHARMACY_PHONE_HOME, phoneHome);
                        final long lastUpdated = System.currentTimeMillis();
                        pharmacyEntity.setProperty(PharmacyFactory.PROPERTY_LAST_UPDATED, lastUpdated);

                        pharmacyEntities.add(pharmacyEntity);
//                    PharmacyFactory.addPharmacy(ID, nameEl, address, addressDetails, lat, lng, localityNameElToUUID.get(localityNameEl), phoneBusiness, phoneHome);
                    }
                    catch (java.util.NoSuchElementException nsee)
                    {
                        log("Error while tokenizing: '" + line + "'", nsee);
                    }
                }

                PharmacyFactory.addPharmacies(pharmacyEntities);

                // also reset memcache
                final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
                memcacheService.clearAll();

                response.sendRedirect("/admin");
            }
        }
    }
}
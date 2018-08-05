package com.aspectsense.pharmacyguidecy.json;

import com.aspectsense.pharmacyguidecy.City;
import com.aspectsense.pharmacyguidecy.Locality;
import com.aspectsense.pharmacyguidecy.OnCall;
import com.aspectsense.pharmacyguidecy.Pharmacy;
import com.aspectsense.pharmacyguidecy.data.CityFactory;
import com.aspectsense.pharmacyguidecy.data.LocalityFactory;
import com.aspectsense.pharmacyguidecy.data.OnCallFactory;
import com.aspectsense.pharmacyguidecy.data.PharmacyFactory;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * User: Nearchos Paspallis
 * Date: 12/28/12
 * Time: 12:03 PM
 */
public class UpdateAllJsonServlet extends HttpServlet
{
    // todo move these to the DB so that it is configurable
    public static final String MAGIC_ANDROID  = "e762ecf7–def1–49f4–937c-517700376674";
    public static final String MAGIC_FACEBOOK = "821b8856-f824-4af4-88bf-61becaff0603"; // ektagon
    public static final String MAGIC_IOS      = "e90bcdc6-84f1-4d5f-9f79-06a62d857e25"; // geoathien

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final StringBuilder stringBuilder = new StringBuilder();
        final String result;

        final String magic = request.getParameter("magic");
        if(magic == null || !(MAGIC_ANDROID.equals(magic) || MAGIC_FACEBOOK.equals(magic) || MAGIC_IOS.equals(magic)))
        {
            log("Error - invalid magic argument: " + magic);
            stringBuilder
                    .append("{\n")
                    .append("  \"status\": \"protocol error (magic)\"\n")
                    .append("}");

            result = stringBuilder.toString();
        }
        else
        {
            final String fromS = request.getParameter("from");
            long from = 0L;
            try
            {
                from = fromS == null ? 0L : Long.parseLong(fromS);
            }
            catch (NumberFormatException nfe)
            {
                log("Error parsing 'from' argument: " + fromS, nfe);
            }

            final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
            if(memcacheService.contains(from))
            {
                result = (String) memcacheService.get(from);
            }
            else
            {
                final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

                stringBuilder
                        .append("{\n")
                        .append("  \"status\": \"ok\",\n");

                long lastUpdated = from;

                lastUpdated = Math.max(lastUpdated, appendCities(datastore, stringBuilder, from));
                lastUpdated = Math.max(lastUpdated, appendLocalities(datastore, stringBuilder, from));
                lastUpdated = Math.max(lastUpdated, appendPharmacies(datastore, stringBuilder, from));
                lastUpdated = Math.max(lastUpdated, appendOnCalls(datastore, stringBuilder, from));

                stringBuilder
                        .append("  \"lastUpdated\": " + lastUpdated + "\n")
                        .append("}");

                result = stringBuilder.toString();

                // store in cache
                memcacheService.put(from, result);
            }
        }

        final PrintWriter printWriter = response.getWriter();

        printWriter.println(result);
    }

    private long appendCities(final DatastoreService datastore, final StringBuilder stringBuilder, final long from)
    {
        final Query.Filter filterFrom = new Query.FilterPredicate(
                CityFactory.PROPERTY_LAST_UPDATED,
                Query.FilterOperator.GREATER_THAN,
                from);

        final Query query = new Query(CityFactory.KIND);
        // filter cities last updated after "from"
        query.setFilter(filterFrom);
        final PreparedQuery preparedQuery = datastore.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();

        stringBuilder.append("  \"cities\": [\n");

        long lastUpdated = 0L;

        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            final City city = CityFactory.getFromEntity(entity);

            stringBuilder.append(
                    "    {\n" +
                            "      \"UUID\": \"" + city.getUUID() + "\",\n" +
                            "      \"nameEl\": \"" + city.getNameEl() + "\",\n" +
                            "      \"nameEn\": \"" + city.getNameEn() + "\",\n" +
                            "      \"lat\": " + city.getLat() + ",\n" +
                            "      \"lng\": " + city.getLng() + "\n" +
                            "    }" + (iterator.hasNext() ? ",\n\n" : "\n")
            );

            lastUpdated = Math.max(lastUpdated, city.getLastUpdated());
        }

        stringBuilder.append("  ],\n\n");

        return lastUpdated;
    }

    private long appendLocalities(final DatastoreService datastore, final StringBuilder stringBuilder, final long from)
    {
        final Query.Filter filterFrom = new Query.FilterPredicate(
                LocalityFactory.PROPERTY_LAST_UPDATED,
                Query.FilterOperator.GREATER_THAN,
                from);

        final Query query = new Query(LocalityFactory.KIND);
        // filter localities last updated after "from"
        query.setFilter(filterFrom);
        final PreparedQuery preparedQuery = datastore.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();

        stringBuilder.append("  \"localities\": [\n");

        long lastUpdated = 0L;

        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            final Locality locality = LocalityFactory.getFromEntity(entity);

            stringBuilder
                    .append("    {\n")
                    .append("      \"UUID\": \"" + locality.getUUID() + "\",\n")
                    .append("      \"nameEl\": \"" + locality.getNameEl() + "\",\n")
                    .append("      \"nameEn\": \"" + locality.getNameEn() + "\",\n")
                    .append("      \"lat\": " + locality.getLat() + ",\n")
                    .append("      \"lng\": " + locality.getLng() + ",\n")
                    .append("      \"cityUUID\": \"" + locality.getCityUUID() + "\"\n")
                    .append("    }" + (iterator.hasNext() ? ",\n\n" : "\n"));

            lastUpdated = Math.max(lastUpdated, locality.getLastUpdated());
        }

        stringBuilder.append("  ],\n\n");

        return lastUpdated;
    }

    private long appendPharmacies(final DatastoreService datastore, final StringBuilder stringBuilder, final long from)
    {
        final Query.Filter filterFrom = new Query.FilterPredicate(
                PharmacyFactory.PROPERTY_LAST_UPDATED,
                Query.FilterOperator.GREATER_THAN,
                from);

        final Query query = new Query(PharmacyFactory.KIND);
        // filter pharmacies last updated after 'from'
        query.setFilter(filterFrom);
        final PreparedQuery preparedQuery = datastore.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();

        stringBuilder.append("  \"pharmacies\": [\n");

        long lastUpdated = 0;

        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            final Pharmacy pharmacy = PharmacyFactory.getFromEntity(entity);

            stringBuilder
                    .append("    {\n")
                    .append("      \"ID\": " + pharmacy.getID() + ",\n")
                    .append("      \"name\": \"" + pharmacy.getName() + "\",\n")
                    .append("      \"address\": \"" + pharmacy.getAddress() + "\",\n")
                    .append("      \"addressPostalCode\": \"" + pharmacy.getAddressPostalCode() + "\",\n")
                    .append("      \"addressDetails\": \"" + pharmacy.getAddressDetails() + "\",\n")
                    .append("      \"lat\": " + pharmacy.getLat() + ",\n")
                    .append("      \"lng\": " + pharmacy.getLng() + ",\n")
                    .append("      \"localityUUID\": \"" + pharmacy.getLocalityUUID() + "\",\n")
                    .append("      \"phoneBusiness\": \"" + pharmacy.getPhoneBusiness() + "\",\n")
                    .append("      \"phoneHome\": \"" + pharmacy.getPhoneHome() + "\"\n")
                    .append("    }" + (iterator.hasNext() ? ",\n\n" : "\n"));

            lastUpdated = Math.max(lastUpdated, pharmacy.getLastUpdated());
        }

        stringBuilder.append("  ],\n\n");

        return lastUpdated;
    }

    private long appendOnCalls(final DatastoreService datastore, final StringBuilder stringBuilder, final long from)
    {
        final Query.Filter filterFrom = new Query.FilterPredicate(
                OnCallFactory.PROPERTY_LAST_UPDATED,
                Query.FilterOperator.GREATER_THAN,
                from);

        final Query query = new Query(OnCallFactory.KIND);
        // filter pharmacies last updated after 'from'
        query.setFilter(filterFrom);
        final PreparedQuery preparedQuery = datastore.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();

        stringBuilder.append("  \"on-calls\": [\n");

        long lastUpdated = 0L;

        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            final OnCall onCall = OnCallFactory.getFromEntity(entity);

            stringBuilder
                    .append("    {\n")
                    .append("      \"date\": \"" + onCall.getDate() + "\",\n")
                    .append("      \"pharmacies\": \"" + onCall.getPharmacies() + "\"\n")
                    .append("    }" + (iterator.hasNext() ? ",\n\n" : "\n"));

            lastUpdated = Math.max(lastUpdated, onCall.getLastUpdated());
        }

        stringBuilder.append("  ],\n\n");

        return lastUpdated;
    }
}
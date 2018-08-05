package com.aspectsense.pharmacyguidecy.services;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * User: Nearchos Paspallis
 * Date: 11/3/12
 * Time: 10:05 AM
 */
public class LogNearbySearch extends HttpServlet
{
    private static final Logger log = Logger.getLogger(LogNearbySearch.class.getCanonicalName());

    public static final String NEARBY_SEARCH_LOG_KIND = "nearby_search_log";

    public static final String PROPERTY_INSTALLATION_ID = "installation_id";
    public static final String PROPERTY_LAT = "lat";
    public static final String PROPERTY_LNG = "lng";
    public static final String PROPERTY_TIMESTAMP = "timestamp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final String installation_id = request.getParameter("installation_id");
        final String lat = request.getParameter("lat");
        final String lng = request.getParameter("lng");

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Entity nearbySearchLog = new Entity(NEARBY_SEARCH_LOG_KIND);
        nearbySearchLog.setProperty(PROPERTY_INSTALLATION_ID, installation_id);
        nearbySearchLog.setProperty(PROPERTY_LAT, lat);
        nearbySearchLog.setProperty(PROPERTY_LNG, lng);
        nearbySearchLog.setProperty(PROPERTY_TIMESTAMP, System.currentTimeMillis());
        datastore.put(nearbySearchLog);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println("{ \"result\": \"ok\" }");
    }
}

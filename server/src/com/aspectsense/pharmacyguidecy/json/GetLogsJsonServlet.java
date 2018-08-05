package com.aspectsense.pharmacyguidecy.json;

import com.aspectsense.pharmacyguidecy.data.LogEventFactory;
import com.google.appengine.api.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

/**
 * Date: 18/07/13
 * Time: 10:26
 */
public class GetLogsJsonServlet extends HttpServlet
{
    public static final int DEFAULT_LIMIT_VALUE = 100;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final StringBuilder stringBuilder = new StringBuilder();
        final String result;

        final String magic = request.getParameter("magic");
        if(magic == null || !(UpdateAllJsonServlet.MAGIC_ANDROID.equals(magic)))
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
            final String limitS = request.getParameter("limit");
            int limit = DEFAULT_LIMIT_VALUE;
            try
            {
                limit = limitS == null ? DEFAULT_LIMIT_VALUE : Integer.parseInt(limitS);
            }
            catch (NumberFormatException nfe)
            {
                log("Error parsing 'limit' argument: " + limitS, nfe);
            }

            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            stringBuilder
                    .append("{\n")
                    .append("  \"status\": \"ok\",\n");

            final int numOfLogs = appendLogs(datastore, stringBuilder, limit);

            stringBuilder
                    .append("  \"numOfLogs\": ").append(numOfLogs).append("\n")
                    .append("}");

            result = stringBuilder.toString();
        }

        final PrintWriter printWriter = response.getWriter();

        printWriter.println(result);
    }

    private int appendLogs(final DatastoreService datastore, final StringBuilder stringBuilder, final int limit)
    {
        final Query query = new Query(LogEventFactory.KIND).addSort(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP, Query.SortDirection.ASCENDING);

        final PreparedQuery preparedQuery = datastore.prepare(query);
        final List<Entity> selectedEntityList = preparedQuery.asList(FetchOptions.Builder.withLimit(limit));
        int numOfSelectedEntities = selectedEntityList.size();

        stringBuilder.append("  \"logs\": [\n");

        final Vector<Key> selectedEntitiesKeys = new Vector<Key>();

        for(int i = 0; i < numOfSelectedEntities; i++)
        {
            Entity entity = selectedEntityList.get(i);
            selectedEntitiesKeys.add(entity.getKey());

            stringBuilder.append("    {\n")
                    .append("      \"installation_id\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_INSTALLATION_ID)).append("\",\n")
                    .append("      \"lat\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_LAT)).append("\",\n")
                    .append("      \"lng\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_LNG)).append("\",\n")
                    .append("      \"type\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_TYPE)).append("\",\n")
                    .append("      \"timestamp\": ").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP)).append("\n")
                    .append("    }").append(i < numOfSelectedEntities - 1 ? ",\n\n" : "\n");
        }

        stringBuilder.append("  ],\n\n");

        return selectedEntitiesKeys.size();
    }
}
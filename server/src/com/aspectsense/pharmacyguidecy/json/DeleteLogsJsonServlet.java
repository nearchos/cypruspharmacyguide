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
public class DeleteLogsJsonServlet extends HttpServlet
{
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
            int limit = GetLogsJsonServlet.DEFAULT_LIMIT_VALUE;
            try
            {
                limit = limitS == null ? GetLogsJsonServlet.DEFAULT_LIMIT_VALUE : Integer.parseInt(limitS);
            }
            catch (NumberFormatException nfe)
            {
                log("Error parsing 'limit' argument: " + limitS, nfe);
            }

            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            final int numOfLogs = deleteLogs(datastore, limit);

            stringBuilder
                    .append("{\n")
                    .append("  \"status\": \"ok\",\n")
                    .append("  \"limit\": ").append(limit).append(",\n")
                    .append("  \"numOfLogsDeleted\": ").append(numOfLogs).append("\n")
                    .append("}");

            result = stringBuilder.toString();
        }

        final PrintWriter printWriter = response.getWriter();

        printWriter.println(result);
    }

    private int deleteLogs(final DatastoreService datastore, final int limit)
    {
        final Query query = new Query(LogEventFactory.KIND).addSort(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP, Query.SortDirection.ASCENDING);

        final PreparedQuery preparedQuery = datastore.prepare(query);
        final List<Entity> selectedEntityList = preparedQuery.asList(FetchOptions.Builder.withLimit(limit));

        int numOfSelectedEntities = selectedEntityList.size();
        final Vector<Key> selectedEntitiesKeys = new Vector<Key>();
        for (final Entity entity : selectedEntityList)
        {
            selectedEntitiesKeys.add(entity.getKey());
        }

        datastore.delete(selectedEntitiesKeys);

        return numOfSelectedEntities;
    }
}
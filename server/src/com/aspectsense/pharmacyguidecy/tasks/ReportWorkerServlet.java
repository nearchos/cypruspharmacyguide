package com.aspectsense.pharmacyguidecy.tasks;

import com.aspectsense.pharmacyguidecy.data.LogEvent;
import com.aspectsense.pharmacyguidecy.data.LogEventFactory;
import com.aspectsense.pharmacyguidecy.data.Report;
import com.aspectsense.pharmacyguidecy.data.ReportFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Text;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created on 16/07/2014 / 19:27.
 * @author Nearchos Paspallis
 */
public class ReportWorkerServlet extends HttpServlet
{
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final String uuid = request.getParameter("uuid");

        final Report report = ReportFactory.getReport(uuid);

        // execute long-running task
        final Vector<LogEvent> logEvents = LogEventFactory.getLogEvents(report.getFromTimestamp(), report.getToTimestamp());

        final StringBuilder stringBuilder = new StringBuilder("{\n");
        stringBuilder.append("  \"reports\": [\n");

        final Iterator<LogEvent> iterator = logEvents.iterator();
        while(iterator.hasNext())
        {
            final LogEvent logEvent = iterator.next();
            stringBuilder
                    .append("    {\n")
                    .append("      \"installation_id\": \"").append(logEvent.getInstallationId()).append("\",\n")
                    .append("      \"lat\": ").append(logEvent.getLat()).append(",\n")
                    .append("      \"lng\": ").append(logEvent.getLng()).append("\",\n")
                    .append("      \"type\": ").append(logEvent.getType()).append(",\n")
                    .append("      \"timestamp\": ").append(logEvent.getTimestamp()).append("\n")
                    .append("    }").append(iterator.hasNext() ? ",\n\n" : "\n");
        }

        stringBuilder.append("  ]\n");
        stringBuilder.append("}\n");

        // store to blobstore
        try
        {
            ReportFactory.editReport(
                    report.getUUID(),
                    report.getCreatedBy(),
                    report.getRequestedOn(),
                    System.currentTimeMillis(),
                    report.getFromTimestamp(),
                    report.getToTimestamp(),
                    new Text(stringBuilder.toString()));
        }
        catch (EntityNotFoundException enfe)
        {
            throw new ServletException(enfe);
        }
    }
}

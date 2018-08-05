package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.LogEventFactory;
import com.aspectsense.pharmacyguidecy.data.Parameter;
import com.aspectsense.pharmacyguidecy.data.ParameterFactory;
import com.google.appengine.api.datastore.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis
 * @date 23/06/2015 / 10:08.
 */
public class DailyReportServlet extends HttpServlet
{
    public static final int ENTITIES_TO_BE_FETCHED_LIMIT = 5000;

    public static final Logger log = Logger.getLogger(DailyReportServlet.class.getCanonicalName());

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final PrintWriter printWriter = response.getWriter();

        // check if daily reports are turned on
        final Parameter parameterDailyReports = ParameterFactory.getParameterByName(ParametersServlet.PARAMETER_DAILY_REPORTS);
        if(parameterDailyReports != null && !parameterDailyReports.getValueAsBoolean()) {
            printWriter.println("OK");
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        final String result;

        final long start = System.currentTimeMillis();

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(LogEventFactory.KIND).addSort(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP, Query.SortDirection.ASCENDING);

        final PreparedQuery preparedQuery = datastore.prepare(query);
        final List<Entity> selectedEntityList = preparedQuery.asList(FetchOptions.Builder.withLimit(ENTITIES_TO_BE_FETCHED_LIMIT));
        int numOfSelectedEntities = selectedEntityList.size();

        stringBuilder
                .append("{\n")
                .append("  \"status\": \"ok\",\n")
                .append("  \"logs\": [\n");

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

        final long from = (Long) selectedEntityList.get(0).getProperty(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP);
        final String fromS = SIMPLE_DATE_FORMAT.format(new Date(from));
        final long to = (Long) selectedEntityList.get(numOfSelectedEntities-1).getProperty(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP);
        final String toS = SIMPLE_DATE_FORMAT.format(new Date(to));
        stringBuilder.append("  ],\n\n")
                    .append("  \"from\": \"").append(fromS).append("\",\n")
                    .append("  \"to\": \"").append(toS).append("\",\n");

        // decide whether to delete or not - in principle we delete the entities if the last one was indeed older than 1 week
        boolean delete = System.currentTimeMillis() - to > WeeklyReportServlet.MILLISECONDS_IN_A_WEEK;

        if(delete)
        {
            datastore.delete(selectedEntitiesKeys); // deleting selected keys
        }

        final long duration = System.currentTimeMillis() - start;

        stringBuilder
                .append("  \"numOfLogs\": ").append(numOfSelectedEntities).append(",\n")
                .append("  \"duration\": ").append("\"").append(duration/ 1000d).append(" seconds\",\n")
                .append("  \"deleted\": ").append(delete).append("\n")
                .append("}");

        result = stringBuilder.toString();

        final Parameter parameter = ParameterFactory.getParameterByName(ParametersServlet.PARAMETER_EMAIL_DAILY_REPORTS_ONLY_WHEN_DELETED);
        if(parameter != null && parameter.getValueAsBoolean()) {
            if(delete)
                sendEmail(result, fromS, toS, numOfSelectedEntities, true, duration);
        } else {
            sendEmail(result, fromS, toS, numOfSelectedEntities, delete, duration);
        }

        printWriter.println("OK");
    }

    public static final String REPORT_RECEIVER_EMAIL = "nearchos@aspectsense.com";
    public static final String REPORT_RECEIVER_NAME  = "Nearchos Paspallis";

    private void sendEmail(String payload, final String fromDate, final String toDate, final int numOfLogs, final boolean deleted, final long duration)
    {
        final String messageText =
                "Daily report by Cyprus Pharmacy Guide\n" +
                "From: " + fromDate + " (inclusive)\n" +
                "To: " + toDate + " (exclusive)\n" +
                "Num of logs: " + numOfLogs + " \n" +
                "Deleted: " + deleted + " \n" +
                "Duration: " + duration/1000d + " seconds \n\n";

        final Session session = Session.getDefaultInstance(new Properties(), null);
        try
        {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("nearchos@gmail.com", "Cyprus Pharmacy Guide", "utf-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(REPORT_RECEIVER_EMAIL, REPORT_RECEIVER_NAME));
            message.setSubject("Daily report ending " + toDate);
            final Multipart multipart = new MimeMultipart();

            // the message part ...
            final BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(messageText, "text/plain");
            multipart.addBodyPart(messageBodyPart);

            // ... and the attachment part
            final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.setFileName(toDate + ".json");
            attachmentBodyPart.setContent(payload, "application/json");
            multipart.addBodyPart(attachmentBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        }
        catch (MessagingException | UnsupportedEncodingException e)
        {
            log.severe(e.getMessage());
        }
    }
}
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
 * @date 15/04/2015 / 15:08.
 */
public class WeeklyReportServlet extends HttpServlet
{
    public static final long MILLISECONDS_IN_A_WEEK = 7L * 24 * 60 * 60 * 1000L;

    public static final Logger log = Logger.getLogger(WeeklyReportServlet.class.getCanonicalName());

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final PrintWriter printWriter = response.getWriter();

        // check if daily reports are turned on
        final Parameter parameterWeeklyReports = ParameterFactory.getParameterByName(ParametersServlet.PARAMETER_WEEKLY_REPORTS);
        if(parameterWeeklyReports != null && !parameterWeeklyReports.getValueAsBoolean()) {
            printWriter.println("OK");
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        final String result;

        // point to most recent Monday 00:00 (i.e. the beginning of this week)
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Nicosia"));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final long to = calendar.getTimeInMillis();
        final String toS = SIMPLE_DATE_FORMAT.format(new Date(to));
        final long from = to - MILLISECONDS_IN_A_WEEK;
        final String fromS = SIMPLE_DATE_FORMAT.format(new Date(from));

//        final SimpleDateFormat detailedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
//        detailedDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Nicosia"));
//        log.info("Filtering from date: " + detailedDateFormat.format(from));
//        log.info("Filtering to date: " + detailedDateFormat.format(to));

        boolean delete = false;

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        stringBuilder
                .append("{\n")
                .append("  \"status\": \"ok\",\n")
                .append("  \"from\": \"").append(fromS).append("\",\n")
                .append("  \"to\": \"").append(toS).append("\",\n");

        final long start = System.currentTimeMillis();
        final int numOfLogs = appendLogs(datastore, stringBuilder, from, to, delete);
        final long duration = System.currentTimeMillis() - start;

        stringBuilder
                .append("  \"numOfLogs\": ").append(numOfLogs).append(",\n")
                .append("  \"duration\": ").append("\"").append(duration/ 1000d).append(" seconds\",\n")
                .append("  \"deleted\": ").append(delete).append("\n")
                .append("}");

        result = stringBuilder.toString();

        sendEmail(result, fromS, toS, numOfLogs, duration);

        printWriter.println(result);
    }

    private int appendLogs(final DatastoreService datastore, final StringBuilder stringBuilder, final long from, final long to, boolean delete)
    {
        final Query.Filter filterFrom = new Query.FilterPredicate(
                LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP,
                Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                from);

        final Query.Filter filterTo = new Query.FilterPredicate(
                LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP,
                Query.FilterOperator.LESS_THAN,
                to);

        final Query.Filter compositeFromToFilter = Query.CompositeFilterOperator.and(filterFrom, filterTo);

        final Query query = new Query(LogEventFactory.KIND);
        // filter cities last updated on or after "from" and before "to"
        query.setFilter(compositeFromToFilter);

        final PreparedQuery preparedQuery = datastore.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();

        stringBuilder.append("  \"logs\": [\n");

        final Vector<Key> allKeys = new Vector<Key>();

        while(iterator.hasNext())
        {
            final Entity entity = iterator.next();
            allKeys.add(entity.getKey());

            stringBuilder.append("    {\n")
                    .append("      \"installation_id\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_INSTALLATION_ID)).append("\",\n")
                    .append("      \"lat\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_LAT)).append("\",\n")
                    .append("      \"lng\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_LNG)).append("\",\n")
                    .append("      \"type\": \"").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_TYPE)).append("\",\n")
                    .append("      \"timestamp\": ").append(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP)).append("\n")
                    .append("    }").append(iterator.hasNext() ? ",\n\n" : "\n");
        }

        if(delete)
        {
            datastore.delete(allKeys);
        }

        stringBuilder.append("  ],\n\n");

        return allKeys.size();
    }

    public static final String REPORT_RECEIVER_EMAIL = "nearchos@aspectsense.com";
    public static final String REPORT_RECEIVER_NAME  = "Nearchos Paspallis";

    private void sendEmail(String payload, final String fromDate, final String toDate, final int numOfLogs, final long duration)
    {
        final String messageText =
                "Weekly report by Cyprus Pharmacy Guide\n" +
                "From: " + fromDate + " (inclusive)\n" +
                "To: " + toDate + " (exclusive)\n" +
                "Num of logs: " + numOfLogs + " \n" +
                "Duration: " + duration/1000d + " seconds \n\n";

        final Session session = Session.getDefaultInstance(new Properties(), null);
        try
        {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("nearchos@gmail.com", "Cyprus Pharmacy Guide", "utf-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(REPORT_RECEIVER_EMAIL, REPORT_RECEIVER_NAME));
            message.setSubject("Weekly report ending " + toDate);
            final Multipart multipart = new MimeMultipart();

            // the message part ...
            final BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(messageText, "text/plain");
            multipart.addBodyPart(messageBodyPart);

            // ... and the attachment part
            final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.setFileName("weekly-" + toDate + ".json");
            attachmentBodyPart.setContent(payload, "application/json");
            multipart.addBodyPart(attachmentBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        }
        catch (AddressException ae)
        {
            log.severe(ae.getMessage());
        }
        catch (MessagingException me)
        {
            log.severe(me.getMessage());
        }
        catch (UnsupportedEncodingException uee)
        {
            log.severe(uee.getMessage());
        }
    }
}
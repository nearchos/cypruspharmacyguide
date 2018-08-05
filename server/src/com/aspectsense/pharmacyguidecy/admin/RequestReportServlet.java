package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.ReportFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

/**
 * Created on 16/07/2014 / 18:51.
 * @author Nearchos Paspallis
 */
public class RequestReportServlet extends HttpServlet
{
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
                final String fromTimestampS = request.getParameter(ReportFactory.PROPERTY_REPORT_FROM_TIMESTAMP);
                final String toTimestampS = request.getParameter(ReportFactory.PROPERTY_REPORT_TO_TIMESTAMP);

                final long requestedOn = System.currentTimeMillis();
                long fromTimestamp;
                long toTimestamp;
                try
                {
                    fromTimestamp = SIMPLE_DATE_FORMAT.parse(fromTimestampS).getTime();
                    toTimestamp = SIMPLE_DATE_FORMAT.parse(toTimestampS).getTime();
                }
                catch (ParseException pe)
                {
                    response.getWriter().print("Invalid or missing argument(s) from/to timestamp: " + pe.getMessage());
                    throw new ServletException(pe);
                }

                // add
                final Key key = ReportFactory.addReport(userEntity.getEmail(), requestedOn, fromTimestamp, toTimestamp);

                // Add the task to the default queue - trigger asynchronous generation of report
                final Queue queue = QueueFactory.getDefaultQueue();
                queue.add(withUrl("/tasks/create-report-worker").param("uuid", KeyFactory.keyToString(key)));

                log("Requested " + ReportFactory.KIND + " with key: " + key);

                response.sendRedirect("/admin/reports");
            }
        }

    }
}
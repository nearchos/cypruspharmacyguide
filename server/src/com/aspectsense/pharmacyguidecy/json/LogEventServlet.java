package com.aspectsense.pharmacyguidecy.json;

import com.aspectsense.pharmacyguidecy.data.LogEvent;
import com.aspectsense.pharmacyguidecy.data.LogEventFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Date: 1/21/13
 * Time: 8:21 PM
 */
public class LogEventServlet extends HttpServlet
{
    static private Logger logger = Logger.getLogger(LogEventServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final String installation_id = request.getParameter("installation_id");
        final String latS = request.getParameter("lat").replaceAll(",", ".");
        final String lngS = request.getParameter("lng").replaceAll(",", ".");
        final double lat = Double.parseDouble(latS);
        final double lng = Double.parseDouble(lngS);
        final long timestamp = System.currentTimeMillis();
        final String type = request.getParameter("type");

        final LogEvent logEvent = new LogEvent(installation_id, lat, lng, timestamp, type);
        LogEventFactory.addLogEvent(logEvent);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println("{ \"result\": \"ok\" }");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final String installation_id = request.getParameter("installation_id");
        final String latS = request.getParameter("lat").replaceAll(",", ".");
        final String lngS = request.getParameter("lng").replaceAll(",", ".");
        final double lat = Double.parseDouble(latS);
        final double lng = Double.parseDouble(lngS);
        final long timestamp = System.currentTimeMillis();
        final String type = request.getParameter("type");

        final LogEvent logEvent = new LogEvent(installation_id, lat, lng, timestamp, type);
        LogEventFactory.addLogEvent(logEvent);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println("{ \"result\": \"ok\" }");
    }
}

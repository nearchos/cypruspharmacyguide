package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.ParameterFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Nearchos
 * 04-Jun-16.
 */
public class ParametersServlet extends HttpServlet {

    public static final String PARAMETER_DAILY_REPORTS = "daily-reports";
    public static final String PARAMETER_EMAIL_DAILY_REPORTS_ONLY_WHEN_DELETED = "daily-reports-only-when-deleted";
    public static final String PARAMETER_WEEKLY_REPORTS = "weekly-reports";
    public static final String REDIRECT_URL = "redirect-url";

    private static final Logger log = Logger.getLogger(ParametersServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String redirectUrl = request.getParameter(REDIRECT_URL);
        final boolean dailyReports = "on".equalsIgnoreCase(request.getParameter(PARAMETER_DAILY_REPORTS));
        final boolean emailDailyReportsOnlyWhenDeleted = "on".equalsIgnoreCase(request.getParameter(PARAMETER_EMAIL_DAILY_REPORTS_ONLY_WHEN_DELETED));
        final boolean weeklyReports = "on".equalsIgnoreCase(request.getParameter(PARAMETER_WEEKLY_REPORTS));

        final Map map = request.getParameterMap();
        log.info("Parameters map: " + map);

        ParameterFactory.addOrUpdateParameter(PARAMETER_DAILY_REPORTS, Boolean.toString(dailyReports));
        ParameterFactory.addOrUpdateParameter(PARAMETER_EMAIL_DAILY_REPORTS_ONLY_WHEN_DELETED, Boolean.toString(emailDailyReportsOnlyWhenDeleted));
        ParameterFactory.addOrUpdateParameter(PARAMETER_WEEKLY_REPORTS, Boolean.toString(weeklyReports));

        response.sendRedirect(URLDecoder.decode(redirectUrl, "UTF-8"));
    }
}
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.pharmacyguidecy.admin.ParametersServlet" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 16/07/2014
  Time: 18:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide - Reports</title>
</head>
<body>

<%@ include file="authenticate.jsp" %>

<%
    if(userEntity == null)
    {
%>
You are not logged in!
<%
}
else if(!userEntity.isAdmin())
{
%>
You are not admin!
<%
}
else
{
    final Vector<Report> reports = ReportFactory.getAllReports();
%>

<h1>Reports</h1>
<%=reports.size()%> reports available.

<table border="1">
    <tr>
        <th>UUID</th>
        <th>CREATED BY</th>
        <th>REQUESTED ON</th>
        <th>GENERATED ON</th>
        <th>FROM TIMESTAMP</th>
        <th>TO TIMESTAMP</th>
        <th>BLOB UUID</th>
    </tr>
<%
    for(final Report report : reports)
    {
%>
    <tr>
        <td><%= report.getUUID() %></td>
        <td><%= report.getCreatedBy() %></td>
        <td><%= new Date(report.getRequestedOn()) %></td>
        <td><%= report.getGeneratedOn() == 0L ? "In progress..." : new Date(report.getGeneratedOn()) %></td>
        <td><%= new Date(report.getFromTimestamp()) %></td>
        <td><%= new Date(report.getToTimestamp()) %></td>
        <td>
<%
    if(report.getJsonText() == null)
    {
%>
            Not available yet
<%
    }
    else
    {
%>
            <form action="/admin/report">
                <input type="submit" value="Download"/>
                <input type="hidden" name="uuid" value="<%=report.getUUID()%>">
            </form>
<%
    }
%>
        </td>
    </tr>
<%
    }

    final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    final long now = System.currentTimeMillis();
    final long SEVEN_DAYS = 7L * 24L * 60L * 60L * 1000L;
%>
</table>

<h1>Request new report</h1>

<form action="/admin/request-report" method="post" onsubmit="submitButton.disabled = true; return true;">
    <table>
        <tr>
            <td>REPORT FROM</td>
            <td><input type="date" name="<%= ReportFactory.PROPERTY_REPORT_FROM_TIMESTAMP%>" value="<%= SIMPLE_DATE_FORMAT.format(new Date(now-SEVEN_DAYS)) %>"/></td>
        </tr>
        <tr>
            <td>REPORT TO</td>
            <td><input type="date" name="<%= ReportFactory.PROPERTY_REPORT_TO_TIMESTAMP%>" value="<%= SIMPLE_DATE_FORMAT.format(new Date(now)) %>"/></td>
        </tr>
    </table>
    <div><input type="submit" name="submitButton" value="Request report" /></div>
</form>

<%
    final Parameter dailyReportsParameter = ParameterFactory.getParameterByName(ParametersServlet.PARAMETER_DAILY_REPORTS);
    final Parameter emailDailyReportsOnlyWhenDeletedParameter = ParameterFactory.getParameterByName(ParametersServlet.PARAMETER_EMAIL_DAILY_REPORTS_ONLY_WHEN_DELETED);
    final Parameter weeklyReportsParameter = ParameterFactory.getParameterByName(ParametersServlet.PARAMETER_WEEKLY_REPORTS);
    final boolean dailyReports = dailyReportsParameter != null && dailyReportsParameter.getValueAsBoolean();
    final boolean emailDailyReportsOnlyWhenDeleted = emailDailyReportsOnlyWhenDeletedParameter != null && emailDailyReportsOnlyWhenDeletedParameter.getValueAsBoolean();
    final boolean weeklyReports = weeklyReportsParameter != null && weeklyReportsParameter.getValueAsBoolean();
%>

    <h1>Recurring reports</h1>

    <form action="/admin/parameters" method="post" onsubmit="saveButton.disabled=true; return true;">
    <input type="checkbox" name="<%=ParametersServlet.PARAMETER_DAILY_REPORTS%>"  <%=dailyReports ? " checked" : ""%> title="Daily reports"/>Daily reports<br>
    <input type="checkbox" name="<%= ParametersServlet.PARAMETER_EMAIL_DAILY_REPORTS_ONLY_WHEN_DELETED%>" <%=emailDailyReportsOnlyWhenDeleted ? " checked" : ""%> title="Email daily reports only when deleted"/>Email daily reports only when deleted<br>
    <input type="checkbox" name="<%= ParametersServlet.PARAMETER_WEEKLY_REPORTS%>" <%=weeklyReports ? " checked" : ""%> title="Weekly reports"/>Weekly reports<br>
    <input type="hidden" name="<%= ParametersServlet.REDIRECT_URL %>" value="<%= URLEncoder.encode("/admin/reports", "UTF-8") %>"/>
    <div><input type="submit" name="saveButton" value="Save" /></div>
    </form>

<%
}
%>
</body>
</html>

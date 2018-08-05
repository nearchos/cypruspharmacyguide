<%@ page import="com.aspectsense.pharmacyguidecy.data.Report" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.ReportFactory" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 16/07/2014
  Time: 18:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/text;charset=UTF-8" language="java" %>

<%
    final String reportUUID = request.getParameter("uuid");
    if(reportUUID == null)
    {
%>
Undefined parameter uuid!
<%
    }
    else
    {
        final Report report = ReportFactory.getReport(reportUUID);
        if(report == null)
        {
%>
Report with UUID=<%=reportUUID%> could not be found!
<%
        }
        else
        {
%>
<%=report.getJsonText().getValue()%>
<%
        }
    }
%>
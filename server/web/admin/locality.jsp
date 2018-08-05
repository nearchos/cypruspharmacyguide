<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LocalityFactory" %>
<%@ page import="java.util.Date" %>
<%--
  Date: 8/16/12
  Time: 6:24 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide - Localities</title>
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
%>
        <h1>Locality</h1>
<%
        String key = request.getParameter("key");

        final Locality locality = LocalityFactory.getLocality(key);
        if(locality == null)
        {
%>
            Locality not found!
<%
        }
        else
        {
%>
    <table border="1">
        <tr>
            <th>UUID</th>
            <th>LAST UPDATED</th>
            <th>NAME (EL)</th>
            <th>NAME (EN)</th>
            <th>CITY UUID</th>
            <th>COORDINATES</th>
        </tr>
<%
        final double lat = locality.getLat();
        final double lng = locality.getLng();
%>
        <tr>
            <td><%= locality.getUUID() %></td>
            <td><%= new Date(locality.getLastUpdated()) %></td>
            <td><%= locality.getNameEl() %></td>
            <td><%= locality.getNameEn() %></td>
            <td><%= locality.getCityUUID() %></td>
            <td>(<a href="https://maps.google.com/maps?q=<%= lat %>,<%= lng %>"><%= lat %>, <%= lng %></a>)</td>
        </tr>

    </table>
<%
        }
    }
%>

</body>
</html>
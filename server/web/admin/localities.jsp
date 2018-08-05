<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LocalityFactory" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
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
        Map<String,String> cityUUIDtoNameEl = new HashMap<String, String>();
        Vector<City> cities = CityFactory.getAllCities();
        for(final City city : cities)
        {
            cityUUIDtoNameEl.put(city.getUUID(), city.getNameEl());
        }

        final Vector<Locality> localities = LocalityFactory.getAllLocalities();
        final int numOfLocalities = localities == null ? 0 : localities.size();
%>
    <h1>Localities</h1>

    <p>Number of localities: <%=numOfLocalities%></p>

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
        if(localities != null)
        {
            for(final Locality locality : localities)
            {
                final double lat = locality.getLat();
                final double lng = locality.getLng();
%>
        <tr>
            <td><%= locality.getUUID() %></td>
            <td><%= new Date(locality.getLastUpdated()) %></td>
            <td><%= locality.getNameEl() %></td>
            <td><%= locality.getNameEn() %></td>
            <td><a href="/admin/cities"><%= cityUUIDtoNameEl.get(locality.getCityUUID()) %></a></td>
            <td>(<a href="https://maps.google.com/maps?q=<%= lat %>,<%= lng %>"><%= lat %>, <%= lng %></a>)</td>
        </tr>
<%
            }
        }
%>
    </table>

    <hr/>

    <form action="/admin/add-locality" method="post">
        <table>
            <tr>
                <td>Locality name (el)</td>
                <td><input type="text" name="<%= LocalityFactory.PROPERTY_LOCALITY_NAME_EL %>" /></td>
            </tr>
            <tr>
                <td>Locality name (en)</td>
                <td><input type="text" name="<%= LocalityFactory.PROPERTY_LOCALITY_NAME_EN %>" /></td>
            </tr>
            <tr>
                <td>City</td>
                <td>
                     <select name="<%= LocalityFactory.PROPERTY_CITY_UUID %>">
                        <%
                            for(final City city : cities)
                            {
                        %>
                        <option value="<%= city.getUUID() %>"><%= cityUUIDtoNameEl.get(city.getUUID()) %></option>
                        <%
                            }
                        %>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Latitude</td>
                <td><input type="text" name="<%= LocalityFactory.PROPERTY_LAT%>" /></td>
            </tr>
            <tr>
                <td>Longitude</td>
                <td><input type="text" name="<%= LocalityFactory.PROPERTY_LNG%>" /></td>
            </tr>
        </table>
        <div><input type="submit" value="Add locality" /></div>
    </form>

<%
    }
%>

</body>
</html>
<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
<%@ page import="java.util.Date" %>
<%--
  Date: 8/16/12
  Time: 6:24 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide - Cities</title>
</head>
<body>

<%@ include file="authenticate.jsp" %>

<%
    if(userEntity == null)
    {
%>
        You are not logged in
<%
    }
    else if(!userEntity.isAdmin())
    {
%>
        You are not admin
<%
    }
    else
    {
%>
    <h1>Cities</h1>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Last updated</th>
            <th>Name (EN)</th>
            <th>Name (EL)</th>
            <th>Coordinates</th>
        </tr>
<%
        final Vector<City> cities = CityFactory.getAllCities();
        if(cities != null)
        {
            for(final City city : cities)
            {
                final double lat = city.getLat();
                final double lng = city.getLng();
%>
        <tr>
            <td><%= city.getUUID() %></td>
            <td><%= new Date(city.getLastUpdated()) %></td>
            <td><%= city.getNameEn() %></td>
            <td><%= city.getNameEl() %></td>
            <td>(<a href="https://maps.google.com/maps?q=<%=lat%>,<%=lng%>"><%= lat %>, <%= lng %></a>)</td>
        </tr>
<%
            }
        }
%>
    </table>

    <hr/>

    <form action="/admin/add-city" method="post">
        <table>
            <tr>
                <td>City name (en)</td>
                <td><input type="text" name="<%= CityFactory.PROPERTY_CITY_NAME_EN%>" /></td>
            </tr>
            <tr>
                <td>City name (el)</td>
                <td><input type="text" name="<%= CityFactory.PROPERTY_CITY_NAME_EL%>" /></td>
            </tr>
            <tr>
                <td>Latitude</td>
                <td><input type="text" name="<%= CityFactory.PROPERTY_CITY_LAT%>" /></td>
            </tr>
            <tr>
                <td>Longitude</td>
                <td><input type="text" name="<%= CityFactory.PROPERTY_CITY_LNG%>" /></td>
            </tr>
        </table>
        <div><input type="submit" value="Add city" /></div>
    </form>

<%
    }
%>

</body>
</html>
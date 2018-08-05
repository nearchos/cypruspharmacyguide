<%@ page import="com.aspectsense.pharmacyguidecy.OnCall" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.*" %>
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
        final String [] allCityNames = {"Nicosia", "Limassol", "Larnaca", "Paphos", "Famagusta"};
        final int [] expectedNumberOfPharmaciesOnCall = {5, 4, 2, 1, 1};
%>
    <h1>On Calls</h1>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Last updated</th>
            <th>Date</th>
            <th>Pharmacies</th>
            <th>Nicosia (<%=expectedNumberOfPharmaciesOnCall[0]%>)</th>
            <th>Limassol (<%=expectedNumberOfPharmaciesOnCall[1]%>)</th>
            <th>Larnaca (<%=expectedNumberOfPharmaciesOnCall[2]%>)</th>
            <th>Paphos (<%=expectedNumberOfPharmaciesOnCall[3]%>)</th>
            <th>Famagusta (<%=expectedNumberOfPharmaciesOnCall[4]%>)</th>
        </tr>
<%
        final Vector<City> allCities = CityFactory.getAllCities();
        final Map<String,City> uuidToCitiesMap = new HashMap<>();
        final Map<String,City> namesToCities = new HashMap<>();
        for(final City city : allCities)
        {
            uuidToCitiesMap.put(city.getUUID(), city);
            namesToCities.put(city.getNameEn(), city);
        }
        final Vector<Locality> allLocalities = LocalityFactory.getAllLocalities();
        final Map<String,City> localityUuidToCity = new HashMap<>();
        for(final Locality locality : allLocalities)
        {
            localityUuidToCity.put(locality.getUUID(), uuidToCitiesMap.get(locality.getCityUUID()));
        }

        final Vector<OnCall> onCalls = OnCallFactory.getAllOnCalls();
        if(onCalls != null)
        {
            final Vector<Pharmacy> allPharmacies = PharmacyFactory.getAllPharmacies();
            final Map<String,Pharmacy> idToPharmacyMap = new HashMap<String, Pharmacy>();
            for(final Pharmacy pharmacy : allPharmacies)
            {
                idToPharmacyMap.put(pharmacy.getID(), pharmacy);
            }

            for(final OnCall onCall : onCalls)
            {
                final String uuid = onCall.getUUID();
%>
        <tr>
            <td><a href="/admin/on-call?key=<%=uuid%>"><%= uuid.substring(uuid.length() - 8) %></a></td>
            <td><%= new Date(onCall.getLastUpdated()) %></td>
            <td><%= onCall.getDate() %></td>
            <td>
<%

    final Map<City,Integer> countOfCityAppearances = new HashMap<>();
    final String pharmacies = onCall.getPharmacies();
    final StringTokenizer stringTokenizer = new StringTokenizer(pharmacies, ",");
    while(stringTokenizer.hasMoreTokens())
    {
        final String id = stringTokenizer.nextToken().trim();
        final Pharmacy pharmacy = idToPharmacyMap.get(id);
        if(pharmacy != null)
        {
            final City city = localityUuidToCity.get(pharmacy.getLocalityUUID());
            if (!countOfCityAppearances.containsKey(city))
            {
                countOfCityAppearances.put(city, 1);
            }
            else
            {
                countOfCityAppearances.put(city, countOfCityAppearances.get(city) + 1);
            }
%>
            <a href="/admin/pharmacy?key=<%=pharmacy.getUUID()%>"><%=id%></a><%=stringTokenizer.hasMoreTokens() ? ", " : ""%>
<%
        }
        else // pharmacy == null
        {
%>
            <span style="color: red; ">Error-<%=id%></span>
<%
        }
    }
%>
            </td>
<%
        for(int i = 0; i < allCityNames.length; i++)
        {
            final String city = allCityNames[i];
            if(!countOfCityAppearances.containsKey(namesToCities.get(city)))
            { // error - missing city
%>
            <td><span style="color: red; ">Error</span></td>
<%
            }
            else if(countOfCityAppearances.get(namesToCities.get(city)) != expectedNumberOfPharmaciesOnCall[i])
            { // warning
                final int diff = countOfCityAppearances.get(namesToCities.get(city)) - expectedNumberOfPharmaciesOnCall[i];
                String diffS = diff > 0 ? "+" + diff : "" + diff;
%>
            <td><span style="color: orange; ">Warning (<%=diffS%>)</span></td>
<%
            }
            else
            { // ok
%>
                <td><span style="color: green; ">OK</span></td>
<%
            }
        }
%>
        </tr>
<%
            }
        }
%>
    </table>

    <hr/>

    <form action="/admin/add-on-call" method="post">
        <table>
            <tr>
                <td>Date</td>
                <td><input type="text" name="<%= OnCallFactory.PROPERTY_ON_CALL_DATE%>" /></td>
            </tr>
            <tr>
                <td>Pharmacies</td>
                <td><input type="text" name="<%= OnCallFactory.PROPERTY_ON_CALL_PHARMACIES%>" /></td>
            </tr>
        </table>
        <div><input type="submit" value="Add on-call" /></div>
    </form>

    <hr/>

    <form action="/admin/add-on-calls" method="post">
        <div><textarea name="on-calls-as-json" rows="10" cols="150" placeholder="Enter JSON formatted text here (see example below)..."></textarea></div>
        <div><input type="submit" value="Add on-calls (as JSON)" /></div>
    </form>

    <p>Example</p>
<pre>
{
  "on-calls": [
    { "date": "2013-09-06", "pharmacies": "958, 750, 784, 660, 888, 796, 632, 890, 977, 982, 983, 897" },
    { "date": "2013-09-07", "pharmacies": "577, 461, 325, 732, 562, 507, 634, 383, 960, 944, 536, 894" }
  ]
}
</pre>
    <hr/>

    <form action="/admin/delete-old-entries" method="post">
        <div><input type="submit" value="Delete old entries" /></div>
    </form>

<%
    }
%>

</body>
</html>
<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 27/09/2014
  Time: 12:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Cyprus Pharmacy Guide</title>
</head>

<body>
    <h1>Cyprus Pharmacy Guide</h1>
    <h2>On call pharmacies today</h2>

    <ul>
    <%
        // compute today's pharmacies on call, by city
        final Vector<City> cities = CityFactory.getAllCities();
        final Map<String, City> cityUUIDToCity = new HashMap<String, City>();
        final Map<String, Set<String>> cityUUIDToLocalityIDs = new HashMap<String, Set<String>>();
        for(final City city : cities)
        {
            cityUUIDToCity.put(city.getUUID(), city);
            cityUUIDToLocalityIDs.put(city.getUUID(), new HashSet<String>());
    %>
        <li><h3><%=city.getNameEl()%></h3></li>
    <%
        }
    %>
    </ul>
    <%

        final Map<String, Locality> localityUUIDToLocality = new HashMap<String, Locality>();
        final Map<String, Vector<Pharmacy>> localityUUIDsToPharmacies = new HashMap<String, Vector<Pharmacy>>();

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(PharmacyFactory.KIND);
        // filter pharmacies last updated after 'from'
        final PreparedQuery preparedQuery = datastore.prepare(query);

        for(final Entity entity : preparedQuery.asIterable())
        {
            final Pharmacy pharmacy = PharmacyFactory.getFromEntity(entity);
    %>

    <%
        }
    %>

</body>

</html>

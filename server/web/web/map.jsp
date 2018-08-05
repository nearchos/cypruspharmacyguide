<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%--
  User: Nearchos Paspallis
  Date: Apr 5, 2013
  Time: 11:06 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <link href="/web/default.css" rel="stylesheet">
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
    <script>
        function initialize() {
            var mapOptions = {
                zoom: 10,
                center: new google.maps.LatLng(35.045, 33.224),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }
            var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

<%
    // compute today's pharmacies on call, by city
    Vector<City> cities = CityFactory.getAllCities();
    final Map<String, City> cityUUIDToCity = new HashMap<String, City>();
    final Map<String, Set<String>> cityUUIDToLocalityIDs = new HashMap<String, Set<String>>();
    for(final City city : cities)
    {
        cityUUIDToCity.put(city.getUUID(), city);
        cityUUIDToLocalityIDs.put(city.getUUID(), new HashSet<String>());
    }

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
            new google.maps.Marker({
                position: new google.maps.LatLng(<%=pharmacy.getLat()%>,<%=pharmacy.getLng()%>),
                title:"<%=pharmacy.getName()%>"
            }).setMap(map);
<%
    }
%>
        }
    </script>
</head>

<body onload="initialize()">
    <div id="map-canvas"></div>

</body>

</html>
<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.OnCallFactory" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.OnCall" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LocalityFactory" %>
<%--
  User: Nearchos Paspallis
  Date: 11/23/12
  Time: 11:06 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1" />
    <meta http-equiv="refresh" content="600" >
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <link rel="apple-touch-icon-precomposed" href="../images/icon_57x57.png" />
</head>

<body>

    <img src="../images/icon_57x57.png" alt="Cyprus Pharmacy Guide icon" />
    <h3>Cyprus Pharmacy Guide</h3>
    <p>Developed by <a href="http://aspectsense.com">aspectsense.com</a></p>

    <%
        String ua=request.getHeader("User-Agent").toLowerCase();
        if(ua.contains("android"))
        {
    %>
        <a href="http://play.google.com/store/apps/details?id=com.aspectsense.pharmacyguidecy" style="margin: 0px; padding: 0px; color: #ffffff; background-color: #7ba428; font-family: 'Helvetica Neue', Arial, 'Lucida Grande', 'Lucida Sans Unicode', 'Microsoft YaHei', sans-serif; line-height: 20px;">
            <img src="http://www.android.com/images/brand/get_it_on_play_logo_small.png" border="0" alt="Get it on Google Play" style="margin: 0px; padding: 0px; border-style: none;" />
        </a>
    <%
        }
        else if(ua.contains("ipad") || ua.contains("iphone") || ua.contains("ipod"))
        {
    %>
        <%--iOS bubble to add bookmark--%>
        <script type="text/javascript" src="../js/bookmark_bubble.js"></script>
        <script type="text/javascript" src="../js/cyprus-pharmacy-guide.js"></script>
    <%
        }

        final Date today = new Date();
        final Date yesterday = new Date(today.getTime() - 24L * 60 * 60 * 1000);
        final Date tomorrow = new Date(today.getTime() + 24L * 60 * 60 * 1000);

        // compute if the time is midlnight to 8am
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final boolean beforeEightAM = hourOfDay >= 0 && hourOfDay < 8;

        final String date1 = new SimpleDateFormat("yyyy-MM-dd").format(beforeEightAM ? yesterday : today);
        final String date2 = new SimpleDateFormat("yyyy-MM-dd").format(beforeEightAM ? today : tomorrow);
    %>
        <p>Pharmacies on call on <b><%=date1%></b> (until 8am next morning)</p>
        <ul>
    <%
        final Query.Filter filterDate = new Query.FilterPredicate(
                OnCallFactory.PROPERTY_ON_CALL_DATE,
                Query.FilterOperator.EQUAL,
                date1);

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(OnCallFactory.KIND);
        // filter pharmacies last updated after 'from'
        query.setFilter(filterDate);
        final PreparedQuery preparedQuery = datastore.prepare(query);
        final Iterable<Entity> iterable = preparedQuery.asIterable();
        final Iterator<Entity> iterator = iterable.iterator();
        Entity entity;
        if((entity = iterator.next()) != null)
        {
            OnCall onCall = OnCallFactory.getFromEntity(entity);

            final StringTokenizer stringTokenizer = new StringTokenizer(onCall.getPharmacies(), ",");
            final Vector<String> pharmacyIDs = new Vector<String>();
            while(stringTokenizer.hasMoreTokens())
            {
                pharmacyIDs.add(stringTokenizer.nextToken().trim());
            }

            // compute today's pharmacies on call, by city
            Vector<City> cities = CityFactory.getAllCities();//todo memcache?
            final Map<String, City> cityUUIDToCity = new HashMap<String, City>();
            final Map<String, Set<String>> cityUUIDToLocalityIDs = new HashMap<String, Set<String>>();
            for(final City city : cities)
            {
                cityUUIDToCity.put(city.getUUID(), city);
                cityUUIDToLocalityIDs.put(city.getUUID(), new HashSet<String>());
            }

            final Map<String, Locality> localityUUIDToLocality = new HashMap<String, Locality>();
            final Map<String, Vector<Pharmacy>> localityUUIDsToPharmacies = new HashMap<String, Vector<Pharmacy>>();
            for(final String id : pharmacyIDs)
            {
                final Pharmacy pharmacy = PharmacyFactory.getPharmacyByID(id);
                if(pharmacy == null)
                {
    %>
            <!-- Invalid entry for ID: <%=id%> -->
    <%
                }
                else
                {
                    final String localityUUID = pharmacy.getLocalityUUID();
                    final Locality locality = LocalityFactory.getLocality(localityUUID);
                    localityUUIDToLocality.put(localityUUID, locality);
                    cityUUIDToLocalityIDs.get(locality.getCityUUID()).add(locality.getUUID());
                    Vector<Pharmacy> pharmaciesInLocality = localityUUIDsToPharmacies.get(localityUUID);
                    if(pharmaciesInLocality == null)
                    {
                        pharmaciesInLocality = new Vector<Pharmacy>();
                        localityUUIDsToPharmacies.put(localityUUID, pharmaciesInLocality);
                    }
                    pharmaciesInLocality.add(pharmacy);
                }
            }

            for(final City city : cities)
            {
    %>
                <%--todo i18n--%>
                <h2><%=city.getNameEl()%></h2>
    <%
                for(final String localityUUID : cityUUIDToLocalityIDs.get(city.getUUID()))
                {
                    final Locality locality = localityUUIDToLocality.get(localityUUID);
    %>
                <h3><%=locality.getNameEl()%></h3>
    <%
                    for(final Pharmacy pharmacy : localityUUIDsToPharmacies.get(locality.getUUID()))
                    {
                        final String addressDetails = pharmacy.getAddressDetails() == null ? "" : pharmacy.getAddressDetails();
    %>
            <li>
                <b><%=pharmacy.getName()%></b>,<br/>
                <%
                    final double lat = pharmacy.getLat();
                    final double lng = pharmacy.getLng();
                    if(lat != 0d || lng != 0d)
                    {
                %>
                Address: <a href="https://maps.google.com/maps?q=<%= lat %>,<%= lng %>"><%=pharmacy.getAddress()%></a>
                <%=addressDetails.isEmpty() ? "" : "(" + addressDetails + ")"%><br/>
    <%
                    }
                    else
                    {
    %>
                Address: <%=pharmacy.getAddress()%> (<%=pharmacy.getAddress()%>)<br/>
    <%
                    }
    %>
                Work ☎ <a href="tel:<%=pharmacy.getPhoneHome()%>"><%=pharmacy.getPhoneHome()%></a><br/>
                Home ☏ <a href="tel:<%=pharmacy.getPhoneBusiness()%>"><%=pharmacy.getPhoneBusiness()%></a><br/>
                <br/>
            </li>
    <%
                    }
                }
            }
    %>
        </ul>
    <%
        }
    %>

</body>
</html>
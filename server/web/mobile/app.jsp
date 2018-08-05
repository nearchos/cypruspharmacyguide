<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.OnCall" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.OnCallFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LocalityFactory" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.util.logging.Logger" %>
<%--
User: Nearchos Paspallis
Date: 11/23/12
Time: 11:06 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable = no" />
    <meta http-equiv="refresh" content="600" >
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <link rel="apple-touch-icon-precomposed" href="http://cypruspharmacyguide.com/ios.png" />

    <title>
        Cyprus Pharmacy Guide
    </title>
    <link rel="stylesheet" href="https://ajax.aspnetcdn.com/ajax/jquery.mobile/1.2.0/jquery.mobile-1.2.0.min.css"/>
    <link rel="stylesheet" href="../themes/CyprusPharmacyGuide.min.css"/>
    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.0-rc.1/jquery.mobile.structure-1.3.0-rc.1.min.css" />
    <script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.3.0-rc.1/jquery.mobile-1.3.0-rc.1.min.js"></script>
    <!-- User-generated js -->
    <script>
        try {
            $(function () {
            });
        } catch (error) {
            console.error("Your javascript has an error: " + error);
        }

        function getLocation()
        {
            if (navigator.geolocation)
            {
                navigator.geolocation.getCurrentPosition(sortCities, showError);
            }
            else
            {
                document.getElementById("location").innerHTML="Geolocation is not supported by this browser.";
            }
        }

        function showError(error)
        {
            switch(error.code)
            {
                case error.PERMISSION_DENIED:
                    document.getElementById("location").innerHTML="User denied the request for Geolocation.";
                    break;

                case error.POSITION_UNAVAILABLE:
                    document.getElementById("location").innerHTML="Location information is unavailable.";
                    break;

                case error.TIMEOUT:
                    document.getElementById("location").innerHTML="The request to get user location timed out.";
                    break;

                case error.UNKNOWN_ERROR:
                    document.getElementById("location").innerHTML="An unknown error occurred.";
                    break;
            }
        }

        function sortCities(position)
        {
            var lat = position.coords.latitude;
            var lng = position.coords.longitude;

            nicosia =   {lat: 35.16932,     lng: 33.36014};
            limassol =  {lat: 34.679038,    lng:33.044171};
            larnaca =   {lat: 34.9177,      lng:33.6319};
            paphos =    {lat: 34.75572,     lng:32.41542};
            famagusta = {lat: 35.1174,      lng:33.941};

            var citiesAndDistances = new Array(5);
            citiesAndDistances[0] = {city: "Nicosia",   distance: computeDistance(lat,lng,nicosia.lat,nicosia.lng)};
            citiesAndDistances[1] = {city: "Limassol",  distance: computeDistance(lat,lng,limassol.lat,limassol.lng)};
            citiesAndDistances[2] = {city: "Larnaca",   distance: computeDistance(lat,lng,larnaca.lat,larnaca.lng)};
            citiesAndDistances[3] = {city: "Paphos",    distance: computeDistance(lat,lng,paphos.lat,paphos.lng)};
            citiesAndDistances[4] = {city: "Famagusta", distance: computeDistance(lat,lng,famagusta.lat,famagusta.lng)};

            citiesAndDistances.sort(function(city1,city2) {return city1.distance - city2.distance});

            $("#" + citiesAndDistances[1].city).insertAfter("#" + citiesAndDistances[0].city);
            $("#" + citiesAndDistances[2].city).insertAfter("#" + citiesAndDistances[1].city);
            $("#" + citiesAndDistances[3].city).insertAfter("#" + citiesAndDistances[2].city);
            $("#" + citiesAndDistances[4].city).insertAfter("#" + citiesAndDistances[3].city);

            $("#" + citiesAndDistances[1].city + "-tomorrow").insertAfter("#" + citiesAndDistances[0].city + "-tomorrow");
            $("#" + citiesAndDistances[2].city + "-tomorrow").insertAfter("#" + citiesAndDistances[1].city + "-tomorrow");
            $("#" + citiesAndDistances[3].city + "-tomorrow").insertAfter("#" + citiesAndDistances[2].city + "-tomorrow");
            $("#" + citiesAndDistances[4].city + "-tomorrow").insertAfter("#" + citiesAndDistances[3].city + "-tomorrow");

            document.getElementById("today_location").innerHTML=" near " + citiesAndDistances[0].city;
            document.getElementById("tomorrow_location").innerHTML=" near " + citiesAndDistances[0].city;
        }

        function computeDistance(lat1, lng1, lat2, lng2)
        {
            var R = 6371; // km
            var dLat = (lat2-lat1).toRad();
            var dLng = (lng2-lng1).toRad();
            var latr1 = lat1.toRad();
            var latr2 = lat2.toRad();

            var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.sin(dLng/2) * Math.sin(dLng/2) * Math.cos(latr1) * Math.cos(latr2);
            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return R * c;
        }

        /** Converts numeric degrees to radians */
        if (typeof(Number.prototype.toRad) === "undefined") {
            Number.prototype.toRad = function() {
                return this * Math.PI / 180;
            }
        }

    </script>
</head>

<body onload="getLocation()">

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
    %>

    <%
        final Date today = new Date();
        final Date yesterday = new Date(today.getTime() - 24L * 60 * 60 * 1000);
        final Date tomorrow = new Date(today.getTime() + 24L * 60 * 60 * 1000);

        // compute if the time is midnight to 8am
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final boolean beforeEightAM = hourOfDay >= 0 && hourOfDay < 8;

        final String date1 = new SimpleDateFormat("yyyy-MM-dd").format(beforeEightAM ? yesterday : today);
        final String date2 = new SimpleDateFormat("yyyy-MM-dd").format(beforeEightAM ? today : tomorrow);
        final String date1Title = new SimpleDateFormat("MMM d").format(beforeEightAM ? yesterday : today);
        final String date2Title = new SimpleDateFormat("MMM d").format(beforeEightAM ? today : tomorrow);

        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();//todo

        final Vector<City> cities = CityFactory.getAllCities();
        final Map<String, City> cityUUIDToCity = new HashMap<String, City>();
        for(final City city : cities)
        {
            cityUUIDToCity.put(city.getUUID(), city);
        }

        final Map<String,Set<Pharmacy>> cityNamesToPharmaciesToday = new HashMap<String, Set<Pharmacy>>();
        final Map<String,Set<Pharmacy>> cityNamesToPharmaciesTomorrow = new HashMap<String, Set<Pharmacy>>();

        final Logger log = Logger.getLogger("mobile/app.jsp");
        {
            final OnCall onCallToday = OnCallFactory.getOnCallFromDate(date1);
            log.fine(date1 + " resulted to " + onCallToday);
            final StringTokenizer stringTokenizer = new StringTokenizer(onCallToday.getPharmacies(), ",");
            final Vector<String> pharmacyIDs = new Vector<String>();
            while(stringTokenizer.hasMoreTokens())
            {
                pharmacyIDs.add(stringTokenizer.nextToken().trim());
            }

            for(final String id : pharmacyIDs)
            {
                final Pharmacy pharmacy = PharmacyFactory.getPharmacyByID(id);
                log.fine(id + " resulted to pharmacy " + pharmacy);
                if(pharmacy != null)
                {
                    final String localityUUID = pharmacy.getLocalityUUID();
                    Locality locality = LocalityFactory.getLocality(localityUUID);
                    final String cityName = cityUUIDToCity.get(locality.getCityUUID()).getNameEn();
                    if(!cityNamesToPharmaciesToday.containsKey(cityName))
                    {
                        cityNamesToPharmaciesToday.put(cityName, new HashSet<Pharmacy>());
                    }
                    final Set<Pharmacy> pharmacies = cityNamesToPharmaciesToday.get(cityName);
                    pharmacies.add(pharmacy);
                }
            }
        }

        {
            final OnCall onCallTomorrow = OnCallFactory.getOnCallFromDate(date2);
            log.fine(date2 + " resulted to " + onCallTomorrow);
            final StringTokenizer stringTokenizer = new StringTokenizer(onCallTomorrow.getPharmacies(), ",");
            final Vector<String> pharmacyIDs = new Vector<String>();
            while(stringTokenizer.hasMoreTokens())
            {
                pharmacyIDs.add(stringTokenizer.nextToken().trim());
            }

            for(final String id : pharmacyIDs)
            {
                final Pharmacy pharmacy = PharmacyFactory.getPharmacyByID(id);
                log.fine(id + " resulted to pharmacy " + pharmacy);
                if(pharmacy != null)
                {
                    final String localityUUID = pharmacy.getLocalityUUID();
                    Locality locality = LocalityFactory.getLocality(localityUUID);
                    final String cityName = cityUUIDToCity.get(locality.getCityUUID()).getNameEn();
                    if(!cityNamesToPharmaciesTomorrow.containsKey(cityName))
                    {
                        cityNamesToPharmaciesTomorrow.put(cityName, new HashSet<Pharmacy>());
                    }
                    final Set<Pharmacy> pharmacies = cityNamesToPharmaciesTomorrow.get(cityName);
                    pharmacies.add(pharmacy);
                }
            }
        }
    %>

    <%!
        public String pharmacyToHtml(final Pharmacy pharmacy)
        {
            final StringBuilder html = new StringBuilder();

            final String addressDetails = pharmacy.getAddressDetails() == null ? "" : pharmacy.getAddressDetails();

            final double lat = pharmacy.getLat();
            final double lng = pharmacy.getLng();

            html.append("<h3>").append(pharmacy.getName()).append("</h3>");

            html.append("<p>").append(pharmacy.getAddress()).append(" CY-").append(pharmacy.getAddressPostalCode()).append((addressDetails.isEmpty() ? "" : " (" + addressDetails + ")</p>"));
            if(lat != 0d || lng != 0d)
            {
                html.append("<p><a data-role=\"button\" href=\"https://maps.google.com/maps?q=").append(lat).append(",").append(lng).append("\" target=\"_blank\">Show on map").append("</a></p>");
            }

            html.append("<p></p>");

            html.append("<p><a data-role=\"button\" href=\"tel:").append(pharmacy.getPhoneBusiness()).append("\">").append("Work ☎ ").append(pharmacy.getPhoneBusiness()).append("</a></p>");

            html.append("<p><a data-role=\"button\" href=\"tel:").append(pharmacy.getPhoneHome()).append("\">").append("Home ☏ ").append(pharmacy.getPhoneHome()).append("</a></p>");

            return html.toString();
        }

        public String pharmaciesToHtml(final Map<String,Set<Pharmacy>> cityNamesToPharmacies, final String cityName)
        {
            final StringBuilder html = new StringBuilder();

            final Set<Pharmacy> pharmacies = cityNamesToPharmacies.get(cityName);
            for(final Pharmacy pharmacy : pharmacies)
            {
                html.append(pharmacyToHtml(pharmacy)).append("\n");
            }

            return html.toString();
        }

    %>

<!-- Page: On call now -->
<div data-role="page" id="on_call_now">
    <div data-theme="a" data-role="header" style="margin: 0.1em 4px 0.1em;">
        <h3>
            <img src="../favicon.png" style="vertical-align:middle;" alt="favorite icon"/>
            Cyprus Pharmacy Guide
        </h3>

        <div data-role="navbar" data-iconpos="top">
            <ul>
                <li>
                    <a href="" data-transition="fade" data-theme="" data-icon="" class="ui-btn-active ui-state-persist">
                        On call now
                    </a>
                </li>
                <li>
                    <a href="#next_day" data-transition="fade" data-theme="" data-icon="">
                        Next day
                    </a>
                </li>
            </ul>
        </div>
    </div>

    <div data-role="content">
        <p>
            On call now, <%=date1Title%> (until 8am next morning)
            <span id="today_location">...</span>
        </p>
        <div data-role="collapsible-set" data-corners="false">
            <div data-role="collapsible" id="Famagusta">
                <h3>
                    Famagusta
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesToday, "Famagusta")%>

            </div>
            <div data-role="collapsible" id="Larnaca">
                <h3>
                    Larnaca
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesToday, "Larnaca")%>

            </div>
            <div data-role="collapsible" id="Limassol">
                <h3>
                    Limassol
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesToday, "Limassol")%>

            </div>
            <div data-role="collapsible" id="Nicosia">
                <h3>
                    Nicosia
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesToday, "Nicosia")%>

            </div>
            <div data-role="collapsible" id="Paphos">
                <h3>
                    Paphos
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesToday, "Paphos")%>

            </div>
        </div>
    </div>
</div>
<!-- End of Page: On call now -->

<!-- Page: Next day -->
<div data-role="page" id="next_day">
    <div data-theme="a" data-role="header">
        <h3>
            <img src="../favicon.png" style="vertical-align:middle;" alt="favorite icon"/>
            Cyprus Pharmacy Guide
        </h3>

        <div data-role="navbar" data-iconpos="top">
            <ul>
                <li>
                    <a href="#on_call_now" data-transition="fade" data-theme="" data-icon="">
                        On call now
                    </a>
                </li>
                <li>
                    <a href="" data-transition="fade" data-theme="" data-icon="" class="ui-btn-active ui-state-persist">
                        Next day
                    </a>
                </li>
            </ul>
        </div>
    </div>

    <div data-role="content">
        <p>
            On call <%=date2Title%>
            <span id="tomorrow_location">...</span>
        </p>
        <div data-role="collapsible-set" data-corners="false">
            <div data-role="collapsible" id="Famagusta-tomorrow">
                <h3>
                    Famagusta
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesTomorrow, "Famagusta")%>

            </div>
            <div data-role="collapsible" id="Larnaca-tomorrow">
                <h3>
                    Larnaca
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesTomorrow, "Larnaca")%>

            </div>
            <div data-role="collapsible" id="Limassol-tomorrow">
                <h3>
                    Limassol
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesTomorrow, "Limassol")%>

            </div>
            <div data-role="collapsible" id="Nicosia-tomorrow">
                <h3>
                    Nicosia
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesTomorrow, "Nicosia")%>

            </div>
            <div data-role="collapsible" id="Paphos-tomorrow">
                <h3>
                    Paphos
                </h3>

                <%= pharmaciesToHtml(cityNamesToPharmaciesTomorrow, "Paphos")%>

            </div>
        </div>
    </div>
</div>
<!-- End of Page: Next day -->

</body>
</html>
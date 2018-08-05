<%@ page import="com.aspectsense.pharmacyguidecy.services.LogNearbySearch" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.aspectsense.pharmacyguidecy.admin.DeleteOldEntriesServlet" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%--
  User: Nearchos Paspallis
  Date: 1/1/12
  Time: 6:26 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Cyprus Pharmacy Guide: ALl pharmacies heatmap</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body, html {
            margin:0;
            padding:0;
            font-family:Arial;
        }
        h1 {
            margin-bottom:10px;
        }
        #main {
            position:relative;
            width:100%;
            margin:auto;
        }
        #heatmapArea {
            position:relative;
            float:left;
            width:100%;
            height:100%;
        }
        #configArea {
            position:relative;
            float:center;
            width:400px;
            padding:15px;
            padding-top:0;
            padding-right:0;
        }
        #titleArea {
            position:absolute;
            text-align:center;
            width:100%;
            padding:15px;
            padding-top:10px;
            z-index: 2;
        }
        #gen:hover{
            background-color:grey;
            color:black;
        }
        textarea{
            width:260px;
            padding:10px;
            height:200px;
        }
        h2{
            margin-top:0;
        }
    </style>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>

</head>

<body>
<div id="main">

    <div id="titleArea">
        <h1>Cyprus Pharmacy Guide: Nearby search heatmap</h1>
    </div>

    <div id="heatmapArea">
    </div>

    <div id="configArea">
        <div id="tog"/>
        <div id="gen"/>
    </div>


</div>
<script type="text/javascript" src="/map/heatmap.js"></script>
<script type="text/javascript" src="/map/heatmap-gmaps.js"></script>
<script type="text/javascript">

    var map;
    var heatmap;

    window.onload = function(){

        var myLatlng = new google.maps.LatLng(35.034, 33.222);
        // sorry - this demo is a beta
        // there is lots of work todo
        // but I don't have enough time for eg redrawing on dragrelease right now
        var myOptions = {
            zoom: 10,
            center: myLatlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            disableDefaultUI: false,
            scrollwheel: true,
            draggable: true,
            navigationControl: true,
            mapTypeControl: false,
            scaleControl: true,
            disableDoubleClickZoom: false
        };
        map = new google.maps.Map(document.getElementById("heatmapArea"), myOptions);

        heatmap = new HeatmapOverlay(map, {"radius":15, "visible":true, "opacity":60});

        document.getElementById("gen").onclick = function(){
            var x = 5;
            while(x--){

                var lat = Math.random()*180;
                var lng = Math.random()*180;
                var count = Math.floor(Math.random()*180+1);

                heatmap.addDataPoint(lat,lng,count);

            }

        };

        document.getElementById("tog").onclick = function(){
            heatmap.toggle();
        };

        var testData={
            data: [
                <%
                    final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

                    final long now = System.currentTimeMillis();
                    final long earliestTimestamp = new Date().getTime() - 100 * DeleteOldEntriesServlet.MILLISECONDS_IN_A_DAY;
                    final int hoursSinceEarliestTimestamp = (int) ((now - earliestTimestamp) / (60*60*1000));

                    final Query.Filter filter = new Query.FilterPredicate(LogNearbySearch.PROPERTY_TIMESTAMP, Query.FilterOperator.GREATER_THAN, earliestTimestamp);
                    final Query query = new Query(LogNearbySearch.NEARBY_SEARCH_LOG_KIND).addSort(LogNearbySearch.PROPERTY_TIMESTAMP).setFilter(filter);
                    final PreparedQuery preparedQuery = datastoreService.prepare(query);

                    int max = 100;
                    for(final Entity entity : preparedQuery.asIterable())
                    {
                        final long timestamp = (Long) entity.getProperty(LogNearbySearch.PROPERTY_TIMESTAMP);
                        final int hoursSinceLog = (int) ((now - timestamp) / (60*60*1000));
                        max = Math.max(max, hoursSinceLog);
                        final double lat = Double.parseDouble(entity.getProperty(LogNearbySearch.PROPERTY_LAT).toString());
                        final double lng = Double.parseDouble(entity.getProperty(LogNearbySearch.PROPERTY_LNG).toString());
                %>
                <%--{lat:<%=lat%>, lng:<%=lng%>, count:<%=hoursSinceEarliestTimestamp - hoursSinceLog%>},--%>
                <%
                    }

                    final Vector<Pharmacy> allPharmacies = PharmacyFactory.getAllPharmacies();
                    for(final Pharmacy pharmacy : allPharmacies)
                    {
                %>
                {lat:<%=pharmacy.getLat()%>, lng:<%=pharmacy.getLng()%>, count:<%=200%>},
                <%
                    }
                %>
                {lat:35.034, lng:33.222, count:0}
            ],
            max: <%=max%>
        };


        // this is important, because if you set the data set too early, the latlng/pixel projection doesn't work
        google.maps.event.addListenerOnce(map, "idle", function(){
            heatmap.setDataSet(testData);
        });
    };

</script>
</body>
</html>
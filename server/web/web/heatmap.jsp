<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.admin.DeleteOldEntriesServlet" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LogEventFactory" %>
<%--
  User: Nearchos Paspallis
  Date: Apr 5, 2013
  Time: 11:06 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide - Nearby search heatmap</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <link href="/web/default.css" rel="stylesheet">
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=visualization"></script>
    <script>
        // Adding 500 Data Points
        var map, pointarray, heatmap;

        var logData = [
            <%
                final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

                final long now = System.currentTimeMillis();
                final long earliestTimestamp = new Date().getTime() - 100 * DeleteOldEntriesServlet.MILLISECONDS_IN_A_DAY;
                final int hoursSinceEarliestTimestamp = (int) ((now - earliestTimestamp) / (60*60*1000));

                final Query.Filter filter = new Query.FilterPredicate(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP, Query.FilterOperator.GREATER_THAN, earliestTimestamp);
                final Query query = new Query(LogEventFactory.KIND).addSort(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP).setFilter(filter);
                final PreparedQuery preparedQuery = datastoreService.prepare(query);
                final int size = preparedQuery.countEntities();

                int max = 100;
                int count = 0;
                for(final Entity entity : preparedQuery.asIterable())
                {
                    final long timestamp = (Long) entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP);
                    final int hoursSinceLog = (int) ((now - timestamp) / (60*60*1000));
                    max = Math.max(max, hoursSinceLog);
                    final double lat = Double.parseDouble(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_LAT).toString());
                    final double lng = Double.parseDouble(entity.getProperty(LogEventFactory.PROPERTY_LOG_EVENT_LNG).toString());
                    boolean last = ++count == size;
            %>
            <%--new google.maps.LatLng(<%=lat%>, <%=lng%>)<%=last?"":","%>--%>
            {location: new google.maps.LatLng(<%=lat%>, <%=lng%>), weight: <%=hoursSinceEarliestTimestamp - hoursSinceLog%>}<%=last?"":","%>
            <%
                }
            %>
        ];

        function initialize() {
            var mapOptions = {
                zoom: 10,
                center: new google.maps.LatLng(35.045, 33.224),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }
            map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

            pointArray = new google.maps.MVCArray(logData);

            heatmap = new google.maps.visualization.HeatmapLayer({
                data: pointArray,
                dissipating: true
            });

            heatmap.setMap(map);
        }

        function toggleHeatmap() {
            heatmap.setMap(heatmap.getMap() ? null : map);
        }

        function changeRadius() {
            heatmap.setOptions({radius: heatmap.get('radius') ? null : 50});
        }

        function changeOpacity() {
            heatmap.setOptions({opacity: heatmap.get('opacity') ? null : 0.2});
        }

    </script>
</head>

<body onload="initialize()">

    <div id="map-canvas"></div>

    <div style="display:block;
            position:absolute;
            bottom:0;
            left:64;
            padding: 4px;">

        <button onclick="toggleHeatmap()">Toggle Heatmap</button>
        <button onclick="changeRadius()">Change radius</button>
        <button onclick="changeOpacity()">Change opacity</button>

    </div>

</body>

</html>
<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 05/06/2014
  Time: 16:07
--%>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LogEvent" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LogEventFactory" %>
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

<%@ include file="/admin/authenticate.jsp" %>

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
    final String fromS = request.getParameter("from");
    long from = 0L;
    try
    {
        from = fromS == null ? 0L : Long.parseLong(fromS);
    }
    catch (NumberFormatException nfe)
    {
        // todo log("Error parsing 'from' argument: " + fromS, nfe);
    }
    if(from == 0) from = -1; // default is 24 hours back
    if(from < 0)
    {
        from = System.currentTimeMillis() + 24L * 60L * 60L * 1000L * from;
    }

    final Query.Filter filterFrom = new Query.FilterPredicate(
            LogEventFactory.PROPERTY_LOG_EVENT_TIMESTAMP,
            Query.FilterOperator.GREATER_THAN,
            from);

    final Query query = new Query(LogEventFactory.KIND);
    // filter cities last updated after "from"
    query.setFilter(filterFrom);
    final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    final PreparedQuery preparedQuery = datastore.prepare(query);
    final Iterable<Entity> iterable = preparedQuery.asIterable();
    final Iterator<Entity> iterator = iterable.iterator();

    final Map<String,TreeSet<LogEvent>> installationIdToEntity = new HashMap<String, TreeSet<LogEvent>>();
%>
    <h1>Logs</h1>
<%
    while(iterator.hasNext())
    {
        final Entity entity = iterator.next();
        final LogEvent logEvent = LogEventFactory.getFromEntity(entity);

        if (!installationIdToEntity.containsKey(logEvent.getInstallationId()))
        {
            installationIdToEntity.put(logEvent.getInstallationId(), new TreeSet<LogEvent>(new Comparator<LogEvent>()
            {
                @Override
                public int compare(LogEvent o1, LogEvent o2)
                {
                    if (o1.getTimestamp() < o2.getTimestamp()) return -1;
                    else if (o1.getTimestamp() == o2.getTimestamp()) return 0;
                    else return 1;
                }
            }));
        }
        installationIdToEntity.get(logEvent.getInstallationId()).add(logEvent);
    }

    final Vector<String> sortedInstallationIDsByCount = new Vector<String>();
    sortedInstallationIDsByCount.addAll(installationIdToEntity.keySet());
    Collections.sort(sortedInstallationIDsByCount, new Comparator<String>()
    {
        @Override
        public int compare(String o1, String o2)
        {
            return installationIdToEntity.get(o2).size() - installationIdToEntity.get(o1).size();
        }
    });
%>
    <h2><%=installationIdToEntity.size()%> different entries</h2>
    <table border="1">
        <tr>
            <th>INSTALLATION ID</th>
            <th>LAST UPDATED</th>
            <th>COUNT</th>
            <th>LOCATIONS</th>
            <th>DISTANCE COVERED</th>
        </tr>
<%
    for(final String installationId : sortedInstallationIDsByCount)
    {
        final TreeSet<LogEvent> allLogEvents = installationIdToEntity.get(installationId);
        final long lastUpdated = allLogEvents.last().getTimestamp();
        final int size = allLogEvents.size();
        final Map<String,Integer> locations = new HashMap<String, Integer>();
        for(LogEvent logEvent : allLogEvents)
        {
            final String location = logEvent.getLat() + "," + logEvent.getLng();
            if(!locations.containsKey(location)) locations.put(location, 1);
            else locations.put(location, locations.get(location) + 1);
        }
%>
        <tr>
            <td><%=installationId%></td>
            <td><%= new Date(lastUpdated) %></td>
            <td><%= size %></td>
            <td>
<%
    String url = "http://maps.googleapis.com/maps/api/staticmap?center=34.91,33.20&zoom=9&size=640x400&scale=2";
    int sum = 0;
    for(String location : locations.keySet())
    {
//        final String url = "https://www.google.com.au/maps/@" + location + ",15z";
        sum += locations.get(location);
        final String label = Integer.toString(locations.get(location));
        url += ("&markers=color:blue%7Clabel:" + label + "%7C" + location);
    }
%>
                <a href="<%=url%>">(<%=locations%></a>) -> <%=sum%> |
            </td>
            <td>n/a</td>
        </tr>

        <%
    }
}
%>
    </table>

</body>
</html>
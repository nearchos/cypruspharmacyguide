<%@ page import="com.aspectsense.pharmacyguidecy.data.OnCallFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.OnCall" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LocalityFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%--
  Date: 8/16/12
  Time: 6:24 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide - Pharmacy</title>
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
    else if(request == null)
    {
%>
        Request is null!
<%
    }
    else
    {
        final String key = request.getParameter(OnCallFactory.KEY);
        final OnCall onCall = OnCallFactory.getOnCall(key);

        if(onCall == null)
        {
%>
        onCall is null!
<%
        }
        else
        {
%>
    <h1>On-Call</h1>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Last updated</th>
            <th>Date</th>
            <th>Pharmacies</th>
        </tr>

        <tr>
            <td><%= onCall.getUUID() %></td>
            <td><%= new Date(onCall.getLastUpdated()) %></td>
            <td><%= onCall.getDate() %></td>
            <td><%= onCall.getPharmacies() %></td>
        </tr>
    </table>

    <hr/>

<%
            final Vector<Locality> allLocalities = LocalityFactory.getAllLocalities();
            final Map<String,Locality> uuidToLocalityMap = new HashMap<>();
            for(final Locality locality : allLocalities)
            {
                uuidToLocalityMap.put(locality.getUUID(), locality);
            }
            final String pharmacies = onCall.getPharmacies();
            final StringTokenizer stringTokenizer = new StringTokenizer(pharmacies, ",");
            while(stringTokenizer.hasMoreTokens())
            {
                final String id = stringTokenizer.nextToken().trim();
                final Pharmacy pharmacy = PharmacyFactory.getPharmacyByID(id);
                if(pharmacy == null)
                {
%>
    <p><span style="color: red; ">Could not locate pharmacy with ID: <%=id%></span></p>
<%
                }
                else
                {
%>
    <p><a href="/admin/pharmacy?key=<%=pharmacy.getUUID()%>"><%=id%></a>: <%=pharmacy.getName()%>, <%=pharmacy.getAddress()%>, CY-<%=pharmacy.getAddressPostalCode()%> (<%=pharmacy.getAddressDetails()%>), <%=uuidToLocalityMap.get(pharmacy.getLocalityUUID()).getNameEl()%></p>
<%
                }
             }
%>

    <hr/>

    <form action="/admin/add-on-call" method="post">
        <table>
            <tr>
                <td>UUID</td>
                <td><%=key%></td>
            </tr>
            <tr>
                <td>Date</td>
                <td><%=onCall.getDate()%></td>
            </tr>
            <tr>
                <td>Pharmacies</td>
                <td><input type="text" size="100" name="<%= OnCallFactory.PROPERTY_ON_CALL_PHARMACIES%>" value="<%=onCall.getPharmacies()%>"/></td>
            </tr>
        </table>
        <div><input type="submit" value="Edit On-Call" /></div>
        <input type="hidden" name="<%= OnCallFactory.PROPERTY_ON_CALL_DATE %>" value="<%= onCall.getDate() %>">
        <input type="hidden" name="<%= OnCallFactory.KEY %>" value="<%= key %>">
    </form>

<%
        }
    }
%>

</body>
</html>
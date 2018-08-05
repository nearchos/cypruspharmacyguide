<%@ page import="java.util.Vector" %>
<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LocalityFactory" %>
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
    else
    {

        final Map<String,String> localityUUIDtoNameEl = new HashMap<String, String>();
        final Vector<Locality> localities = LocalityFactory.getAllLocalities();
        for(final Locality locality : localities)
        {
            localityUUIDtoNameEl.put(locality.getUUID(), locality.getNameEl());
        }

        final String key = request.getParameter(PharmacyFactory.KEY);
        final Pharmacy pharmacy = PharmacyFactory.getPharmacy(key);
%>
    <h1>Pharmacy</h1>

    <table border="1">
        <tr>
            <th>UUID</th>
            <th>Last updated</th>
            <th>ID</th>
            <th>Name (EN)</th>
            <th>Address</th>
            <th>Postal Code</th>
            <th>Address Details</th>
            <th>Coordinates</th>
            <th>Locality</th>
            <th>Phone (business)</th>
            <th>Phone (home)</th>
        </tr>
<%
                final double lat = pharmacy.getLat();
                final double lng = pharmacy.getLng();
%>
        <tr>
            <td><%= pharmacy.getUUID() %></td>
            <td><%= new Date(pharmacy.getLastUpdated()) %></td>
            <td><%= pharmacy.getID() %></td>
            <td><%= pharmacy.getName() %></td>
            <td><%= pharmacy.getAddress() %></td>
            <td><%= pharmacy.getAddressPostalCode() %></td>
            <td><%= pharmacy.getAddressDetails() %></td>
            <%
                if(pharmacy.getLat() != 0d)
                {
            %>

            <td> (<a href="http://maps.google.com/maps?q=<%= lat %>,<%= lng %>" target="_blank"><%= lat %>, <%= lng %></a>) </td>

            <%
                }
                else
                {
            %>

            <td> (<%= lat %>, <%= lng %>) </td>

            <%
                }
            %>
            <td> <a href="/admin/locality?key=<%= pharmacy.getLocalityUUID() %>"><%= localityUUIDtoNameEl.get(pharmacy.getLocalityUUID()) %></a></td>
            <td><%= pharmacy.getPhoneBusiness() %></td>
            <td><%= pharmacy.getPhoneHome() %></td>
        </tr>
    </table>

    <hr/>

    <form action="/admin/add-pharmacy?key=<%= key %>" method="post">
        <table>
            <tr>
                <td>UUID</td>
                <td><%=key%></td>
            </tr>
            <tr>
                <td>ID</td>
                <td><%=pharmacy.getID()%></td>
            </tr>
            <tr>
                <td>Name (el)</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_NAME_EL%>" value="<%=pharmacy.getName()%>"/></td>
            </tr>
            <tr>
                <td>Address</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ADDRESS%>" value="<%=pharmacy.getAddress()%>"/></td>
            </tr>
            <tr>
                <td>Postal Code</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE%>" value="<%=pharmacy.getAddressPostalCode()%>"/></td>
            </tr>
            <tr>
                <td>Address details</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_DETAILS%>" value="<%=pharmacy.getAddressDetails()%>"/></td>
            </tr>
            <tr>
                <td>Lat</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_LAT%>" value="<%=pharmacy.getLat()%>"/></td>
            </tr>
            <tr>
                <td>Lng</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_LNG%>" value="<%=pharmacy.getLng()%>"/></td>
            </tr>
            <tr>
                <td>Locality</td>
                <td>
                <select name="<%= PharmacyFactory.PROPERTY_PHARMACY_LOCALITY_UUID%>">
<%
    for(final Locality locality : localities)
    {
%>
        <option value="<%=locality.getUUID()%>" <%= pharmacy.getLocalityUUID().equals(locality.getUUID()) ? "selected" : "" %>><%=locality.getNameEl()%></option>
<%
    }
%>
                </select>
                </td>
            </tr>
            <tr>
                <td>Phone (business)</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_PHONE_BUSINESS%>" value="<%=pharmacy.getPhoneBusiness()%>"/></td>
            </tr>
            <tr>
                <td>Phone (home)</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_PHONE_HOME%>" value="<%=pharmacy.getPhoneHome()%>"/></td>
            </tr>
        </table>
        <div><input type="submit" value="Edit Pharmacy" /></div>
        <input type="hidden" name="<%= PharmacyFactory.KEY%>" value="<%= key %>">
        <input type="hidden" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ID%>" value="<%= pharmacy.getID() %>">
    </form>

<%
    }
%>

</body>
</html>
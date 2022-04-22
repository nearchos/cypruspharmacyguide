<%@ page import="com.aspectsense.pharmacyguidecy.City" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.CityFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Pharmacy" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.PharmacyFactory" %>
<%@ page import="com.aspectsense.pharmacyguidecy.Locality" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.LocalityFactory" %>
<%@ page import="java.util.*" %>
<%--
  Date: 8/16/12
  Time: 6:24 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide - Pharmacies</title>
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

        // this query will return the pharmacies sorted but 1000 shows to be smaller than 900 because the sorting is lexicographical instead of numerical...
        final Vector<Pharmacy> pharmacies = PharmacyFactory.getAllPharmacies();
        // ...thus, with this line we sort numerically
        Collections.sort(pharmacies, new Comparator<Pharmacy>() {
            @Override
            public int compare(Pharmacy left, Pharmacy right) {
                return new Integer(left.getID()).compareTo(new Integer(right.getID()));
            }
        });

        final int numOfPharmacies = pharmacies == null ? 0 : pharmacies.size();
        int numOfGeolocatedPharmacies = 0;
        if(pharmacies != null)
        {
            for(final Pharmacy pharmacy : pharmacies)
            {
                if(pharmacy.getLat() != 0.0d) numOfGeolocatedPharmacies++;
            }
        }
%>
    <h1>Pharmacies</h1>

    <p>Number of pharmacies: <%= numOfPharmacies %></p>
    <p>Number of geolocated pharmacies: <%= numOfGeolocatedPharmacies %> (<%= String.format("%.2f", 100d*numOfGeolocatedPharmacies/numOfPharmacies) %>%)</p>

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
            <th>Active</th>
        </tr>
<%
        if(pharmacies != null)
        {
            for(final Pharmacy pharmacy : pharmacies)
            {
                final String uuid = pharmacy.getUUID();
                final double lat = pharmacy.getLat();
                final double lng = pharmacy.getLng();
%>
        <tr>
            <td><a href="/admin/pharmacy?key=<%=uuid%>"><%= uuid.substring(uuid.length() - 8) %></a></td>
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
            <td><%= pharmacy.isActive() ? "<p style='color:green'>active</p>" : "<p style='color:red'>inactive</p>" %></td>
        </tr>
<%
            }
        }
%>
    </table>

    <hr/>

    <form action="/admin/add-pharmacy" method="post">
        <table>
            <tr>
                <td>ID</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ID%>" /></td>
            </tr>
            <tr>
                <td>Name (el)</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_NAME_EL%>" /></td>
            </tr>
            <tr>
                <td>Address</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ADDRESS%>" /></td>
            </tr>
            <tr>
                <td>Postal Code</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE%>" /></td>
            </tr>
            <tr>
                <td>Address details</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_DETAILS%>" /></td>
            </tr>
            <tr>
                <td>Lat</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_LAT%>" /></td>
            </tr>
            <tr>
                <td>Lng</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_LNG%>" /></td>
            </tr>
            <tr>
                <td>Locality</td>
                <td>
                <select name="<%= PharmacyFactory.PROPERTY_PHARMACY_LOCALITY_UUID%>">
<%
    for(final Locality locality : localities)
    {
%>
        <option value="<%=locality.getUUID()%>"><%=locality.getNameEl()%></option>
<%
    }
%>
                </select>
                </td>
            </tr>
            <tr>
                <td>Phone (business)</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_PHONE_BUSINESS%>" /></td>
            </tr>
            <tr>
                <td>Phone (home)</td>
                <td><input type="text" name="<%= PharmacyFactory.PROPERTY_PHARMACY_PHONE_HOME%>" /></td>
            </tr>
            <tr>
                <td>Active</td>
                <td><input type="hidden" name="<%= PharmacyFactory.PROPERTY_PHARMACY_ACTIVE%>" value="true"/>true</td>
            </tr>
        </table>
        <div><input type="submit" value="Add Pharmacy" /></div>
    </form>

<%
    }
%>

</body>
</html>
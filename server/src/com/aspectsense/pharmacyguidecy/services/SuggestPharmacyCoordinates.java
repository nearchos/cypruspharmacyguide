package com.aspectsense.pharmacyguidecy.services;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Date: 8/8/12
 * Time: 3:10 PM
 */
public class SuggestPharmacyCoordinates extends HttpServlet
{
    public static final String GOOGLE_MAPS_URL = "http://maps.google.com/maps?q=latitude,longitude";

    private static final Logger log = Logger.getLogger(SuggestPharmacyCoordinates.class.getCanonicalName());

    public static final String PHARMACY_SUGGESTION_KIND = "pharmacy_suggestion";

    public static final String PROPERTY_INSTALLATION_ID = "installation_id";
    public static final String PROPERTY_PHARMACY_ID = "pharmacy_id";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_ADDRESS = "address";
    public static final String PROPERTY_NOTES = "notes";
    public static final String PROPERTY_LATITUDE = "latitude ";
    public static final String PROPERTY_LONGITUDE = "longitude";

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final String installation_id = request.getParameter("installation_id");
        final String pharmacy_id = request.getParameter("pharmacy_id");
        final String name = request.getParameter("name");
        final String address = request.getParameter("address");
        final String notes = request.getParameter("notes");
        final String latitude = request.getParameter("latitude");
        final String longitude = request.getParameter("longitude");

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Entity pharmacySuggestion = new Entity(PHARMACY_SUGGESTION_KIND);
        pharmacySuggestion.setProperty(PROPERTY_INSTALLATION_ID, installation_id);
        pharmacySuggestion.setProperty(PROPERTY_PHARMACY_ID, pharmacy_id);
        pharmacySuggestion.setProperty(PROPERTY_NAME, name);
        pharmacySuggestion.setProperty(PROPERTY_ADDRESS, address);
        pharmacySuggestion.setProperty(PROPERTY_NOTES, notes);
        pharmacySuggestion.setProperty(PROPERTY_LATITUDE, latitude);
        pharmacySuggestion.setProperty(PROPERTY_LONGITUDE, longitude);
        datastore.put(pharmacySuggestion);

        final Session session = Session.getDefaultInstance(new Properties(), null);
        try
        {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("nearchos@gmail.com", "Nearchos"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("nearchos@aspectsense.com", "Nearchos"));
            message.setSubject("Cyprus Pharmacy Guide - Coordinates submission for: #" + pharmacy_id);
            message.setText("installation_id: " + installation_id
                    + "\npharmacy_id: " + pharmacy_id
                    + "\nname: " + name
                    + "\naddress: " + address
                    + "\nnotes: " + notes
                    + "\nlatitude: " + latitude
                    + "\nlongitude: " + longitude
                    + "\nGoogle maps url: " + GOOGLE_MAPS_URL.replace("latitude", latitude).replace("longitude", longitude)
                    + "\nReceived (UTC): " + new Date()
                    + "\nSender IP: " + request.getRemoteAddr());
            Transport.send(message);
        }
        catch (AddressException ae)
        {
            log.severe(ae.getMessage());
        }
        catch (MessagingException me)
        {
            log.severe(me.getMessage());
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println("{ \"result\": \"ok\" }");
    }
}
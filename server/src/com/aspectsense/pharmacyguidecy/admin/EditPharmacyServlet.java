package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.PharmacyFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: Nearchos Paspallis
 * Date: 11/21/12
 * Time: 1:04 PM
 */
public class EditPharmacyServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();

        response.setContentType("text/html");

        if(user == null)
        {
            response.getWriter().print("You must sign in first");
        }
        else
        {
            final UserEntity userEntity = UserEntity.getUserEntity(user.getEmail());
            if(userEntity == null || !userEntity.isAdmin())
            {
                response.getWriter().print("User '" + user.getEmail() + "' is not an admin");
            }
            else
            {
                final String uuid = request.getParameter(PharmacyFactory.KEY);
                log(getClass() + ": uuid: " + uuid);

                final String ID = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_ID);
                final String name_el = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_NAME_EL);
                final String address = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_ADDRESS);
                final String addressPostalCode = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_POSTAL_CODE);
                final String addressDetails = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_ADDRESS_DETAILS);
                final String latS = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_LAT);
                final double lat = latS == null || latS.isEmpty() ? 0d : Double.parseDouble(latS);
                final String lngS = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_LNG);
                final double lng = lngS == null || lngS.isEmpty() ? 0d : Double.parseDouble(lngS);
                final String localityUUID = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_LOCALITY_UUID);
                final String phoneBusiness = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_PHONE_BUSINESS);
                final String phoneHome = request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_PHONE_HOME);
                final boolean active = "on".equalsIgnoreCase(request.getParameter(PharmacyFactory.PROPERTY_PHARMACY_ACTIVE));

                // if uuid is not empty, then EDIT, else ADD
                if(uuid != null && !uuid.isEmpty())
                {
                    // edit rather than add
                    try
                    {
                        PharmacyFactory.editPharmacy(uuid, ID, name_el, address, addressPostalCode, addressDetails, lat, lng, localityUUID, phoneBusiness, phoneHome, active);

                        log("Edited " + PharmacyFactory.KIND + " with key: " + uuid);

                        // also reset memcache
                        final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
                        memcacheService.clearAll();

                        response.sendRedirect("/admin/pharmacy?key=" + uuid);
                    }
                    catch (EntityNotFoundException enfe)
                    {
                        response.getWriter().println("<h1>Error while editing pharmacy with UUID: " + uuid + "</h1><p>" + Arrays.toString(enfe.getStackTrace()) + "</p>");
                    }
                }
                else
                {
                    // add
                    final Key key =
                            PharmacyFactory.addPharmacy(ID, name_el, address, addressPostalCode, addressDetails, lat, lng, localityUUID, phoneBusiness, phoneHome);

                    log("Added " + PharmacyFactory.KIND + " with key: " + key);

                    // also reset memcache
                    final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
                    memcacheService.clearAll();

                    response.sendRedirect("/admin/pharmacy?key=" + KeyFactory.keyToString(key));
                }
            }
        }
    }
}

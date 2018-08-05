package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.LocalityFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Nearchos Paspallis
 * Date: 8/16/12
 * Time: 6:30 PM
 */
public class EditLocalityServlet extends HttpServlet
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
                final String name_el = request.getParameter(LocalityFactory.PROPERTY_LOCALITY_NAME_EL);
                final String name_en = request.getParameter(LocalityFactory.PROPERTY_LOCALITY_NAME_EN);
                final String cityUUID = request.getParameter(LocalityFactory.PROPERTY_CITY_UUID);
                final String latS = request.getParameter(LocalityFactory.PROPERTY_LAT);
                final double lat = latS == null || latS.isEmpty() ? 0d : Double.parseDouble(latS);
                final String lngS = request.getParameter(LocalityFactory.PROPERTY_LNG);
                final double lng = lngS == null || lngS.isEmpty() ? 0d : Double.parseDouble(lngS);

//                final Key key =
                        LocalityFactory.addLocality(name_el, name_en, cityUUID, lat, lng);

                response.sendRedirect("/admin/localities");
            }
        }
    }
}
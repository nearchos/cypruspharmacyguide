package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.OnCallFactory;
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
public class EditOnCallServlet extends HttpServlet
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
                final String uuid = request.getParameter(OnCallFactory.KEY);

                final String date = request.getParameter(OnCallFactory.PROPERTY_ON_CALL_DATE);
                final String pharmacies = request.getParameter(OnCallFactory.PROPERTY_ON_CALL_PHARMACIES);

                // if uuid is not empty, then EDIT, else ADD
                if(uuid != null && !uuid.isEmpty())
                {
                    // edit rather than add
                    try
                    {
                        OnCallFactory.editOnCall(uuid, date, pharmacies);

                        log("Edited " + OnCallFactory.KIND + " with key: " + uuid);

                        response.sendRedirect("/admin/on-call?key=" + uuid);
                    }
                    catch (EntityNotFoundException enfe)
                    {
                        response.getWriter().println("<h1>Error while editing on-call with UUID: " + uuid + "</h1><p>" + Arrays.toString(enfe.getStackTrace()) + "</p>");
                    }
                }
                else
                {
                    // add
                    final Key key =
                            OnCallFactory.addOnCall(date, pharmacies);

                    log("Added " + OnCallFactory.KIND + " with key: " + key);

                    response.sendRedirect("/admin/on-call?key=" + KeyFactory.keyToString(key));
                }

                // also reset memcache
                final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
                memcacheService.clearAll();
            }
        }

    }
}
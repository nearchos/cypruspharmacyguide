package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.City;
import com.aspectsense.pharmacyguidecy.data.CityFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * User: Nearchos Paspallis
 * Date: 11/21/12
 * Time: 10:06 AM
 */
public class InitCitiesServlet extends HttpServlet
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
                // delete existing entries...
                final Vector<City> cities = CityFactory.getAllCities();
                final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
                for(final City city : cities)
                {
                    datastoreService.delete(KeyFactory.stringToKey(city.getUUID()));
                }

                final InputStream inputStream = getServletContext().getResourceAsStream("/WEB-INF/cities.txt");
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                    final String cityNameEn = stringTokenizer.nextToken().trim();
                    final String cityNameEl = stringTokenizer.nextToken().trim();
                    final String latS = stringTokenizer.nextToken().trim();
                    final double lat = latS == null ? 0d : Double.parseDouble(latS);
                    final String lngS = stringTokenizer.nextToken().trim();
                    final double lng = lngS == null ? 0d : Double.parseDouble(lngS);

                    CityFactory.addCity(cityNameEn, cityNameEl, lat, lng);
                }

                // also reset memcache
                final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
                memcacheService.clearAll();

                response.sendRedirect("/admin");
            }
        }
    }
}
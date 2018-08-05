package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.City;
import com.aspectsense.pharmacyguidecy.Locality;
import com.aspectsense.pharmacyguidecy.data.CityFactory;
import com.aspectsense.pharmacyguidecy.data.LocalityFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
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
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * User: Nearchos Paspallis
 * Date: 11/21/12
 * Time: 10:06 AM
 */
public class InitLocalitiesServlet extends HttpServlet
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
                final Vector<Locality> localities = LocalityFactory.getAllLocalities();
                final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
                for(final Locality locality : localities)
                {
                    datastoreService.delete(KeyFactory.stringToKey(locality.getUUID()));
                }

                final Map<String,String> cityNameElToUUID = new HashMap<String, String>();
                final Vector<City> allCities = CityFactory.getAllCities();
                for(final City city : allCities)
                {
                    cityNameElToUUID.put(city.getNameEl(), city.getUUID());
                }

                final InputStream inputStream = getServletContext().getResourceAsStream("/WEB-INF/localities.txt");
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String line;
                while((line = bufferedReader.readLine()) != null)
                {
                    StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                    final String nameEl = stringTokenizer.nextToken();
                    final String nameEn = stringTokenizer.nextToken();
                    final String cityNameEl = stringTokenizer.nextToken();
                    final String latS = stringTokenizer.nextToken();
                    final double lat = latS == null ? 0d : Double.parseDouble(latS);
                    final String lngS = stringTokenizer.nextToken();
                    final double lng = lngS == null ? 0d : Double.parseDouble(lngS);

                    LocalityFactory.addLocality(nameEl, nameEn, cityNameElToUUID.get(cityNameEl), lat, lng);
                }

                // also reset memcache
                final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
                memcacheService.clearAll();

                response.sendRedirect("/admin");
            }
        }
    }
}
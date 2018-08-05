package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.OnCallFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: Nearchos Paspallis
 * Date: 11/21/12
 * Time: 1:04 PM
 */
public class EditOnCallsServlet extends HttpServlet
{
    private static Gson gson = new Gson();

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
                final String json = request.getParameter("on-calls-as-json");
//                try
//                {
                    OnCalls onCalls = gson.fromJson(json, OnCalls.class);
//                    JSONObject jsonObject = new JSONObject(json);
//                    JSONArray onCallsArray = jsonObject.getJSONArray("on-calls");

                    final PrintWriter printWriter = response.getWriter();
                    printWriter.println("<h1>JSON</h1><hr/>");

                    for(int i = 0; i < onCalls.getSize(); i++)
//                    for(int i = 0; i < onCallsArray.length(); i++)
                    {
//                        final JSONObject onCall = onCallsArray.getJSONObject(i);
                        final OnCallEntry onCallEntry = onCalls.get(i);
//                        final String date = onCall.getString("date");
                        final String date = onCallEntry.getDate();
//                        final String pharmacies = onCall.getString("pharmacies");
                        final String pharmacies = onCallEntry.getPharmacies();
                        printWriter.println("<p>" + date + " -> " + pharmacies + "</p>");

                        OnCallFactory.addOnCall(date, pharmacies);
                    }
//                }
//                catch (JSONException jsone)
//                {
//                    response.getWriter().println("<h1>JSONException</h1><p>" + jsone.getMessage() + "</p><p>" + Arrays.toString(jsone.getStackTrace()) + "</p>");
//                }

                // also reset memcache
                final MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();
                memcacheService.clearAll();

                response.sendRedirect("/admin/on-calls");
            }
        }
    }

    class OnCalls {
        @com.google.gson.annotations.SerializedName("on-calls")
        private OnCallEntry [] onCallEntries;

        public int getSize() { return onCallEntries.length; }
        public OnCallEntry get(final int index) { return onCallEntries[index]; }
    }

    class OnCallEntry {
        private String date;
        private String pharmacies;

        public String getDate() {
            return date;
        }

        public String getPharmacies() {
            return pharmacies;
        }
    }
}
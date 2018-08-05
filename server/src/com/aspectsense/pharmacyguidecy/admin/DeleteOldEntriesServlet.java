package com.aspectsense.pharmacyguidecy.admin;

import com.aspectsense.pharmacyguidecy.data.OnCallFactory;
import com.aspectsense.pharmacyguidecy.data.UserEntity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date: 1/1/13
 * Time: 10:48 AM
 */
public class DeleteOldEntriesServlet extends HttpServlet
{
    public static final long MILLISECONDS_IN_A_DAY = 24L * 60 * 60 * 1000L;

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
                final Date dayBeforeYesterday = new Date(new Date().getTime() - 2 * MILLISECONDS_IN_A_DAY);
                final String cutoffDate = new SimpleDateFormat("yyyy-MM-dd").format(dayBeforeYesterday);
                OnCallFactory.deleteEntriesBefore(cutoffDate);

                response.sendRedirect("/admin/on-calls");
            }
        }

    }
}

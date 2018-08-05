<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.aspectsense.pharmacyguidecy.data.UserEntity" %>
<%--
  Date: 8/17/12
  Time: 10:41 AM
--%>
    <p> <img src="../favicon.ico" alt="icon"/> Cyprus Pharmacy Guide </p>
<%
    final UserService userService = UserServiceFactory.getUserService();
    final User user = userService.getCurrentUser();
    UserEntity userEntity = null;
    if (user == null)
    {
%>
    <p>You need to <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> to use this service.</p>
<%
    }
    else
    {
        userEntity = UserEntity.getUserEntity(user.getEmail());
        if(userEntity == null)
        {
            userEntity = UserEntity.setUserEntity(user.getEmail(), user.getNickname(), false);
        }
%>
    <p>Logged in as: <%= user.getNickname() %> <b> <%= userEntity.isAdmin() ? "(admin)" : "" %> </b> [<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>]</p>
<%
    }
%>
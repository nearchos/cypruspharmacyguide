<%--
  Created by IntelliJ IDEA.
  User: Nearchos Paspallis
  Date: 11/3/12
  Time: 6:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

  <head>
    <title>Cyprus Pharmacy Guide</title>
  </head>

  <body>

  <img src="../favicon.ico"/>
  The Cyprus Pharmacy Guide is an ANDROID app, available in the <a href="https://play.google.com/store/apps/details?id=com.aspectsense.pharmacyguidecy">Play store</a>.

  <hr/>

  <%@ include file="authenticate.jsp" %>

  <%
      if(userEntity == null)
      {
  %>
  You are not logged in!
  <%
  }
  else if(!userEntity.isAdmin())
  {
  %>
  You are not admin!
  <%
  }
  else
  {
  %>

  <h2>admin services</h2>
  <ul>
      <li>
          <a href="/admin/admin">admin/admin</a>
      </li>
      <li>
          <a href="/admin/cities">admin/cities</a>
      </li>
      <li>
          <a href="/admin/localities">admin/localities</a>
      </li>
      <li>
          <a href="/admin/pharmacies">admin/pharmacies</a>
      </li>
      <li>
          <a href="/admin/on-calls">admin/on-calls</a>
      </li>
  </ul>

  <h2>reporting services</h2>
  <ul>
      <li>
          <a href="/admin/weekly-report">admin/weekly-report</a> (automatically called by CRON every Monday)
      </li>
      <li>
          <a href="/admin/reports">admin/reports</a>
      </li>
  </ul>

  <h2>json services</h2>
  <ul>
      <li>
          <a href="/json/update-all">json/update-all</a>
      </li>
      <li>
          <a href="/json/get-logs">json/get-logs</a> (requires magic)
      </li>
      <li>
          <a href="/json/get-logs">json/delete-logs</a> (requires magic)
      </li>
  </ul>

    <%
  }
    %>
  </body>

</html>
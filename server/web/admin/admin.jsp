<%--
  User: Nearchos Paspallis
  Date: 11/21/12
  Time: 10:01 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cyprus Pharmacy Guide - Admin</title>
</head>

<body>

    <p>
        <b>Warning</b>: When you reset an entity 'Kind', you delete everything and then you restore their default values
        from the static TXT files included in the deployment.
    </p>
    <ul>
        <li>
            'Cities' are root (independent)
        </li>
        <li>
            'Localities' depend on 'Cities'
        </li>
        <li>
            'Pharmacies' depend on 'Localities'
        </li>
        <li>
            'On-Calls' are root (independent)
        </li>
    </ul>
<p>
    If you need to do a full reset of the system, make sure that you have what you need in the TXT files, and then
    reset the entity 'Kinds' in this order: 'Cities', 'Localities', 'Pharmacies', 'OnCalls'.
</p>
<p>
    <span style="color: red; ">Please note that <b>RESET</b> means: 1. delete all entries, 2. restore them from the TXT files </span>
</p>

    <hr/>

    <a href="/admin/cities">admin/cities</a>
    <form action="/admin/init-cities" method="post">
        <div><input type="submit" value="Reset (cities)" /></div>
    </form>

    <hr/>

    <a href="/admin/localities">admin/localities</a>
    <form action="/admin/init-localities" method="post">
        <div><input type="submit" value="Reset (localities)" /></div>
    </form>

    <hr/>

    <a href="/admin/pharmacies">admin/pharmacies</a>
    <form action="/admin/init-pharmacies" method="post">
        <div><input type="submit" value="Reset (pharmacies)" /></div>
    </form>

    <hr/>

    <a href="/admin/on-calls">admin/on-calls</a>
    <form action="/admin/init-on-calls" method="post">
        <div><input type="submit" value="Reset (on-calls)" /></div>
    </form>

</body>

</html>
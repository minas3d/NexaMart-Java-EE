<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="nti.models.User" %>
<%!
    private String ctx;
%>
<%
    ctx = request.getContextPath();

    User sessionUser = (User) session.getAttribute("user");
    boolean isLoggedIn = (sessionUser != null);
    boolean isAdmin = isLoggedIn && "ADMIN".equals(sessionUser.getRole());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NexaMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
    <script src="<%= ctx %>/js/main.js" defer></script>
</head>
<body>

<header class="navbar">
    <div class="container">

        <a href="<%= ctx %>/index.jsp" class="navbar-brand">
            <img src="<%= ctx %>/images/logo.svg" alt="NexaMart">
            <span>NexaMart</span>
        </a>

        <div class="navbar-actions">
            <%
                int headerCartCount = new nti.services.CartService()
                                        .getCartItemCount(session, sessionUser);
            %>
            <a href="<%= ctx %>/cart" class="nav-icon" title="Cart">
                🛒
                <% if (headerCartCount > 0) { %>
                    <span class="nav-badge"><%= headerCartCount %></span>
                <% } %>
            </a>

            <% if (isAdmin) { %>
                <a href="<%= ctx %>/admin/dashboard" class="btn btn-secondary btn-sm">Admin Panel</a>
                <a href="<%= ctx %>/logout" class="btn btn-secondary btn-sm">Logout</a>
            <% } else if (isLoggedIn) {
                    Object cidObj = session.getAttribute("customerId");
                    Long customerId = (cidObj instanceof Long) ? (Long) cidObj : null;
            %>
                <% if (customerId != null) { %>
                    <a href="<%= ctx %>/customer/dashboard.jsp" class="nav-profile" title="My Account">
                        <img src="<%= ctx %>/image?type=avatar&id=<%= customerId %>"
                             alt="Profile"
                             class="nav-avatar"
                             onerror="this.onerror=null; this.src='<%= ctx %>/images/default-avatar.svg';">
                        <span>Profile</span>
                    </a>
                <% } else { %>
                    <a href="<%= ctx %>/customer/dashboard.jsp" class="nav-profile" title="My Account">
                        <img src="<%= ctx %>/images/default-avatar.svg"
                             alt="Profile"
                             class="nav-avatar">
                        <span>Profile</span>
                    </a>
                <% } %>
                <a href="<%= ctx %>/logout" class="btn btn-secondary btn-sm">Logout</a>
            <% } else { %>
                <a href="<%= ctx %>/login.jsp" class="btn btn-secondary btn-sm">Login</a>
                <a href="<%= ctx %>/register.jsp" class="btn btn-primary btn-sm">Register</a>
            <% } %>
        </div>

    </div>
</header>

<main class="main-content">

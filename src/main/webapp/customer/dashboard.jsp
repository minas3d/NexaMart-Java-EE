<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="nti.models.Cart" %>
<%@ page import="nti.models.Customer" %>
<%@ page import="nti.models.User" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(ctx + "/login.jsp");
        return;
    }

    Customer customer = (Customer) request.getAttribute("customer");
    Cart     cart     = (Cart)     request.getAttribute("cart");

    if (cart == null) {
        response.sendRedirect(ctx + "/customer/dashboard");
        return;
    }

    String displayName = (customer != null && customer.getName() != null)
                         ? customer.getName()
                         : user.getUsername();

    String memberSince = "";
    if (customer != null && customer.getCreatedAt() != null) {
        memberSince = customer.getCreatedAt()
            .format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }
%>

<div class="container">

    <div class="dashboard-header">
        <h1>Welcome back, <%= displayName%>!</h1>
        <p>Here's a summary of your NexaMart account.</p>
    </div>

    <div class="dashboard-grid">

        <div class="dashboard-card">
            <div class="dashboard-card-icon">🛒</div>
            <h3>Cart</h3>
            <%
                if (cart.isEmpty()) {
%>
                <p class="dashboard-card-value">Empty</p>
                <p class="dashboard-card-detail">Nothing in your cart yet.</p>
                <a href="<%= ctx%>/browse" class="btn btn-secondary btn-sm">Browse Products</a>
            <%
                } else {
%>
                <p class="dashboard-card-value">
                    <%= cart.getTotalItems()%>
                    item<%= cart.getTotalItems() == 1 ? "" : "s"%>
                </p>
                <p class="dashboard-card-detail">
                    Total: $<%= String.format("%.2f", cart.getTotalPrice())%>
                </p>
                <a href="<%= ctx%>/cart" class="btn btn-primary btn-sm">View Cart</a>
            <%
                }
%>
        </div>

        <div class="dashboard-card">
            <div class="dashboard-card-icon">📦</div>
            <h3>Orders</h3>
            <p class="dashboard-card-value">Order History</p>
            <p class="dashboard-card-detail">Track and review your past orders.</p>
            <a href="<%= ctx%>/customer/orders" class="btn btn-secondary btn-sm">View Orders</a>
        </div>

        <div class="dashboard-card">
            <div class="dashboard-card-icon">👤</div>
            <h3>Profile</h3>
            <%
                if (customer != null) {
%>
                <p class="dashboard-card-value"><%= customer.getEmail()%></p>
                <p class="dashboard-card-detail">Manage your account info.</p>
            <%
                } else {
%>
                <p class="dashboard-card-value">—</p>
                <p class="dashboard-card-detail">Profile not available.</p>
            <%
                }
%>
            <a href="<%= ctx%>/customer/profile" class="btn btn-secondary btn-sm">View Profile</a>
        </div>

    </div>

    <div class="dashboard-columns">

        <div class="dashboard-panel">
            <h3>Account Information</h3>

            <%
                if (customer != null) {
%>
                <div class="dashboard-info-row">
                    <span class="dashboard-info-label">Name</span>
                    <span class="dashboard-info-value"><%= customer.getName()%></span>
                </div>
                <div class="dashboard-info-row">
                    <span class="dashboard-info-label">Email</span>
                    <span class="dashboard-info-value"><%= customer.getEmail()%></span>
                </div>
                <div class="dashboard-info-row">
                    <span class="dashboard-info-label">Phone</span>
                    <span class="dashboard-info-value">
                        <%= customer.getPhone() != null && !customer.getPhone().isEmpty()
                            ? customer.getPhone() : "—"%>
                    </span>
                </div>
                <div class="dashboard-info-row">
                    <span class="dashboard-info-label">Address</span>
                    <span class="dashboard-info-value">
                        <%= customer.getAddress() != null && !customer.getAddress().isEmpty()
                            ? customer.getAddress() : "—"%>
                    </span>
                </div>
                <%
                    if (!memberSince.isEmpty()) {
%>
                    <div class="dashboard-info-row">
                        <span class="dashboard-info-label">Member since</span>
                        <span class="dashboard-info-value"><%= memberSince%></span>
                    </div>
                <%
                    }
%>
            <%
                } else {
%>
                <p class="text-muted">No profile information available.</p>
            <%
                }
%>
        </div>

        <div class="dashboard-panel">
            <h3>Quick Actions</h3>

            <div class="dashboard-actions">
                <a href="<%= ctx%>/browse" class="btn btn-primary btn-block">Browse Products</a>

                <%
                    if (!cart.isEmpty()) {
%>
                    <a href="<%= ctx%>/cart" class="btn btn-primary btn-block">View Cart (<%= cart.getTotalItems()%>)</a>
                <%
                    }
%>

                <a href="<%= ctx%>/logout" class="btn btn-secondary btn-block">Logout</a>
            </div>
        </div>

    </div>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
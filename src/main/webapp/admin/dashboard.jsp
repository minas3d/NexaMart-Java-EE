<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="nti.services.AdminService.DashboardStats" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    DashboardStats stats = (DashboardStats) request.getAttribute("stats");
    if (stats == null) {
        response.sendRedirect(ctx + "/admin/dashboard");
        return;
    }
%>

<div class="container">
    <div class="admin-layout">

        <%@ include file="/WEB-INF/partials/admin-sidebar.jsp" %>

        <div class="admin-content">

            <div class="section-title">
                <h1>Dashboard</h1>
                <p>An overview of your store right now.</p>
            </div>

            <div class="stat-grid">
                <div class="stat-card">
                    <span class="stat-label">Products</span>
                    <span class="stat-value"><%= stats.getTotalProducts() %></span>
                </div>
                <div class="stat-card">
                    <span class="stat-label">Categories</span>
                    <span class="stat-value"><%= stats.getTotalCategories() %></span>
                </div>
                <div class="stat-card">
                    <span class="stat-label">Orders</span>
                    <span class="stat-value"><%= stats.getTotalOrders() %></span>
                </div>
                <div class="stat-card">
                    <span class="stat-label">Customers</span>
                    <span class="stat-value"><%= stats.getTotalCustomers() %></span>
                </div>
                <div class="stat-card stat-card-accent">
                    <span class="stat-label">Pending Orders</span>
                    <span class="stat-value"><%= stats.getPendingOrders() %></span>
                </div>
                <div class="stat-card stat-card-accent">
                    <span class="stat-label">Total Revenue</span>
                    <span class="stat-value">$<%= String.format("%.2f", stats.getTotalRevenue()) %></span>
                </div>
            </div>

            <div class="admin-quick-links">
                <a href="<%= ctx %>/admin/products?action=new" class="btn btn-primary">+ Add Product</a>
                <a href="<%= ctx %>/admin/orders" class="btn btn-secondary">View Orders</a>
                <a href="<%= ctx %>/admin/categories" class="btn btn-secondary">Manage Categories</a>
            </div>

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

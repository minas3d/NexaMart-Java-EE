<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String currentPath = request.getServletPath();
%>
<aside class="admin-sidebar">
    <nav class="admin-nav">
        <a href="<%= ctx %>/admin/dashboard"
           class="admin-nav-link <%= currentPath.equals("/admin/dashboard") ? "active" : "" %>">
            <span class="admin-nav-icon">📊</span> Dashboard
        </a>
        <a href="<%= ctx %>/admin/products"
           class="admin-nav-link <%= currentPath.startsWith("/admin/products") ? "active" : "" %>">
            <span class="admin-nav-icon">📦</span> Products
        </a>
        <a href="<%= ctx %>/admin/categories"
           class="admin-nav-link <%= currentPath.equals("/admin/categories") ? "active" : "" %>">
            <span class="admin-nav-icon">🗂️</span> Categories
        </a>
        <a href="<%= ctx %>/admin/orders"
           class="admin-nav-link <%= currentPath.equals("/admin/orders") ? "active" : "" %>">
            <span class="admin-nav-icon">🧾</span> Orders
        </a>
        <a href="<%= ctx %>/admin/customers"
           class="admin-nav-link <%= currentPath.equals("/admin/customers") ? "active" : "" %>">
            <span class="admin-nav-icon">👥</span> Customers
        </a>
    </nav>
</aside>

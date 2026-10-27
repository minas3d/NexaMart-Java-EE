<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Order" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    if (orders == null) orders = java.util.Collections.emptyList();

    DateTimeFormatter orderFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
%>

<div class="container">
    <div class="admin-layout">

        <%@ include file="/WEB-INF/partials/admin-sidebar.jsp" %>

        <div class="admin-content">

            <div class="section-title">
                <h1>Orders</h1>
                <p>All orders placed by customers.</p>
            </div>

            <% if (orders.isEmpty()) { %>
                <div class="empty-state">
                    <p>No orders yet.</p>
                </div>
            <% } else { %>
                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Order #</th>
                                <th>Date</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Order o : orders) { %>
                                <tr>
                                    <td data-label="Order #">#<%= o.getId() %></td>
                                    <td data-label="Date">
                                        <%= o.getOrderDate() != null ? o.getOrderDate().format(orderFmt) : "—" %>
                                    </td>
                                    <td data-label="Total">$<%= String.format("%.2f", o.getTotalAmount()) %></td>
                                    <td data-label="Status">
                                        <span class="order-status status-<%= o.getStatus().toLowerCase() %>">
                                            <%= o.getStatus() %>
                                        </span>
                                    </td>
                                    <td class="table-actions">
                                        <a href="<%= ctx %>/admin/orders?id=<%= o.getId() %>"
                                           class="btn btn-secondary btn-sm">View</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

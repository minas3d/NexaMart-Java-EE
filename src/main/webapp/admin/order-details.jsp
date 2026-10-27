<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Customer" %>
<%@ page import="nti.models.Order" %>
<%@ page import="nti.models.OrderItem" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Order order = (Order) request.getAttribute("order");
    Customer orderCustomer = (Customer) request.getAttribute("customer");
    List<String> statuses = (List<String>) request.getAttribute("statuses");
    String orderError = (String) request.getAttribute("error");

    if (order == null) {
        response.sendRedirect(ctx + "/admin/orders");
        return;
    }
    if (statuses == null) statuses = java.util.Collections.emptyList();

    DateTimeFormatter orderFmt = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
%>

<div class="container">
    <div class="admin-layout">

        <%@ include file="/WEB-INF/partials/admin-sidebar.jsp" %>

        <div class="admin-content">

            <div class="section-title">
                <h1>Order #<%= order.getId() %></h1>
                <p><%= order.getOrderDate() != null ? order.getOrderDate().format(orderFmt) : "" %></p>
            </div>

            <% if (orderError != null) { %>
                <div class="alert alert-danger"><%= orderError %></div>
            <% } %>

            <div class="order-detail-layout">

                <div class="order-detail-items">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Price</th>
                                <th>Qty</th>
                                <th>Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (OrderItem item : order.getItems()) { %>
                                <tr>
                                    <td data-label="Product"><%= item.getProduct().getName() %></td>
                                    <td data-label="Price">$<%= String.format("%.2f", item.getPrice()) %></td>
                                    <td data-label="Qty"><%= item.getQuantity() %></td>
                                    <td data-label="Subtotal">$<%= String.format("%.2f", item.getSubtotal()) %></td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>

                    <div class="summary-row summary-total">
                        <span>Total:</span>
                        <span>$<%= String.format("%.2f", order.getTotalAmount()) %></span>
                    </div>
                </div>

                <aside class="cart-summary">
                    <h3>Customer</h3>
                    <% if (orderCustomer != null) { %>
                        <p><strong><%= orderCustomer.getName() %></strong></p>
                        <p><%= orderCustomer.getEmail() %></p>
                        <p><%= orderCustomer.getPhone() != null ? orderCustomer.getPhone() : "—" %></p>
                        <p><%= orderCustomer.getAddress() != null ? orderCustomer.getAddress() : "—" %></p>
                    <% } else { %>
                        <p>Customer not found.</p>
                    <% } %>

                    <h3>Status</h3>
                    <form action="<%= ctx %>/admin/orders" method="post" class="admin-form">
                        <input type="hidden" name="id" value="<%= order.getId() %>">
                        <div class="form-group">
                            <select name="status" class="form-control">
                                <% for (String s : statuses) {
                                    boolean selected = s.equals(order.getStatus());
                                %>
                                    <option value="<%= s %>" <%= selected ? "selected" : "" %>><%= s %></option>
                                <% } %>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary btn-block">Update Status</button>
                    </form>

                    <a href="<%= ctx %>/admin/orders" class="btn btn-secondary btn-block" style="margin-top: 12px;">
                        ← Back to Orders
                    </a>
                </aside>

            </div>

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

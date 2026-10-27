<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Order" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");

    if (orders == null) {
        response.sendRedirect(ctx + "/customer/orders");
        return;
    }

    DateTimeFormatter dateFmt =
        DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
%>

<div class="container">

    <div class="section-title">
        <h1>My Orders</h1>
        <p>Track and view your past orders.</p>
    </div>

    <%
        if (orders.isEmpty()) {
%>

        <div class="empty-state">
            <div class="empty-state-icon">📦</div>
            <h2>No orders yet</h2>
            <p>You haven't placed any orders. Start shopping to see them here.</p>
            <a href="<%= ctx%>/browse" class="btn btn-primary btn-lg">Browse Products</a>
        </div>

    <%
        } else {
%>

        <div class="orders-list">

            <%
                for (Order o : orders) {
%>
                <div class="order-card">
                    <div class="order-card-left">
                        <div class="order-card-id">Order #<%= o.getId()%></div>
                        <div class="order-card-date">
                            <%= o.getOrderDate() != null
                                ? o.getOrderDate().format(dateFmt)
                                : "—"%>
                        </div>
                    </div>

                    <div class="order-card-middle">
                        <div class="order-card-total-label">Total</div>
                        <div class="order-card-total">
                            $<%= String.format("%.2f", o.getTotalAmount())%>
                        </div>
                    </div>

                    <div class="order-card-right">
                        <span class="order-status status-<%= o.getStatus().toLowerCase()%>">
                            <%= o.getStatus()%>
                        </span>
							<a href="<%= ctx%>/order-confirmation?id=<%= o.getId()%>"
							   class="btn btn-secondary btn-sm">
							    View Details
							</a>
                    </div>
                </div>
            <%
                }
%>

        </div>

    <%
        }
%>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="nti.models.Order" %>
<%@ page import="nti.models.OrderItem" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Order order = (Order) request.getAttribute("order");

    if (order == null) {
        response.sendRedirect(ctx + "/");
        return;
    }

    DateTimeFormatter dateFmt =
        DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
%>

<div class="container">

<%
    boolean isFresh = "1".equals(request.getParameter("new"));
%>

    <div class="order-confirm-header <%= isFresh ? "" : "order-confirm-header--plain"%>">
        <%
            if (isFresh) {
%>
            <div class="order-confirm-icon">✓</div>
            <h1>Order Confirmed!</h1>
            <p>Thank you for your order. We'll notify you when it ships.</p>
        <%
            } else {
%>
            <h1>Order #<%= order.getId()%></h1>
            <p>Placed on <%= order.getOrderDate() != null
                    ? order.getOrderDate().format(dateFmt)
                    : "—"%></p>
        <%
            }
%>
    </div>
    <div class="order-confirm-card">

        <div class="order-confirm-meta">
            <div>
                <span class="meta-label">Order</span>
                <span class="meta-value">#<%= order.getId()%></span>
            </div>
            <div>
                <span class="meta-label">Placed</span>
                <span class="meta-value">
                    <%= order.getOrderDate() != null
                        ? order.getOrderDate().format(dateFmt)
                        : "—"%>
                </span>
            </div>
            <div>
                <span class="meta-label">Status</span>
                <span class="order-status status-<%= order.getStatus().toLowerCase()%>">
                    <%= order.getStatus()%>
                </span>
            </div>
        </div>

        <h3 class="order-confirm-section-title">Items</h3>

        <table class="order-confirm-table">
            <thead>
                <tr>
                    <th>Product</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Subtotal</th>
                </tr>
            </thead>
            <tbody>
                <%
                    for (OrderItem item : order.getItems()) {
%>
                    <tr>
                        <td data-label="Product">
                            <%
                                String pname = item.getProduct() != null
                                    ? item.getProduct().getName()
                                    : ("Product #" + item.getProductId());
%>
                            <%= pname%>
                        </td>
                        <td data-label="Price">
                            $<%= String.format("%.2f", item.getPrice())%>
                        </td>
                        <td data-label="Quantity">
                            × <%= item.getQuantity()%>
                        </td>
                        <td data-label="Subtotal">
                            $<%= String.format("%.2f", item.getSubtotal())%>
                        </td>
                    </tr>
                <%
                    }
%>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="3" class="order-confirm-total-label">Total</td>
                    <td class="order-confirm-total-amount">
                        $<%= String.format("%.2f", order.getTotalAmount())%>
                    </td>
                </tr>
            </tfoot>
        </table>

        <div class="order-confirm-actions">
            <a href="<%= ctx%>/customer/orders" class="btn btn-secondary">
                View My Orders
            </a>
            <a href="<%= ctx%>/browse" class="btn btn-primary">
                Continue Shopping
            </a>
        </div>

    </div>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
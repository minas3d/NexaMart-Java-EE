<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="nti.models.Cart" %>
<%@ page import="nti.models.CartItem" %>
<%@ page import="nti.models.Customer" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Customer customer = (Customer) request.getAttribute("customer");
    Cart     cart     = (Cart)     request.getAttribute("cart");

    if (cart == null) {
        response.sendRedirect(ctx + "/checkout");
        return;
    }

    if (cart.isEmpty()) {
        response.sendRedirect(ctx + "/cart");
        return;
    }

    String checkoutError = (String) request.getAttribute("error");
%>

<div class="container">

    <div class="section-title">
        <h1>Checkout</h1>
        <p>Review your order and confirm to place it.</p>
    </div>

    <%
        if (checkoutError != null) {
%>
        <div class="alert alert-danger"><%= checkoutError%></div>
    <%
        }
%>

    <form action="<%= ctx%>/checkout" method="post">

        <div class="checkout-layout">

            <div class="checkout-panel">
                <h3>Shipping Information</h3>

                <div class="form-group">
                    <label class="form-label" for="fullName">
                        Full Name <span class="required">*</span>
                    </label>
                    <input type="text" id="fullName" name="fullName" class="form-control"
                           value="<%= customer != null && customer.getName() != null ? customer.getName() : ""%>"
                           required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="phone">
                        Phone <span class="required">*</span>
                    </label>
                    <input type="text" id="phone" name="phone" class="form-control"
                           value="<%= customer != null && customer.getPhone() != null ? customer.getPhone() : ""%>"
                           required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="address">
                        Shipping Address <span class="required">*</span>
                    </label>
                    <textarea id="address" name="address" class="form-control"
                              placeholder="Street, city, region"
                              required><%= customer != null && customer.getAddress() != null ? customer.getAddress() : ""%></textarea>
                </div>

                <p class="text-muted text-sm">
                    Need to update your details? You can edit them in the fields above for this order.
                </p>
            </div>

            <aside class="checkout-panel checkout-summary">
                <h3>Order Summary</h3>

                <%
                    for (CartItem item : cart.getItems()) {
%>
                    <div class="checkout-item-row">
                        <span class="checkout-item-name">
                            <%= item.getProduct().getName()%>
                        </span>
                        <span class="checkout-item-qty">× <%= item.getQuantity()%></span>
                        <span class="checkout-item-subtotal">
                            $<%= String.format("%.2f", item.getSubtotal())%>
                        </span>
                    </div>
                <%
                    }
%>

                <div class="summary-row">
                    <span>Items:</span>
                    <span><%= cart.getTotalItems()%></span>
                </div>
                <div class="summary-row">
                    <span>Subtotal:</span>
                    <span>$<%= String.format("%.2f", cart.getTotalPrice())%></span>
                </div>
                <div class="summary-row">
                    <span>Shipping:</span>
                    <span>$0.00</span>
                </div>
                <div class="summary-row summary-total">
                    <span>Total:</span>
                    <span>$<%= String.format("%.2f", cart.getTotalPrice())%></span>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg">
                    Confirm Order
                </button>

                <a href="<%= ctx%>/cart" class="btn btn-secondary btn-block">
                    ← Back to Cart
                </a>
            </aside>

        </div>

    </form>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
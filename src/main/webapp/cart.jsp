<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="nti.models.Cart" %>
<%@ page import="nti.models.CartItem" %>
<%@ page import="nti.models.Product" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Cart cart = (Cart) request.getAttribute("cart");

    if (cart == null) {
        response.sendRedirect(ctx + "/cart");
        return;
    }

    String cartError = (String) request.getAttribute("error");
%>

<div class="container">

    <div class="section-title">
        <h1>Your Cart</h1>
    </div>

    <%
        if (cartError != null) {
%>
        <div class="alert alert-danger"><%= cartError%></div>
    <%
        }

        if (cart.isEmpty()) {
%>

        <div class="empty-state">
            <div class="empty-state-icon">🛒</div>
            <h2>Your cart is empty</h2>
            <p>Add some products to get started.</p>
            <a href="<%= ctx%>/browse" class="btn btn-primary btn-lg">Browse Products</a>
        </div>

    <%
        } else {
%>

        <div class="cart-layout">

            <div class="cart-items">

                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>

                        <%
                            for (CartItem item : cart.getItems()) {
                                Product p = item.getProduct();
%>
                            <tr>

                                <td data-label="Product">
                                    <div class="cart-product">
                                        <img src="<%= ctx%>/image?type=product&id=<%= p.getId()%>"
                                             alt="<%= p.getName()%>"
                                             class="cart-product-image"
                                             onerror="this.onerror=null; this.src='<%= ctx%>/images/default-product.svg';">
                                        <div class="cart-product-info">
                                            <a href="<%= ctx%>/product-details?id=<%= p.getId()%>"
                                               class="cart-product-name">
                                                <%= p.getName()%>
                                            </a>
                                            <span class="cart-product-category">
                                                <%= p.getCategoryName() != null ? p.getCategoryName() : "Uncategorized"%>
                                            </span>
                                        </div>
                                    </div>
                                </td>

                                <td data-label="Price" class="cart-price">
                                    $<%= String.format("%.2f", p.getPrice())%>
                                </td>

                                <td data-label="Quantity">
                                    <div class="quantity-control">

                                        <form action="<%= ctx%>/cart" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="decrease">
                                            <input type="hidden" name="productId" value="<%= p.getId()%>">
                                            <button type="submit" class="qty-btn" title="Decrease">−</button>
                                        </form>

                                        <span class="qty-value"><%= item.getQuantity()%></span>

                                        <form action="<%= ctx%>/cart" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="increase">
                                            <input type="hidden" name="productId" value="<%= p.getId()%>">
                                            <button type="submit" class="qty-btn" title="Increase">+</button>
                                        </form>

                                    </div>
                                </td>

                                <td data-label="Subtotal" class="cart-subtotal">
                                    $<%= String.format("%.2f", item.getSubtotal())%>
                                </td>

                                <td class="cart-remove">
                                    <form action="<%= ctx%>/cart" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="productId" value="<%= p.getId()%>">
                                        <button type="submit" class="remove-btn" title="Remove">✕</button>
                                    </form>
                                </td>
                            </tr>
                        <%
                            }
%>

                    </tbody>
                </table>

                <div class="cart-actions-top">
                    <a href="<%= ctx%>/browse" class="btn btn-secondary btn-sm">
                        ← Continue Shopping
                    </a>
                    <form action="<%= ctx%>/cart" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="clear">
                        <button type="submit" class="btn btn-secondary btn-sm">Clear Cart</button>
                    </form>
                </div>

            </div>

            <aside class="cart-summary">
                <h3>Order Summary</h3>

                <div class="summary-row">
                    <span>Items:</span>
                    <span><%= cart.getTotalItems()%></span>
                </div>
                <div class="summary-row">
                    <span>Subtotal:</span>
                    <span>$<%= String.format("%.2f", cart.getTotalPrice())%></span>
                </div>
                <div class="summary-row summary-total">
                    <span>Total:</span>
                    <span>$<%= String.format("%.2f", cart.getTotalPrice())%></span>
                </div>

                <a href="<%= ctx%>/checkout" class="btn btn-primary btn-block btn-lg">
                    Proceed to Checkout
                </a>

                <p class="summary-note">Shipping calculated at checkout.</p>
            </aside>

        </div>

    <%
        }
%>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
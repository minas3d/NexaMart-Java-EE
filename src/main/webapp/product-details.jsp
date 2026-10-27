<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="nti.models.Product" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Product product = (Product) request.getAttribute("product");

    if (product == null) {
        response.sendRedirect(ctx + "/browse");
        return;
    }

    boolean inStock = product.getStock() > 0;
%>

<div class="container">

    <a href="<%= ctx%>/browse" class="back-link">
        ← Back to Browse
    </a>

    <div class="product-details">

        <div class="product-details-image">
            <img src="<%= ctx%>/image?type=product&id=<%= product.getId()%>"
                 alt="<%= product.getName()%>"
                 class="product-details-img"
                 onerror="this.onerror=null; this.src='<%= ctx%>/images/default-product.svg';">
        </div>

        <div class="product-details-info">

            <h1 class="product-details-title"><%= product.getName()%></h1>

            <p class="product-details-price">$<%= String.format("%.2f", product.getPrice())%></p>

            <div class="product-details-meta">
                <div>
                    <span class="meta-label">Category:</span>
                    <span class="meta-value">
                        <%
                            String catName = product.getCategoryName();
                            if (catName != null && !catName.isEmpty()) {
%>
                            <%= catName%>
                        <%
                            } else {
%>
                            Uncategorized
                        <%
                            }
%>
                    </span>
                </div>
                <div>
                    <span class="meta-label">Status:</span>
                    <%
                        if (inStock) {
%>
                        <span class="meta-value stock in-stock">
                            In Stock (<%= product.getStock()%>)
                        </span>
                    <%
                        } else {
%>
                        <span class="meta-value stock out-of-stock">
                            Out of Stock
                        </span>
                    <%
                        }
%>
                </div>
            </div>

            <div class="product-details-description">
                <h3>Description</h3>
                <%
                    String desc = product.getDescription();
                    if (desc != null && !desc.isEmpty()) {
%>
                    <p><%= desc%></p>
                <%
                    } else {
%>
                    <p class="text-muted">No description available.</p>
                <%
                    }
%>
            </div>

                        <div class="product-details-actions">

                <%
                    String added = request.getParameter("added");
                    if ("1".equals(added)) {
%>
                    <div class="alert alert-success" style="width: 100%;">
                        Added to cart.
                        <a href="<%= ctx%>/cart">View cart</a>
                    </div>
                <%
                    }

                    String error = request.getParameter("error");
                    if (error != null && !error.isEmpty()) {
%>
                    <div class="alert alert-danger" style="width: 100%;">
                        <%= error%>
                    </div>
                <%
                    }

                    if (inStock) {
%>
                    <form action="<%= ctx%>/add-to-cart" method="post"
                          style="display: flex; gap: var(--space-sm); align-items: center; width: 100%;">
                        <input type="hidden" name="productId" value="<%= product.getId()%>">

                        <label class="form-label" for="quantity"
                               style="margin: 0; white-space: nowrap;">
                            Quantity:
                        </label>
                        <input type="number" id="quantity" name="quantity"
                               class="form-control" value="1"
                               min="1" max="<%= product.getStock()%>"
                               style="width: 80px;">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add to Cart
                        </button>
                    </form>
                <%
                    } else {
%>
                    <button type="button" class="btn btn-secondary btn-lg" disabled>
                        Out of Stock
                    </button>
                <%
                    }
%>
            </div>
            </div>

        </div>

    </div>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
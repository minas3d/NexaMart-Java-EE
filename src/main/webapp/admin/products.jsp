<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Product" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    List<Product> products = (List<Product>) request.getAttribute("products");
    if (products == null) products = java.util.Collections.emptyList();

    String productsError = (String) request.getAttribute("error");
%>

<div class="container">
    <div class="admin-layout">

        <%@ include file="/WEB-INF/partials/admin-sidebar.jsp" %>

        <div class="admin-content">

            <div class="admin-content-header">
                <div class="section-title">
                    <h1>Products</h1>
                    <p>Manage the products available in your store.</p>
                </div>
                <a href="<%= ctx %>/admin/products?action=new" class="btn btn-primary">+ Add Product</a>
            </div>

            <% if (productsError != null) { %>
                <div class="alert alert-danger"><%= productsError %></div>
            <% } %>

            <% if (products.isEmpty()) { %>
                <div class="empty-state">
                    <p>No products yet.</p>
                    <a href="<%= ctx %>/admin/products?action=new" class="btn btn-primary">Add your first product</a>
                </div>
            <% } else { %>
                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Product p : products) { %>
                                <tr>
                                    <td data-label="Product">
                                        <div class="table-product">
                                            <img src="<%= ctx %>/image?type=product&id=<%= p.getId() %>"
                                                 alt="<%= p.getName() %>"
                                                 class="table-product-image"
                                                 onerror="this.onerror=null; this.src='<%= ctx %>/images/default-product.svg';">
                                            <span><%= p.getName() %></span>
                                        </div>
                                    </td>
                                    <td data-label="Category">
                                        <%= p.getCategoryName() != null ? p.getCategoryName() : "Uncategorized" %>
                                    </td>
                                    <td data-label="Price">$<%= String.format("%.2f", p.getPrice()) %></td>
                                    <td data-label="Stock">
                                        <span class="<%= p.getStock() > 0 ? "in-stock" : "out-of-stock" %>">
                                            <%= p.getStock() %>
                                        </span>
                                    </td>
                                    <td class="table-actions">
                                        <a href="<%= ctx %>/admin/products?action=edit&id=<%= p.getId() %>"
                                           class="btn btn-secondary btn-sm">Edit</a>
                                        <form action="<%= ctx %>/admin/products" method="post" style="display:inline;"
                                              onsubmit="return confirm('Delete this product?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="<%= p.getId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                        </form>
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

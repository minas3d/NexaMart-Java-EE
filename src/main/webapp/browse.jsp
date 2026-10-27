<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Product" %>
<%@ page import="nti.models.Category" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    List<Product>  products   = (List<Product>)  request.getAttribute("products");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    Integer totalProducts     = (Integer) request.getAttribute("totalProducts");
    Integer currentPage       = (Integer) request.getAttribute("currentPage");
    Integer totalPages        = (Integer) request.getAttribute("totalPages");

    String q                = (String) request.getAttribute("q");
    String selectedCategory = (String) request.getAttribute("selectedCategory");

    if (products == null)     products   = java.util.Collections.emptyList();
    if (categories == null)   categories = java.util.Collections.emptyList();
    if (totalProducts == null) totalProducts = 0;
    if (currentPage == null)  currentPage = 1;
    if (totalPages == null)   totalPages  = 1;
    if (q == null)             q = "";
    if (selectedCategory == null) selectedCategory = "";
%>

<div class="container">

    <div class="section-title">
        <h1>Browse Products</h1>
        <p>Browse laptops, phones, audio, smart home, and more — all in stock at NexaMart.</p>
    </div>

    <form action="<%= ctx%>/browse" method="get" class="browse-filters">

        <input type="text" name="q" class="form-control"
               placeholder="Search by product name..."
               value="<%= q%>">

        <select name="category" class="form-control">
            <option value="">All Categories</option>
            <%
                for (Category c : categories) {
                    String selected = String.valueOf(c.getId()).equals(selectedCategory)
                                      ? "selected" : "";
%>
                <option value="<%= c.getId()%>" <%= selected%>><%= c.getName()%></option>
            <%
                }
%>
        </select>

        <button type="submit" class="btn btn-primary">Search</button>

        <a href="<%= ctx%>/browse" class="btn btn-secondary">Clear</a>
    </form>

    <%
        if (products.isEmpty()) {
%>
        <div class="empty-state">
            <p>No products found matching your filters.</p>
            <a href="<%= ctx%>/browse" class="btn btn-secondary">Clear Filters</a>
        </div>
    <%
        } else {
%>
        <div class="product-grid">
            <%
                for (Product p : products) {
                    boolean inStock = p.getStock() > 0;
%>
                <div class="product-card">
                    <img src="<%= ctx%>/image?type=product&id=<%= p.getId()%>"
                         alt="<%= p.getName()%>"
                         class="card-image"
                         onerror="this.onerror=null; this.src='<%= ctx%>/images/default-product.svg';">
                    <div class="card-body">
                        <h3 class="card-title"><%= p.getName()%></h3>
                        <p class="product-price">$<%= String.format("%.2f", p.getPrice())%></p>
                        <p class="product-category">
                            <%= p.getCategoryName() != null ? p.getCategoryName() : "Uncategorized"%>
                        </p>
                        <p class="product-stock <%= inStock ? "in-stock" : "out-of-stock"%>">
                            <%= inStock ? "In Stock (" + p.getStock() + ")" : "Out of Stock"%>
                        </p>
                        <a href="<%= ctx%>/product-details?id=<%= p.getId()%>"
                           class="btn <%= inStock ? "btn-primary" : "btn-secondary"%> btn-sm btn-block">
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

    <%
        if (!products.isEmpty()) {
            int start = (currentPage - 1) * 12 + 1;
            int end   = Math.min(currentPage * 12, totalProducts);
%>
        <div class="pagination">
            <p class="pagination-info">
                Showing <%= start%>–<%= end%> of <%= totalProducts%> products
            </p>
            <div class="pagination-controls">
                <%
                    if (currentPage > 1) {
%>
                    <a href="<%= ctx%>/browse?page=<%= currentPage - 1%>&q=<%= java.net.URLEncoder.encode(q, "UTF-8")%>&category=<%= selectedCategory%>"
                       class="btn btn-secondary btn-sm">← Previous</a>
                <%
                    } else {
%>
                    <span class="btn btn-secondary btn-sm disabled">← Previous</span>
                <%
                    }
%>

                <span class="pagination-page">
                    Page <%= currentPage%> of <%= totalPages%>
                </span>

                <%
                    if (currentPage < totalPages) {
%>
                    <a href="<%= ctx%>/browse?page=<%= currentPage + 1%>&q=<%= java.net.URLEncoder.encode(q, "UTF-8")%>&category=<%= selectedCategory%>"
                       class="btn btn-secondary btn-sm">Next →</a>
                <%
                    } else {
%>
                    <span class="btn btn-secondary btn-sm disabled">Next →</span>
                <%
                    }
%>
            </div>
        </div>
    <%
        }
%>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
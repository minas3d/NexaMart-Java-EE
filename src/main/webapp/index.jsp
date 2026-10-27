<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Category" %>
<%@ page import="nti.models.Product" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Product>  featured   = (List<Product>)  request.getAttribute("featured");

    if (categories == null || featured == null) {
        response.sendRedirect(ctx + "/home");
        return;
    }
%>

<section class="home-hero">
    <div class="container">
        <h1>Smart tech, delivered fast.</h1>
        <p>
            Laptops, phones, audio, and smart home gear — curated, in stock,
            and ready to ship across Egypt.
        </p>
        <div class="home-hero-actions">
            <a href="<%= ctx%>/browse" class="btn btn-primary btn-lg">Shop Now</a>
            <a href="<%= ctx%>/register.jsp" class="btn btn-secondary btn-lg">Create an Account</a>
        </div>
    </div>
</section>

<section class="container home-section">
    <div class="section-title">
        <h2>Shop by Category</h2>
        <p>Find exactly what you're looking for.</p>
    </div>

    <%
        if (categories.isEmpty()) {
%>
        <p class="text-center text-muted">No categories available.</p>
    <%
        } else {
%>
        <div class="category-grid">
            <%
                for (Category c : categories) {
%>
                <a href="<%= ctx%>/browse?category=<%= c.getId()%>" class="category-card">
                    <span class="category-name"><%= c.getName()%></span>
                </a>
            <%
                }
%>
        </div>
    <%
        }
%>
</section>

<section class="home-section">
    <div class="container">
        <div class="section-title">
            <h2>Featured Products</h2>
            <p>Handpicked picks from our current catalog.</p>
        </div>

        <%
            if (featured.isEmpty()) {
%>
            <p class="text-center text-muted">No products to feature yet.</p>
        <%
            } else {
%>
            <div class="home-featured-grid">
                <%
                    for (Product p : featured) {
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

            <div class="home-cta-link">
                <a href="<%= ctx%>/browse">See all products →</a>
            </div>
        <%
            }
%>
    </div>
</section>

<section class="home-section home-how-it-works">
    <div class="container">
        <div class="section-title">
            <h2>How It Works</h2>
            <p>Three simple steps to get your order delivered.</p>
        </div>

        <div class="steps-grid">
            <div class="step-card">
                <div class="step-number">1</div>
                <h3>Create an account</h3>
                <p>Sign up in seconds — just an email and password.</p>
            </div>
            <div class="step-card">
                <div class="step-number">2</div>
                <h3>Browse the catalog</h3>
                <p>Compare laptops, phones, audio gear, and more.</p>
            </div>
            <div class="step-card">
                <div class="step-number">3</div>
                <h3>Checkout securely</h3>
                <p>Add to cart, confirm your order, and track its status.</p>
            </div>
        </div>
    </div>
</section>

<section class="home-final-cta">
    <div class="container">
        <h2>Ready to upgrade your setup?</h2>
        <p>Join NexaMart and get the latest tech delivered to your door.</p>
        <a href="<%= ctx%>/register.jsp" class="btn btn-primary btn-lg">Create an Account</a>
    </div>
</section>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
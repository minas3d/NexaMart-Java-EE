<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Category" %>
<%@ page import="nti.models.Product" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Product product = (Product) request.getAttribute("product");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    if (categories == null) categories = java.util.Collections.emptyList();

    String formError = (String) request.getAttribute("error");
    boolean isEdit = (product != null);
%>

<div class="container">
    <div class="admin-layout">

        <%@ include file="/WEB-INF/partials/admin-sidebar.jsp" %>

        <div class="admin-content">

            <div class="section-title">
                <h1><%= isEdit ? "Edit Product" : "Add Product" %></h1>
                <p>Fill in the product details below.</p>
            </div>

            <% if (formError != null) { %>
                <div class="alert alert-danger"><%= formError %></div>
            <% } %>

            <form action="<%= ctx %>/admin/products" method="post" enctype="multipart/form-data"
                  class="admin-form">

                <% if (isEdit) { %>
                    <input type="hidden" name="id" value="<%= product.getId() %>">
                <% } %>

                <div class="form-group">
                    <label for="name">Product Name</label>
                    <input type="text" id="name" name="name" class="form-control"
                           value="<%= isEdit ? product.getName() : "" %>" required>
                </div>

                <div class="form-group">
                    <label for="categoryId">Category</label>
                    <select id="categoryId" name="categoryId" class="form-control" required>
                        <option value="">Select a category</option>
                        <% for (Category c : categories) {
                            boolean selected = isEdit && c.getId() == product.getCategoryId();
                        %>
                            <option value="<%= c.getId() %>" <%= selected ? "selected" : "" %>>
                                <%= c.getName() %>
                            </option>
                        <% } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" class="form-control" rows="4"><%=
                        isEdit && product.getDescription() != null ? product.getDescription() : ""
                    %></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="price">Price ($)</label>
                        <input type="number" id="price" name="price" class="form-control"
                               step="0.01" min="0"
                               value="<%= isEdit ? product.getPrice() : "" %>" required>
                    </div>

                    <div class="form-group">
                        <label for="stock">Stock</label>
                        <input type="number" id="stock" name="stock" class="form-control"
                               min="0"
                               value="<%= isEdit ? product.getStock() : "" %>" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="image">Product Image</label>
                    <input type="file" id="image" name="image" class="form-control" accept="image/*">
                    <% if (isEdit) { %>
                        <p class="form-hint">Leave empty to keep the current image.</p>
                    <% } %>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        <%= isEdit ? "Save Changes" : "Add Product" %>
                    </button>
                    <a href="<%= ctx %>/admin/products" class="btn btn-secondary">Cancel</a>
                </div>

            </form>

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

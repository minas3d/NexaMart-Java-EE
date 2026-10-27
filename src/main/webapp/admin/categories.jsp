<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Category" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    if (categories == null) categories = java.util.Collections.emptyList();

    String catError = (String) request.getAttribute("error");
%>

<div class="container">
    <div class="admin-layout">

        <%@ include file="/WEB-INF/partials/admin-sidebar.jsp" %>

        <div class="admin-content">

            <div class="section-title">
                <h1>Categories</h1>
                <p>Organize your products into categories.</p>
            </div>

            <% if (catError != null) { %>
                <div class="alert alert-danger"><%= catError %></div>
            <% } %>

            <div class="admin-split">

                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Description</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (categories.isEmpty()) { %>
                                <tr><td colspan="3">No categories yet.</td></tr>
                            <% } %>
                            <% for (Category c : categories) { %>
                                <tr>
                                    <td data-label="Name"><%= c.getName() %></td>
                                    <td data-label="Description">
                                        <%= c.getDescription() != null ? c.getDescription() : "—" %>
                                    </td>
                                    <td class="table-actions">
                                        <button type="button" class="btn btn-secondary btn-sm"
                                                data-id="<%= c.getId() %>"
                                                data-name="<%= nti.utils.HtmlUtil.escape(c.getName()) %>"
                                                data-description="<%= nti.utils.HtmlUtil.escape(c.getDescription()) %>"
                                                onclick="fillCategoryForm(this)">Edit</button>
                                        <form action="<%= ctx %>/admin/categories" method="post" style="display:inline;"
                                              onsubmit="return confirm('Delete this category? Products in it will become uncategorized.');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="<%= c.getId() %>">
                                            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <div class="admin-side-form">
                    <h3>Add / Edit Category</h3>
                    <form action="<%= ctx %>/admin/categories" method="post" class="admin-form">
                        <input type="hidden" id="catId" name="id" value="">

                        <div class="form-group">
                            <label for="catName">Name</label>
                            <input type="text" id="catName" name="name" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label for="catDescription">Description</label>
                            <textarea id="catDescription" name="description" class="form-control" rows="3"></textarea>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Save Category</button>
                        </div>
                    </form>
                </div>

            </div>

        </div>
    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

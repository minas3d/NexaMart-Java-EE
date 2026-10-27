<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="nti.models.Customer" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Customer editCustomer = (Customer) request.getAttribute("customer");
    if (editCustomer == null) {
        response.sendRedirect(ctx + "/customer/dashboard");
        return;
    }

    String editError = (String) request.getAttribute("error");
%>

<div class="container">
    <div class="form-page">

        <div class="section-title">
            <h1>Edit Profile</h1>
        </div>

        <% if (editError != null) { %>
            <div class="alert alert-danger"><%= editError %></div>
        <% } %>

        <form action="<%= ctx %>/customer/edit-profile" method="post"
              enctype="multipart/form-data" class="admin-form">

            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" class="form-control"
                       value="<%= editCustomer.getName() %>" required>
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" class="form-control"
                       value="<%= editCustomer.getEmail() %>" required>
            </div>

            <div class="form-group">
                <label for="phone">Phone</label>
                <input type="text" id="phone" name="phone" class="form-control"
                       placeholder="+20 100 123 4567"
                       value="<%= editCustomer.getPhone() != null ? editCustomer.getPhone() : "" %>">
            </div>

            <div class="form-group">
                <label for="address">Address</label>
                <textarea id="address" name="address" class="form-control" rows="3"><%=
                    editCustomer.getAddress() != null ? editCustomer.getAddress() : ""
                %></textarea>
            </div>

            <div class="form-group">
                <label for="profilePic">Profile Picture</label>
                <input type="file" id="profilePic" name="profilePic" class="form-control" accept="image/*">
                <p class="form-hint">Leave empty to keep your current picture.</p>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Changes</button>
                <a href="<%= ctx %>/customer/profile" class="btn btn-secondary">Cancel</a>
            </div>

        </form>

    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="nti.models.Customer" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    Customer profileCustomer = (Customer) request.getAttribute("customer");
    if (profileCustomer == null) {
        response.sendRedirect(ctx + "/customer/dashboard");
        return;
    }

    DateTimeFormatter profileFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
%>

<div class="container">

    <div class="section-title">
        <h1>My Profile</h1>
    </div>

    <div class="profile-layout">

        <div class="profile-card">
            <img src="<%= ctx %>/image?type=avatar&id=<%= profileCustomer.getId() %>"
                 alt="<%= profileCustomer.getName() %>"
                 class="profile-avatar"
                 onerror="this.onerror=null; this.src='<%= ctx %>/images/default-avatar.svg';">

            <h2><%= profileCustomer.getName() %></h2>
            <p class="profile-since">
                Member since <%= profileCustomer.getCreatedAt() != null
                    ? profileCustomer.getCreatedAt().format(profileFmt) : "—" %>
            </p>

            <a href="<%= ctx %>/customer/edit-profile" class="btn btn-primary">Edit Profile</a>
        </div>

        <div class="profile-details">
            <div class="summary-row">
                <span>Email</span>
                <span><%= profileCustomer.getEmail() %></span>
            </div>
            <div class="summary-row">
                <span>Phone</span>
                <span><%= profileCustomer.getPhone() != null ? profileCustomer.getPhone() : "—" %></span>
            </div>
            <div class="summary-row">
                <span>Address</span>
                <span><%= profileCustomer.getAddress() != null ? profileCustomer.getAddress() : "—" %></span>
            </div>

            <a href="<%= ctx %>/customer/orders" class="btn btn-secondary btn-block" style="margin-top: 20px;">
                View My Orders
            </a>
        </div>

    </div>

</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

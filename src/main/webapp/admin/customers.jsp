<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="nti.models.Customer" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    if (customers == null) customers = java.util.Collections.emptyList();

    DateTimeFormatter customerFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
%>

<div class="container">
    <div class="admin-layout">

        <%@ include file="/WEB-INF/partials/admin-sidebar.jsp" %>

        <div class="admin-content">

            <div class="section-title">
                <h1>Customers</h1>
                <p>Everyone registered on NexaMart.</p>
            </div>

            <% if (customers.isEmpty()) { %>
                <div class="empty-state">
                    <p>No customers yet.</p>
                </div>
            <% } else { %>
                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Joined</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Customer c : customers) { %>
                                <tr>
                                    <td data-label="Name"><%= c.getName() %></td>
                                    <td data-label="Email"><%= c.getEmail() %></td>
                                    <td data-label="Phone"><%= c.getPhone() != null ? c.getPhone() : "—" %></td>
                                    <td data-label="Joined">
                                        <%= c.getCreatedAt() != null ? c.getCreatedAt().format(customerFmt) : "—" %>
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

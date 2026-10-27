<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String flashError = (String) request.getAttribute("error");
    String flashSuccess = (String) request.getAttribute("success");
%>
<% if (flashError != null && !flashError.isEmpty()) { %>
    <div class="alert alert-danger"><%= flashError %></div>
<% } %>
<% if (flashSuccess != null && !flashSuccess.isEmpty()) { %>
    <div class="alert alert-success"><%= flashSuccess %></div>
<% } %>

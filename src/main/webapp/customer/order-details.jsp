<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctx = request.getContextPath();
    String id = request.getParameter("id");

    if (id == null || id.isEmpty()) {
        response.sendRedirect(ctx + "/customer/orders");
    } else {
        response.sendRedirect(ctx + "/order-confirmation?id=" + id);
    }
%>

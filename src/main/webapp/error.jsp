<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<div class="container">
    <div class="empty-state">
        <div class="empty-state-icon">⚠️</div>
        <h2>Something went wrong</h2>
        <p>We hit an unexpected error. Please try again, or head back to the homepage.</p>
        <a href="<%= ctx %>/index.jsp" class="btn btn-primary btn-lg">Back to NexaMart</a>
    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>

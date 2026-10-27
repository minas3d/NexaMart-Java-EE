<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<%
    String token = request.getParameter("token");
    if (token == null || token.isEmpty()) {
        response.sendRedirect(ctx + "/forgot-password.jsp");
        return;
    }
%>

<div class="auth-wrapper">
    <div class="auth-card">

        <h1>Reset Your Password</h1>
        <p class="auth-subtitle">Choose a new password for your account.</p>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
%>
            <div class="alert alert-danger"><%= error%></div>
        <%
            }
%>

        <form action="<%= ctx%>/reset-password" method="post">

            <input type="hidden" name="token" value="<%= token%>">

            <div class="form-group">
                <label class="form-label" for="newPassword">
                    New Password <span class="required">*</span>
                </label>
                <input type="password" id="newPassword" name="newPassword"
                       class="form-control"
                       placeholder="At least 6 characters" required minlength="6">
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">
                    Confirm New Password <span class="required">*</span>
                </label>
                <input type="password" id="confirmPassword" name="confirmPassword"
                       class="form-control"
                       placeholder="Re-enter new password" required minlength="6">
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg">
                Reset Password
            </button>

        </form>

        <div class="auth-footer">
            <a href="<%= ctx%>/login.jsp">Back to login</a>
        </div>

    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
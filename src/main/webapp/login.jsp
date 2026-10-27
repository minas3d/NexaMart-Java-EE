<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<div class="auth-wrapper">
    <div class="auth-card">

        <h1>Welcome Back</h1>
        <p class="auth-subtitle">Log in to your NexaMart account</p>

        <%
            String registered = request.getParameter("registered");
            String loggedout  = request.getParameter("loggedout");
            String reset      = request.getParameter("reset");
            String error      = (String) request.getAttribute("error");

            if ("1".equals(registered)) {
%>
            <div class="alert alert-success">
                Account created successfully! Please log in.
            </div>
        <%
            } else if ("1".equals(loggedout)) {
%>
            <div class="alert alert-info">
                You have been logged out successfully.
            </div>
        <%
            } else if ("1".equals(reset)) {
%>
            <div class="alert alert-success">
                Password reset successfully! Please log in with your new password.
            </div>
        <%
            } else if (error != null) {
%>
            <div class="alert alert-danger"><%= error%></div>
        <%
            }
%>

        <form action="<%= ctx%>/login" method="post">

            <div class="form-group">
                <label class="form-label" for="identifier">
                    Username or Email <span class="required">*</span>
                </label>
                <input type="text" id="identifier" name="identifier" class="form-control"
                       placeholder="Enter your username or email" required
                       value="<%= request.getParameter("identifier") != null ? request.getParameter("identifier") : ""%>">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">
                    Password <span class="required">*</span>
                </label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your password" required>
                <div style="text-align: right; margin-top: 4px;">
                    <a href="<%= ctx%>/forgot-password"
                       style="font-size: var(--fs-xs);">Forgot password?</a>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg">Log In</button>

        </form>

        <div class="auth-footer">
            Don't have an account? <a href="<%= ctx%>/register.jsp">Register here</a>
        </div>

    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
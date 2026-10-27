<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<div class="auth-wrapper">
    <div class="auth-card">

        <h1>Forgot Password?</h1>
        <p class="auth-subtitle">
            Enter your email address and we'll send you a link to reset your password.
        </p>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
%>
            <div class="alert alert-danger"><%= error%></div>
        <%
            }

            String success = (String) request.getAttribute("success");
            if (success != null) {
%>
            <div class="alert alert-success"><%= success%></div>
        <%
            }
%>

        <form action="<%= ctx%>/forgot-password" method="post">

            <div class="form-group">
                <label class="form-label" for="email">
                    Email <span class="required">*</span>
                </label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="you@example.com" required
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : ""%>">
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg">
                Send Reset Link
            </button>

        </form>

        <div class="auth-footer">
            Remembered your password?
            <a href="<%= ctx%>/login.jsp">Back to login</a>
        </div>

    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/partials/header.jsp" %>

<div class="auth-wrapper">
    <div class="auth-card">

        <h1>Create Account</h1>
        <p class="auth-subtitle">Join NexaMart and start shopping</p>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
%>
            <div class="alert alert-danger"><%= error%></div>
        <%
            }
%>

		<form action="<%= ctx%>/register" method="post" enctype="multipart/form-data">

            <div class="form-group">
                <label class="form-label" for="name">
                    Full Name <span class="required">*</span>
                </label>
                <input type="text" id="name" name="name" class="form-control"
                       placeholder="Your full name" required
                       value="<%= request.getParameter("name") != null ? request.getParameter("name") : ""%>">
            </div>

            <div class="form-group">
                <label class="form-label" for="email">
                    Email <span class="required">*</span>
                </label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="you@example.com" required
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : ""%>">
            </div>

            <div class="form-group">
                <label class="form-label" for="username">
                    Username <span class="required">*</span>
                </label>
                <input type="text" id="username" name="username" class="form-control"
                       placeholder="Choose a username" required
                       value="<%= request.getParameter("username") != null ? request.getParameter("username") : ""%>">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">
                    Password <span class="required">*</span>
                </label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="At least 6 characters" required>

                <div class="password-strength" id="passwordStrength">
                    <div class="password-strength-bar">
                        <div class="password-strength-fill" id="passwordStrengthFill"></div>
                    </div>
                    <span class="password-strength-label" id="passwordStrengthLabel"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">
                    Confirm Password <span class="required">*</span>
                </label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                       placeholder="Re-enter your password" required>
                <span class="form-help" id="passwordMatchMsg"></span>
            </div>

            <div class="form-group">
                <label class="form-label" for="phoneNumber">Phone</label>
                <div class="phone-input">

                    <div class="country-picker" id="countryPicker"
                         data-ctx="<%= ctx%>"
                         data-selected="+20">
                        <input type="text" id="countryCodeInput"
                               class="form-control phone-code"
                               autocomplete="off"
                               placeholder="+20"
                               value="+20">
                        <input type="hidden" id="countryCode" name="countryCode" value="+20">
                        <span class="country-flag" id="countryFlag">
                            <img src="<%= ctx%>/images/flags/eg.svg" alt="flag">
                        </span>
                        <ul class="country-list" id="countryList"></ul>
                    </div>

                    <input type="text" id="phoneNumber" name="phoneNumber" class="form-control"
                           placeholder="1012345678"
                           value="<%= request.getParameter("phoneNumber") != null ? request.getParameter("phoneNumber") : ""%>">
                </div>
                <span class="form-help">Type a code like +20, or pick from the list</span>
            </div>

            <div class="form-group">
                <label class="form-label" for="address">Address</label>
                <textarea id="address" name="address" class="form-control"
                          placeholder="Your address"><%= request.getParameter("address") != null ? request.getParameter("address") : ""%></textarea>
            </div>

            <div class="form-group">
                <label class="form-label">Profile Picture</label>

                <div class="profile-pic-upload">

                    <div class="profile-pic-preview" id="profilePicPreview">
                        <img id="profilePicPreviewImg"
                             src="<%= ctx%>/images/default-avatar.svg"
                             alt="Profile preview">
                    </div>

                    <div class="profile-pic-actions">
                        <label for="profilePic" class="btn btn-secondary btn-sm">Choose File</label>
                        <button type="button" id="profilePicClear"
                                class="btn btn-secondary btn-sm">Clear</button>

                        <input type="file" id="profilePic" name="profilePic"
                               accept="image/png, image/jpeg, image/gif">
                    </div>

                    <span class="form-help">JPG, PNG, or GIF. Max 2 MB.</span>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg">Create Account</button>

        </form>

        <div class="auth-footer">
            Already have an account? <a href="<%= ctx%>/login.jsp">Login here</a>
        </div>

    </div>
</div>

<%@ include file="/WEB-INF/partials/footer.jsp" %>
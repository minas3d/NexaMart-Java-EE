package nti.servlets.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.exceptions.BusinessException;
import nti.services.AuthService;

public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        if (token == null || token.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/forgot-password.jsp");
            return;
        }

        request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String token           = request.getParameter("token");
        String newPassword     = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            authService.resetPassword(token, newPassword, confirmPassword);

            response.sendRedirect(request.getContextPath() + "/login.jsp?reset=1");

        } catch (BusinessException e) {

            request.setAttribute("error", e.getMessage());

            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
        }
    }
}
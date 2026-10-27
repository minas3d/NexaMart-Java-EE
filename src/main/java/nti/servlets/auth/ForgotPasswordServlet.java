package nti.servlets.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.exceptions.BusinessException;
import nti.services.AuthService;

public class ForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");

        try {
            authService.requestPasswordReset(email);

            request.setAttribute("success",
                "If that email is registered, we've sent a reset link. " +
                "Please check your inbox (and spam folder).");

        } catch (BusinessException e) {

            request.setAttribute("error", e.getMessage());
        }

        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }
}
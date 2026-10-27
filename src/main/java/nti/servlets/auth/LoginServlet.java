package nti.servlets.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import nti.exceptions.BusinessException;
import nti.models.Customer;
import nti.models.User;
import nti.services.AuthService;
import nti.services.CartService;

public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String identifier = request.getParameter("identifier");
        String password   = request.getParameter("password");

        try {
            User user = authService.login(identifier, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            if ("CUSTOMER".equals(user.getRole())) {
                Customer customer = authService.findCustomerByUserId(user.getId());
                if (customer != null) {
                    session.setAttribute("customerId", customer.getId());
                    cartService.mergeSessionCartIntoDb(session, customer.getId());
                }
            }

            response.sendRedirect(request.getContextPath() + "/customer/dashboard");
        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
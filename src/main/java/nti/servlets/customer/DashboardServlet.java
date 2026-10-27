package nti.servlets.customer;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import nti.exceptions.BusinessException;
import nti.models.Cart;
import nti.models.Customer;
import nti.models.User;
import nti.services.AuthService;
import nti.services.CartService;

public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!"CUSTOMER".equals(user.getRole())) {

            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Customer customer = null;
        try {
            customer = authService.findCustomerByUserId(user.getId());
        } catch (BusinessException e) {
            e.printStackTrace();

        }

        Cart cart = cartService.getCart(session, user);

        request.setAttribute("customer", customer);
        request.setAttribute("cart",     cart);

        request.getRequestDispatcher("/customer/dashboard.jsp").forward(request, response);
    }
}
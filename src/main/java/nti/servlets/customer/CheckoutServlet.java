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
import nti.models.Order;
import nti.models.User;
import nti.services.AuthService;
import nti.services.CartService;
import nti.services.OrderService;

public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService  authService  = new AuthService();
    private final CartService  cartService  = new CartService();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?next=/checkout");
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
        if (cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        request.setAttribute("customer", customer);
        request.setAttribute("cart",     cart);
        request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?next=/checkout");
            return;
        }
        if (!"CUSTOMER".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Object cidObj = session.getAttribute("customerId");
        Long customerId = (cidObj instanceof Long) ? (Long) cidObj : null;
        if (customerId == null) {
            response.sendRedirect(request.getContextPath() + "/logout");
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone    = request.getParameter("phone");
        String address  = request.getParameter("address");

        try {
            Order order = orderService.placeOrder(
                customerId, fullName, phone, address);

            response.sendRedirect(request.getContextPath()
            	    + "/order-confirmation?id=" + order.getId() + "&new=1");

        } catch (BusinessException e) {

            request.setAttribute("error", e.getMessage());

            try {
                request.setAttribute("customer",
                    authService.findCustomerByUserId(user.getId()));
            } catch (BusinessException ignored) {}

            request.setAttribute("cart", cartService.getCart(session, user));

            request.getRequestDispatcher("/customer/checkout.jsp").forward(request, response);
        }
    }
}
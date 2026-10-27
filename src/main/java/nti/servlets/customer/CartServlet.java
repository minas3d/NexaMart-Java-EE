package nti.servlets.customer;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import nti.exceptions.BusinessException;
import nti.models.Cart;
import nti.models.User;
import nti.services.CartService;

public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");

        Cart cart = cartService.getCart(session, sessionUser);
        request.setAttribute("cart", cart);

        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String idStr  = request.getParameter("productId");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            switch (action) {

                case "increase": {
                    long productId = parseProductId(idStr);
                    if (productId > 0) {
                        Cart cart = cartService.getCart(session, user);
                        int currentQty = findQuantity(cart, productId);
                        cartService.updateQuantity(session, user, productId, currentQty + 1);
                    }
                    break;
                }

                case "decrease": {
                    long productId = parseProductId(idStr);
                    if (productId > 0) {
                        Cart cart = cartService.getCart(session, user);
                        int currentQty = findQuantity(cart, productId);
                        int newQty = currentQty - 1;
                        if (newQty <= 0) {
                            cartService.removeItem(session, user, productId);
                        } else {
                            cartService.updateQuantity(session, user, productId, newQty);
                        }
                    }
                    break;
                }

                case "remove": {
                    long productId = parseProductId(idStr);
                    if (productId > 0) {
                        cartService.removeItem(session, user, productId);
                    }
                    break;
                }

                case "clear": {
                    cartService.clear(session, user);
                    break;
                }

                default:

                    break;
            }

        } catch (BusinessException e) {

            request.setAttribute("error", e.getMessage());
            Cart cart = cartService.getCart(session, user);
            request.setAttribute("cart", cart);
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private long parseProductId(String idStr) {
        if (idStr == null) return 0;
        try {
            return Long.parseLong(idStr.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int findQuantity(Cart cart, long productId) {
        for (nti.models.CartItem item : cart.getItems()) {
            if (item.getProduct().getId() == productId) {
                return item.getQuantity();
            }
        }
        return 0;
    }
}
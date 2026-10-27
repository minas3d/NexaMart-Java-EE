package nti.servlets.customer;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import nti.exceptions.BusinessException;
import nti.models.User;
import nti.services.CartService;

public class AddToCartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CartService cartService = new CartService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String productIdStr = request.getParameter("productId");
        String quantityStr  = request.getParameter("quantity");

        long productId;
        int  quantity;

        try {
            productId = Long.parseLong(productIdStr);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 1) quantity = 1;
        } catch (NumberFormatException | NullPointerException e) {
            quantity = 1;
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        try {
            cartService.addItem(session, user, productId, quantity);
            response.sendRedirect(request.getContextPath()
                + "/product-details?id=" + productId + "&added=1");

        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath()
                + "/product-details?id=" + productId
                + "&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
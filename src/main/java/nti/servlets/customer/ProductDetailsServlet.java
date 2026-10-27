package nti.servlets.customer;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.models.Product;
import nti.services.ProductService;

public class ProductDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Product product = productService.findById(id);

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("product", product);

        request.getRequestDispatcher("/product-details.jsp").forward(request, response);
    }
}
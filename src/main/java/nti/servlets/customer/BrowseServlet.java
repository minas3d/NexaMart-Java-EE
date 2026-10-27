package nti.servlets.customer;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.services.ProductService;

public class BrowseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final int PAGE_SIZE = 12;

    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String q        = request.getParameter("q");
        String category = request.getParameter("category");
        String pageStr  = request.getParameter("page");

        int page = 1;
        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr.trim());
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }

        ProductService.BrowseResult result =
            productService.browse(q, category, page, PAGE_SIZE);

        request.setAttribute("products",      result.getProducts());
        request.setAttribute("categories",    result.getCategories());
        request.setAttribute("totalProducts", result.getTotalCount());
        request.setAttribute("currentPage",   result.getCurrentPage());
        request.setAttribute("totalPages",    result.getTotalPages());
        request.setAttribute("q",             q == null ? "" : q);
        request.setAttribute("selectedCategory", category == null ? "" : category);

        request.getRequestDispatcher("/browse.jsp").forward(request, response);
    }
}
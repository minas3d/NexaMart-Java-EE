package nti.servlets.admin;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import nti.dao.CategoryDAO;
import nti.exceptions.BusinessException;
import nti.models.Product;
import nti.services.AdminService;

public class AdminProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminService();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("new".equals(action)) {
            request.setAttribute("categories", safeCategoryList());
            request.getRequestDispatcher("/admin/product-form.jsp").forward(request, response);
            return;
        }

        if ("edit".equals(action)) {
            long id = parseId(request.getParameter("id"));
            Product product = adminService.getProduct(id);
            if (product == null) {
                request.setAttribute("error", "Product not found.");
                request.setAttribute("products", adminService.listProducts());
                request.getRequestDispatcher("/admin/products.jsp").forward(request, response);
                return;
            }
            request.setAttribute("product", product);
            request.setAttribute("categories", safeCategoryList());
            request.getRequestDispatcher("/admin/product-form.jsp").forward(request, response);
            return;
        }

        request.setAttribute("products", adminService.listProducts());
        request.getRequestDispatcher("/admin/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            long id = parseId(request.getParameter("id"));
            try {
                adminService.deleteProduct(id);
            } catch (BusinessException e) {
                request.setAttribute("error", e.getMessage());
                request.setAttribute("products", adminService.listProducts());
                request.getRequestDispatcher("/admin/products.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(ctx + "/admin/products");
            return;
        }

        String idParam = request.getParameter("id");
        Long id = (idParam == null || idParam.isEmpty()) ? null : Long.valueOf(idParam);

        InputStream imageStream = null;
        int imageSize = 0;
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            imageStream = imagePart.getInputStream();
            imageSize = (int) imagePart.getSize();
        }

        try {
            adminService.saveProduct(
                id,
                request.getParameter("categoryId"),
                request.getParameter("name"),
                request.getParameter("description"),
                request.getParameter("price"),
                request.getParameter("stock"),
                imageStream,
                imageSize
            );
        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("categories", safeCategoryList());
            if (id != null) {
                Product product = adminService.getProduct(id);
                request.setAttribute("product", product);
            }
            request.getRequestDispatcher("/admin/product-form.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(ctx + "/admin/products");
    }

    private java.util.List<nti.models.Category> safeCategoryList() {
        try {
            return categoryDAO.findAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    private long parseId(String raw) {
        try {
            return Long.parseLong(raw);
        } catch (Exception e) {
            return -1;
        }
    }
}

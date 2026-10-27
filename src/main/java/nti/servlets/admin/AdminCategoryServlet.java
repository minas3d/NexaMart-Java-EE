package nti.servlets.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.exceptions.BusinessException;
import nti.services.AdminService;

public class AdminCategoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("categories", adminService.listCategories());
        request.getRequestDispatcher("/admin/categories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");
        Long id = (idParam == null || idParam.isEmpty()) ? null : Long.valueOf(idParam);

        if ("delete".equals(action)) {
            try {
                adminService.deleteCategory(id);
            } catch (BusinessException e) {
                request.setAttribute("error", e.getMessage());
                request.setAttribute("categories", adminService.listCategories());
                request.getRequestDispatcher("/admin/categories.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(ctx + "/admin/categories");
            return;
        }

        try {
            adminService.saveCategory(id, request.getParameter("name"),
                request.getParameter("description"));
        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("categories", adminService.listCategories());
            request.getRequestDispatcher("/admin/categories.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(ctx + "/admin/categories");
    }
}

package nti.servlets.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.services.AdminService;

public class AdminCustomerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("customers", adminService.listCustomers());
        request.getRequestDispatcher("/admin/customers.jsp").forward(request, response);
    }
}

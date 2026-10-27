package nti.servlets.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.dao.CustomerDAO;
import nti.exceptions.BusinessException;
import nti.models.Customer;
import nti.models.Order;
import nti.services.AdminService;

public class AdminOrderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminService();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            long id = Long.parseLong(idParam);
            Order order = adminService.getOrderWithItems(id);
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }
            request.setAttribute("order", order);
            request.setAttribute("statuses", AdminService.ORDER_STATUSES);
            request.setAttribute("customer", findCustomerSafe(order.getCustomerId()));
            request.getRequestDispatcher("/admin/order-details.jsp").forward(request, response);
            return;
        }

        request.setAttribute("orders", adminService.listOrders());
        request.getRequestDispatcher("/admin/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        long id = Long.parseLong(request.getParameter("id"));
        String status = request.getParameter("status");

        try {
            adminService.updateOrderStatus(id, status);
        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("order", adminService.getOrderWithItems(id));
            request.setAttribute("statuses", AdminService.ORDER_STATUSES);
            request.getRequestDispatcher("/admin/order-details.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(ctx + "/admin/orders?id=" + id);
    }

    private Customer findCustomerSafe(long customerId) {
        try {
            return customerDAO.findById(customerId);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

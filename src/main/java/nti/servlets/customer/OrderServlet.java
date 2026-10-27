package nti.servlets.customer;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OrderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        String ctx = request.getContextPath();

        if (id == null || id.isEmpty()) {
            response.sendRedirect(ctx + "/customer/orders");
            return;
        }

        response.sendRedirect(ctx + "/order-confirmation?id=" + id);
    }
}

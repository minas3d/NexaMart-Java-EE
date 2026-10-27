package nti.servlets.customer;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import nti.exceptions.BusinessException;
import nti.models.Customer;
import nti.models.User;
import nti.services.AuthService;

public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Customer customer = null;
        try {
            customer = authService.findCustomerByUserId(user.getId());
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        request.setAttribute("customer", customer);

        if ("/customer/edit-profile".equals(request.getServletPath())) {
            request.getRequestDispatcher("/customer/edit-profile.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/customer/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Customer customer;
        try {
            customer = authService.findCustomerByUserId(user.getId());
        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/customer/edit-profile.jsp").forward(request, response);
            return;
        }

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            return;
        }

        InputStream picStream = null;
        int picSize = 0;
        Part picPart = request.getPart("profilePic");
        if (picPart != null && picPart.getSize() > 0) {
            picStream = picPart.getInputStream();
            picSize = (int) picPart.getSize();
        }

        try {
            authService.updateProfile(
                customer.getId(),
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("phone"),
                request.getParameter("address"),
                picStream,
                picSize
            );
        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/customer/edit-profile.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/customer/profile");
    }
}

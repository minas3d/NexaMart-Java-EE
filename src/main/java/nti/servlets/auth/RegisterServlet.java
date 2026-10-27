package nti.servlets.auth;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import nti.exceptions.BusinessException;
import nti.services.AuthService;

public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name            = request.getParameter("name");
        String email           = request.getParameter("email");
        String username        = request.getParameter("username");
        String password        = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String address         = request.getParameter("address");
        String countryCode     = request.getParameter("countryCode");
        String phoneNumber     = request.getParameter("phoneNumber");

        String phone = combinePhone(countryCode, phoneNumber);

        InputStream picStream = null;
        int picSize = 0;
        try {
            Part picPart = request.getPart("profilePic");
            if (picPart != null && picPart.getSize() > 0) {
                picSize   = (int) picPart.getSize();
                picStream = picPart.getInputStream();
            }
        } catch (IllegalStateException e) {
            request.setAttribute("error",
                "Image is too large. Maximum size is 2 MB.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            authService.register(
                name, email, username,
                password, confirmPassword,
                phone, address,
                picStream, picSize
            );

            response.sendRedirect(request.getContextPath() + "/login.jsp?registered=1");

        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);

        } finally {
            if (picStream != null) {
                try { picStream.close(); } catch (IOException ignored) {}
            }
        }
    }

    private String combinePhone(String countryCode, String phoneNumber) {
        if (phoneNumber == null) return null;

        String num = phoneNumber.trim();
        if (num.isEmpty()) return null;

        String code = (countryCode == null || countryCode.trim().isEmpty())
                ? "+20"
                : countryCode.trim();

        return code + " " + num;
    }
}
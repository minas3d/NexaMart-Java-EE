package nti.servlets.customer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        String encoded = "";
        if (q != null && !q.isEmpty()) {
            try {
                encoded = "?q=" + URLEncoder.encode(q, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                encoded = "";
            }
        }

        response.sendRedirect(request.getContextPath() + "/browse" + encoded);
    }
}

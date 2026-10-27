package nti.servlets.common;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import nti.dao.CustomerDAO;
import nti.dao.ProductDAO;

public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int CACHE_SECONDS = 86400;

    private final ProductDAO  productDAO  = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String idStr = request.getParameter("id");

        if (type == null || idStr == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] bytes = null;
        try {
            if ("product".equals(type)) {
                bytes = productDAO.findImageById(id);
            } else if ("avatar".equals(type)) {
                bytes = customerDAO.findPicById(id);
            } else {

                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (bytes == null || bytes.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(detectContentType(bytes));
        response.setContentLength(bytes.length);
        response.setHeader("Cache-Control", "public, max-age=" + CACHE_SECONDS);

        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(bytes);
        }
    }

    private String detectContentType(byte[] bytes) {

        if (bytes.length < 4) return "application/octet-stream";

        if ((bytes[0] & 0xFF) == 0xFF
         && (bytes[1] & 0xFF) == 0xD8
         && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }

        if ((bytes[0] & 0xFF) == 0x89
         && bytes[1] == 'P'
         && bytes[2] == 'N'
         && bytes[3] == 'G') {
            return "image/png";
        }

        if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            return "image/gif";
        }

        if (bytes.length >= 12
         && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
         && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "image/webp";
        }

        return "application/octet-stream";
    }
}
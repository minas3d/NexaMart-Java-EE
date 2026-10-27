package nti.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import nti.dao.CartItemDAO;
import nti.dao.OrderDAO;
import nti.dao.OrderItemDAO;
import nti.dao.ProductDAO;
import nti.exceptions.BusinessException;
import nti.models.CartItem;
import nti.models.Order;
import nti.models.OrderItem;
import nti.models.Product;
import nti.utils.DBConnection;

public class OrderService {

    private final ProductDAO    productDAO    = new ProductDAO();
    private final CartItemDAO   cartItemDAO   = new CartItemDAO();
    private final OrderDAO      orderDAO      = new OrderDAO();
    private final OrderItemDAO  orderItemDAO  = new OrderItemDAO();

    public Order placeOrder(long customerId,
                            String fullName,
                            String phone,
                            String address) throws BusinessException {

        fullName = trimToNull(fullName);
        phone    = trimToNull(phone);
        address  = trimToNull(address);

        if (fullName == null) {
            throw new BusinessException("Please enter your full name.");
        }
        if (phone == null) {
            throw new BusinessException("Please enter your phone number.");
        }
        if (address == null) {
            throw new BusinessException("Please enter your shipping address.");
        }

        List<CartItem> items;
        try {
            items = cartItemDAO.findByCustomer(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException(
                "Could not load your cart. Please try again.", e);
        }

        if (items.isEmpty()) {
            throw new BusinessException("Your cart is empty.");
        }

        items.sort(Comparator.comparing(ci -> ci.getProduct().getId()));

        double total = 0;
        for (CartItem ci : items) {
            total += ci.getSubtotal();
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            for (CartItem ci : items) {
                Product p = ci.getProduct();
                int qty = ci.getQuantity();

                int rows = productDAO.decrementStock(conn, p.getId(), qty);
                if (rows == 0) {
                    throw new BusinessException(
                        "Sorry, out of stock: " + p.getName());
                }
            }

            Order order = new Order();
            order.setCustomerId(customerId);
            order.setTotalAmount(total);
            order.setStatus("PENDING");

            long orderId = orderDAO.insert(order, conn);

            for (CartItem ci : items) {
                OrderItem oi = new OrderItem();
                oi.setOrderId(orderId);
                oi.setProductId(ci.getProduct().getId());
                oi.setQuantity(ci.getQuantity());
                oi.setPrice(ci.getProduct().getPrice());
                orderItemDAO.insert(oi, conn);
            }

            cartItemDAO.deleteByCustomer(customerId, conn);

            conn.commit();

            return order;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            throw new BusinessException(
                "Could not place your order. Please try again.", e);

        } catch (BusinessException e) {

            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public List<Order> getOrdersForCustomer(long customerId) {
        try {
            return orderDAO.findByCustomer(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Order getOrderWithItems(long orderId, long customerId) {

        try {
            Order order = orderDAO.findById(orderId);

            if (order == null || order.getCustomerId() != customerId) {
                return null;
            }

            List<OrderItem> items = orderItemDAO.findByOrderId(orderId);
            order.setItems(items);

            return order;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Order getOrderById(long orderId) {
        try {
            Order order = orderDAO.findById(orderId);
            if (order == null) return null;

            List<OrderItem> items = orderItemDAO.findByOrderId(orderId);
            order.setItems(items);

            return order;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
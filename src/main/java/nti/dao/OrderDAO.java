package nti.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import nti.models.Order;
import nti.utils.DBConnection;

public class OrderDAO {

    public long insert(Order order, Connection conn) throws SQLException {

        String sql = "INSERT INTO orders (customer_id, total_amount, status) " +
                     "VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, order.getCustomerId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getStatus());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    order.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating order failed, no ID returned.");
                }
            }
        }
    }

    public Order findById(long id) throws SQLException {

        String sql = "SELECT id, customer_id, order_date, total_amount, status " +
                     "FROM orders WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        }
    }

    public List<Order> findByCustomer(long customerId) throws SQLException {

        String sql = "SELECT id, customer_id, order_date, total_amount, status " +
                     "FROM orders WHERE customer_id = ? " +
                     "ORDER BY order_date DESC, id DESC";

        List<Order> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public List<Order> findAll() throws SQLException {

        String sql = "SELECT id, customer_id, order_date, total_amount, status " +
                     "FROM orders ORDER BY order_date DESC, id DESC";

        List<Order> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    public boolean updateStatus(long id, String status) throws SQLException {

        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setCustomerId(rs.getLong("customer_id"));

        Timestamp ts = rs.getTimestamp("order_date");
        if (ts != null) o.setOrderDate(ts.toLocalDateTime());

        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));

        return o;
    }
}
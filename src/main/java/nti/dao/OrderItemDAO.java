package nti.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nti.models.OrderItem;
import nti.models.Product;
import nti.utils.DBConnection;

public class OrderItemDAO {

    public void insert(OrderItem item, Connection conn) throws SQLException {

        String sql = "INSERT INTO order_items " +
                     "(order_id, product_id, quantity, price) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());

            ps.executeUpdate();
        }
    }

    public List<OrderItem> findByOrderId(long orderId) throws SQLException {

        String sql =
            "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.price, " +
            "       p.name, p.category_id, p.description, p.stock " +
            "FROM order_items oi " +
            "JOIN products p ON p.id = oi.product_id " +
            "WHERE oi.order_id = ? " +
            "ORDER BY oi.id";

        List<OrderItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowWithProduct(rs));
                }
            }
        }

        return list;
    }

    private OrderItem mapRowWithProduct(ResultSet rs) throws SQLException {

        Product p = new Product();
        p.setId(rs.getLong("product_id"));
        p.setName(rs.getString("name"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setDescription(rs.getString("description"));
        p.setStock(rs.getInt("stock"));

        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPrice(rs.getDouble("price"));
        item.setProduct(p);

        return item;
    }
}
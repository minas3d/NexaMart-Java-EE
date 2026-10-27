package nti.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nti.models.CartItem;
import nti.models.Product;
import nti.utils.DBConnection;

public class CartItemDAO {

    public List<CartItem> findByCustomer(long customerId) throws SQLException {

        String sql =
            "SELECT ci.quantity, " +
            "       p.id, p.category_id, p.name, p.description, p.price, p.stock " +
            "FROM cart_items ci " +
            "JOIN products p ON p.id = ci.product_id " +
            "WHERE ci.customer_id = ? " +
            "ORDER BY ci.added_at";

        List<CartItem> list = new ArrayList<>();

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

    public void insert(long customerId, long productId, int quantity)
            throws SQLException {

        String sql = "INSERT INTO cart_items (customer_id, product_id, quantity) " +
                     "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);

            ps.executeUpdate();
        }
    }

    public void addOrIncrement(long customerId, long productId, int quantity)
            throws SQLException {

        String sql =
            "INSERT INTO cart_items (customer_id, product_id, quantity) " +
            "VALUES (?, ?, ?) " +
            "ON CONFLICT (customer_id, product_id) " +
            "DO UPDATE SET quantity = cart_items.quantity + EXCLUDED.quantity";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);

            ps.executeUpdate();
        }
    }

    public boolean updateQuantity(long customerId, long productId, int quantity)
            throws SQLException {

        String sql = "UPDATE cart_items SET quantity = ? " +
                     "WHERE customer_id = ? AND product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setLong(2, customerId);
            ps.setLong(3, productId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long customerId, long productId) throws SQLException {

        String sql = "DELETE FROM cart_items " +
                     "WHERE customer_id = ? AND product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ps.setLong(2, productId);

            return ps.executeUpdate() > 0;
        }
    }

    public void deleteByCustomer(long customerId) throws SQLException {

        String sql = "DELETE FROM cart_items WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ps.executeUpdate();
        }
    }

    private CartItem mapRow(ResultSet rs) throws SQLException {

        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));

        CartItem item = new CartItem();
        item.setProduct(p);
        item.setQuantity(rs.getInt("quantity"));

        return item;
    }

    public void deleteByCustomer(long customerId, Connection conn)
            throws SQLException {

        String sql = "DELETE FROM cart_items WHERE customer_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.executeUpdate();
        }
    }
}
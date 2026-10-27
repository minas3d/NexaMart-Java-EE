package nti.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import nti.models.Product;
import nti.utils.DBConnection;

public class ProductDAO {

    public List<Product> findPage(String q,
                                  Long categoryId,
                                  int page,
                                  int pageSize) throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT id, category_id, name, description, price, stock " +
            "FROM products WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isEmpty()) {
            sql.append("AND name ILIKE ? ");
            params.add("%" + q + "%");
        }

        if (categoryId != null) {
            sql.append("AND category_id = ? ");
            params.add(categoryId);
        }

        sql.append("ORDER BY id DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public int countFiltered(String q, Long categoryId) throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM products WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isEmpty()) {
            sql.append("AND name ILIKE ? ");
            params.add("%" + q + "%");
        }

        if (categoryId != null) {
            sql.append("AND category_id = ? ");
            params.add(categoryId);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    public Product findByIdWithCategory(long id) throws SQLException {

        String sql =
            "SELECT p.id, p.category_id, p.name, p.description, " +
            "       p.price, p.stock, c.name AS category_name " +
            "FROM products p " +
            "LEFT JOIN categories c ON c.id = p.category_id " +
            "WHERE p.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowWithCategory(rs);
                }
                return null;
            }
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));

        return p;
    }

    private Product mapRowWithCategory(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));

        p.setCategoryName(rs.getString("category_name"));

        return p;
    }

    public byte[] findImageById(long id) throws SQLException {

        String sql = "SELECT image FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("image");
                }
                return null;
            }
        }
    }

    public int decrementStock(Connection conn, long productId, int qty)
            throws SQLException {

        String sql = "UPDATE products " +
                     "SET stock = stock - ? " +
                     "WHERE id = ? AND stock >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setLong(2, productId);
            ps.setInt(3, qty);
            return ps.executeUpdate();
        }
    }

    public List<Product> findAll() throws SQLException {

        String sql =
            "SELECT p.id, p.category_id, p.name, p.description, p.price, p.stock, " +
            "       c.name AS category_name " +
            "FROM products p " +
            "LEFT JOIN categories c ON c.id = p.category_id " +
            "ORDER BY p.id DESC";

        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowWithCategory(rs));
            }
        }

        return list;
    }

    public long insert(Product product, java.io.InputStream imageStream, int imageSize)
            throws SQLException {

        String sql = "INSERT INTO products " +
                     "(category_id, name, description, price, stock, image) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindProduct(ps, product, imageStream, imageSize);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    product.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating product failed, no ID returned.");
                }
            }
        }
    }

    public boolean update(Product product, java.io.InputStream imageStream, int imageSize)
            throws SQLException {

        boolean replaceImage = imageStream != null && imageSize > 0;

        String sql = replaceImage
            ? "UPDATE products SET category_id = ?, name = ?, description = ?, " +
              "price = ?, stock = ?, image = ? WHERE id = ?"
            : "UPDATE products SET category_id = ?, name = ?, description = ?, " +
              "price = ?, stock = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, product.getCategoryId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStock());

            if (replaceImage) {
                ps.setBinaryStream(6, imageStream, imageSize);
                ps.setLong(7, product.getId());
            } else {
                ps.setLong(6, product.getId());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {

        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void bindProduct(PreparedStatement ps, Product product,
                             java.io.InputStream imageStream, int imageSize)
            throws SQLException {

        ps.setLong(1, product.getCategoryId());
        ps.setString(2, product.getName());
        ps.setString(3, product.getDescription());
        ps.setDouble(4, product.getPrice());
        ps.setInt(5, product.getStock());

        if (imageStream != null && imageSize > 0) {
            ps.setBinaryStream(6, imageStream, imageSize);
        } else {
            ps.setNull(6, java.sql.Types.BINARY);
        }
    }
}
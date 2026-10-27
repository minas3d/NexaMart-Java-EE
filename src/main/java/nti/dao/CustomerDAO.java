package nti.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import nti.models.Customer;
import nti.utils.DBConnection;

public class CustomerDAO {

    public long insert(Customer customer, InputStream picStream, int picSize)
            throws SQLException {

        try (Connection conn = DBConnection.getConnection()) {
            return insert(customer, picStream, picSize, conn);
        }
    }

    public long insert(Customer customer,
                       InputStream picStream,
                       int picSize,
                       Connection conn) throws SQLException {

        String sql = "INSERT INTO customers " +
                     "(user_id, name, email, phone, address, profile_pic) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, customer.getUserId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getAddress());

            if (picStream != null && picSize > 0) {
                ps.setBinaryStream(6, picStream, picSize);
            } else {
                ps.setNull(6, Types.BINARY);
            }

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    customer.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating customer failed, no ID returned.");
                }
            }
        }
    }

    public Customer findById(long id) throws SQLException {
        String sql = "SELECT id, user_id, name, email, phone, address, " +
                     "profile_pic, created_at " +
                     "FROM customers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        }
    }

    public Customer findByUserId(long userId) throws SQLException {
        String sql = "SELECT id, user_id, name, email, phone, address, " +
                     "profile_pic, created_at " +
                     "FROM customers WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        }
    }

    public Customer findByEmail(String email) throws SQLException {
        String sql = "SELECT id, user_id, name, email, phone, address, " +
                     "profile_pic, created_at " +
                     "FROM customers WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        }
    }

    public byte[] findPicById(long customerId) throws SQLException {
        String sql = "SELECT profile_pic FROM customers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("profile_pic");
                }
                return null;
            }
        }
    }

    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, email = ?, " +
                     "phone = ?, address = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.setLong(5, customer.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProfilePic(long customerId, InputStream picStream, int picSize)
            throws SQLException {

        String sql = "UPDATE customers SET profile_pic = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (picStream != null && picSize > 0) {
                ps.setBinaryStream(1, picStream, picSize);
            } else {
                ps.setNull(1, Types.BINARY);
            }
            ps.setLong(2, customerId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT id, user_id, name, email, phone, address, " +
                     "profile_pic, created_at " +
                     "FROM customers ORDER BY created_at DESC, id DESC";

        List<Customer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getLong("id"));
        c.setUserId(rs.getLong("user_id"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        c.setProfilePic(rs.getBytes("profile_pic"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            c.setCreatedAt(ts.toLocalDateTime());
        }
        return c;
    }
}
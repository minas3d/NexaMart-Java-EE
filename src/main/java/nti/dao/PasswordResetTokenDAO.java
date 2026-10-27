package nti.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import nti.models.PasswordResetToken;
import nti.utils.DBConnection;

public class PasswordResetTokenDAO {

    public long insert(PasswordResetToken token) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens " +
                     "(user_id, token, expires_at) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, token.getUserId());
            ps.setString(2, token.getToken());
            ps.setTimestamp(3, Timestamp.valueOf(token.getExpiresAt()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating token failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    token.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating token failed, no ID returned.");
                }
            }
        }
    }

    public PasswordResetToken findByToken(String token) throws SQLException {
        String sql = "SELECT id, user_id, token, expires_at, created_at " +
                     "FROM password_reset_tokens WHERE token = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public boolean deleteByToken(String token) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE token = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);
            return ps.executeUpdate() > 0;
        }
    }

    private PasswordResetToken mapRow(ResultSet rs) throws SQLException {
        PasswordResetToken t = new PasswordResetToken();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getLong("user_id"));
        t.setToken(rs.getString("token"));

        java.sql.Timestamp exp = rs.getTimestamp("expires_at");
        if (exp != null) t.setExpiresAt(exp.toLocalDateTime());

        java.sql.Timestamp cre = rs.getTimestamp("created_at");
        if (cre != null) t.setCreatedAt(cre.toLocalDateTime());

        return t;
    }
}
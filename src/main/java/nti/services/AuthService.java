package nti.services;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import nti.dao.CustomerDAO;
import nti.dao.UserDAO;
import nti.exceptions.BusinessException;
import nti.models.Customer;
import nti.models.User;
import nti.utils.DBConnection;
import nti.utils.PasswordUtil;
import java.time.LocalDateTime;
import nti.dao.PasswordResetTokenDAO;
import nti.models.PasswordResetToken;
import nti.utils.ConfigUtil;
import nti.utils.MailUtil;
import nti.utils.TokenUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PasswordResetTokenDAO resetTokenDAO = new PasswordResetTokenDAO();

    public void register(String name,
                         String email,
                         String username,
                         String password,
                         String confirmPassword,
                         String phone,
                         String address,
                         InputStream picStream,
                         int picSize) throws BusinessException {

        name     = trimToNull(name);
        email    = trimToNull(email);
        username = trimToNull(username);
        phone    = trimToNull(phone);
        address  = trimToNull(address);

        validateRegistration(name, email, username,
                             password, confirmPassword,
                             phone, address);

        try {
            if (userDAO.existsByUsername(username)) {
                throw new BusinessException("Username is already taken.");
            }
            if (userDAO.existsByEmail(email)) {
                throw new BusinessException("Email is already registered.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException(
                "Could not verify account details. Please try again.", e);
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            User user = new User();
            user.setUsername(username);
            user.setPassword(PasswordUtil.hash(password));
            user.setRole("CUSTOMER");

            long userId = userDAO.insert(user, conn);

            Customer customer = new Customer();
            customer.setUserId(userId);
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setAddress(address);

            customerDAO.insert(customer, picStream, picSize, conn);

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            throw new BusinessException(
                "Could not create your account. Please try again.", e);

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    private void validateRegistration(String name,
                                      String email,
                                      String username,
                                      String password,
                                      String confirmPassword,
                                      String phone,
                                      String address) throws BusinessException {

        if (name == null) {
            throw new BusinessException("Full name is required.");
        }
        if (name.length() < 2 || name.length() > 100) {
            throw new BusinessException(
                "Full name must be between 2 and 100 characters.");
        }

        if (email == null) {
            throw new BusinessException("Email is required.");
        }
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("Please enter a valid email address.");
        }

        if (username == null) {
            throw new BusinessException("Username is required.");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new BusinessException(
                "Username must be between 3 and 50 characters.");
        }
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new BusinessException(
                "Username can only contain letters, numbers, and underscore.");
        }

        if (password == null || password.isEmpty()) {
            throw new BusinessException("Password is required.");
        }
        if (password.length() < 6) {
            throw new BusinessException(
                "Password must be at least 6 characters.");
        }
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            throw new BusinessException("Passwords do not match.");
        }

        if (phone != null) {
            if (!phone.matches("^\\+[0-9]{1,4}\\s[0-9\\-\\s]{5,20}$")) {
                throw new BusinessException("Phone number format is invalid.");
            }
        }

        if (address != null && address.length() > 500) {
            throw new BusinessException(
                "Address is too long (max 500 characters).");
        }
    }

    public User login(String identifier, String password) throws BusinessException {

        identifier = trimToNull(identifier);

        if (identifier == null) {
            throw new BusinessException("Please enter your username or email.");
        }
        if (password == null || password.isEmpty()) {
            throw new BusinessException("Please enter your password.");
        }

        User user;
        try {
            user = userDAO.findByUsername(identifier);
            if (user == null) {
                user = userDAO.findByEmail(identifier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException(
                "Could not process login. Please try again.", e);
        }

        if (user == null) {
            throw new BusinessException("Invalid username or password.");
        }

        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new BusinessException("Invalid username or password.");
        }

        return user;
    }

    public void requestPasswordReset(String email) throws BusinessException {

        email = trimToNull(email);
        if (email != null) {
            email = email.toLowerCase();
        }

        if (email == null) {
            throw new BusinessException("Please enter your email address.");
        }
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("Please enter a valid email address.");
        }

        User user;
        try {
            user = userDAO.findByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException(
                "Could not process your request. Please try again.", e);
        }

        if (user == null) {
            return;
        }

        String token = TokenUtil.generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        PasswordResetToken prt = new PasswordResetToken(
            user.getId(), token, expiresAt);

        try {
            resetTokenDAO.insert(prt);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException(
                "Could not process your request. Please try again.", e);
        }

        String link = ConfigUtil.getAppBaseUrl()
                      + "/reset-password?token=" + token;

        String subject = "Reset your NexaMart password";
        String body =
            "Hello " + user.getUsername() + ",\n\n" +
            "We received a request to reset your password.\n\n" +
            "Click the link below to choose a new password:\n\n" +
            link + "\n\n" +
            "This link expires in 1 hour. If you did not request this, you can safely ignore this email.\n\n" +
            "— NexaMart";

        MailUtil.sendAsync(email, subject, body);
    }

    public void resetPassword(String token,
                              String newPassword,
                              String confirmPassword) throws BusinessException {

        token = trimToNull(token);

        if (token == null) {
            throw new BusinessException("Reset link is invalid.");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException("Password is required.");
        }
        if (newPassword.length() < 6) {
            throw new BusinessException("Password must be at least 6 characters.");
        }
        if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
            throw new BusinessException("Passwords do not match.");
        }

        PasswordResetToken prt;
        try {
            prt = resetTokenDAO.findByToken(token);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException(
                "Could not process your request. Please try again.", e);
        }

        if (prt == null) {
            throw new BusinessException("Reset link is invalid or has been used.");
        }

        if (prt.isExpired()) {

            try { resetTokenDAO.deleteByToken(token); } catch (SQLException ignored) {}
            throw new BusinessException(
                "Reset link has expired. Please request a new one.");
        }

        String newHash = PasswordUtil.hash(newPassword);

        try {
            userDAO.updatePassword(prt.getUserId(), newHash);
            resetTokenDAO.deleteByToken(token);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException(
                "Could not reset your password. Please try again.", e);
        }
    }

    public Customer findCustomerByUserId(long userId) throws BusinessException {
        try {
            return customerDAO.findByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not load customer profile.", e);
        }
    }

    public void updateProfile(long customerId,
                              String name,
                              String email,
                              String phone,
                              String address,
                              InputStream picStream,
                              int picSize) throws BusinessException {

        name    = trimToNull(name);
        email   = trimToNull(email);
        phone   = trimToNull(phone);
        address = trimToNull(address);

        if (name == null || name.length() < 2 || name.length() > 100) {
            throw new BusinessException("Full name must be between 2 and 100 characters.");
        }
        if (email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("Please enter a valid email address.");
        }
        if (phone != null && !phone.matches("^\\+[0-9]{1,4}\\s[0-9\\-\\s]{5,20}$")) {
            throw new BusinessException("Phone number format is invalid.");
        }
        if (address != null && address.length() > 500) {
            throw new BusinessException("Address is too long (max 500 characters).");
        }

        try {
            Customer existing = customerDAO.findByEmail(email);
            if (existing != null && existing.getId() != customerId) {
                throw new BusinessException("Email is already registered to another account.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not verify account details. Please try again.", e);
        }

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);

        try {
            customerDAO.update(customer);
            if (picStream != null && picSize > 0) {
                customerDAO.updateProfilePic(customerId, picStream, picSize);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not update your profile. Please try again.", e);
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
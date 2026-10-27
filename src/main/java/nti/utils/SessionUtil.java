package nti.utils;

import jakarta.servlet.http.HttpSession;

import nti.models.User;

public class SessionUtil {

    public static User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        Object user = session.getAttribute("user");
        return (user instanceof User) ? (User) user : null;
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public static boolean isAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public static Long getCustomerId(HttpSession session) {
        if (session == null) return null;
        Object cid = session.getAttribute("customerId");
        return (cid instanceof Long) ? (Long) cid : null;
    }
}

package nti.utils;

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^\\+[0-9]{1,4}\\s[0-9\\-\\s]{5,20}$";

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches(PHONE_REGEX);
    }

    public static boolean isPositiveNumber(String s) {
        if (isBlank(s)) return false;
        try {
            return Double.parseDouble(s.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

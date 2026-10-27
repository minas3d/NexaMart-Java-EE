package nti.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import nti.utils.ConfigUtil;
import nti.utils.DBConnection;
import java.io.InputStream;
import java.util.Properties;
import nti.utils.MailUtil;
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== App Starting ===");
        DBConnection.init(sce.getServletContext());
        System.out.println("=== HikariCP Pool Initialized ===");

        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("mail.properties")) {
            if (in == null) {
                System.err.println("mail.properties not found on classpath");
            } else {
                Properties mailProps = new Properties();
                mailProps.load(in);
                MailUtil.init(mailProps);
                ConfigUtil.setAppBaseUrl(mailProps.getProperty("app.base.url"));
                System.out.println("=== MailUtil Initialized ===");
            }
        } catch (Exception e) {
            System.err.println("Failed to load mail.properties: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== App Shutting Down ===");
        DBConnection.shutdown();
        System.out.println("=== HikariCP Pool Closed ===");
    }
}

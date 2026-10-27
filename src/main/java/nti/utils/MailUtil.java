package nti.utils;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class MailUtil {

    private static String host;
    private static int    port;
    private static String username;
    private static String password;
    private static String fromAddress;
    private static boolean auth;
    private static boolean starttls;

    public static void init(Properties config) {
        host        = config.getProperty("mail.smtp.host");
        port        = Integer.parseInt(config.getProperty("mail.smtp.port", "587"));
        username    = config.getProperty("mail.smtp.username");
        password    = config.getProperty("mail.smtp.password");
        fromAddress = config.getProperty("mail.from");
        auth        = Boolean.parseBoolean(config.getProperty("mail.smtp.auth", "true"));
        starttls    = Boolean.parseBoolean(config.getProperty("mail.smtp.starttls", "true"));
    }

    public static void send(String to, String subject, String textBody)
            throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.starttls.enable", String.valueOf(starttls));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromAddress));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject, "UTF-8");
        msg.setText(textBody, "UTF-8");

        Transport.send(msg);
    }

    public static void sendAsync(final String to, final String subject, final String body) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    send(to, subject, body);
                } catch (MessagingException e) {
                    System.err.println("Failed to send email to " + to + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
package nti.utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletContext;

public class DBConnection {
	 private static HikariDataSource dataSource;

	    public static void init(ServletContext ctx) {
	        HikariConfig config = new HikariConfig();

	        config.setJdbcUrl(ctx.getInitParameter("db.url"));
	        config.setUsername(ctx.getInitParameter("db.username"));
	        config.setPassword(ctx.getInitParameter("db.password"));
	        config.setDriverClassName(ctx.getInitParameter("db.driverClassName"));

	        config.setMaximumPoolSize(
	            Integer.parseInt(ctx.getInitParameter("db.pool.maximumPoolSize")));
	        config.setMinimumIdle(
	            Integer.parseInt(ctx.getInitParameter("db.pool.minimumIdle")));
	        config.setConnectionTimeout(
	            Long.parseLong(ctx.getInitParameter("db.pool.connectionTimeout")));
	        config.setIdleTimeout(
	            Long.parseLong(ctx.getInitParameter("db.pool.idleTimeout")));
	        config.setMaxLifetime(
	            Long.parseLong(ctx.getInitParameter("db.pool.maxLifetime")));
	        config.setPoolName(
	            ctx.getInitParameter("db.pool.poolName"));

	        dataSource = new HikariDataSource(config);
	    }

	    public static Connection getConnection() throws SQLException {
	        if (dataSource == null) {
	            throw new IllegalStateException("DBConnection not initialized");
	        }
	        return dataSource.getConnection();
	    }

	    public static void shutdown() {
	        if (dataSource != null && !dataSource.isClosed()) {
	            dataSource.close();
	        }
	    }
}

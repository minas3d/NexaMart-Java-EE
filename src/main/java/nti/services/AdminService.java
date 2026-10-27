package nti.services;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nti.dao.CategoryDAO;
import nti.dao.CustomerDAO;
import nti.dao.OrderDAO;
import nti.dao.OrderItemDAO;
import nti.dao.ProductDAO;
import nti.exceptions.BusinessException;
import nti.models.Category;
import nti.models.Customer;
import nti.models.Order;
import nti.models.Product;
import nti.utils.ValidationUtil;

public class AdminService {

    public static final List<String> ORDER_STATUSES =
        List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public DashboardStats getDashboardStats() {
        List<Product> products = safeList(() -> productDAO.findAll());
        List<Category> categories = safeList(() -> categoryDAO.findAll());
        List<Order> orders = safeList(() -> orderDAO.findAll());
        List<Customer> customers = safeList(() -> customerDAO.findAll());

        double revenue = 0;
        int pending = 0;
        for (Order o : orders) {
            revenue += o.getTotalAmount();
            if ("PENDING".equals(o.getStatus())) pending++;
        }

        return new DashboardStats(
            products.size(), categories.size(), orders.size(),
            customers.size(), pending, revenue);
    }

    public List<Product> listProducts() {
        return safeList(() -> productDAO.findAll());
    }

    public Product getProduct(long id) {
        try {
            return productDAO.findByIdWithCategory(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveProduct(Long id, String categoryIdStr, String name, String description,
                            String priceStr, String stockStr,
                            InputStream imageStream, int imageSize) throws BusinessException {

        if (ValidationUtil.isBlank(name)) {
            throw new BusinessException("Product name is required.");
        }
        if (name.trim().length() > 150) {
            throw new BusinessException("Product name is too long.");
        }
        if (!ValidationUtil.isPositiveNumber(priceStr)) {
            throw new BusinessException("Please enter a valid price.");
        }
        if (ValidationUtil.isBlank(stockStr)) {
            throw new BusinessException("Please enter the stock quantity.");
        }

        long categoryId;
        int stock;
        double price;
        try {
            categoryId = Long.parseLong(categoryIdStr.trim());
            stock = Integer.parseInt(stockStr.trim());
            price = Double.parseDouble(priceStr.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("Please check the category, price, and stock fields.");
        }

        if (stock < 0) {
            throw new BusinessException("Stock cannot be negative.");
        }

        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setName(name.trim());
        product.setDescription(ValidationUtil.trimToNull(description));
        product.setPrice(price);
        product.setStock(stock);

        try {
            if (id == null) {
                productDAO.insert(product, imageStream, imageSize);
            } else {
                product.setId(id);
                productDAO.update(product, imageStream, imageSize);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not save the product. Please try again.", e);
        }
    }

    public void deleteProduct(long id) throws BusinessException {
        try {
            productDAO.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not delete the product. Please try again.", e);
        }
    }

    public List<Category> listCategories() {
        return safeList(() -> categoryDAO.findAll());
    }

    public Category getCategory(long id) {
        try {
            return categoryDAO.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveCategory(Long id, String name, String description) throws BusinessException {
        if (ValidationUtil.isBlank(name)) {
            throw new BusinessException("Category name is required.");
        }
        if (name.trim().length() > 100) {
            throw new BusinessException("Category name is too long.");
        }

        Category category = new Category();
        category.setName(name.trim());
        category.setDescription(ValidationUtil.trimToNull(description));

        try {
            if (id == null) {
                categoryDAO.insert(category);
            } else {
                category.setId(id);
                categoryDAO.update(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not save the category. Please try again.", e);
        }
    }

    public void deleteCategory(long id) throws BusinessException {
        try {
            categoryDAO.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not delete the category. Please try again.", e);
        }
    }

    public List<Order> listOrders() {
        return safeList(() -> orderDAO.findAll());
    }

    public Order getOrderWithItems(long id) {
        try {
            Order order = orderDAO.findById(id);
            if (order == null) return null;
            order.setItems(orderItemDAO.findByOrderId(id));
            return order;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateOrderStatus(long id, String status) throws BusinessException {
        if (!ORDER_STATUSES.contains(status)) {
            throw new BusinessException("Invalid order status.");
        }
        try {
            orderDAO.updateStatus(id, status);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BusinessException("Could not update the order status. Please try again.", e);
        }
    }

    public List<Customer> listCustomers() {
        return safeList(() -> customerDAO.findAll());
    }

    private interface SqlSupplier<T> {
        T get() throws SQLException;
    }

    private <T> List<T> safeList(SqlSupplier<List<T>> supplier) {
        try {
            return supplier.get();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static class DashboardStats {
        private final int totalProducts;
        private final int totalCategories;
        private final int totalOrders;
        private final int totalCustomers;
        private final int pendingOrders;
        private final double totalRevenue;

        public DashboardStats(int totalProducts, int totalCategories, int totalOrders,
                              int totalCustomers, int pendingOrders, double totalRevenue) {
            this.totalProducts = totalProducts;
            this.totalCategories = totalCategories;
            this.totalOrders = totalOrders;
            this.totalCustomers = totalCustomers;
            this.pendingOrders = pendingOrders;
            this.totalRevenue = totalRevenue;
        }

        public int getTotalProducts() { return totalProducts; }
        public int getTotalCategories() { return totalCategories; }
        public int getTotalOrders() { return totalOrders; }
        public int getTotalCustomers() { return totalCustomers; }
        public int getPendingOrders() { return pendingOrders; }
        public double getTotalRevenue() { return totalRevenue; }
    }
}

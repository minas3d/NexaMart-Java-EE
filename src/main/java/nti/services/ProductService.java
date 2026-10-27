package nti.services;

import java.util.Collections;

import java.util.List;

import nti.models.Category;
import nti.models.Product;
import java.util.List;

import nti.dao.CategoryDAO;
import nti.dao.ProductDAO;
import nti.models.Category;
import nti.models.Product;

public class ProductService {

	private final ProductDAO  productDAO  = new ProductDAO();
	private final CategoryDAO categoryDAO = new CategoryDAO();

    public BrowseResult browse(String q,
                               String category,
                               int page,
                               int pageSize) {

        if (q != null) {
            q = q.trim();
            if (q.isEmpty()) q = null;
        }

        Long categoryId = null;
        if (category != null && !category.trim().isEmpty()) {
            try {
                categoryId = Long.parseLong(category.trim());
            } catch (NumberFormatException ignored) {
                categoryId = null;
            }
        }

        if (page < 1)        page = 1;
        if (pageSize < 1)    pageSize = 12;

        List<Category> categories;
        try {
            categories = categoryDAO.findAll();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            categories = java.util.Collections.emptyList();
        }

        int totalCount;
        try {
            totalCount = productDAO.countFiltered(q, categoryId);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            totalCount = 0;
        }

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages < 1) totalPages = 1;

        if (page > totalPages) page = totalPages;

        List<Product> products;
        try {
            products = productDAO.findPage(q, categoryId, page, pageSize);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            products = java.util.Collections.emptyList();
        }

        return new BrowseResult(products, categories, totalCount, page, totalPages);
    }

    public static class BrowseResult {

        private final List<Product>  products;
        private final List<Category> categories;
        private final int totalCount;
        private final int currentPage;
        private final int totalPages;

        public BrowseResult(List<Product> products,
                            List<Category> categories,
                            int totalCount,
                            int currentPage,
                            int totalPages) {
            this.products    = products;
            this.categories  = categories;
            this.totalCount  = totalCount;
            this.currentPage = currentPage;
            this.totalPages  = totalPages;
        }

        public List<Product>  getProducts()    { return products; }
        public List<Category> getCategories()  { return categories; }
        public int getTotalCount()             { return totalCount; }
        public int getCurrentPage()            { return currentPage; }
        public int getTotalPages()             { return totalPages; }
    }

    public Product findById(long id) {

        try {
            return productDAO.findByIdWithCategory(id);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
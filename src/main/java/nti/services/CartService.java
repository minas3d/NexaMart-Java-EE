package nti.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import nti.dao.CartItemDAO;
import nti.dao.ProductDAO;
import nti.exceptions.BusinessException;
import nti.models.Cart;
import nti.models.CartItem;
import nti.models.Product;
import nti.models.User;

public class CartService {

    private static final String SESSION_CART_KEY       = "cart";
    private static final String SESSION_CUSTOMER_KEY   = "customerId";

    private final CartItemDAO cartItemDAO = new CartItemDAO();
    private final ProductDAO  productDAO  = new ProductDAO();

    public Cart getCart(HttpSession session, User user) {
        if (isGuest(user)) {
            return buildCartFromSession(session);
        } else {
            Long customerId = getCustomerId(session);
            if (customerId == null) {
                return new Cart();
            }
            return buildCartFromDb(customerId);
        }
    }

    public int getCartItemCount(HttpSession session, User user) {
        return getCart(session, user).getTotalItems();
    }

    public void addItem(HttpSession session, User user,
                        long productId, int quantity) throws BusinessException {

        if (quantity < 1) {
            throw new BusinessException("Quantity must be at least 1.");
        }

        Product product = loadProduct(productId);
        if (product == null) {
            throw new BusinessException("Product not found.");
        }

        if (isGuest(user)) {
            Map<Long, Integer> cart = getSessionCartMap(session);

            int existing = cart.containsKey(productId) ? cart.get(productId) : 0;
            int newQty   = existing + quantity;

            if (newQty > product.getStock()) {
                throw new BusinessException(
                    "Only " + product.getStock() + " in stock.");
            }

            cart.put(productId, newQty);
            saveSessionCartMap(session, cart);

        } else {
            Long customerId = getCustomerId(session);
            if (customerId == null) {
                throw new BusinessException("Only customers can use the cart.");
            }

            try {

                int existing = 0;
                for (CartItem item : cartItemDAO.findByCustomer(customerId)) {
                    if (item.getProduct().getId() == productId) {
                        existing = item.getQuantity();
                        break;
                    }
                }

                int newQty = existing + quantity;
                if (newQty > product.getStock()) {
                    throw new BusinessException(
                        "Only " + product.getStock() + " in stock.");
                }

                if (existing == 0) {
                    cartItemDAO.insert(customerId, productId, quantity);
                } else {
                    cartItemDAO.updateQuantity(customerId, productId, newQty);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new BusinessException(
                    "Could not update your cart. Please try again.", e);
            }
        }
    }

    public void updateQuantity(HttpSession session, User user,
                               long productId, int quantity) throws BusinessException {

        if (quantity < 1) {

            removeItem(session, user, productId);
            return;
        }

        Product product = loadProduct(productId);
        if (product == null) {
            throw new BusinessException("Product not found.");
        }

        if (quantity > product.getStock()) {
            throw new BusinessException(
                "Only " + product.getStock() + " in stock.");
        }

        if (isGuest(user)) {
            Map<Long, Integer> cart = getSessionCartMap(session);
            cart.put(productId, quantity);
            saveSessionCartMap(session, cart);

        } else {
            Long customerId = getCustomerId(session);
            if (customerId == null) return;

            try {
                cartItemDAO.updateQuantity(customerId, productId, quantity);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new BusinessException(
                    "Could not update your cart. Please try again.", e);
            }
        }
    }

    public void removeItem(HttpSession session, User user, long productId) {
        if (isGuest(user)) {
            Map<Long, Integer> cart = getSessionCartMap(session);
            cart.remove(productId);
            saveSessionCartMap(session, cart);

        } else {
            Long customerId = getCustomerId(session);
            if (customerId == null) return;

            try {
                cartItemDAO.delete(customerId, productId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void clear(HttpSession session, User user) {
        if (isGuest(user)) {
            session.removeAttribute(SESSION_CART_KEY);

        } else {
            Long customerId = getCustomerId(session);
            if (customerId == null) return;

            try {
                cartItemDAO.deleteByCustomer(customerId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mergeSessionCartIntoDb(HttpSession session, long customerId) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> sessionCart =
                (Map<Long, Integer>) session.getAttribute(SESSION_CART_KEY);

        if (sessionCart == null || sessionCart.isEmpty()) {
            return;
        }

        try {
            for (Map.Entry<Long, Integer> e : sessionCart.entrySet()) {
                cartItemDAO.addOrIncrement(customerId, e.getKey(), e.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        session.removeAttribute(SESSION_CART_KEY);
    }

    private boolean isGuest(User user) {
        return user == null;
    }

    private Long getCustomerId(HttpSession session) {
        Object cid = session.getAttribute(SESSION_CUSTOMER_KEY);
        if (cid instanceof Long) {
            return (Long) cid;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getSessionCartMap(HttpSession session) {
        Object o = session.getAttribute(SESSION_CART_KEY);
        if (o instanceof Map) {
            return (Map<Long, Integer>) o;
        }
        Map<Long, Integer> cart = new HashMap<>();
        session.setAttribute(SESSION_CART_KEY, cart);
        return cart;
    }

    private void saveSessionCartMap(HttpSession session, Map<Long, Integer> cart) {
        session.setAttribute(SESSION_CART_KEY, cart);
    }

    private Product loadProduct(long productId) {
        try {
            return productDAO.findByIdWithCategory(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cart buildCartFromSession(HttpSession session) {
        Map<Long, Integer> map = getSessionCartMap(session);

        List<CartItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : map.entrySet()) {
            Product p = loadProduct(e.getKey());
            if (p != null) {
                items.add(new CartItem(p, e.getValue()));
            }
        }

        return new Cart(items);
    }

    private Cart buildCartFromDb(long customerId) {
        try {
            List<CartItem> items = cartItemDAO.findByCustomer(customerId);
            return new Cart(items);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Cart();
        }
    }
}
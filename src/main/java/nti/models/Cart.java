package nti.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public Cart(List<CartItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    public void addItem(CartItem item) {
        this.items.add(item);
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getTotalItems() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    public int getDistinctItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Cart{" +
                "distinctItems=" + items.size() +
                ", totalQuantity=" + getTotalItems() +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
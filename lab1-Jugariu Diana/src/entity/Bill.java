package entity;

import java.sql.Array;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Bill {
    private Vector<Product> items;
    private Vector<Integer> quantities;
    private int totalPrice = 0;

    public Bill() {
        this.items = new Vector<Product>();
        this.quantities = new Vector<Integer>();
    }

    public void addToBill(Product product, int quantity)
    {
        int i = 0;

        while (i < items.size() && this.items.get(i).getName().compareTo(product.getName()) < 0)
            i++;

        this.items.insertElementAt(product, i);
        this.quantities.insertElementAt(quantity, i);
        this.totalPrice += quantity * product.getPrice();
    }

    public Vector<Product> getItems() {
        return items;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public Vector<Integer> getQuantities() {
        return quantities;
    }

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder(new String());

        for (Product product : this.items)
        {
            content.append(product.toString());
            content.append("  Sold quantity: ").append(this.getQuantities().get(this.items.indexOf(product))).append("\n");
        }

        content.append("Total price: ").append(this.totalPrice).append("\n");

        return content.toString();
    }
}

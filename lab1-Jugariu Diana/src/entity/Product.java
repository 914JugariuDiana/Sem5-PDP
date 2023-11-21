package entity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Product {
    private final String name;
    private final int price;
    private int quantity;
    private ReentrantReadWriteLock readLock;


    public Product(final String name, final int price, int quantity)
    {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.readLock = new ReentrantReadWriteLock(true);
    }

    public String getName() {
        String name = this.name;

        return name;
    }

    public int getPrice() {
        int price = this.price;

        return price;
    }

    public int getQuantity() {
        this.readLock.readLock().lock();
        int quantity = this.quantity;
        this.readLock.readLock().unlock();

        return quantity;
    }

    private void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean executeSale(int quantity)
    {
        this.readLock.writeLock().lock();

        if (this.getQuantity() > quantity){
            this.setQuantity(this.getQuantity() - quantity);
            this.readLock.writeLock().unlock();
            return true;
        }

        this.readLock.writeLock().unlock();

        return false;
    }

    public void revertSale(int quantity)
    {
        this.readLock.writeLock().lock();
        this.setQuantity(this.getQuantity() + quantity);
        this.readLock.writeLock().unlock();
    }

    @Override
    public String toString() {
        return "Name: " + this.getName() + "  Price: " + this.getPrice() + "  Quantity: " + this.getQuantity();
    }
}

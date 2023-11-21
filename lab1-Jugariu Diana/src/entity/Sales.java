package entity;

import java.time.LocalTime;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Sales {
    private Vector<Bill> bills;
    private int saleValue = 0;
    private ReadWriteLock readWriteLock;
    private Random random = new Random();

    public Sales()
    {
        bills = new Vector<Bill>(){};
        readWriteLock = new ReentrantReadWriteLock();
    }


    public void execute(Bill bill)
    {
        Vector<Integer> quantities = bill.getQuantities();
        Vector<Product> items = bill.getItems();

        for (Product product : items)
        {
//            if (random.nextInt(2) == 1){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//            }


            boolean result = product.executeSale(quantities.get(items.indexOf(product)));

            System.out.println(Thread.currentThread().threadId() + " " + product.toString() + " " + LocalTime.now());

            if (!result) {
                revert(bill);

                return;
            }
        }

        this.readWriteLock.writeLock().lock();
        this.bills.add(bill);
        saleValue += bill.getTotalPrice();
        this.readWriteLock.writeLock().unlock();
    }

    public void revert(Bill bill)
    {
        Vector<Integer> quantities = bill.getQuantities();
        Vector<Product> items = bill.getItems();

        for (Product product : items)
        {
            product.revertSale(quantities.get(items.indexOf(product)));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        readWriteLock.readLock().lock();
        int sum = 0;

        stringBuilder.append("------------------------------------------------------\n");
        stringBuilder.append("Number of bills: ").append(bills.size()).append("\n");
        for (Bill bill : bills) {
            stringBuilder.append(bill.toString());
            sum += bill.getTotalPrice();
        }
        if (saleValue == sum)
            stringBuilder.append("Ok").append("\n");
        else
            stringBuilder.append("Not ok").append("\n");
        stringBuilder.append("------------------------------------------------------\n");

        readWriteLock.readLock().unlock();

        return stringBuilder.toString();
    }
}

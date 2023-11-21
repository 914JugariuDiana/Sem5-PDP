import entity.Bill;
import entity.Product;
import entity.Sales;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Product product1 = new Product("lemon", 10, 150);
        Product product2 = new Product("apple", 5, 150);
        Product product3 = new Product("pasta", 15, 200);
        Product product4 = new Product("milk", 7, 100);
        Product product5 = new Product("microwave", 1000, 10);
        Product product6 = new Product("onion", 3, 150);
        Product product7 = new Product("nutella - 400g", 30, 15);
        Product product8 = new Product("almond milk", 20, 200);
        Product product9 = new Product("milk chocolate", 15, 150);
        Product product10 = new Product("knife", 50, 25);


        Bill bill1 = new Bill();
        Bill bill2 = new Bill();
        Bill bill3 = new Bill();
        Bill bill4 = new Bill();
        Bill bill5 = new Bill();

        bill1.addToBill(product3, 10);
        bill1.addToBill(product1, 10);
        bill1.addToBill(product9, 10);
        bill1.addToBill(product4, 5);

        bill2.addToBill(product2, 20);
        bill2.addToBill(product5, 2);
        bill2.addToBill(product7, 3);
        bill2.addToBill(product4, 5);
        bill2.addToBill(product10, 4);

        bill3.addToBill(product4, 1);
        bill3.addToBill(product10, 7);
        bill3.addToBill(product1, 4);
        bill3.addToBill(product6, 3);
        bill3.addToBill(product7, 2);

        bill4.addToBill(product8, 5);
        bill4.addToBill(product1, 5);
        bill4.addToBill(product4, 5);

        bill5.addToBill(product1, 1);
        bill5.addToBill(product2, 2);
        bill5.addToBill(product3, 3);
        bill5.addToBill(product5, 4);
        bill5.addToBill(product4, 5);
        bill5.addToBill(product7, 6);
        bill5.addToBill(product9, 7);
        bill5.addToBill(product6, 8);

        Sales sales = new Sales();

//        Thread thread1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                sales.execute(bill1);
//                sales.execute(bill2);
//                sales.execute(bill3);
//            }
//        });
//
//        Thread thread2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                sales.execute(bill4);
//                sales.execute(bill5);
//            }
//        });

        Thread threadCheck = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(sales.toString());
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(sales.toString());
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(sales.toString());
            }
        });

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                sales.execute(bill1);
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                sales.execute(bill2);
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                sales.execute(bill3);
            }
        });

        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                sales.execute(bill4);
            }
        });

        Thread thread5 = new Thread(new Runnable() {
            @Override
            public void run() {
                sales.execute(bill5);
            }
        });

        long startTime = System.nanoTime();

//        sales.execute(bill1);
//        sales.execute(bill2);
//        sales.execute(bill3);
//        sales.execute(bill4);
//        sales.execute(bill5);

//        thread1.start();
//        thread2.start();
//        threadCheck.start();
//
//        thread1.join();
//        thread2.join();
//        threadCheck.join();

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        threadCheck.start();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();
        threadCheck.join();


        long endTime = System.nanoTime();

        long duration = (endTime - startTime);

        System.out.println(sales);
        System.out.println(duration);

//      No threads, no mutexes : 28343000
//      2 threads, added products in alphabetic order to bill, added mutexes : 24101500 - not ok
//      5 threads, mutexes : 4128107300

    }
}
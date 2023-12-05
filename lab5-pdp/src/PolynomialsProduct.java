import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class PolynomialsProduct {
    ArrayList<Integer> polynomial1 = new ArrayList<Integer>();
    ArrayList<Integer> polynomial2 = new ArrayList<Integer>();
    ArrayList<Integer> polynomialProductSequential = new ArrayList<Integer>();
    ArrayList<Integer> polynomialProductParallel = new ArrayList<Integer>();
    ArrayList<ReadWriteLock> readWriteLock = new ArrayList<>();
    String sequentialTime = "";
    String parallelTime = "";

    public PolynomialsProduct(){
        this.polynomial1 = generatePolynomial(this.polynomial1, getRandomInt(100000, 5));
        this.polynomial2 = generatePolynomial(this.polynomial2, getRandomInt(100000, 5));
    }

    public int getRandomInt(int max, int min) {
        return (int) Math.floor(Math.random() *(max - min + 1) + min);
    }
    
    public ArrayList<Integer> generatePolynomial(ArrayList<Integer> list, int size){
        for (int i = 0; i < size; i++){
            list.add(getRandomInt(1000, 0));
        }

        return list;
    }

    public void multypliPartOfPolynomyal(int pos){
        for (int i = 0; i < this.polynomial2.size(); i++){
            this.readWriteLock.get(i + pos).writeLock().lock();
//            System.out.println(System.currentTimeMillis() + " " + pos + " " + i + " " + Thread.currentThread().threadId());
            this.polynomialProductParallel.set(i+pos,
                    this.polynomialProductParallel.get(i+pos) + this.polynomial1.get(pos) * this.polynomial2.get(i));
            this.readWriteLock.get(i+pos).writeLock().unlock();
        }
    }

    public void multypliPolynomyalsParallel() throws InterruptedException {
        for (int i = 0; i < polynomial1.size()+polynomial2.size(); i++){
            this.polynomialProductParallel.add(0);
            this.readWriteLock.add(new ReentrantReadWriteLock());
        }

        long startTime = System.nanoTime();

        ArrayList<Runnable> runnables = new ArrayList<Runnable>();
        for (int i = 0; i < this.polynomial1.size(); i++){
            int pos = i;
            runnables.add(() -> multypliPartOfPolynomyal(pos));
        }

        List<Thread> threads = runnables.stream()
                .map(Thread::new)
                .toList();

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        });


        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime);
//        System.out.println("result = ");
//        System.out.println(polynomialToString(this.polynomialProductParallel));
//        System.out.println(executionTime);
        this.parallelTime = String.valueOf(executionTime);
    }


    public void multiplyPolynomialsSequential(){
        long startTime = System.nanoTime();

        for (int i = 0; i < this.polynomial1.size(); i++){
            for (int j = 0; j < this.polynomial2.size(); j++){
                if (this.polynomialProductSequential.size() > i + j)
                    this.polynomialProductSequential.set(i+j,
                            this.polynomialProductSequential.get(i+j) + this.polynomial1.get(i) * this.polynomial2.get(j));
                else
                    this.polynomialProductSequential.add(i+j, this.polynomial1.get(i) * this.polynomial2.get(j));
            }

        }

        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime);
//        System.out.println(toString());
//        System.out.println("result = ");
//        System.out.println(polynomialToString(this.polynomialProductSequential));
//        System.out.println(executionTime);
        this.sequentialTime = String.valueOf(executionTime);
    }

    public String polynomialToString(ArrayList<Integer> polynomial){
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < polynomial.size(); i++){
            if (i != 0)
                string.append(" + ");
            string.append(polynomial.get(i)).append("x^").append(i);
        }

        return string.toString();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("polynomial1 = ");
        string.append(polynomialToString(polynomial1));
        string.append("\npolynomial2 = ");
        string.append(polynomialToString(polynomial2));

        return string.toString();
    }
}




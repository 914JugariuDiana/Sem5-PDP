import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
public class PolynomialsProduct {
    ArrayList<Integer> polynomial1 = new ArrayList<Integer>();
    ArrayList<Integer> polynomial2 = new ArrayList<Integer>();
    ArrayList<Integer> part1Polynomial1 = new ArrayList<>();
    ArrayList<Integer> part1Polynomial2 = new ArrayList<>();
    ArrayList<Integer> part2Polynomial1 = new ArrayList<>();
    ArrayList<Integer> part2Polynomial2 = new ArrayList<>();


    ArrayList<Integer> polynomialProductSequential = new ArrayList<>();
    ArrayList<Integer> polynomialProductParallel = new ArrayList<>();
    ArrayList<ReadWriteLock> readWriteLock = new ArrayList<>();
    String sequentialTime = "";
    String parallelTime = "";
    String sequentialTimeKaratsuba = "";
    String parallelTimeKaratsuba = "";
    int size;

    public PolynomialsProduct(){
        this.size = getRandomInt(1000, 500);
        this.polynomial1 = generatePolynomial(this.polynomial1, 2 * size);
        this.polynomial2 = generatePolynomial(this.polynomial2, 2 * size);
        this.part1Polynomial1 = new ArrayList<>(this.polynomial1.subList(0, this.size));
        this.part1Polynomial2 = new ArrayList<>(this.polynomial1.subList(this.size, this.polynomial1.size()));
        this.part2Polynomial1 = new ArrayList<>(this.polynomial2.subList(0, this.size));
        this.part2Polynomial2 = new ArrayList<>(this.polynomial2.subList(this.size, this.polynomial1.size()));
    }

    public int getRandomInt(int max, int min) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public ArrayList<Integer> generatePolynomial(ArrayList<Integer> list, int size){
        for (int i = 0; i < size; i++){
            list.add(getRandomInt(10, 0));
        }

        return list;
    }

    public void multiplyPolynomialsKaratsubaParallel() throws InterruptedException {
        long startTime = System.nanoTime();

        ReadWriteLock lock = new ReentrantReadWriteLock();
        final ArrayList<ArrayList<Integer>> res = new ArrayList<>(3);
        res.add(new ArrayList<>());
        res.add(new ArrayList<>());
        res.add(new ArrayList<>());

        Runnable task1 = () -> {
            ArrayList<Integer> r = multiplyPolynomialsSequentially(part1Polynomial1, part1Polynomial2);
            lock.writeLock().lock();
            res.set(0, r);
            lock.writeLock().unlock();
        };

        Runnable task2 = () -> {
            ArrayList<Integer> r = multiplyPolynomialsSequentially(part2Polynomial1, part2Polynomial2);
            lock.writeLock().lock();
            res.set(1, r);
            lock.writeLock().unlock();
        };

        Runnable task3 = () -> {
            ArrayList<Integer> r = multiplyPolynomialsSequentially(
                    addPolynomials(part1Polynomial1, part2Polynomial1, 0, 0),
                    addPolynomials(part1Polynomial2, part2Polynomial2, 0, 0));
            lock.writeLock().lock();
            res.set(2, r);
            lock.writeLock().unlock();
        };

        Thread t1 = new Thread(task1);
        t1.start();
        Thread t2 = new Thread(task2);
        t2.start();
        Thread t3 = new Thread(task3);
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        res.set(2, substractPolynomials(res.get(2), res.get(0)));
        res.set(2, substractPolynomials(res.get(2), res.get(1)));

        ArrayList<Integer> result = new ArrayList<>();

        result = addPolynomials(res.get(0), res.get(1), 2 * this.size, 0);
        result = addPolynomials(result, res.get(2), 0, this.size);

        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime);
        this.parallelTimeKaratsuba = String.valueOf(executionTime);

    }

    public void  multiplyPolynomialsKaratsubaSequential(){
        long startTime = System.nanoTime();

        ArrayList<Integer> part2Polynomial1 = new ArrayList<>(this.polynomial1.subList(0, this.size));
        ArrayList<Integer> part1Polynomial1 = new ArrayList<>(this.polynomial1.subList(this.size, this.polynomial1.size()));
        ArrayList<Integer> part2Polynomial2 = new ArrayList<>(this.polynomial2.subList(0, this.size));
        ArrayList<Integer> part1Polynomial2 = new ArrayList<>(this.polynomial2.subList(this.size, this.polynomial1.size()));

        ArrayList<Integer> res1, res2, res3;
        res1 = multiplyPolynomialsSequentially(part1Polynomial1, part1Polynomial2);
        res2 = multiplyPolynomialsSequentially(part2Polynomial1, part2Polynomial2);
        res3 = multiplyPolynomialsSequentially(addPolynomials(part1Polynomial1, part2Polynomial1, 0, 0),
                addPolynomials(part1Polynomial2, part2Polynomial2, 0, 0));
        res3 = substractPolynomials(res3, res1);
        res3 = substractPolynomials(res3, res2);

        ArrayList<Integer> result = new ArrayList<>();
        result = addPolynomials(res1, res2, 2 * this.size, 0);
        result = addPolynomials(result, res3, 0, this.size);

        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime);

        this.sequentialTimeKaratsuba = String.valueOf(executionTime);
    }

    public ArrayList<Integer> substractPolynomials(ArrayList<Integer> pol1, ArrayList<Integer> pol2){
        for (int i = 0; i < pol2.size(); i++){
            pol1.set(i, pol1.get(i) - pol2.get(i));
        }

        return pol1;
    }

    public ArrayList<Integer> addPolynomials(ArrayList<Integer> pol1, ArrayList<Integer> pol2, int powerPol1, int powerPol2){
        ArrayList<Integer> result = new ArrayList<>();

        for (int i = 0; i < Math.max(powerPol1 + pol1.size(), powerPol2 + pol2.size()); i++){
            result.add(0);
        }

        for (int i = 0; i < pol1.size(); i++){
            result.set(i + powerPol1, result.get(i+powerPol1) + pol1.get(i));
        }

        for (int i = 0; i < pol2.size(); i++){
            result.set(i + powerPol2, result.get(i+powerPol2) + pol2.get(i));
        }

        return result;
    }

    public void multiplyPartOfPolynomial(int value){
        for (int i = 0; i < this.polynomial1.size(); i++){
            if (value - i < this.polynomial2.size() && value - i >= 0){
                try {
                    this.readWriteLock.get(value).writeLock().lock();
                    this.polynomialProductParallel.set(value,
                            this.polynomialProductParallel.get(value) + this.polynomial1.get(i) * this.polynomial2.get(value - i));
                } finally {
                    this.readWriteLock.get(value).writeLock().unlock();
                }
            }
        }
    }

    public void multiplyPolynomialsParallel() throws InterruptedException {
        for (int i = 0; i < polynomial1.size()+polynomial2.size() - 1; i++){
            this.polynomialProductParallel.add(0);
            this.readWriteLock.add(new ReentrantReadWriteLock());
        }

        long startTime = System.nanoTime();

        ArrayList<Runnable> runnables = new ArrayList<Runnable>();
        for (int i = 0; i < this.polynomial1.size() + this.polynomial2.size() - 1; i++){
            int pos = i;
            runnables.add(() -> multiplyPartOfPolynomial(pos));
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
//        System.out.println(polynomialToString(this.polynomialProductParallel));
        this.parallelTime = String.valueOf(executionTime);
    }

    public ArrayList<Integer> multiplyPolynomialsSequentially(ArrayList<Integer> pol1, ArrayList<Integer>pol2) {
        ArrayList<Integer> res = new ArrayList<>();

        for (int i = 0; i < pol1.size(); i++) {
            for (int j = 0; j < pol2.size(); j++) {
                if (res.size() > i + j)
                    res.set(i + j, res.get(i + j) + pol1.get(i) * pol2.get(j));
                else
                    res.add(i + j, pol1.get(i) * pol2.get(j));
            }
        }

        return res;
    }

    public void multiplyPolynomialsSequential(){
        long startTime = System.nanoTime();

        this.polynomialProductSequential =
                multiplyPolynomialsSequentially(this.polynomial1, this.polynomial2);

        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime);
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
        String string = "polynomial1 = " +
                polynomialToString(polynomial1) +
                "\npolynomial2 = " +
                polynomialToString(polynomial2);

        return string;
    }
}



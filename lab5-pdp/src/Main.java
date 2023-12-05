import java.util.ArrayList;
// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static ArrayList<Integer> polynom1, polynom2;


    public static void main(String[] args) throws InterruptedException {
        int size = getRandomInt(1000, 50);
        PolynomialsProduct polynomialsProduct = new PolynomialsProduct();

        polynomialsProduct.multiplyPolynomialsSequential();
        try {
            polynomialsProduct.multypliPolynomyalsParallel();
        } catch (InterruptedException e){
            System.out.println(e);
        }


        System.out.println("Sequential time: " + polynomialsProduct.sequentialTime);
        System.out.println("Parallel time:   " + polynomialsProduct.parallelTime);
//      O(n^2) when size of polynomials is between 5 and 10000 , most of the time sequential variant is faster, between 5
//      and 100000 the parallel begins to have a better time than sequential (Sequential time: 23812935500, Parallel time:   17923226200)

    }

    public static int getRandomInt(int max, int min) {
        return (int) Math.floor(Math.random() *(max - min + 1) + min);
    }

}
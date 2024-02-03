import mpi.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    static PolynomialsProduct polynomialsProduct = new PolynomialsProduct();
    static ArrayList<Integer> mpiNormal = new ArrayList<>();
    static int[] fullResult;
    static long startTime;

    private static final int MPI_TAG = 123;

    public static void main(String[] args) {
//        runMpi(args);
        runMpiKaratsuba(args);
    }

    public static void runMpi(String[] args){
        MPI.Init(args);
        Polynomials polynomials = new Polynomials();

        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();


        if (me == 0) {
            runLab5();
            startTime = System.nanoTime();
            polynomials.polynomial1 = polynomials.generatePolynomial(polynomials.polynomial1, 2 * polynomials.size);
            polynomials.polynomial2 = polynomials.generatePolynomial(polynomials.polynomial2, 2 * polynomials.size);

            int[] poly1Array = polynomials.polynomial1.stream().mapToInt(Integer::intValue).toArray();
            int[] poly2Array = polynomials.polynomial2.stream().mapToInt(Integer::intValue).toArray();

            MPI.COMM_WORLD.Bcast(new int[]{polynomials.size}, 0, 1, MPI.INT, 0);
            MPI.COMM_WORLD.Bcast(poly1Array, 0, polynomials.size, MPI.INT, 0);
            MPI.COMM_WORLD.Bcast(poly2Array, 0, polynomials.size, MPI.INT, 0);
        }

        if (me != 0) {
            worker(me, size);
        }

        if (me == 0) {
            sumResult(polynomials.size, size);
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < 2 * polynomials.size -1; i++) {
                if (i != 0)
                    string.append(" + ");
                string.append(fullResult[i]).append("x^").append(i);
            }

//            System.out.println(string.toString());
            long endTime = System.nanoTime();

            long executionTime = (endTime - startTime);

            System.out.println("MPI parallel :     " + executionTime);
        }

        MPI.Finalize();
    }

    static public void runMpiKaratsuba(String[] args){
        int[] poly1Array1 = new int[0], poly1Array2 = new int[0] , poly2Array1 = new int[0], poly2Array2 = new int[0];

        MPI.Init(args);
        Polynomials polynomials = new Polynomials();

        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();


        if (me == 0) {
            runLab5();
            startTime = System.nanoTime();
            polynomials.polynomial1 = polynomials.generatePolynomial(polynomials.polynomial1, polynomials.size);
            polynomials.polynomial2 = polynomials.generatePolynomial(polynomials.polynomial2, polynomials.size);
            polynomials.splitPolynomials();

            poly1Array1 = polynomials.part1Polynomial1.stream().mapToInt(Integer::intValue).toArray();
            poly1Array2 = polynomials.part1Polynomial2.stream().mapToInt(Integer::intValue).toArray();
            poly2Array1 = polynomials.part2Polynomial1.stream().mapToInt(Integer::intValue).toArray();
            poly2Array2 = polynomials.part2Polynomial2.stream().mapToInt(Integer::intValue).toArray();

            MPI.COMM_WORLD.Ssend(new int[]{polynomials.size / 2}, 0, 1, MPI.INT, 1, MPI_TAG);
            MPI.COMM_WORLD.Ssend(new int[]{polynomials.size / 2}, 0, 1, MPI.INT, 2, MPI_TAG);
            MPI.COMM_WORLD.Ssend(poly1Array2, 0, polynomials.size / 2, MPI.INT, 2, MPI_TAG);
            MPI.COMM_WORLD.Ssend(poly2Array2, 0, polynomials.size / 2, MPI.INT, 2, MPI_TAG);
            MPI.COMM_WORLD.Ssend(poly1Array1, 0, polynomials.size / 2, MPI.INT, 1, MPI_TAG);
            MPI.COMM_WORLD.Ssend(poly2Array1, 0, polynomials.size / 2, MPI.INT, 1, MPI_TAG);
        }

        if (me == 1 || me == 2) {
            workerKaratsuba();
        }

        if (me == 0) {
            int[] firstProd = new int[polynomials.size];
            int[] secondProd = new int[polynomials.size];
            MPI.COMM_WORLD.Recv(firstProd, 0, polynomials.size, MPI.INT, 1, MPI_TAG);
            MPI.COMM_WORLD.Recv(secondProd, 0, polynomials.size, MPI.INT, 2, MPI_TAG);

            int[] result1 = multiplyPolynomialsSequentially(addPolynomials(poly1Array1, poly2Array1, 0, 0),
                    addPolynomials(poly1Array2, poly2Array2, 0, 0));

            result1 = substractPolynomials(result1, firstProd);
            result1 = substractPolynomials(result1, secondProd);

            int[] sum = addPolynomials(firstProd, secondProd, polynomials.size, 0);
            sum = addPolynomials(sum, result1, 0, polynomials.size / 2);


            StringBuilder string = new StringBuilder();
            for (int i = 0; i < 2 * polynomials.size -1; i++) {
                if (i != 0)
                    string.append(" + ");
                string.append(sum[i]).append("x^").append(i);
            }

//            System.out.println(string.toString());
            long endTime = System.nanoTime();

            long executionTime = (endTime - startTime);

            System.out.println("MPI K parallel  :  " + executionTime);
        }

        MPI.Finalize();
    }

    static public int[] substractPolynomials(int[] pol1, int[] pol2){
        for (int i = 0; i < pol2.length; i++){
            pol1[i] = pol1[i] - pol2[i];
        }

        return pol1;
    }

    static public int[] addPolynomials(int[] pol1, int[] pol2, int start1, int start2){
        int[] result = new int[Math.max(start1 + pol1.length, start2 + pol2.length)];

        for (int i = 0; i < pol1.length; i++){
            result[i + start1] += pol1[i];
        }

        for (int i = 0; i < pol2.length; i++){
            result[i + start2] += pol2[i];
        }

        return result;
    }

    private static void workerKaratsuba(){
        int[] size = new int[1];

        MPI.COMM_WORLD.Recv(size, 0, 1, MPI.INT, 0, MPI_TAG);

        int[] result;
        int[] poly1 = new int[size[0]];
        int[] poly2 = new int[size[0]];

        // Receive the polynomials
        MPI.COMM_WORLD.Recv(poly1, 0, size[0], MPI.INT, 0, MPI_TAG);
        MPI.COMM_WORLD.Recv(poly2, 0, size[0], MPI.INT, 0, MPI_TAG);

        result = multiplyPolynomialsSequentially(poly1, poly2);

//        System.out.println(me + Arrays.toString(result));
        MPI.COMM_WORLD.Ssend(result, 0, size[0] * 2, MPI.INT, 0, MPI_TAG);
    }

    static public int[] multiplyPolynomialsSequentially(int[] pol1, int[] pol2) {
        int[] res = new int[pol1.length * 2];

        for (int i = 0; i < pol1.length; i++) {
            for (int j = 0; j < pol2.length; j++) {
                res[i + j] += pol1[i] * pol2[j];
            }
        }

        return res;
    }

    private static void sumResult(int size, int nrProc) {
        fullResult = new int[2 * size];
        int[] partialResult = new int[2 * size];

//        MPI.COMM_WORLD.Scatter(fullResult, 0, size * size/nrProc, MPI.INT, partialResult, 0, size * size / nrProc, MPI.INT, 0);
        for (int i = 1; i < nrProc; i++){
            MPI.COMM_WORLD.Recv(partialResult, 0, 2 * size, MPI.INT, i, MPI_TAG);
            for (int j = 0; j < 2 * size; j++){
                fullResult[j] += partialResult[j];
            }
        }
    }

    public static void worker(int me, int nrNodes){
        int[] size = new int[1];

        MPI.COMM_WORLD.Bcast(size, 0, 1, MPI.INT, 0);

        int[] result = new int[size[0] * 2 - 1];
        int[] poly1Array = new int[size[0]];
        int[] poly2Array = new int[size[0]];

        // Receive the polynomials
        MPI.COMM_WORLD.Bcast(poly1Array, 0, poly1Array.length, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(poly2Array, 0, poly2Array.length, MPI.INT, 0);

        int currentStart = size[0] / nrNodes * me - 1;
        int end = size[0] / nrNodes + currentStart;
        if (me == nrNodes - 1){
            end = size[0];
        }

        for (int i = currentStart; i < end; i++){
            for (int j = 0; j < size[0]; j++){
                result[j + i] += poly1Array[i] * poly2Array[j];
            }
        }
//        System.out.println(me + Arrays.toString(result));
        MPI.COMM_WORLD.Ssend(result, 0, size[0] * 2 - 1, MPI.INT, 0, MPI_TAG);
    }

    public static void runLab5(){
        polynomialsProduct.multiplyPolynomialsSequential();
        polynomialsProduct.multiplyPolynomialsKaratsubaSequential();

        try {
            polynomialsProduct.multiplyPolynomialsParallel();
            polynomialsProduct.multiplyPolynomialsKaratsubaParallel();
        } catch (InterruptedException e){
            System.out.println(e);
        }

        System.out.println("Sequential time:   " + polynomialsProduct.sequentialTime);
        System.out.println("Parallel time:     " + polynomialsProduct.parallelTime);
        System.out.println("Sequential time k: " + polynomialsProduct.sequentialTimeKaratsuba);
        System.out.println("Parallel time k  : " + polynomialsProduct.parallelTimeKaratsuba);
    }
}

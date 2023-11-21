public class Main {
    public static void main(String[] args) throws InterruptedException {
//        9 x 9   9 x 4  *  4 x 9
        //1 2    3 5 6
        //2 3    5 7 8
        int[][] matrix1 = {{1, 2, 7, 1}, {2, 3, 1, 2}, {3, 2, 1, 0}, {4, 5, 3, 2}, {1, 2, 7, 1}, {2, 3, 1, 2}, {3, 2, 1, 0}, {4, 5, 3, 2}, {9, 8, 7, 6}};
        int[][] matrix2 = {{3, 5, 6, 5, 7, 8, 7, 6, 8}, {4, 2, 1, 0, 4, 6, 3, 2, 1}, {0, 1, 2, 3, 4, 5, 6, 7, 8}, {7, 6, 2, 3, 4, 9, 20, 3, 2}};

        int[][] matrix3 = {{6, 5, 8, 6, 2, 3, 9, 8, 2, 9, 0, 6, 1},
                {4, 1, 1, 6, 9, 3, 9, 2, 8, 7, 3, 5, 6},
                {3, 1, 1, 2, 5, 9, 8, 4, 8, 9, 2, 4, 0},
                {0, 5, 4, 3, 1, 6, 2, 1, 0, 6, 7, 6, 0},
                {5, 8, 2, 3, 1, 5, 4, 8, 3, 0, 7, 9, 1},
                {6, 5, 5, 7, 9, 3, 2, 9, 4, 5, 0, 9, 8},
                {3, 9, 8, 1, 4, 5, 3, 4, 8, 1, 5, 3, 3},
                {7, 8, 2, 1, 4, 0, 4, 9, 9, 0, 4, 7, 7},
                {0, 6, 7, 6, 0, 0, 3, 7, 0, 1, 6, 0, 9},
                {0, 0, 2, 5, 4, 3, 0, 7, 3, 7, 4, 1, 5},
                {5, 9, 6, 5, 1, 2, 9, 9, 3, 3, 9, 6, 3},
                {7, 3, 6, 6, 3, 5, 8, 2, 0, 3, 1, 9, 4},
                {3, 0, 0, 6, 5, 9, 7, 0, 3, 6, 0, 7, 0},
                {8, 4, 0, 5, 1, 2, 7, 2, 7, 8, 5, 5, 8},
                {1, 6, 3, 3, 0, 4, 3, 4, 3, 1, 3, 0, 9}};

        int[][] matrix4 = {{4, 1, 4, 8, 7, 3, 6, 8, 7, 8, 9, 3, 3, 3, 1, 2, 8},
                {5, 4, 6, 6, 7, 4, 6, 6, 5, 8, 8, 9, 2, 0, 6, 4, 6},
                {7, 8, 7, 6, 5, 7, 6, 1, 3, 5, 5, 8, 8, 1, 2, 2, 5},
                {1, 5, 9, 7, 8, 1, 0, 6, 4, 1, 5, 8, 0, 0, 4, 2, 3},
                {2, 9, 5, 1, 4, 1, 1, 7, 3, 6, 2, 4, 8, 5, 9, 7, 7},
                {8, 9, 8, 9, 4, 6, 7, 7, 0, 6, 9, 2, 4, 9, 5, 2, 0},
                {3, 8, 9, 0, 2, 6, 8, 0, 1, 0, 5, 9, 2, 6, 2, 2, 3},
                {1, 5, 6, 0, 6, 4, 7, 1, 6, 6, 7, 1, 9, 5, 3, 5, 8},
                {7, 7, 1, 7, 0, 2, 5, 3, 6, 5, 6, 4, 1, 0, 8, 3, 6},
                {0, 0, 6, 6, 0, 8, 4, 6, 7, 8, 1, 6, 1, 8, 5, 4, 7},
                {4, 2, 9, 9, 9, 2, 7, 1, 8, 1, 1, 8, 3, 9, 3, 2, 2},
                {4, 7, 7, 6, 4, 6, 9, 1, 8, 3, 8, 8, 4, 6, 7, 7, 8},
                {2, 3, 4, 8, 7, 0, 4, 0, 4, 5, 2, 4, 7, 4, 5, 4, 9}};

//            computeMatrixProductLinear(matrix1, matrix2);
        computeMatrixProductLinear(matrix3, matrix4);

//            computeMatrixProductOnLines(matrix1, matrix2, 80);
        computeMatrixProductOnLines(matrix3, matrix4, 80);

//            computeMatrixProductOnColumns(matrix1, matrix2, 80);
        computeMatrixProductOnColumns(matrix3, matrix4, 80);

//            computeMatrixProductFromKtoK(matrix1, matrix2, 80);
        computeMatrixProductFromKtoK(matrix3, matrix4, 80);

//        MatrixProduct matrixProduct1 = new MatrixProduct(matrix1, matrix2);
        MatrixProduct matrixProduct1 = new MatrixProduct(matrix3, matrix4);
//        MatrixProduct matrixProduct2 = new MatrixProduct(matrix1, matrix2);
        MatrixProduct matrixProduct2 = new MatrixProduct(matrix3, matrix4);
//        MatrixProduct matrixProduct3 = new MatrixProduct(matrix1, matrix2);
        MatrixProduct matrixProduct3 = new MatrixProduct(matrix3, matrix4);

        long start = System.nanoTime();
        matrixProduct1.multiplyMatricesThreadPoolRows(matrix3.length, matrix4[0].length, 80);
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.print("Execution time for threads on lines - thread pool: ");
        printResult(timeElapsed, matrixProduct1.getResultMatrix());

        start = System.nanoTime();
        matrixProduct2.multiplyMatricesThreadPoolColumns(matrix3.length, matrix4[0].length, 80);
        finish = System.nanoTime();
        timeElapsed = finish - start;
        System.out.print("Execution time for threads on columns - thread pool: ");
        printResult(timeElapsed, matrixProduct2.getResultMatrix());

        start = System.nanoTime();
        matrixProduct3.multiplyMatricesThreadPoolKToK(matrix3.length, matrix4[0].length, 80);
        finish = System.nanoTime();
        timeElapsed = finish - start;
        System.out.print("Execution time for threads from k to k - thread pool: ");
        printResult(timeElapsed, matrixProduct3.getResultMatrix());
    }

    private static void printResult(long timeElapsed, int[][] res4) {
        System.out.println(timeElapsed);

        for (int[] re : res4) {
            for (int j = 0; j < res4[0].length; j++) {
                System.out.print(re[j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private static void computeMatrixProductLinear(int[][] matrix1, int[][] matrix2){
        MatrixProduct mp = new MatrixProduct(matrix1, matrix2);

        long start = System.nanoTime();
        int[][] res = mp.multiplyMatrixLinear();

        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.print("Execution time for linear: ");
        printResult(timeElapsed, res);
    }

    private static void computeMatrixProductOnLines(int[][] matrix1, int[][] matrix2, int nrThreads) throws InterruptedException {
        MatrixProduct mp = new MatrixProduct(matrix1, matrix2);

        long start = System.nanoTime();
        int[][] res1 = mp.multiplyMatricesNThreadsOnLines(matrix1.length, matrix2[0].length, nrThreads);

        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.print("Execution time for threads on lines: ");
        printResult(timeElapsed, res1);
    }

    private static void computeMatrixProductOnColumns(int[][] matrix1, int[][] matrix2, int nrThreads) throws InterruptedException {
        MatrixProduct mp = new MatrixProduct(matrix1, matrix2);

        long start = System.nanoTime();
        int[][] res1 = mp.multiplyMatricesNThreadsOnColumns(matrix1.length, matrix2[0].length, nrThreads);

        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.print("Execution time for threads on columns: ");
        printResult(timeElapsed, res1);
    }

    private static void computeMatrixProductFromKtoK(int[][] matrix1, int[][] matrix2, int nrThreads) throws InterruptedException {
        MatrixProduct mp = new MatrixProduct(matrix1, matrix2);

        long start = System.nanoTime();
        int[][] res1 = mp.multiplyMatrixFromKToK(matrix1.length, matrix2[0].length, nrThreads);

        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.print("Execution time for threads from k to k: ");
        printResult(timeElapsed, res1);
    }
}
//
//          18	22	24	28	47	64	75	62	68
//        	32	29	21	17	38	57	69	31	31
//        	17	20	22	15	33	41	33	29	34
//        	46	45	39	31	68	95	101	61	65
//        	18	22	24	28	47	64	75	62	68
//        	32	29	21	17	38	57	69	31	31
//        	17	20	22	15	33	41	33	29	34
//        	46	45	39	31	68	95	101	61	65
//        	80	86	82	66	135	182	189	128	142

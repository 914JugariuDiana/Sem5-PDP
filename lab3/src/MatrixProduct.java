import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixProduct {
    private final int[][] matrix1;
    private final int[][] matrix2;
    private final int[][] resultMatrix;

    MatrixProduct(int[][] matrix1, int[][] matrix2) {
        this.matrix1 = matrix1;
        this.matrix2 = this.transverseMatrix(matrix2);
        this.resultMatrix = new int[matrix1.length][this.matrix2.length];
    }

    public int computeDotProduct(int[] a, int[] b) {
        int result = 0;

        for (int i = 0; i < a.length; i++)
            result += a[i] * b[i];

        return result;
    }

    public int[][] transverseMatrix(int[][] matrix) {
        int[][] transverse = new int[matrix[1].length][matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[1].length; j++) {
                transverse[j][i] = matrix[i][j];
            }
        }

        return transverse;
    }

    public int[][] multiplyMatrixLinear() {
        for (int i = 0; i < this.matrix1.length; i++) {
            int[] row = this.matrix1[i];

            for (int j = 0; j < this.matrix2.length; j++) {
                int[] column = this.matrix2[j];
                int result = this.computeDotProduct(row, column);
                this.resultMatrix[i][j] = result;
            }
        }

        return this.resultMatrix;
    }

    public void multiplyZoneAfterLines(int startLine, int startColumn, int numElements) {
        for (int i = startColumn; i < this.matrix2.length && numElements > 0; i++) {
            int[] row = this.matrix1[startLine];
            int[] column = this.matrix2[i];
            int result = this.computeDotProduct(row, column);
            this.resultMatrix[startLine][i] = result;
            numElements--;
        }

        for (int i = startLine + 1; i < this.matrix1.length && numElements > 0; i++) {
            int[] row = this.matrix1[i];

            for (int j = 0; j < this.matrix2.length && numElements > 0; j++) {
                int[] column = this.matrix2[j];
                int result = this.computeDotProduct(row, column);
                this.resultMatrix[i][j] = result;
                numElements--;
            }
        }
    }

    public void multiplyZoneAfterColumns(int startLine, int startColumn, int numElements) {
        int start = numElements;
        for (int i = startLine; i < this.matrix1.length && numElements > 0; i++) {
            int[] row = this.matrix1[i];
            int[] column = this.matrix2[startColumn];
            int result = this.computeDotProduct(row, column);
            this.resultMatrix[i][startColumn] = result;
            numElements--;
        }

        start = (numElements == start) ? startColumn : startColumn + 1;
        for (int i = start; i < this.matrix2.length && numElements > 0; i++) {
            int[] column = this.matrix2[i];

            for (int j = 0; j < this.matrix1.length && numElements > 0; j++) {
                int[] row = this.matrix1[j];
                int result = this.computeDotProduct(row, column);
                this.resultMatrix[j][i] = result;
                numElements--;
            }
        }
    }

    public int[][] multiplyMatricesNThreadsOnLines(int nrLines, int nrColumns, int nrThreads) throws InterruptedException {
        int startLine, startColumn;
        int nrElem = nrLines * nrColumns;
        startLine = startColumn = 0;
        Thread[] threads = new Thread[nrThreads];

        for (int i = 0; i < nrThreads; i++) {
            int finalStartLine = startLine;
            int finalStartColumn = startColumn;
            int ranThreads = i;


            if (i == nrThreads - 1) {
                int elemToCompute = nrElem / nrThreads;
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        multiplyZoneAfterLines(finalStartLine, finalStartColumn, elemToCompute);
                    }
                });
            } else {
                int elemToCompute = nrElem - (nrThreads * ranThreads);
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        multiplyZoneAfterLines(finalStartLine, finalStartColumn, elemToCompute);
                    }
                });
            }

            startLine += nrElem / nrThreads / nrColumns;
            startColumn += nrElem / nrThreads % nrColumns + 1;

        }

        for (int i = 0; i < nrThreads; i++)
            threads[i].start();

        for (int i = 0; i < nrThreads; i++)
            threads[i].join();

        return resultMatrix;
    }

    //Each task computes consecutive elements, going column after column.
    // This is like the previous example, but interchanging the rows with the columns: task 0 takes columns 0 and 1,
    // plus elements 0 and 1 from column 2, and so on.
    public int[][] multiplyMatricesNThreadsOnColumns(int nrLines, int nrColumns, int nrThreads) throws InterruptedException {
        int startLine, startColumn;
        int nrElem = nrLines * nrColumns;
        startLine = startColumn = 0;
        Thread[] threads = new Thread[nrThreads];

        for (int i = 0; i < nrThreads; i++) {
            int finalStartLine = startLine;
            int finalStartColumn = startColumn;
            int ranThreads = i;

            if (i == nrThreads - 1) {
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        multiplyZoneAfterColumns(finalStartLine, finalStartColumn, nrElem / nrThreads);
                    }
                });
            } else {

                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        multiplyZoneAfterColumns(finalStartLine, finalStartColumn, nrElem - (nrThreads * ranThreads));
                    }
                });
            }

            startColumn += nrElem / nrThreads / nrColumns;
            startLine += nrElem / nrThreads % nrColumns + 1;

        }

        for (int i = 0; i < nrThreads; i++)
            threads[i].start();

        for (int i = 0; i < nrThreads; i++)
            threads[i].join();

//        multiplyZoneAfterColumns(0, 0, 20);
//        multiplyZoneAfterColumns(2, 2, 20);
//        multiplyZoneAfterColumns(4, 4, 20);
//        multiplyZoneAfterColumns(6, 6, 21);

        return resultMatrix;
    }

//    Each task takes every k-th element (where k is the number of tasks), going row by row.
//    So, task 0 takes elements (0,0), (0,4), (0,8), (1,3), (1,7), (2,2), (2,6), (3,1), (3,5), (4,0), etc.

    public void multiplyFromKToK(int startLine, int startColumn, int numJumps) {
        int i, j, length1;
        i = startLine;
        length1 = matrix2.length;
        j = startColumn % length1;

        while (i < matrix1.length) {
            int[] row = this.matrix1[i];
            int[] column = this.matrix2[j];
            int result = this.computeDotProduct(row, column);
            this.resultMatrix[i][j] = result;

            j += numJumps;

            if (j >= length1) {
                i += j / length1;
                j = j % length1;
            }
        }
    }

    public int[][] multiplyMatrixFromKToK(int nrLines, int nrColumns, int nrThreads) throws InterruptedException {
        int startLine, startColumn;
        startLine = startColumn = 0;
        Thread[] threads = new Thread[nrThreads];

        for (int i = 0; i < nrThreads; i++) {
            int finalStartLine = startLine;
            int finalStartColumn = startColumn;

            threads[i] = new Thread(new Runnable() {
                int sizeJump = nrThreads;
                @Override
                public void run() {
                    multiplyFromKToK(finalStartLine, finalStartColumn, sizeJump);
                }
            });

            startColumn++;

            if (startColumn > nrColumns) {
                startColumn %= nrColumns;
                startLine += 1;
            }
        }

        for (int i = 0; i < nrThreads; i++)
            threads[i].start();

        for (int i = 0; i < nrThreads; i++)
            threads[i].join();

//        multiplyFromKToK(0, 0, 4);
//        multiplyFromKToK(0, 1, 4);
//        multiplyFromKToK(0, 2, 4);
//        multiplyFromKToK(0, 3, 4);

        return this.resultMatrix;
    }

    public int[][] multiplyMatricesThreadPoolRows(int nrLines, int nrColumns, int nrThreads) throws InterruptedException {
        int numTasks = nrThreads;
        Runnable[] tasks = new Runnable[numTasks];

        int nrElem = nrLines * nrColumns;
        int elementsPerTask = nrElem / numTasks;

        for (int i = 0; i < numTasks; i++) {
            int startLine = i * elementsPerTask / nrColumns;
            int startColumn = (i * elementsPerTask) % nrColumns;
            int numElements = (i == numTasks - 1) ? nrElem - (elementsPerTask * i) : elementsPerTask;

            tasks[i] = () -> multiplyZoneAfterLines(startLine, startColumn, numElements);
        }

        executeTasks(numTasks, 16, tasks);

        return resultMatrix;
    }

    public int[][] multiplyMatricesThreadPoolColumns(int nrLines, int nrColumns, int nrThreads) throws InterruptedException {
        int numTasks = nrThreads;
        Runnable[] tasks = new Runnable[numTasks];

        int nrElem = nrLines * nrColumns;
        int elementsPerTask = nrElem / numTasks;

        for (int i = 0; i < numTasks; i++) {
            int startLine = (i * elementsPerTask) / nrColumns;
            int startColumn = (i * elementsPerTask) % nrColumns;
            int numElements = (i == numTasks - 1) ? nrElem - (elementsPerTask * i) : elementsPerTask;

            tasks[i] = () -> multiplyZoneAfterColumns(startLine, startColumn, numElements);
        }

        executeTasks(numTasks, 16, tasks);

        return resultMatrix;
        }

    public int[][] multiplyMatricesThreadPoolKToK(int nrLines, int nrColumns, int nrThreads) throws InterruptedException {
        int numTasks = matrix1.length * matrix2[0].length / nrThreads;
        int startLine = 0;
        int startColumn = 0;
        Runnable[] tasks = new Runnable[numTasks];

        for (int i = 0; i < numTasks; i++) {
            startColumn = (startColumn + 1) % nrColumns;
            startLine = startLine + startColumn / nrColumns;

            int numJumps = numTasks;

            int finalStartLine = startLine;
            int finalStartColumn = startColumn;
            tasks[i] = () -> multiplyFromKToK(finalStartLine, finalStartColumn, numJumps);
        }

        executeTasks(numTasks, 16, tasks);

        return resultMatrix;
    }

    public void executeTasks(int numTasks, int threadPoolSize, Runnable[] tasks) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        for (int i = 0; i < numTasks; i++) {
            executor.execute(tasks[i]);
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    public int[][] getResultMatrix(){
        return resultMatrix;
    }
}

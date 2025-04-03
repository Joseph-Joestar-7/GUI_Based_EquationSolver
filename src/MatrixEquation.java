import java.util.Arrays;

public class MatrixEquation extends Equation {
    private double[][] A;
    private double[] B;

    public MatrixEquation(double[][] A, double[] B) {
        this.A = A;
        this.B = B;
    }

    @Override
    public double evaluate(double x) {
        throw new UnsupportedOperationException("Matrix equations cannot be evaluated for a single variable x.");
    }

    
    public EquationResult solveGaussian() {
        int n = B.length;
        double[][] A_copy = new double[n][n];
        for (int i = 0; i < n; i++) {
            A_copy[i] = Arrays.copyOf(A[i], n);
        }
        double[] B_copy = Arrays.copyOf(B, n);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n - 1; i++) {
            if (A_copy[i][i] == 0) {
                boolean pivotFound = false;
                for (int k = i + 1; k < n; k++) {
                    if (A_copy[k][i] != 0) {
                        double[] tempRow = A_copy[i];
                        A_copy[i] = A_copy[k];
                        A_copy[k] = tempRow;
                        double tempVal = B_copy[i];
                        B_copy[i] = B_copy[k];
                        B_copy[k] = tempVal;
                        pivotFound = true;
                        break;
                    }
                }
                if (!pivotFound) {
                    return new EquationResult("No unique solution (singular matrix encountered).");
                }
            }
            for (int j = i + 1; j < n; j++) {
                double factor = A_copy[j][i] / A_copy[i][i];
                for (int k = i; k < n; k++) {
                    A_copy[j][k] -= factor * A_copy[i][k];
                }
                B_copy[j] -= factor * B_copy[i];
            }
        }
        
        sb.append("Upper Triangular Matrix A:\n");
        for (int i = 0; i < n; i++) {
            sb.append(Arrays.toString(A_copy[i])).append("\n");
        }
        sb.append("\nUpdated Vector B:\n").append(Arrays.toString(B_copy)).append("\n\n");

        double[] X = new double[n];
        if (A_copy[n - 1][n - 1] == 0) {
            return new EquationResult("No unique solution (division by zero encountered).");
        }
        X[n - 1] = B_copy[n - 1] / A_copy[n - 1][n - 1];
        for (int i = n - 2; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A_copy[i][j] * X[j];
            }
            if (A_copy[i][i] == 0) {
                return new EquationResult("No unique solution (division by zero encountered).");
            }
            X[i] = (B_copy[i] - sum) / A_copy[i][i];
        }
        
        sb.append("Solution (Gaussian Elimination):\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("x%d = %.6f\n", (i + 1), X[i]));
        }
        return new EquationResult(sb.toString());
    }

    
    public EquationResult solveJacobi() {
        int n = B.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = B[i];
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Original Augmented Matrix:\n");
        for (int i = 0; i < n; i++) {
            sb.append(Arrays.toString(M[i])).append("\n");
        }
        sb.append("\n");

        double[][] E = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            double rowMax = 0.0;
            int index = 0;
            for (int j = 0; j < n; j++) {
                if (Math.abs(M[i][j]) > rowMax) {
                    rowMax = Math.abs(M[i][j]);
                    index = j;
                }
            }
            E[index] = Arrays.copyOf(M[i], n + 1);
        }
        M = E;

        sb.append("Rearranged Augmented Matrix:\n");
        for (int i = 0; i < n; i++) {
            sb.append(Arrays.toString(M[i])).append("\n");
        }
        sb.append("\n");

        if (isDiagonallyDominant(M)) {
            sb.append("Matrix is strictly diagonally dominant. Convergence is likely.\n\n");
        } else {
            sb.append("Matrix is NOT strictly diagonally dominant. Convergence is not guaranteed.\n");
            return new EquationResult(sb.toString());
        }

        double[] X = new double[n]; 
        double[] temp = new double[n];
        double[] prevX = new double[n];
        int maxIterations = 1000;
        double tolerance = 1e-6;
        int iter = 0;

        sb.append("Jacobi Iteration Table:\n");
        sb.append(String.format("%-10s", "Iter"));
        for (int i = 1; i <= n; i++) {
            sb.append(String.format("%-12s", "x" + i));
        }
        sb.append("\n");
        sb.append("--------------------------------\n");

        for (int i = 0; i < n; i++) {
            temp[i] = M[i][n];
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    temp[i] -= M[i][j] * X[j];
                }
            }
            temp[i] /= M[i][i];
        }
        System.arraycopy(temp, 0, X, 0, n);

        sb.append(formatTableRow(iter, X));

        while (iter < maxIterations && !converged(X, prevX, tolerance)) {
            iter++;
            System.arraycopy(X, 0, prevX, 0, n);
            for (int i = 0; i < n; i++) {
                temp[i] = M[i][n];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        temp[i] -= M[i][j] * prevX[j];
                    }
                }
                temp[i] /= M[i][i];
            }
            System.arraycopy(temp, 0, X, 0, n);
            sb.append(formatTableRow(iter, X));
        }

        sb.append("\nFinal Approximation (Jacobi):\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("x%d = %.6f\n", (i + 1), X[i]));
        }
        return new EquationResult(sb.toString());
    }

    public EquationResult solveSeidel() {
        int n = B.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = B[i];
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Original Augmented Matrix:\n");
        for (int i = 0; i < n; i++) {
            sb.append(Arrays.toString(M[i])).append("\n");
        }
        sb.append("\n");

        double[][] E = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            double rowMax = 0.0;
            int index = 0;
            for (int j = 0; j < n; j++) {
                if (Math.abs(M[i][j]) > rowMax) {
                    rowMax = Math.abs(M[i][j]);
                    index = j;
                }
            }
            E[index] = Arrays.copyOf(M[i], n + 1);
        }
        M = E;

        sb.append("Rearranged Augmented Matrix:\n");
        for (int i = 0; i < n; i++) {
            sb.append(Arrays.toString(M[i])).append("\n");
        }
        sb.append("\n");

        if (isDiagonallyDominant(M)) {
            sb.append("Matrix is strictly diagonally dominant. Convergence is likely.\n\n");
        } else {
            sb.append("Matrix is NOT strictly diagonally dominant. Convergence is not guaranteed.\n");
            return new EquationResult(sb.toString());
        }

        double[] X = new double[n]; 
        double[] prevX = new double[n];
        int maxIterations = 1000;
        double tolerance = 1e-6;
        int iter = 0;

        sb.append("Gauss-Seidel Iteration Table:\n");
        sb.append(String.format("%-10s", "Iter"));
        for (int i = 1; i <= n; i++) {
            sb.append(String.format("%-12s", "x" + i));
        }
        sb.append("\n");
        sb.append("--------------------------------\n");

        for (int i = 0; i < n; i++) {
            double sum = M[i][n];
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    sum -= M[i][j] * X[j];
                }
            }
            X[i] = sum / M[i][i];
        }
        sb.append(formatTableRow(iter, X));

        while (iter < maxIterations && !converged(X, prevX, tolerance)) {
            iter++;
            System.arraycopy(X, 0, prevX, 0, n);
            for (int i = 0; i < n; i++) {
                double sum = M[i][n];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        if (j < i) {
                            sum -= M[i][j] * X[j];
                        } else {
                            sum -= M[i][j] * prevX[j];
                        }
                    }
                }
                X[i] = sum / M[i][i];
            }
            sb.append(formatTableRow(iter, X));
        }

        sb.append("\nFinal Approximation (Gauss-Seidel):\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("x%d = %.6f\n", (i + 1), X[i]));
        }
        return new EquationResult(sb.toString());
    }

    private boolean converged(double[] X, double[] prevX, double tolerance) {
        for (int i = 0; i < X.length; i++) {
            if (Math.abs(X[i] - prevX[i]) > tolerance) {
                return false;
            }
        }
        return true;
    }

    private String formatTableRow(int iteration, double[] X) {
        StringBuilder row = new StringBuilder();
        row.append(String.format("%-10d", iteration));
        for (double v : X) {
            row.append(String.format("%-12.6f", v));
        }
        row.append("\n");
        return row.toString();
    }

    private boolean isDiagonallyDominant(double[][] M) {
        int n = M.length;
        for (int i = 0; i < n; i++) {
            double diag = Math.abs(M[i][i]);
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    sum += Math.abs(M[i][j]);
                }
            }
            if (diag <= sum) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EquationResult solve() {
        return solveGaussian(); 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Matrix Equation:\nA =\n");
        for (double[] row : A) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        sb.append("B = ").append(Arrays.toString(B));
        return sb.toString();
    }
}

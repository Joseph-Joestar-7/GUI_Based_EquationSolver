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

    /**
     * Solves the system using Gaussian Elimination.
     */
    public EquationResult solveGaussian() {
        int n = B.length;
        // Create copies to avoid modifying original arrays.
        double[][] A_copy = new double[n][n];
        for (int i = 0; i < n; i++) {
            A_copy[i] = Arrays.copyOf(A[i], n);
        }
        double[] B_copy = Arrays.copyOf(B, n);
        
        // Forward elimination with partial pivoting.
        for (int i = 0; i < n - 1; i++) {
            // Pivoting: if A_copy[i][i] is zero, swap with a row below.
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
            // Eliminate entries below pivot.
            for (int j = i + 1; j < n; j++) {
                double factor = A_copy[j][i] / A_copy[i][i];
                for (int k = i; k < n; k++) {
                    A_copy[j][k] -= factor * A_copy[i][k];
                }
                B_copy[j] -= factor * B_copy[i];
            }
        }
        
        // Back substitution.
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
        
        StringBuilder sb = new StringBuilder();
        sb.append("Gaussian Elimination Solution:\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("x%d = %.6f\n", (i + 1), X[i]));
        }
        return new EquationResult(sb.toString());
    }

    /**
     * Solves the system using the Jacobi iterative method.
     */
    public EquationResult solveJacobi() {
        int n = B.length;
        double[] X = new double[n];        // initial guess (all zeros)
        double[] newX = new double[n];
        int maxIterations = 1000;
        double tolerance = 1e-6;
        int iter = 0;
        
        while (iter < maxIterations) {
            for (int i = 0; i < n; i++) {
                double sum = B[i];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum -= A[i][j] * X[j];
                    }
                }
                newX[i] = sum / A[i][i];
            }
            // Check convergence.
            boolean converged = true;
            for (int i = 0; i < n; i++) {
                if (Math.abs(newX[i] - X[i]) > tolerance) {
                    converged = false;
                    break;
                }
            }
            System.arraycopy(newX, 0, X, 0, n);
            iter++;
            if (converged) {
                break;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Jacobi Method Solution (after ").append(iter).append(" iterations):\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("x%d = %.6f\n", (i + 1), X[i]));
        }
        return new EquationResult(sb.toString());
    }

    /**
     * Solves the system using the Gauss-Seidel iterative method.
     */
    public EquationResult solveSeidel() {
        int n = B.length;
        double[] X = new double[n];        // initial guess (all zeros)
        double[] prevX = new double[n];
        int maxIterations = 1000;
        double tolerance = 1e-6;
        int iter = 0;
        
        while (iter < maxIterations) {
            System.arraycopy(X, 0, prevX, 0, n);
            for (int i = 0; i < n; i++) {
                double sum = B[i];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        // Use updated X for j < i and previous for j > i.
                        sum -= A[i][j] * ((j < i) ? X[j] : prevX[j]);
                    }
                }
                X[i] = sum / A[i][i];
            }
            iter++;
            // Check convergence.
            boolean converged = true;
            for (int i = 0; i < n; i++) {
                if (Math.abs(X[i] - prevX[i]) > tolerance) {
                    converged = false;
                    break;
                }
            }
            if (converged) {
                break;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Gauss-Seidel Method Solution (after ").append(iter).append(" iterations):\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("x%d = %.6f\n", (i + 1), X[i]));
        }
        return new EquationResult(sb.toString());
    }
    
    /**
     * Default solve() returns the Gaussian elimination solution.
     */
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

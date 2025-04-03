import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class TranscendentalEquation extends Equation {
    private String expressionStr;
    private Expression expression;
    private double xMin, xMax, step, tol;
    private final int maxIter = 1000;
    private final double delta = 1e-8; 

    public TranscendentalEquation(String expressionStr, double xMin, double xMax, double step, double tol) {
        this.expressionStr = expressionStr;
        this.xMin = xMin;
        this.xMax = xMax;
        this.step = step;
        this.tol = tol;
        this.expression = new ExpressionBuilder(expressionStr)
                            .variable("x")
                            .build();
    }

    @Override
    public double evaluate(double x) {
        return expression.setVariable("x", x).evaluate();
    }

    public double f(double x) {
        return expression.setVariable("x", x).evaluate();
    }

    public double df(double x) {
        double fPlus = f(x + delta);
        double fMinus = f(x - delta);
        return (fPlus - fMinus) / (2 * delta);
    }

    public EquationResult solveBisection() {
        StringBuilder sb = new StringBuilder();
        double a = xMin, b = xMin;
        boolean found = false;
        for (double x = xMin; x < xMax; x += step) {
            double f1 = f(x);
            double f2 = f(x + step);
            if (f1 * f2 < 0) {
                a = x;
                b = x + step;
                found = true;
                break;
            }
        }
        if (!found) {
            return new EquationResult("No sign change found in the given interval.");
        }
        sb.append(String.format("Automatically chosen interval: [%.6f, %.6f]\n\n", a, b));
        sb.append(String.format("%-12s %-12s %-12s %-15s %-15s\n", "Iteration", "a", "b", "x=(a+b)/2", "f(x)"));
        sb.append("-------------------------------------------------------------------\n");
        int iterations = 0;
        while (Math.abs(b - a) > tol && iterations < maxIter) {
            double c = (a + b) / 2;
            double fc = f(c);
            sb.append(String.format("%-12d %-12.6f %-12.6f %-15.6f %-15.6f\n", iterations, a, b, c, fc));
            if (fc == 0.0) { 
                a = c;
                break;
            } else if (f(a) * fc < 0) {
                b = c;
            } else {
                a = c;
            }
            iterations++;
        }
        sb.append(String.format("\nRoot of the equation = %.6f\n", a));
        sb.append("\nIterations: " + iterations);
        return new EquationResult(sb.toString());
    }

    public EquationResult solveRegulaFalsi() {
        StringBuilder sb = new StringBuilder();
        double a = xMin, b = xMin;
        boolean found = false;
        for (double x = xMin; x < xMax; x += step) {
            double f1 = f(x);
            double f2 = f(x + step);
            if (f1 * f2 < 0) {
                a = x;
                b = x + step;
                found = true;
                break;
            }
        }
        if (!found) {
            return new EquationResult("No sign change found in the given interval.");
        }
        
        double fa = f(a);
        double fb = f(b);
        double prev = 0;
        int iterations = 0;
        double c = 0;
        
        sb.append(String.format("Automatically chosen interval: [%.6f, %.6f]\n\n", a, b));
        sb.append(String.format("%-12s %-12s %-12s %-12s %-12s %-12s\n", 
                    "Iteration", "a", "b", "h", "c", "f(c)"));
        sb.append("--------------------------------------------------------------------------\n");
        
        while (iterations < maxIter) {
            double h = ((b - a) * Math.abs(fa)) / (Math.abs(fa) + Math.abs(fb));
            c = a + h;
            double fc = f(c);
            
            sb.append(String.format("%-12d %-12.6f %-12.6f %-12.6f %-12.6f %-12.6f\n", 
                            iterations, a, b, h, c, fc));
            
            if (Math.abs(c - prev) < tol) {
                break;
            }
            prev = c;
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
            iterations++;
        }
        
        sb.append(String.format("\nRegula Falsi Method : Root = %.6f \nIterations: %d", c, iterations));
        return new EquationResult(sb.toString());
    }
    
    public EquationResult solveNewtonRaphson() {
        StringBuilder sb = new StringBuilder();
        double aInterval = 0, bInterval = 0;
        boolean found = false;
        for (double x = xMin; x < xMax; x += step) {
            if (f(x) * f(x + step) < 0) {
                aInterval = x;
                bInterval = x + step;
                found = true;
                break;
            }
        }
        if (!found) {
            return new EquationResult("No sign change found for initial guess.");
        }
        
        double x0 = (aInterval + bInterval) / 2;
        sb.append(String.format("Initial guess based on sign change : %.6f\n\n", x0));
        
        sb.append(String.format("%-12s %-12s %-12s %-12s %-12s\n", "Iteration", "x0", "f(x0)", "h", "x1"));
        sb.append("----------------------------------------------------------------\n");
        
        int iterations = 0;
        double x1 = x0;
        while (iterations < maxIter) {
            double f_x0 = f(x0);
            double df_x0 = df(x0);
            if (df_x0 == 0) {
                return new EquationResult("Zero derivative encountered. Cannot continue.");
            }
            double h = -f_x0 / df_x0;
            x1 = x0 + h;
            
            sb.append(String.format("%-12d %-12.6f %-12.6f %-12.6f %-12.6f\n",
                    iterations, x0, f_x0, h, x1));
            
            if (Math.abs(x1 - x0) < tol) {
                break;
            }
            x0 = x1;
            iterations++;
        }
        
        sb.append(String.format("\nNewton-Raphson Method: Root = %.6f \nIterations: %d", x1, iterations));
        return new EquationResult(sb.toString());
    }
         
    @Override
    public EquationResult solve() {
        return solveBisection();
    }
    
    @Override
    public String toString() {
        return "Transcendental Equation: f(x) = " + expressionStr;
    }
}

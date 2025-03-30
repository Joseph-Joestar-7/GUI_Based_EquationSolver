public class LogarithmicEquation extends Equation {
    private double c;

    public LogarithmicEquation(double c) {
        this.c = c;
    }

    @Override
    public double evaluate(double x) {
        if (x <= 0) {
            return Double.NaN;  // Indicate that x is outside the domain.
        }
        return Math.log(x) + c;
    }

    @Override
    public EquationResult solve() {
        double x = Math.pow(10, -c); // Solving log(x) + c = 0 => x = 10^(-c)
        return new EquationResult("x = " + x);
    }
}

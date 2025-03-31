public class LogarithmicEquation extends Equation {
    private double c;

    public LogarithmicEquation(double c) {
        this.c = c;
    }

    @Override
    public double evaluate(double x) {
        if (x <= 0) {
            return Double.NaN;  
        }
        return Math.log(x) + c;
    }

    @Override
    public EquationResult solve() {
        double x = Math.pow(10, -c); 
        return new EquationResult("x = " + x);
    }
}

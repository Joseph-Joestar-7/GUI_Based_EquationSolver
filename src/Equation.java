//Super Class Equation that will be extended by other Equations
//Contains Evaluate to evaluate results for different values of x for graphing


public abstract class Equation {
    public abstract double evaluate(double x);
    
    public abstract EquationResult solve();

}

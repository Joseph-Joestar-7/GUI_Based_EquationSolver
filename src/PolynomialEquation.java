import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PolynomialEquation extends Equation {
    private double[] coefficients;

    public PolynomialEquation(double... coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public double evaluate(double x) {
        double result = 0.0;
        int degree = coefficients.length - 1;
        for (int i = 0; i <= degree; i++) {
            result += coefficients[i] * Math.pow(x, i);
        }
        return result;
    }

    @Override
    public EquationResult solve() {
        int degree = coefficients.length - 1;

        if (degree == 0) {
            if (coefficients[0] == 0) {
                return new EquationResult("Infinite solutions (0 = 0).");
            } else {
                return new EquationResult("No solution (non-zero constant equation).");
            }
        }

        if (degree == 1) {
            double b = coefficients[0];
            double a = coefficients[1];
            if (a == 0) {
                return new EquationResult("No solution or infinite solutions.");
            }
            return new EquationResult("x = " + (-b / a));
        }

        if (degree == 2) {
            double c = coefficients[0];
            double b = coefficients[1];
            double a = coefficients[2];
            double discriminant = b * b - 4 * a * c;
            if (discriminant > 0) {
                double x1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                double x2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                return new EquationResult("x1 = " + x1 + ", x2 = " + x2);
            } else if (discriminant == 0) {
                double x = -b / (2 * a);
                return new EquationResult("x = " + x);
            } else {
                double realPart = -b / (2 * a);
                double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
                String root1 = realPart + " + " + imaginaryPart + "i";
                String root2 = realPart + " - " + imaginaryPart + "i";
                return new EquationResult("x1 = " + root1 + ", x2 = " + root2);
            }
        }

        List<Double> rationalRoots = new ArrayList<>();
        double[] currentCoeffs = Arrays.copyOf(coefficients, coefficients.length);
        List<Double> candidates = findPossibleRationalRoots();

        for (double candidate : candidates) {
            double sum = 0.0;
            for (int i = coefficients.length - 1; i >= 0; i--) {
                sum = sum * candidate + coefficients[i];
            }
            if (Math.abs(sum) < 1e-6) {
                rationalRoots.add(candidate);
                currentCoeffs = syntheticDivision(currentCoeffs, candidate);
            }
        }

        List<String> roots = new ArrayList<>();
        for (Double root : rationalRoots)
            roots.add(root.toString());

        int remainingDegree = currentCoeffs.length - 1;
        if (remainingDegree > 0) {
            if (remainingDegree == 1 || remainingDegree == 2) {
                for (var c : currentCoeffs) {
                    System.out.println(c);
                }
                PolynomialEquation remainingPoly = new PolynomialEquation(currentCoeffs);
                EquationResult result = remainingPoly.solve();
                String resStr = result.toString().trim();

                List<String> additionalRoots = parseRootsFromResult(resStr);

                roots.addAll(additionalRoots);
            } else {
                roots.addAll(durandKerner(currentCoeffs));
            }
        }

        StringBuilder resultStr = new StringBuilder();
        resultStr.append("Roots are: ");
        for (String root : roots)
            resultStr.append(root).append(", ");
        return new EquationResult(resultStr.toString());
    }

    private List<String> parseRootsFromResult(String resStr) {
        List<String> parsedRoots = new ArrayList<>();
        resStr = resStr.trim();
        if (resStr.startsWith("x = ")) {
            String valueStr = resStr.substring("x = ".length()).trim();
            parsedRoots.add(valueStr);

        } else if (resStr.startsWith("x1 = ") && resStr.contains("x2 = ")) {
            String[] parts = resStr.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("x1 = ")) {
                    String valueStr = part.substring("x1 = ".length()).trim();
                    parsedRoots.add(valueStr);

                } else if (part.startsWith("x2 = ")) {
                    String valueStr = part.substring("x2 = ".length()).trim();
                    parsedRoots.add(valueStr);

                }
            }
        }
        System.out.println(parsedRoots.size());

        return parsedRoots;
    }

    private List<Double> findPossibleRationalRoots() {
        List<Double> roots = new ArrayList<>();
        if (coefficients.length == 0)
            return roots;
        double constantTerm = coefficients[0];
        double leadingCoefficient = coefficients[coefficients.length - 1];
        Set<Double> possibleP = new HashSet<>();
        Set<Double> possibleQ = new HashSet<>();

        for (double p = 1; p <= Math.abs(constantTerm); p++) {
            if (Math.abs(constantTerm) % p == 0) {
                possibleP.add(p);
                possibleP.add(-p);
            }
        }
        possibleP.add(constantTerm);
        possibleP.add(-constantTerm);

        for (double q = 1; q <= Math.abs(leadingCoefficient); q++) {
            if (Math.abs(leadingCoefficient) % q == 0) {
                possibleQ.add(q);
                possibleQ.add(-q);
            }
        }
        possibleQ.add(leadingCoefficient);
        possibleQ.add(-leadingCoefficient);

        Set<Double> candidates = new HashSet<>();
        for (Double p : possibleP) {
            for (Double q : possibleQ) {
                if (q != 0)
                    candidates.add(p / q);
            }
        }
        roots.addAll(candidates);
        return new ArrayList<>(candidates);
    }

    private double[] syntheticDivision(double[] coefficients, double root) {
        int n = coefficients.length;
        double[] desc = new double[n];
        for (int i = 0; i < n; i++) {
            desc[i] = coefficients[n - 1 - i];
        }

        double[] quotientDesc = new double[n - 1];
        quotientDesc[0] = desc[0];
        for (int i = 1; i < n; i++) {
            double temp = quotientDesc[i - 1] * root;
            if (i < n - 1) {
                quotientDesc[i] = desc[i] + temp;
            }
        }

        double[] quotientAsc = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            quotientAsc[i] = quotientDesc[n - 2 - i];
        }
        return quotientAsc;
    }

    private Complex evaluateComplex(double[] coeffs, Complex x) {
        Complex result = new Complex(0, 0);
        for (int i = 0; i < coeffs.length; i++) {
            Complex term = new Complex(coeffs[i], 0);
            result = result.add(term.multiply(x.pow(i)));
        }
        return result;
    }

    private List<String> durandKerner(double[] coefficients) {
        int degree = coefficients.length - 1;
        double[] monicCoeffs = Arrays.copyOf(coefficients, coefficients.length);
        double leadingCoeff = monicCoeffs[degree];
        for (int i = 0; i <= degree; i++)
            monicCoeffs[i] /= leadingCoeff;

        List<Complex> roots = new ArrayList<>();
        List<String> rootsString = new ArrayList<>();
        double angle = 2 * Math.PI / degree;
        for (int k = 0; k < degree; k++) {
            roots.add(new Complex(Math.cos(angle * k), Math.sin(angle * k)));
        }

        int maxIter = 1000;
        double epsilon = 1e-6;
        for (int iter = 0; iter < maxIter; iter++) {
            List<Complex> newRoots = new ArrayList<>();
            boolean converged = true;
            for (int i = 0; i < degree; i++) {
                Complex zi = roots.get(i);
                Complex numerator = evaluateComplex(monicCoeffs, zi);
                Complex denominator = new Complex(1, 0);
                for (int j = 0; j < degree; j++) {
                    if (i != j)
                        denominator = denominator.multiply(zi.subtract(roots.get(j)));
                }
                Complex delta = numerator.divide(denominator);
                newRoots.add(zi.subtract(delta));
                if (delta.abs() > epsilon)
                    converged = false;
            }
            roots = newRoots;
            if (converged)
                break;
        }
        for (Complex r : roots)
            rootsString.add(r.toString());
        return rootsString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int degree = coefficients.length - 1;
        for (int i = 0; i <= degree; i++) {
            double coeff = coefficients[i];
            if (coeff != 0) {
                if (sb.length() > 0) {
                    sb.append(coeff > 0 ? " + " : " - ");
                    coeff = Math.abs(coeff);
                }
                if (degree - i == 0) {
                    sb.append(coeff);
                } else if (degree - i == 1) {
                    sb.append(coeff).append("x");
                } else {
                    sb.append(coeff).append("x^").append(degree - i);
                }
            }
        }
        return sb.toString();
    }
}

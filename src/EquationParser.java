//Parses Input and returns Equations

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class EquationParser {
    
    public static Equation parsePolynomial(String input) throws EquationParseException {
        input = input.replaceAll("\\s+", ""); 
        String[] sides = input.split("=");

        if (sides.length != 2 || !sides[1].equals("0")) {
            throw new EquationParseException(
                    "Invalid polynomial equation format. Expected format: 'ax^n + bx^n-1 + ... + c = 0'");
        }

        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)x\\^?(\\d*)|([+-]?\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(sides[0]);

        int maxDegree = -1;
        List<Double> coeffs = new ArrayList<>();

        while (matcher.find()) {
            double coefficient;
            int degree;

            if (matcher.group(3) != null) {
                coefficient = Double.parseDouble(matcher.group(3));
                degree = 0;
            } else {
                coefficient = matcher.group(1).isEmpty() || matcher.group(1).equals("+") ? 1
                        : matcher.group(1).equals("-") ? -1
                                : Double.parseDouble(matcher.group(1));

                degree = matcher.group(2).isEmpty() ? 1 : Integer.parseInt(matcher.group(2));
            }

            while (coeffs.size() <= degree) {
                coeffs.add(0.0);
            }
            coeffs.set(degree, coeffs.get(degree) + coefficient); 
            maxDegree = Math.max(maxDegree, degree);
        }

        double[] finalCoefficients = new double[maxDegree + 1];
        for (int i = 0; i <= maxDegree; i++) {
            finalCoefficients[i] = coeffs.get(i);
        }
        return new PolynomialEquation(finalCoefficients);
    }

    public static Equation parseLogarithmic(String input) throws EquationParseException {
        input = input.replaceAll("\\s+", "");
        Pattern pattern = Pattern.compile("log\\(x\\)([+-]\\d+)?=0");
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            String cStr = matcher.group(1);
            double c = (cStr == null) ? 0 : Double.parseDouble(cStr);
            return new LogarithmicEquation(c);
        } else {
            throw new EquationParseException("Failed to parse logarithmic equation. Expected format: log(x)+c=0");
        }
    }

    public static Equation parseMatrixEquation(String input) throws EquationParseException {
        String[] lines = input.split("\\n");
        if (lines.length < 2) {
            throw new EquationParseException("Matrix equation input must contain two lines: first for matrix A and second for vector B.");
        }
        String matrixLine = lines[0].trim();
        String vectorLine = lines[1].trim();
        
        String[] rowStrings = matrixLine.split(";");
        int n = rowStrings.length;
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            String[] tokens = rowStrings[i].trim().split("\\s+");
            if (tokens.length != n) {
                throw new EquationParseException("Matrix A must be square. Row " + (i + 1) + " has " + tokens.length + " elements, expected " + n + ".");
            }
            for (int j = 0; j < n; j++) {
                try {
                    A[i][j] = Double.parseDouble(tokens[j]);
                } catch (NumberFormatException e) {
                    throw new EquationParseException("Invalid number in matrix A at row " + (i + 1) + ", column " + (j + 1) + ".");
                }
            }
        }
        
        String[] tokensB = vectorLine.split("\\s+");
        if (tokensB.length != n) {
            throw new EquationParseException("Vector B must have " + n + " elements, but found " + tokensB.length + ".");
        }
        double[] B = new double[n];
        for (int i = 0; i < n; i++) {
            try {
                B[i] = Double.parseDouble(tokensB[i]);
            } catch (NumberFormatException e) {
                throw new EquationParseException("Invalid number in vector B at position " + (i + 1) + ".");
            }
        }
        return new MatrixEquation(A, B);
    }

    public static Equation parseTranscendentalEquation(String input) throws EquationParseException {
        String[] lines = input.split("\\n");
        if (lines.length < 2) {
            throw new EquationParseException("Transcendental equation input must contain at least two lines: the function and the parameters.");
        }
        String expr = lines[0].trim();
        String[] params = lines[1].trim().split("\\s+");
        if (params.length < 4) {
            throw new EquationParseException("The second line must contain four numbers: x_min, x_max, step size, and tolerance.");
        }
        try {
            double xMin = Double.parseDouble(params[0]);
            double xMax = Double.parseDouble(params[1]);
            double step = Double.parseDouble(params[2]);
            double tol = Double.parseDouble(params[3]);
            return new TranscendentalEquation(expr, xMin, xMax, step, tol);
        } catch (NumberFormatException e) {
            throw new EquationParseException("Invalid parameter format in transcendental equation.");
        }
    }

}

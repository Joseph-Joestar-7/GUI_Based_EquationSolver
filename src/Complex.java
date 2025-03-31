public class Complex {
    private final double real;
    private final double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complex add(Complex other) {
        return new Complex(real + other.real, imaginary + other.imaginary);
    }

    public Complex subtract(Complex other) {
        return new Complex(real - other.real, imaginary - other.imaginary);
    }

    public Complex multiply(Complex other) {
        double realPart = real * other.real - imaginary * other.imaginary;
        double imaginaryPart = real * other.imaginary + imaginary * other.real;
        return new Complex(realPart, imaginaryPart);
    }

    public Complex divide(Complex other) {
        double denominator = other.real * other.real + other.imaginary * other.imaginary;
        double realPart = (real * other.real + imaginary * other.imaginary) / denominator;
        double imaginaryPart = (imaginary * other.real - real * other.imaginary) / denominator;
        return new Complex(realPart, imaginaryPart);
    }

    public double abs() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    public Complex pow(int exponent) {
        Complex result = new Complex(1, 0);
        for (int i = 0; i < exponent; i++) {
            result = result.multiply(this);
        }
        return result;
    }

    @Override
    public String toString() {
        if (Math.abs(imaginary) < 1e-6) return String.format("%.4f", real);
        else if (Math.abs(real) < 1e-6) return String.format("%.4fi", imaginary);
        else return String.format("%.4f %s %.4fi", real, imaginary > 0 ? "+" : "-", Math.abs(imaginary));
    }
}
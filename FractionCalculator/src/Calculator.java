public class Calculator {

    public Fraction add(Fraction f1, Fraction f2) {
        int numerator = f1.getNumerator() * f2.getDenominator() + f2.getNumerator() * f1.getDenominator();
        int denominator = f1.getDenominator() * f2.getDenominator();
        return new Fraction(numerator, denominator);
    }

    public Fraction subtract(Fraction f1, Fraction f2) {
        int numerator = f1.getNumerator() * f2.getDenominator() - f2.getNumerator() * f1.getDenominator();
        int denominator = f1.getDenominator() * f2.getDenominator();
        return new Fraction(numerator, denominator);
    }

    public Fraction multiply(Fraction f1, Fraction f2) {
        int numerator = f1.getNumerator() * f2.getNumerator();
        int denominator = f1.getDenominator() * f2.getDenominator();
        return new Fraction(numerator, denominator);
    }

    public Fraction divide(Fraction f1, Fraction f2) {
        if (f2.getNumerator() == 0) {
            throw new ArithmeticException("Division durch 0 nicht erlaubt.");
        }
        int numerator = f1.getNumerator() * f2.getDenominator();
        int denominator = f1.getDenominator() * f2.getNumerator();
        return new Fraction(numerator, denominator);
    }
}

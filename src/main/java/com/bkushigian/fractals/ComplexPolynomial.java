package com.bkushigian.fractals;

import java.util.Arrays;

public class ComplexPolynomial {

    public static ComplexPolynomial zero = new ComplexPolynomial();

    public static ComplexPolynomial of(Complex...coefficients)  {
        return new ComplexPolynomial(coefficients);
    }

    public final int degree;
    protected Complex[] coefficients;

    /**
     * @param coefficients a coefficients array with {@code coefficients[n] := the coefficient of z^n}
     */
    public ComplexPolynomial(Complex...coefficients) {
        this.coefficients = coefficients.clone();
        degree = coefficients.length;
    }

    public Complex at(Complex pt) {
        Complex result = Complex.zero;
        Complex coef;
        for (int d = 0; d < degree; ++d) {
            coef = coefficients[d];
            result = result.plus(coef.times(pt.power(d)));
        }
        return result;
    }

    public ComplexPolynomial computeDerivative() {
        if (degree < 2) {
            return zero;
        }
        Complex[] d = new Complex[degree - 1];
        for (int i = 1; i < degree; ++i) {
            d[i-1] = coefficients[i].times(i);
        }
        return new ComplexPolynomial(d);
    }

    @Override
    public String toString() {
        if (coefficients.length == 0) return "<Zero ComplexPolynomial>";
        StringBuilder sb = new StringBuilder();
        for (int i = coefficients.length - 1; i > 0; --i) {
            sb.append('(').append(coefficients[i]).append(")z^").append(i).append(" + ");
        }
        return sb.append(coefficients[0]).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexPolynomial that = (ComplexPolynomial) o;
        return Arrays.equals(coefficients, that.coefficients);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(coefficients);
    }
}

package com.bkushigian.fractals;

import java.util.Objects;

/**
 * Complex number implementation for implementing a Mandelbrot set.
 * For whatever reason the JDK doesn't supply a complex number class, so we
 * have to make one.
 */
public class Complex extends Number {
    public static final Complex one = new Complex(1.0, 0.0);
    public static final Complex zero = new Complex(0.0, 0.0);
    public static final Complex negOne = new Complex(-1.0, 0.0);
    public static final Complex i =  new Complex(0.0, 1.0);
    public static final Complex negI =  new Complex(0.0, -1.0);


    public static Complex of(double re, double im) {
        return new Complex(re, im);
    }

    public static Complex im(double im) {
        return Complex.of(0.0, im);
    }

    public static Complex re(double re) {
        return Complex.of(re, 0.0);
    }

    public final double re;
    public final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Add two complex numbers
     */
    public Complex plus(Complex z) {
        return new Complex(re + z.re, im + z.im);
    }

    public Complex minus(Complex z) {
        return new Complex(re - z.re, im - z.im);
    }

    /**
     * Multiply two complex numbers together
     */
    public Complex times(Complex z) {
        return new Complex(re * z.re - im * z.im, re * z.im + im * z.re);
    }

    /**
     * Scalar multiplication
     */
    public Complex times(double x) {
        return new Complex(re * x, im * x);
    }

    /**
     * Get this * -1
     */
    public Complex neg(Complex z) {
        return new Complex(-re, -im);
    }

    /**
     * @return the complex conjugate of this
     */
    Complex conj() {
        return new Complex(re, -im);
    }

    Complex power(int n) {
        if (n == 0) return one;
        if (n < 0) return one.divide(this).power(-n);
        Complex c = Complex.one;
        int mask = 0x40000000;

        while (mask != 0) {
            c = c.times(c);
            if ((mask & n) != 0) {
                c = c.times(this);
            }
            mask >>= 1;
        }
        return c;
    }

    /**
     *
     * @param z nonzero
     * @return this divided by z
     */
    Complex divide(Complex z) {
        return times(z.conj()).times(1.0 / z.argSquared());
    }

    /**
     * Return the argument, the Euclidean distance from this from the point 
     * (0, 0) in the complex plane.
     */
    public double arg() {
        return Math.sqrt(argSquared());
    }

    /**
     * This is often as useful as the arg itself and doesn't involve an
     * expensive call to Math.sqrt()
     */
    public double argSquared() {
        return re * re + im * im;
    }

    public String toString() {
        return String.format("%.5f + %.5fi", re, im);
    }

    @Override
    public int intValue() {
        throw new RuntimeException("Complex numbers do not have canonical `intValue()`");
    }

    @Override
    public long longValue() {
        throw new RuntimeException("Complex numbers do not have canonical `longValue()`");
    }

    @Override
    public float floatValue() {
        throw new RuntimeException("Complex numbers do not have canonical `floatValue()`");
    }

    @Override
    public double doubleValue() {
        throw new RuntimeException("Complex numbers do not have canonical `doubleValue()`");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Complex complex = (Complex) o;
        return complex.re == re && complex.im == im;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }
}


package com.bkushigian.fractals;

import org.junit.Test;

import static org.junit.Assert.*;

public class ComplexPolynomialTest {
    @Test
    public void testAt1() {
        assertEquals(Complex.zero, ComplexPolynomial.zero.at(Complex.one));
        assertEquals(1.0, ComplexPolynomial.of(Complex.one).at(Complex.one).re, 0.00001);

        // p = 1 + z + z^2
        ComplexPolynomial p = ComplexPolynomial.of(Complex.one, Complex.one, Complex.one);
        Complex z = p.at(Complex.one);
        assertEquals(3.0, z.re, 0.0001);
        assertEquals(0.0, z.im, 0.0001);

        z = p.at(Complex.i);
        assertEquals(0.0, z.re, 0.0001);
        assertEquals(1.0, z.im, 0.0001);

        z = p.at(Complex.of(1.0, 1.0));
        assertEquals(2.0, z.re, 0.0001);
        assertEquals(3.0, z.im, 0.0001);

        z = p.at(Complex.of(3.0, -2.0));
        assertEquals(9.0, z.re, 0.0001);
        assertEquals(-14.0, z.im, 0.0001);
    }

    @Test
    public void testAt2() {
        // p = 1 + -z + z^2 -z^3
        ComplexPolynomial p = ComplexPolynomial.of(Complex.one, Complex.negOne, Complex.one, Complex.negOne);
        Complex z = p.at(Complex.one);
        assertEquals(0.0, z.re, 0.0001);
        assertEquals(0.0, z.im, 0.0001);

        z = p.at(Complex.i);
        assertEquals(0.0, z.re, 0.0001);
        assertEquals(0.0, z.im, 0.0001);

        z = p.at(Complex.of(1.0, 1.0));
        assertEquals(2.0, z.re, 0.0001);
        assertEquals(-1.0, z.im, 0.0001);

        z = p.at(Complex.of(3.0, -2.0));
        assertEquals(12.0, z.re, 0.0001);
        assertEquals(36.0, z.im, 0.0001);
    }

    @Test public void test_at3() {
        ComplexPolynomial p =  ComplexPolynomial.of(Complex.negOne, Complex.zero, Complex.one);
        ComplexPolynomial dp = p.computeDerivative();

        Complex z = Complex.re(0.5);


        double[] zs = {1.25, 1.025, 1.0003, 1.0000};
        double[] pzs = {-0.75, 0.5625, 0.0506, 0.00061};
        double[] dpzs = {1.0, 2.5, 2.05, 2.0006};
        for (int i = 0; i < 4; ++i) {
            final Complex p_z = p.at(z);
            final Complex dp_z = dp.at(z);
            z = z.minus(p_z.divide(dp_z));
            System.out.println(z.toString() + ", " + p_z + ", " + dp_z);

            assertEquals(pzs[i], p_z.re, 0.0001);
            assertEquals(dpzs[i], dp_z.re, 0.0001);
            assertEquals(zs[i], z.re, 0.0001);
        }



    }

    @Test
    public void test_derivative() {
        ComplexPolynomial p = ComplexPolynomial.of(Complex.one, Complex.negOne, Complex.one, Complex.negOne);
        ComplexPolynomial dp = ComplexPolynomial.of(Complex.negOne, Complex.of(2,0), Complex.of(-3, 0));
        assertEquals(dp, p.computeDerivative());
    }

    @Test
    public void test_derivative2() {
        ComplexPolynomial p = ComplexPolynomial.of(Complex.of(1,1), Complex.of(2,3), Complex.of(1,0));
        ComplexPolynomial dp = ComplexPolynomial.of(Complex.of(2,3), Complex.of(2, 0));
        assertEquals(dp, p.computeDerivative());
    }

}
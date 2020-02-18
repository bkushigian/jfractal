package com.bkushigian.fractals;

import org.junit.Test;

import static org.junit.Assert.*;

public class NewtonApproximatorTest {
    @Test
    public void test_findAttractor1() {
        // p = z^2 - 1
        ComplexPolynomial p = ComplexPolynomial.of(Complex.negOne, Complex.zero, Complex.one);
        NewtonApproximator na = new NewtonApproximator(p);
        Complex a = na.findAttractor(Complex.re(0.5), 0.2, 500);
        assertNotNull(a);
        assertEquals(a.re, Complex.one.re, 0.2);
        assertEquals(a.im, Complex.one.im, 0.2);

    }

}
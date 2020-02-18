package com.bkushigian.fractals;

import org.junit.Test;

import static org.junit.Assert.*;

public class ComplexTest {

    @Test
    public void testAdd() {
        Complex z = Complex.of(11.0, 1).plus(Complex.of(2.0, 2.0));
        assertEquals(13.0, z.re, 0.001);
        assertEquals(3.0, z.im, 0.001);

        z = z.plus(Complex.zero);
        assertEquals(13.0, z.re, 0.001);
        assertEquals(3.0, z.im, 0.001);

        z =  z.plus(Complex.of(-0.5, 0.5));
        assertEquals(12.5, z.re, 0.001);
        assertEquals(3.5, z.im, 0.001);
    }

    @Test
    public void testPower1() {
        Complex z = Complex.one.power(1);
        assertEquals(1.0, z.re, 0.001);
        assertEquals(0.0, z.im, 0.001);

        z = Complex.one.power(2);
        assertEquals(1.0, z.re, 0.001);
        assertEquals(0.0, z.im, 0.001);
    }
    @Test
    public void testPower2() {

        Complex z = Complex.i.power(2);
        assertEquals(-1.0, z.re, 0.001);
        assertEquals(0.0, z.im, 0.001);

        z = Complex.of(2.0, 0.0).power(3);
        assertEquals(8.0, z.re, 0.001);
        assertEquals(0.0, z.im, 0.001);

        z = Complex.i.power(3);
        assertEquals(0.0, z.re, 0.001);
        assertEquals(-1.0, z.im, 0.001);

        z = Complex.i.power(4);
        assertEquals(1.0, z.re, 0.001);
        assertEquals(0.0, z.im, 0.001);

        z = Complex.i.power(32);
        assertEquals(1.0, z.re, 0.001);
        assertEquals(0.0, z.im, 0.001);
    }

    @Test public void test_divide1() {
        Complex a = Complex.of(1,1);
        Complex b = Complex.of(2, 2);
        Complex c = a.divide(b);
        assertEquals(0.5, c.re, 0.0001);
        assertEquals(0.0, c.im, 0.0001);
    }

    @Test public void test_conjugate1() {
        assertEquals(Complex.of(3.0, 4.0), Complex.of(3.0, -4.0).conj());
        assertEquals(Complex.of(1000.0, 11.0), Complex.of(1000.0, -11.0).conj());
        assertEquals(Complex.of(0.0,  1.0), Complex.of(0.0, -1.0).conj());
        assertEquals(Complex.of(12.0,  0.0), Complex.of(12.0, 0.0).conj());
    }

    @Test public void test_equals() {
        System.out.println("-0.0 < 0.0? " + (-0.0 < 0.0));
        assertEquals(Complex.of(1.0, 1.0), Complex.of(1.0, 1.0));
        assertEquals(Complex.of(0.0, 1.0), Complex.of(-0.0, 1.0));
        assertEquals(Complex.of(1.0, -0.0), Complex.of(1.0, 0.0));
    }
}
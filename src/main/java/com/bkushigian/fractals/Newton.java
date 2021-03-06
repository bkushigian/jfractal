package com.bkushigian.fractals;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Newton extends ComplexFractal {

    /**
     * Polynomial we are working over
     */
    protected final ComplexPolynomial p;

    private final NewtonApproximator newton;

    /**
     * Roots of polynomial, as they are discovered.
     */
    protected final List<Complex> roots;
    protected double xMin = -2.0;
    protected double xMax = 2.0;
    protected double minDelta = 0.001;

    /**
     * the simplest constructor that handles default arguments
     */
    public Newton() {
        this(1000, 700);
    }

    public Newton(int width, int height) {
        this(width, height, null);
        updateColors();
    }

    public Newton(int width, int height, ColorScheme colorScheme) {
        this(width, height, colorScheme, ComplexPolynomial.of(Complex.negOne, Complex.zero, Complex.zero, Complex.one));
    }

    public Newton(int width, int height, ColorScheme colorScheme, ComplexPolynomial p) {
        super(width, height, colorScheme, -2.0, 2.0, -2.0, 2.0);
        maxIterations = 256;
        if (colorScheme == null) {
            colorScheme = new ColorScheme(
                    27, 155,
                    30, 155,
                    31, 155);
        }
        this.colorScheme = colorScheme;
        this.p = p;
        this.newton = new NewtonApproximator(p);
        this.roots = new ArrayList<>(this.p.degree);
        showKey = false;
    }

    /**
     * Draw the key in the bottom left hand corner detailing relevant data.
     */
    @Override
    protected void drawKey(Graphics g) {
        String[] toDraw = new String[] {
                String.format("zoom:    %.6f", zoomDepth),
                String.format("x-range: %.6f, %.6f", xMin, xMax),
                String.format("y-range: %.6f, %.6f", yMin, yMax),
        };
        drawKey(g, toDraw);
    }

    @Override
    public int calculateIterations(Complex z, int max) {
        Complex attractor = newton.findAttractor(z);
        if (attractor == null) return -1;
        return newton.iterations;

    }

    @Override
    public Color getColor(int colorNumber) {
        if (colorNumber < 0) {
            return Color.BLACK;
        }
        return super.getColor(colorNumber);
    }

    public static void main(String[] args) {
        DisplayWindow window = new DisplayWindow();
        window.addPanel(new Newton(1200, 1200, new ColorScheme(
                2, 155,
                0, 0,
                24, 32
        ), ComplexPolynomial.nthRootsOfUnity(7)));
        window.showFrame();
    }

    @Override
    public String getFractalName() {
        return "newton";
    }
}

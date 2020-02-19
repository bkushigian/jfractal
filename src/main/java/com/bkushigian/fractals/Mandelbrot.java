package com.bkushigian.fractals;

public class Mandelbrot extends ComplexFractal {


    public Mandelbrot() {
        this(700, 700);
    }

    public Mandelbrot(int width, int height) {
        this(width, height, null);
        colorScheme = new ColorScheme(12, 0, 24, 155, 0, 0);
        updateColors();
    }

    public Mandelbrot(int width, int height, ColorScheme colorScheme) {
        super(width, height, colorScheme);
        this.fractalName = "mandelbrot";
    }

    /**
     * Compute the mandelbrot number of a point. This is defined to be the number
     * of iterations needed to break out of the circle of radius 2, where a
     * maximum number is set to avoid infinite loops.
     */
    @Override
    public int calculateIterations(Complex z, int max) {
        Complex c = z;
        int iterations = 0;
        while (z.argSquared() < 4 && iterations < maxIterations) {
            z = c.plus(z.times(z));    // z = z^2 + c
            ++iterations;
        }
        return iterations;
    }

    public static void main(String[] args) {
        Mandelbrot mandel = new Mandelbrot(1200, 1200);
        DisplayWindow window = new DisplayWindow();
        window.addPanel(mandel);
        window.showFrame();
    }
}

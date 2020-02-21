package com.bkushigian.fractals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Julia extends ComplexFractal {

    Complex c;
    double cDelta = 1.0/ 128.0;
    JButton moveCLeft;
    JButton moveCRight;
    JButton moveCUp;
    JButton moveCDown;
    JButton increaseCDelta;
    JButton decreaseCDelta;
    protected double xMin = -2.0;
    protected double xMax = 2.0;

    /**
     * the simplest constructor that handles default arguments
     */
    public Julia() {
        this(1000, 700);
    }

    public Julia(int width, int height) {
        this(width, height, new ColorScheme.BWColorScheme());
    }

    public Julia(int width, int height, ColorScheme colorScheme) {
        this(width, height, colorScheme, null);
        this.c = new Complex(0.365, 0.11);
    }

    public Julia(int width, int height, ColorScheme colorScheme, Complex c) {
        super(width, height, colorScheme, -2.0, 2.0, -2.0, 2.0);
        this.c = c;
        maxIterations = 128;
        moveCUp = registerButton("C↑", "move-C-up");
        moveCDown = registerButton("C↓", "move-C-down");
        moveCLeft = registerButton("C←", "move-C-left");
        moveCRight = registerButton("C→", "move-C-right");
        increaseCDelta = registerButton("++ΔC", "increase-delta");
        decreaseCDelta = registerButton("--ΔC", "decreaseDelta");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == moveCUp) {
            updated = true;
            c = new Complex(c.re, c.im + cDelta);
        } else if (e.getSource() == moveCDown) {
            updated = true;
            c = new Complex(c.re, c.im - cDelta);
        }
        else if (e.getSource() == moveCLeft) {
            updated = true;
            c = new Complex(c.re - cDelta, c.im);
        } else if (e.getSource() == moveCRight) {
            updated = true;
            c = new Complex(c.re + cDelta, c.im);
        } else if (e.getSource() == increaseCDelta) {
            cDelta *= 2;
        } else if (e.getSource() == decreaseCDelta) {
            double minDelta = 1.0/2048;
            double newCDelta = cDelta / 2;
            cDelta = newCDelta <= minDelta ? minDelta : newCDelta;
        }
        super.actionPerformed(e);
    }

    @Override
    public String getFractalName() {
        return "julia";
    }

    /**
     * Draw the key in the bottom left hand corner detailing relevant data.
     */
    @Override
    protected void drawKey(Graphics g) {
        String[] toDraw = new String[] {
                "maxIters: " + maxIterations,
                String.format("zoom:    %.6f", zoomDepth),
                String.format("x-range: %.6f, %.6f", xMin, xMax),
                String.format("y-range: %.6f, %.6f", yMin, yMax),
                "C: " + c,
                "ΔC: " + cDelta
        };
        drawKey(g, toDraw);
    }

    /**
     * Compute the mandelbrot number of a point. This is defined to be the number
     * of iterations needed to break out of the circle of radius 2, where a
     * maximum number is set to avoid infinite loops.
     */
    @Override
    public int calculateIterations(Complex z, int max) {
        int iterations = 0;
        while (z.argSquared() < 4 && iterations < maxIterations) {
            z = c.plus(z.times(z));    // z = z^2 + c
            ++iterations;
        }
        return iterations;
    }

    public static void main(String[] args) {
        Julia julia = new Julia(1000, 750);
        DisplayWindow window = new DisplayWindow();
        window.addPanel(julia);
        window.showFrame();
    }
}

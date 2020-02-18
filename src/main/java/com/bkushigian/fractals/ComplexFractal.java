package com.bkushigian.fractals;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * An iterative fractal in the complex plane, this takes care of most of the work
 * involved with making the Mandelbrot and Julia sets.
 *
 * Certain functionality, such as drawing a key with relevant data, included.
 */
public abstract class ComplexFractal extends JPanel implements ActionListener {

    private final static int NOT_SHIFTED = 0,
                             SHIFTED_DOWN = 1,
                             SHIFTED_UP = 2,
                             SHIFTED_LEFT = 3,
                             SHIFTED_RIGHT = 4;

    protected final int numWorkers = 12;
    /**
     * How much have we shifted since the last compute()? This makes moving up/down/left/right
     * more efficient.
     */
    private int shiftedStatus = NOT_SHIFTED;
    private int shiftedAmount = 0;

    /**
     * Width and height of window
     */
    protected int width;
    protected int height;

    // has this been updated since the last time iterMatrix was computed?
    protected boolean updated = true;
    private int[][] iterMatrix;

    /**
     * How much to zoom in/out by
     */
    protected final double zoomInFactor = 0.66666666;
    protected final double zoomOutFactor = 1.5;
    protected double zoomDepth = 1.0;   // How far zoomed in are we?

    protected int maxIterations = 256;

    protected Color[] colors;

    // Key related constants
    protected Color keyBackground = new Color(156, 156, 156, 160);
    protected Color keyBorder = new Color(32, 32, 220, 160);
    protected int keyLineSeparation = 3;
    protected int keyLineHeight = -1;
    protected boolean showKey = true;

    /**
     * Number of pixels to move by when left/right/up/down is clicked
     */
    final double shiftAmount = 200;

    /**
     * The difference in x/y values between adjacent pixels.
     *
     * WARNING: This needs to be recalculated at EVERY screen resize!!
     * This may be done with method calculateDeltas();
     */
    protected double delta;

    /**
     * These values define the visible region of the complex plain. Note that the y-values
     * are derived from the x-values and the width/height.
     */
    double xMax = 1.2, yMax = 0.0, xMin = -2.1, yMin = 0.0, yCenter = 0.0;

    /**
     * This determines the colors of the colors
     */
    ColorScheme colorScheme;

    JButton left ;
    JButton right;
    JButton up;
    JButton down;
    JButton in;
    JButton out;
    JButton increaseMaxIter;
    JButton decreaseMaxIter;
    JButton exit;
    JButton toggleKey;


    java.util.List<JButton> buttons;

    protected ComplexFractal(int width, int height, ColorScheme colorScheme) {
        this(width, height, colorScheme, -2.1, 1.2);
    }

    protected ComplexFractal(int width, int height, ColorScheme colorScheme, double xmin, double xmax) {
        this.width = width;
        this.height = height;
        this.colorScheme = colorScheme;
        iterMatrix = new int[height][width];
        xMin = xmin;
        xMax = xmax;
        buttons = new LinkedList<>();
        left  = new JButton("⇐");
        right = new JButton("⇒");
        up    = new JButton("⇑");
        down  = new JButton("⇓");
        in    = new JButton("\uD83D\uDD0E +");
        out   = new JButton("\uD83D\uDD0E -");
        exit  = new JButton("✗");
        increaseMaxIter = new JButton("▩");
        decreaseMaxIter = new JButton("□");
        toggleKey = new JButton("\uD83D\uDD11");

        addButton(left, right, up, down, in, out, increaseMaxIter, decreaseMaxIter, toggleKey, exit);
        setPreferredSize(new Dimension(width, height));
        calculateDeltas();

        colors = null;
        if (this.colorScheme != null) {
            colors = new Color[maxIterations];
            updateColors();
        }
    }

    /**
     * Track buttons, adding them to the buttons list and to the
     * @param bs
     */
    protected void addButton(JButton ... bs ) {
        for (JButton b : bs) {
            buttons.add(b);
            add(b);
            b.addActionListener(this);
        }
    }

    /**
     * Get a point in the complex plane from a pixel position.
     */
    protected Complex pointFromPixel(int x, int y) {
        return new Complex(xMin + delta * x, yMax - delta * y);
    }

    /**
     * After a window resize or shift, recalculate derived data.
     */
    protected void calculateDeltas() {
        // First, calculate delta
        delta = (xMax - xMin) / width;

        // Then, calculate yMin and yMax
        double yRange = delta * height / 2;
        yMax = yCenter + yRange;
        yMin = yCenter - yRange;
    }

    /**
     * Calculate the number of iterations until a fixed point is reached. This is used for coloring.
     * @param pt
     * @param max
     * @return
     */
    public abstract int calculateIterations(Complex pt, int max);

    public int calculateIterations(Complex pt) {
        return calculateIterations(pt, maxIterations);
    }

    public void actionPerformed(ActionEvent e){
        System.out.println("ActionEvent: " + e.getActionCommand());
        System.out.println(">>> updated = " + updated);
        if (e.getSource() == left) {
            double shift = delta * shiftAmount;
            xMax -= shift;
            xMin -= shift;
            updated = true;

        } else if (e.getSource() == right) {
            double shift = delta * shiftAmount;
            xMax += shift;
            xMin += shift;
            updated = true;

        } else if (e.getSource() == up) {
            double shift = delta * shiftAmount;
            yCenter += shift;
            updated = true;

        } else if (e.getSource() == down) {
            double shift = delta * shiftAmount;
            yCenter -= shift;
            updated = true;
        } else if (e.getSource() == in) {
            double center = (xMin + xMax) / 2;
            double diff = xMax - center;
            xMin = center - zoomInFactor * diff ;
            xMax = center + zoomInFactor * diff ;

            center = (yMin + yMax) / 2;
            diff = yMax - center;
            yMin = center - zoomInFactor * diff ;
            yMax = center + zoomInFactor * diff ;
            zoomDepth *= zoomOutFactor;
            updated = true;
        } else if (e.getSource() == out) {
            double center = (xMin + xMax) / 2;
            double diff = xMax - center;
            xMin = center - zoomOutFactor * diff ;
            xMax = center + zoomOutFactor * diff ;

            center = (yMin + yMax) / 2;
            diff = yMax - center;
            yMin = center - zoomOutFactor * diff ;
            yMax = center + zoomOutFactor * diff ;
            zoomDepth *= zoomInFactor;
            updated = true;
        } else if (e.getSource() == exit) {
            System.exit(0);
        }
        else if (e.getSource() == increaseMaxIter) {
            maxIterations = maxIterations < 64 ? 64 : maxIterations + 64;
            updateColors();
            updated = true;
        }
        else if (e.getSource() == decreaseMaxIter) {
            maxIterations = maxIterations < 64 ? 128: maxIterations - 64;
        } else if (e.getSource() == toggleKey) {
            showKey ^= true;
            updated = true;
        }

        System.out.println("<<< updated = " + updated);
        calculateDeltas();
        if(updated) repaint();
    }

    public void compute() {
        System.out.println("compute: updated="+updated);
        if (!updated) return;
        updated = false;
        final java.util.List<Thread> threads = new ArrayList<>(numWorkers);

        final int delta = height / numWorkers;
        for (int w = 0; w < numWorkers; ++w) {
            int start = w*delta;
            int end = Math.min((w+1)*delta, height);
            final Thread t = new Thread(new IterWorker(0, width, start, end));
            threads.add(t);
            t.start();
        }
        for (Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Uncaught interruption of thread");
            }
        }
    }

    public void paintComponent(final Graphics g) {
        if (!updated) return;
        super.paintComponent(g);
        compute();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                final Complex z = pointFromPixel(i,j);
                final int iters = calculateIterations(z);
                final Color c = getColor(iters);
                g.setColor(c);
                g.fillRect(i,j,1,1);    // A super dumb way to draw a pixel
            }
        }
        drawKey(g);
    }

    /**
     * We are mapping ints to colors;
     * @param colorNumber a non-negative integer
     * @return the color corresponding to {@code colorNumber}
     */
    public Color getColor(int colorNumber) {
        final int c = colorNumber % maxIterations;
        return colors[c];
    }

    protected void drawKey(Graphics g) {

        if (showKey) {
            final String[] toDraw = new String[]{
                    "maxIters: " + maxIterations,
                    String.format("zoom:    %.6f", zoomDepth),
                    String.format("x-range: %.6f, %.6f", xMin, xMax),
                    String.format("y-range: %.6f, %.6f", yMin, yMax)
            };

            drawKey(g, toDraw);
        }
    }


    protected void drawKey(Graphics g, String[] lines) {
        if (showKey) {
            if (keyLineHeight < 0) {
                keyLineHeight = g.getFontMetrics().getMaxAscent();
            }

            int lineHeight = keyLineHeight + keyLineSeparation;
            int h = lineHeight * lines.length + 8;

            int maxLineWidth = 0;
            g.setFont(new Font("default", Font.BOLD, 15));
            for (String s : lines) {
                maxLineWidth = Math.max(maxLineWidth, g.getFontMetrics().stringWidth(s));
            }
            maxLineWidth += 22;
            g.setColor(keyBorder);   // Border color
            g.fillRect(2, height - h - 20, maxLineWidth + 6, h + 16);
            g.setColor(keyBackground);
            g.fillRect(5, height - h - 17, maxLineWidth, h + 10);

            h -= 3;

            g.setColor(Color.black);
            for (String s : lines) {
                g.drawString(s, 18, height - h);
                h -= lineHeight;
            }
        }
    }

    void updateColors() {
        if (colors == null) {
            colors = new Color[maxIterations];
            for (int i = 0; i < maxIterations; ++i) {
                colors[i] = colorScheme.getColor(i);
            }
        }

        if (maxIterations > colors.length - 1) {
            Color[] newColors = new Color[maxIterations];
            for (int i = 0; i < colors.length; ++i) {
                newColors[i] = colors[i];
            }
            for (int i = colors.length; i < maxIterations; ++i){
                newColors[i] = colorScheme.getColor(i);
            }
            colors = newColors;
        }
    }


    public class ColorScheme {
        final int rScale, gScale, bScale, rOffset, gOffset, bOffset;
        public ColorScheme(int redScale, int redOffset, int greenScale, int greenOffset, int blueScale, int blueOffset) {
            rScale = redScale;
            gScale = greenScale;
            bScale = blueScale;
            rOffset = redOffset;
            gOffset = greenOffset;
            bOffset = blueOffset;
        }

        int r(int iter) {
            return iter == maxIterations ?
                    0:
                    (iter * rScale + rOffset) % 256;
        }

        int g(int iter) {
            return iter == maxIterations ?
                    0:
                    (iter * gScale + gOffset) % 256;
        }

        int b(int iter) {
            return iter == maxIterations ?
                    0:
                    (iter * bScale + bOffset) % 256;
        }

        Color getColor(int iter) {
            return new Color(r(iter), g(iter), b(iter));
        }
    }

    class IterWorker implements Runnable {

        private int widthStart;
        private int widthEnd;
        private int heightStart;
        private int heightEnd;

        IterWorker(int widthStart, int widthEnd, int heightStart, int heightEnd) {
            this.widthStart = widthStart;
            this.widthEnd = widthEnd;
            this.heightStart = heightStart;
            this.heightEnd = heightEnd;
        }

        @Override
        public void run() {
            for (int j = heightStart; j < heightEnd; ++j) {
                for (int i = widthStart; i < widthEnd; ++i ) {
                    Complex z = pointFromPixel(i,j);
                    int iters = calculateIterations(z);
                    iterMatrix[j][i] = iters;
                }
            }

        }
    }
}

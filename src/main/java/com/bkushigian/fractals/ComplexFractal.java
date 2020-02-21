package com.bkushigian.fractals;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * An iterative fractal in the complex plane, this takes care of most of the work
 * involved with making the Mandelbrot and Julia sets.
 *
 * Certain functionality, such as drawing a key with relevant data, included.
 */
public abstract class ComplexFractal extends Fractal {

    private final static int NOT_SHIFTED = 0,
                             SHIFTED_DOWN = 1,
                             SHIFTED_UP = 2,
                             SHIFTED_LEFT = 3,
                             SHIFTED_RIGHT = 4;


    /**
     * How much have we shifted since the last compute()? This makes moving up/down/left/right
     * more efficient.
     */
    private int shiftedStatus = NOT_SHIFTED;
    private int shiftedAmount = 0;


    /**
     * height x width, with origin (0,0) in to left corner
     */
    private int[][] iterMatrix;

    protected int maxIterations = 256;

    protected Color[] colors;

    protected int keyLineSeparation = 3;
    protected int keyLineHeight = -1;

    /**
     * This determines the colors of the colors
     */
    protected ColorScheme colorScheme;

    private final JButton increaseMaxIter;
    private final JButton decreaseMaxIter;

    protected ComplexFractal(int width, int height, ColorScheme colorScheme) {
        this(width, height, colorScheme, -2.1, 1.2, 1.1, 1.1);
    }

    protected ComplexFractal(int width, int height, ColorScheme colorScheme, double xmin, double xmax,
                             double ymin, double ymax) {
        super(width, height, xmin, xmax, ymin, ymax);
        if (colorScheme == null) {
            colorScheme = new ColorScheme(2,155, 0, 0, 24, 0);
        }
        this.colorScheme = colorScheme;
        iterMatrix = new int[height][width];
        xMin = xmin;
        xMax = xmax;

        increaseMaxIter = registerButton("▩", "increaseMaxIter");
        decreaseMaxIter = registerButton("□", "decreaseMaxIter");
        setPreferredSize(new Dimension(this.width, this.height));
        calculateDeltas();

        updateColors();
    }


    /**
     * Get a point in the complex plane from a pixel position.
     */
    protected Complex pointFromPixel(int x, int y) {
        return new Complex(xMin + delta * x, yMax - delta * y);
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


    public void compute() {
        System.out.println("compute: updated="+updated);
        if (!updated) return;
        updated = false;
        final java.util.List<Thread> threads = new ArrayList<>(numWorkers);

        final int delta = height / numWorkers;
        for (int w = 0; w < numWorkers; ++w) {
            int start = w*delta;
            int end = Math.min((w+1)*delta, height);
            final IterWorker worker = new IterWorker(0, width, start, end);
            final Thread t = new Thread(worker);
            System.out.println("Spawned worker: " + worker);
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
                final Color c = getColor(iterMatrix[j][i]);
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
        System.out.println("drawKey(Graphics): showKey = " + showKey);

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
        System.out.println("drawKey(Graphics, String[]): showKey = " + showKey);

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
                    final Complex z = pointFromPixel(i,j);
                    final int iters = calculateIterations(z);
                    iterMatrix[j][i] = iters;
                }
            }
        }

        @Override
        public String toString() {
            return "IterWorker{" +
                    "widthStart=" + widthStart +
                    ", widthEnd=" + widthEnd +
                    ", heightStart=" + heightStart +
                    ", heightEnd=" + heightEnd +
                    '}';
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ActionEvent: " + e.getActionCommand());
        System.out.println(">>> updated = " + updated);

        final Object source = e.getSource();

        if (source instanceof JButton) {
            System.out.println("Button: " + getButtonName((JButton) source));
        }
        if (source == increaseMaxIter) {
            maxIterations = maxIterations < 64 ? 64 : maxIterations + 64;
            updateColors();
            updated = true;
        }
        else if (source == decreaseMaxIter) {
            maxIterations = maxIterations < 64 ? 128: maxIterations - 64;
        }
        super.actionPerformed(e);
    }

    public BufferedImage createBufferedImage() {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] rgbData = new int[width * height];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                final int iters = iterMatrix[i][j];
                final int colorInt = colorScheme.getColorInt(iters);
                rgbData[i * width + j] = colorInt;
            }
        }
        bi.setRGB(0, 0, width, height, rgbData, 0, width);
        return bi;
    }

    public void writeToImage(String name, String ext) throws IOException {
        File fileName = new File(String.format("%s.%s", name, ext));
        ImageIO.write(createBufferedImage(), ext, fileName);
    }
}

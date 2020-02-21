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
public abstract class ComplexFractal extends JPanel implements ActionListener {

    private final static int NOT_SHIFTED = 0,
                             SHIFTED_DOWN = 1,
                             SHIFTED_UP = 2,
                             SHIFTED_LEFT = 3,
                             SHIFTED_RIGHT = 4;

    protected String fractalName = "fractal";

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

    /**
     * height x width, with origin (0,0) in to left corner
     */
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

    Map<JButton, String> buttonNames;


    java.util.List<JButton> buttons;

    protected ComplexFractal(int width, int height, ColorScheme colorScheme) {
        this(width, height, colorScheme, -2.1, 1.2);
    }

    protected ComplexFractal(int width, int height, ColorScheme colorScheme, double xmin, double xmax) {
        this.width = width;
        this.height = height;
        if (colorScheme == null) {
            colorScheme = new ColorScheme(2,155, 0, 0, 24, 0);
        }
        this.colorScheme = colorScheme;
        iterMatrix = new int[height][width];
        xMin = xmin;
        xMax = xmax;
        buttons = new LinkedList<>();

        buttonNames = new HashMap<>();
        left  = new JButton("⇐"); buttonNames.put(left, "left");
        right = new JButton("⇒"); buttonNames.put(right, "right");
        up    = new JButton("⇑"); buttonNames.put(up, "up");
        down  = new JButton("⇓"); buttonNames.put(down, "down");
        in    = new JButton("\uD83D\uDD0E +"); buttonNames.put(in, "in");
        out   = new JButton("\uD83D\uDD0E -"); buttonNames.put(out, "out");
        exit  = new JButton("✗"); buttonNames.put(exit, "exit");
        increaseMaxIter = new JButton("▩"); buttonNames.put(increaseMaxIter, "increaseMaxIter");
        decreaseMaxIter = new JButton("□"); buttonNames.put(decreaseMaxIter, "decreaseMaxIter");
        toggleKey = new JButton("toggleKey"); buttonNames.put(toggleKey, "toggleKey");

        addButton(left, right, up, down, in, out, increaseMaxIter, decreaseMaxIter, toggleKey, exit);
        setPreferredSize(new Dimension(width, height));
        calculateDeltas();

        updateColors();
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
        final Object source = e.getSource();
        if (source instanceof JButton) {
            System.out.println("Button: " + buttonNames.get((JButton) source));
        }
        if (source == left) {
            double shift = delta * shiftAmount;
            xMax -= shift;
            xMin -= shift;
            updated = true;

        } else if (source == right) {
            double shift = delta * shiftAmount;
            xMax += shift;
            xMin += shift;
            updated = true;

        } else if (source == up) {
            double shift = delta * shiftAmount;
            yCenter += shift;
            updated = true;

        } else if (source == down) {
            double shift = delta * shiftAmount;
            yCenter -= shift;
            updated = true;
        } else if (source == in) {
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
        } else if (source == out) {
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
        } else if (source == exit) {
            try {
                final String d = (new SimpleDateFormat("-dd-MM-yyyy_HH-mm-ss")).format(new Date());
                System.out.println("Writing file");
                writeToImage(fractalName + d, "png");
                System.out.println("done! ");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
        else if (source == increaseMaxIter) {
            maxIterations = maxIterations < 64 ? 64 : maxIterations + 64;
            updateColors();
            updated = true;
        }
        else if (source == decreaseMaxIter) {
            maxIterations = maxIterations < 64 ? 128: maxIterations - 64;
        } else if (source == toggleKey) {
            showKey = ! showKey;
            System.out.println("showKey = " + showKey);
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

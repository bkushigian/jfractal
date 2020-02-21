package com.bkushigian.fractals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public abstract class Fractal  extends JPanel implements ActionListener {

    protected final List<JButton> buttons;

    protected Fractal(final int width, final int height,
                      final double xMin, final double xMax,
                      final double yMin, final double yMax) {
        this.width = width;
        this.height = height;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

        buttonNames = new HashMap<>();
        buttons = new ArrayList<>(16);
        left  = registerButton("⇐", "left");
        right = registerButton("⇒", "right");
        up    = registerButton("⇑", "up");
        down  = registerButton("⇓", "down");
        in    = registerButton("\uD83D\uDD0E +", "in");
        out   = registerButton("\uD83D\uDD0E -", "out");
        exit  = registerButton("✗", "exit");
        toggleKey = registerButton("toggleKey", "toggleKey");
    }


    /**
     * These values define the visible region of the complex plain. Note that the y-values
     * are derived from the x-values and the width/height.
     */
    protected double xMax, yMax, xMin, yMin;

    protected double yCenter = 0.0;

    protected double zoomDepth = 1.0;   // How far zoomed in are we?

    /**
     * How much to zoom in/out by
     */
    protected final double zoomInFactor = 0.66666666;
    protected final double zoomOutFactor = 1.5;

    /**
     * Number of threads
     */
    protected int numWorkers = 12;

    /**
     * Width and height of window
     */
    protected int width;
    protected int height;

    protected boolean showKey = true;
    // Key related constants
    protected Color keyBackground = new Color(156, 156, 156, 160);
    protected Color keyBorder = new Color(32, 32, 220, 160);


    /**
     * Number of pixels to move by when left/right/up/down is clicked
     */
    final double shiftAmount = 200;

    private final JButton left ;
    private final JButton right;
    private final JButton up;
    private final JButton down;
    private final JButton in;
    private final JButton out;
    private final JButton exit;
    private final JButton toggleKey;

    private final Map<JButton, String> buttonNames;

    protected String getButtonName(JButton b) {
        if (buttonNames.containsKey(b)) return buttonNames.get(b);
        return "<unnamed-button>";
    }

    /**
     * Create and register a new button
     * @param text text to be displayed by Button in GUI
     * @param name name to be displayed in logging to stdout
     * @return the newly minted button
     */
    protected JButton registerButton(String text, String name)  {
        JButton b = new JButton(text);
        buttonNames.put(b, name);
        buttons.add(b);
        add(b);
        b.addActionListener(this);
        return b;
    }

    // has this been updated since the last time iterMatrix was computed?
    protected boolean updated = true;

    public void actionPerformed(ActionEvent e){
        System.out.println("ActionEvent: " + e.getActionCommand());
        System.out.println(">>> updated = " + updated);
        final Object source = e.getSource();
        if (source instanceof JButton) {
            System.out.println("Button: " + getButtonName((JButton) source));
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
            final String d = (new SimpleDateFormat("-dd-MM-yyyy_HH-mm-ss")).format(new Date());
            try {
                writeToImage(getFractalName() + d, "png");
            } catch (IOException ex) {
                System.err.printf("Failed to write image %s%s.%s\n", getFractalName(), d, "png");
                ex.printStackTrace();
            }
            System.exit(0);
        }
        else if (source == toggleKey) {
            showKey = ! showKey;
            System.out.println("showKey = " + showKey);
            updated = true;
        }

        System.out.println("<<< updated = " + updated);
        calculateDeltas();
        if(updated) repaint();
    }

    /**
     * The difference in x/y values between adjacent pixels.
     *
     * WARNING: This needs to be recalculated at EVERY screen resize!!
     * This may be done with method calculateDeltas();
     */
    protected double delta;

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

    public abstract String getFractalName();

    public abstract BufferedImage createBufferedImage();

    /**
     * Write this fractal as {@code name.ext}
     * @param name name of image file
     * @param ext extension of image file
     */
    public abstract void writeToImage(String name, String ext) throws IOException;
}

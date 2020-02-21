package com.bkushigian.fractals;

import java.awt.*;

public class ColorScheme {
    final int rScale, gScale, bScale, rOffset, gOffset, bOffset;
    int maxIterations = 256;
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

    int getColorInt(int iter) {
        return (r(iter) << 16) | (g(iter) << 8) | b(iter);
    }

    public static class BWColorScheme extends ColorScheme {
        public BWColorScheme() {
            super(0,0,0,0,0,0);
        }
        @Override
        int r(int iter) {
            return (iter % 2) * 255;
        }

        @Override
        int g(int iter) {
            return (iter % 2) * 255;
        }

        @Override
        int b(int iter) {
            return (iter % 2) * 255;
        }
    }
}

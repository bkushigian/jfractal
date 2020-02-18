package com.bkushigian.fractals;

public class NewtonApproximator {
    final ComplexPolynomial p;
    final ComplexPolynomial dp;
    int iterations = -1;

    public NewtonApproximator(ComplexPolynomial p) {
        this.p = p;
        dp = p.computeDerivative();
    }


    public Complex findAttractor(Complex z) {
        return findAttractor(z, 0.01, 100);
    }

    public Complex findAttractor(final Complex z, final double minDelta, final int maxIters) {
        int iters = 0;
        double delta = 0.0;
        Complex last;
        Complex root = z;
        do {
            ++iters;
            last = root;
            // z <- z - p(z)/p'(z)
            root = root.minus(p.at(root).divide(dp.at(root)));
            delta = root.minus(last).arg();
        } while (delta > minDelta && iters < maxIters);
        iterations = iters;

        if (iters == maxIters) return null;     // never reached a stable point

        return root;

    }
}

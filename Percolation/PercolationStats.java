/******************************************************************************
 *  Compilation:  javac PercolationStats.java
 *  Execution:    java PercolationStats n trials
 *
 *  This is the percolation statistic class.
 *
 ******************************************************************************/

import  edu.princeton.cs.algs4.StdRandom;
import  edu.princeton.cs.algs4.StdStats;

public  class  PercolationStats  {
    private double c = 1.96;

    private int trials;

    private double[] results;

  // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) throw new IllegalArgumentException("n is less or equal zero");
        if (trials <= 0) throw new IllegalArgumentException("trials is less or equal zero");

        this.trials = trials;

        results = new double[this.trials];

        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                percolation.open(StdRandom.uniform(n)+1, StdRandom.uniform(n)+1);
            }
            this.results[i] = (double) percolation.numberOfOpenSites() / (n*n);
        }
    }

    // sample mean of percolation threshold
    public double  mean() {
        return StdStats.mean(this.results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(this.results);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return this.mean() - c * this.stddev() / Math.sqrt(this.trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return this.mean() + c * this.stddev() / Math.sqrt(this.trials);
    }

    // test client (described below)
    public static void main(String[] args) {
        if (args.length != 2) return;

        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats p = new PercolationStats(n, trials);

        System.out.println("mean                   : " + p.mean());
        System.out.println("stddev:                : " + p.stddev());
        System.out.println("95% confidence interval: " + p.confidenceLo() + "," + p.confidenceHi());
    }
}

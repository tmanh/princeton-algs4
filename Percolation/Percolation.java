/******************************************************************************
 *  Compilation:  javac Percolation.java
 *  Execution:    java Percolation input.txt
 *
 *  This is the percolation statistic class.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.Arrays;

public class Percolation {
    private byte[][] state;

    private int[] dx = {0, 0, -1, 1};
    private int[] dy  = {-1, 1, 0, 0};

    private boolean isPercolates = false;
    private byte block = (byte) 0x00;
    private byte opened = (byte) 0x01;
    private byte connectedTop = (byte) 0xf1;
    private byte connectedBottom = (byte) 0x0f;
    private byte connected = (byte) 0xff;

    private int numOpenSites = 0;

    private WeightedQuickUnionUF solver;

    /**
      *  create  n-by-n  grid,  with  all  sites  blocked
      *  @param  n  Size  of  the  Percolation  index.
      */
    public Percolation(int n) {
        if (n <= 0)  {
            throw new IllegalArgumentException("n is less or equal zero");
        }

        this.state = new byte[n][n];
        for (byte[] row: this.state) {
            Arrays.fill(row, block);
        }

        solver = new WeightedQuickUnionUF(n * n);
    }

    public void open(int row, int col) {
        if (this.isOpen(row, col)) {
            return;
        }

        this.state[row - 1][col - 1] = opened;
        if (row == 1) {
            this.state[row - 1][col - 1] |= connectedTop;
        }
        if (row == this.state.length) {
            this.state[row - 1][col - 1] |= connectedBottom;
        }

        numOpenSites++;

        byte status = this.state[row - 1][col - 1];

        int p = (row - 1) * this.state.length + col - 1;
        for (int i = 0; i < 4; i++) {
            if (row + dx[i] <= 0 || row + dx[i] > this.state.length) {
                continue;
            }
            if (col + dy[i] <= 0 || col + dy[i] > this.state.length) {
                continue;
            }
            if (!this.isOpen(row + dx[i], col + dy[i])) {
                continue;
            }

            int q = (row + dx[i] - 1) * this.state.length + col + dy[i] - 1;
            int rootID = solver.find(q);
            status = this.getNewStatus(status, rootID);

            solver.union(p, q);
        }

        int rootID = solver.find(p);
        this.updateRootStatus(status, rootID);
    } // open site (row, col) if it is not open already

    /**
     * is site (row, col) open?
     * @param row Row index.
     * @param col Column index.
     */
    public boolean isOpen(int row, int col) {
        this.checkBounds(row, col);

        if ((this.state[row - 1][col - 1] & opened) == opened) {
            return true;
        }

        return false;
    }

    /**
     *  is site (row, col) full?
     *  @param row Row index.
     *  @param col Column index.
     */
    public boolean isFull(int row, int col) {
        if (!this.isOpen(row, col)) {
            return false;
        }

        int p = (row - 1)  *  this.state.length + col - 1;

        if ((this.getRootStatus(solver.find(p)) & connectedTop) == connectedTop) {
            return true;
        }

        return false;
    }

    public int numberOfOpenSites() {
        return numOpenSites;
    } // number of open sites

    /**
     * does the system percolate?.
     */
    public boolean percolates() {
        return this.isPercolates;
    }

    public static void main(String[] args) {
    }  // test client (optional)

    private void checkBounds(int row, int col) {
        if (row <= 0 || row > this.state.length) {
            throw  new  IndexOutOfBoundsException("row index i out of bounds");
        }
        if (col <=  0 || col > this.state.length) {
            throw new IndexOutOfBoundsException("column index i out of bounds");
        }
    }

    private byte getNewStatus(byte status, int rootID) {
        int rootRow = rootID / this.state.length;
        int rootCol = rootID - rootRow * this.state.length;

        byte result = (byte) (this.state[rootRow][rootCol] | status);
        return result;
    }

    private void updateRootStatus(byte status, int rootID) {
        int rootRow = rootID / this.state.length;
        int rootCol = rootID - rootRow * this.state.length;

        this.state[rootRow][rootCol] |= status;

        if (this.state[rootRow][rootCol] == connected) {
            this.isPercolates = true;
        }
    }

    private byte getRootStatus(int rootID) {
        int rootRow = rootID / this.state.length;
        int rootCol = rootID - rootRow * this.state.length;

        return this.state[rootRow][rootCol];
    }
}

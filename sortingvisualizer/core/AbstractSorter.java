package sortingvisualizer.core;

import javax.swing.JPanel;

/**
 * Base class for every sorting algorithm.
 * Subclasses only need to implement {@link #sort()}.
 * All visual feedback helpers live here.
 */
public abstract class AbstractSorter {

    protected final SortContext ctx;
    private   final JPanel      canvas;   // repainted after each step

    protected AbstractSorter(SortContext ctx, JPanel canvas) {
        this.ctx    = ctx;
        this.canvas = canvas;
    }

    /** Implement the actual sorting logic here. */
    public abstract void sort() throws InterruptedException;

    /** Human-readable name shown in the UI dropdown. */
    public abstract String getName();

    // ── visual helpers ────────────────────────────────────────

    protected void markCompare(int i, int j) throws InterruptedException {
        ctx.colorState[i] = 1;
        ctx.colorState[j] = 1;
        ctx.comparisons++;
        repaintAndSleep();
        ctx.colorState[i] = 0;
        ctx.colorState[j] = 0;
    }

    protected void markSwap(int i, int j) throws InterruptedException {
        int tmp = ctx.arr[i];
        ctx.arr[i] = ctx.arr[j];
        ctx.arr[j] = tmp;
        ctx.colorState[i] = 1;
        ctx.colorState[j] = 1;
        ctx.swaps++;
        repaintAndSleep();
        ctx.colorState[i] = 0;
        ctx.colorState[j] = 0;
    }

    /** Returns true if a and b are in the wrong order based on sort direction. */
    protected boolean outOfOrder(int a, int b) {
        return ctx.descending ? a < b : a > b;
    }

    protected void markSorted(int i) {
        ctx.colorState[i] = 2;
    }

    protected void markPivot(int i) {
        ctx.colorState[i] = 3;
    }

    protected void repaintAndSleep() throws InterruptedException {
        canvas.repaint();
        int ms = 120 - ctx.speed;          // speed slider → delay
        Thread.sleep(Math.max(5, ms));
        while (ctx.paused && ctx.sorting) Thread.sleep(50);
    }
}

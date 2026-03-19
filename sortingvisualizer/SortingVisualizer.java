package sortingvisualizer;

import sortingvisualizer.algorithms.*;
import sortingvisualizer.core.AbstractSorter;
import sortingvisualizer.core.SortContext;
import sortingvisualizer.ui.BarPanel;
import sortingvisualizer.ui.ControlPanel;
import sortingvisualizer.utils.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * ┌─────────────────────────────────────────────┐
 *  Sorting Visualizer — entry point & controller
 *
 *  Package layout:
 *  sortingvisualizer/
 *  ├── SortingVisualizer.java   ← you are here
 *  ├── core/
 *  │   ├── SortContext.java     ← shared mutable state
 *  │   └── AbstractSorter.java  ← base class for all algorithms
 *  ├── algorithms/
 *  │   ├── BubbleSorter.java
 *  │   ├── SelectionSorter.java
 *  │   ├── InsertionSorter.java
 *  │   ├── MergeSorter.java
 *  │   ├── QuickSorter.java
 *  │   └── ShellSorter.java
 *  ├── ui/
 *  │   ├── BarPanel.java        ← rendering canvas
 *  │   └── ControlPanel.java    ← buttons, slider, dropdown
 *  └── utils/
 *      └── Theme.java           ← neon color palette
 * └─────────────────────────────────────────────┘
 */
public class SortingVisualizer extends JFrame {

    private static int N = 20;          // number of bars
    private static final int W = 1100, H = 680;

    private final SortContext  ctx     = new SortContext(N);
    private final BarPanel     barPanel;
    private final ControlPanel ctrl;

    private AbstractSorter[] sorters;
    private AbstractSorter   activeSorter;
    private JLabel            statsLabel;

    // ── constructor ────────────────────────────────────────────
    public SortingVisualizer() {
        super("⚡ Sorting Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(W, H);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        barPanel = new BarPanel(ctx);
        ctrl     = new ControlPanel();

        buildSorters();
        generateArray();
        buildUI();
        wireListeners();
        setVisible(true);
    }

    // ── setup ──────────────────────────────────────────────────

    private void buildSorters() {
        sorters = new AbstractSorter[]{
            new BubbleSorter   (ctx, barPanel),
            new SelectionSorter(ctx, barPanel),
            new InsertionSorter(ctx, barPanel),
            new MergeSorter    (ctx, barPanel),
            new QuickSorter    (ctx, barPanel),
            new ShellSorter    (ctx, barPanel),
        };
        activeSorter = sorters[0];
    }

   private void generateArray() {
        Random r = new Random();
        for (int i = 0; i < ctx.arr.length; i++) ctx.arr[i] = 20 + r.nextInt(380);
        System.arraycopy(new int[ctx.arr.length], 0, ctx.colorState, 0, ctx.arr.length);
        ctx.comparisons = 0;
        ctx.swaps = 0;
    }

    private void buildUI() {
        // title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.PANEL_BG);
        titleBar.setPreferredSize(new Dimension(W, 46));

        JLabel title = new JLabel("  ⚡ SORTING VISUALIZER", SwingConstants.LEFT);
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(Theme.BAR_BASE);
        titleBar.add(title, BorderLayout.WEST);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsLabel.setForeground(Theme.TEXT);
        titleBar.add(statsLabel, BorderLayout.EAST);

        add(titleBar,  BorderLayout.NORTH);
        add(barPanel,  BorderLayout.CENTER);
        add(ctrl,      BorderLayout.SOUTH);

        updateStats();
    }

    private void wireListeners() {
        ctrl.algoBox.addActionListener(e -> {
            int idx = ctrl.algoBox.getSelectedIndex();
            activeSorter = sorters[idx];
            barPanel.setAlgoLabel(activeSorter.getName());
        });
        barPanel.setAlgoLabel(activeSorter.getName());

        ctrl.startBtn.addActionListener(e -> startSorting());
        ctrl.pauseBtn.addActionListener(e -> togglePause());
        ctrl.resetBtn.addActionListener(e -> resetAll());
        
    ctrl.orderLabel.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        if (ctx.descending == false) {
            ctx.descending = true;
            ctrl.orderLabel.setText("ORDER: DESC");
        } else {
            ctx.descending = false;
            ctrl.orderLabel.setText("ORDER: ASC");
        }
        resetAll();
    }
});
        ctrl.resetBtn.addActionListener(e -> {
    try {
        int val = Integer.parseInt(ctrl.barCountField.getText().trim());
        if (val >= 10 && val <= 200) N = val; // clamp between 10–200
    } catch (NumberFormatException ignored) {}
    resetAll();
});
        ctrl.speedSlider.addChangeListener(e -> ctx.speed = ctrl.speedSlider.getValue());
    }

    // ── actions ────────────────────────────────────────────────

    private void startSorting() {
        if (ctx.sorting) return;
        ctx.sorting = true;
        ctx.paused  = false;
        ctx.comparisons = 0;
        ctx.swaps   = 0;
        System.arraycopy(new int[N], 0, ctx.colorState, 0, N);
        ctrl.onSortStart();

        new Thread(() -> {
            try {
                activeSorter.sort();
                // satisfying green sweep on completion
                for (int i = 0; i < N; i++) {
                    ctx.colorState[i] = 2;
                    barPanel.repaint();
                    Thread.sleep(6);
                }
            } catch (InterruptedException ignored) {
            } finally {
                ctx.sorting = false;
                SwingUtilities.invokeLater(() -> {
                    ctrl.onSortEnd();
                    updateStats();
                });
            }
        }).start();

        // live stats ticker
        new Timer(80, e -> {
            if (!ctx.sorting) ((Timer) e.getSource()).stop();
            updateStats();
        }).start();
    }

    private void togglePause() {
        ctx.paused = !ctx.paused;
        ctrl.pauseBtn.setText(ctx.paused ? "▶  RESUME" : "⏸  PAUSE");
    }

     // resize() is now handled by ctx.resize()
    private void updateStats() {
        statsLabel.setText(String.format(
            "  ⚖ Comparisons: %,d    🔄 Swaps: %,d  ",
            ctx.comparisons, ctx.swaps));
    }

    private void resetAll() {
        ctx.sorting = false;
        ctx.paused  = false;
        ctx.resize(N);      // 👈 add this
        generateArray();
        ctrl.onSortEnd();
        updateStats();
        barPanel.repaint();
        }

       
        
    // ── entry point ────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(SortingVisualizer::new);
    }
}

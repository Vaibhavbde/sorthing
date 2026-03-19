package sortingvisualizer.ui;

import sortingvisualizer.utils.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Bottom control bar: algorithm selector, Start / Pause / Reset buttons,
 * and a speed slider.
 */
public class ControlPanel extends JPanel {

    public  final JComboBox<String> algoBox;
    public  final JButton           startBtn;
    public  final JButton           pauseBtn;
    public  final JButton           resetBtn;
    //public  final JButton           orderBtn;
    public  final JSlider           speedSlider;
    public final JLabel orderLabel = new JLabel("ORDER: DESC");
    public final JTextField barCountField = new JTextField("80", 4);
    private static final String[] ALGORITHMS = {
        "Bubble Sort", "Selection Sort", "Insertion Sort",
        "Merge Sort",  "Quick Sort",     "Shell Sort"
    };

    public ControlPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 18, 10));
        setBackground(Theme.PANEL_BG);
        setPreferredSize(new Dimension(1100, 90));

        algoBox     = buildCombo();
        startBtn    = makeButton("▶  START",   new Color(0,   200, 255));
        pauseBtn    = makeButton("⏸  PAUSE/Play",   new Color(255, 200,   0));
        resetBtn    = makeButton("↺  RESET",   new Color(255,  60, 130));
        //orderBtn    = makeButton("Order",       new Color(160,  80, 255));
        speedSlider = buildSlider();

        JLabel speedLbl = new JLabel("Speed:");
        speedLbl.setForeground(Theme.TEXT);
        speedLbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        orderLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        orderLabel.setForeground(new Color(160, 80, 255));
        orderLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        orderLabel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(160, 80, 255), 1),
        BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        barCountField.setBackground(new Color(30, 30, 55));
        barCountField.setForeground(new Color(160, 80, 255));
        barCountField.setFont(new Font("Monospaced", Font.BOLD, 13));
        barCountField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(160, 80, 255), 1),
        BorderFactory.createEmptyBorder(4, 6, 4, 6)
));
    JLabel barLbl = new JLabel("Bars:");
    barLbl.setForeground(Theme.TEXT);
    barLbl.setFont(new Font("Monospaced", Font.BOLD, 13));


        add(algoBox);
        add(startBtn);
        add(pauseBtn);
        add(resetBtn);
        //add(orderBtn);
        add(orderLabel); 
        add(speedLbl);
        add(speedSlider);
        add(barLbl);
        add(barCountField);

        pauseBtn.setEnabled(false);
    }

    // ── builders ──────────────────────────────────────────────

    private JComboBox<String> buildCombo() {
        JComboBox<String> cb = new JComboBox<>(ALGORITHMS);
        cb.setBackground(new Color(30, 30, 55));
        cb.setForeground(Theme.BAR_BASE);
        cb.setFont(new Font("Monospaced", Font.BOLD, 13));
        cb.setPreferredSize(new Dimension(170, 38));
        cb.setBorder(BorderFactory.createLineBorder(Theme.BAR_BASE, 1));
        return cb;
    }

    private JSlider buildSlider() {
        JSlider s = new JSlider(10, 110, 70);
        s.setBackground(Theme.PANEL_BG);
        s.setForeground(Theme.TEXT);
        s.setPreferredSize(new Dimension(140, 30));
        return s;
    }

    public static JButton makeButton(String text, Color accent) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = isEnabled()
                    ? (getModel().isPressed() ? accent.darker() : accent.darker().darker())
                    : new Color(60, 60, 80);
                g2.setColor(fill);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                g2.setColor(isEnabled() ? accent : new Color(100, 100, 120));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));

                g2.setColor(isEnabled() ? Color.WHITE : new Color(120, 120, 140));
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text,
                    (getWidth()  - fm.stringWidth(text)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(130, 38));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Called while sorting is active — disable controls that shouldn't change. */
    public void onSortStart() {
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        algoBox.setEnabled(false);
    }

    /** Called when sorting finishes or resets. */
    public void onSortEnd() {
        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        pauseBtn.setText("⏸  PAUSE");
        algoBox.setEnabled(true);
    }
}

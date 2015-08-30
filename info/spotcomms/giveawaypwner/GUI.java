/*
 * Copyright (c) 2015. Spot Communications
 */

package info.spotcomms.giveawaypwner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 8/30/15
 * Time: 7:18 AM
 */
public class GUI extends JFrame implements ActionListener {

    private SecureRandom random = null;
    private boolean running = false;
    private boolean threadRunning = false;
    private Thread pwner = null;

    private JPanel panContent;
    private JPanel panOptions;
    private JPanel panRun;
    private JButton btnRun;
    private JLabel lblStatus;
    private JTextField txtMaxNum;
    private JTextField txtRuns;
    private JTextField txtMaxSubRuns;
    private JPanel panMiscOptions;
    private JLabel lblMaxNum;
    private JLabel lblRuns;
    private JLabel lblMaxSubRuns;
    private JPanel panPRNG;
    private JRadioButton radRandom;
    private JRadioButton radSecureRandom;

    public GUI() {
        String os = getOS();
        try {
            if (os.equals("Linux")) {
                random = SecureRandom.getInstance("NativePRNG");
                radSecureRandom.setText(radSecureRandom.getText() + " (NativePRNG)");
            } else {
                random = SecureRandom.getInstance("SHA1PRNG");
                radSecureRandom.setText(radSecureRandom.getText() + " (SHA1PRNG)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("Giveaway Pwner");
        setLocation(150, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panContent);
        pack();
        setVisible(true);
        radRandom.addActionListener(this);
        radSecureRandom.addActionListener(this);
        btnRun.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == radRandom) {
            radSecureRandom.setSelected(false);
        }
        if (e.getSource() == radSecureRandom) {
            radRandom.setSelected(false);
        }
        if (e.getSource() == btnRun) {
            if (threadRunning) {
                threadRunning = false;
                btnRun.setText("Start");
                lblStatus.setText("Idle");
                pwner.stop();
                pwner = null;
            } else {
                threadRunning = true;
                btnRun.setText("Stop");
                lblStatus.setText("Preparing...");
                pwner = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pwner(Integer.valueOf(txtMaxNum.getText()), Integer.valueOf(txtRuns.getText()), Integer.valueOf(txtMaxSubRuns.getText()), radRandom.isSelected());
                        threadRunning = false;
                        btnRun.setText("Start");
                    }
                });
                pwner.setName("GiveawayPwner: Pwner");
                pwner.start();
            }
        }
    }

    private void pwner(int top, int runs, int subRunCap, boolean fast) {
        running = true;
        int[] averages = new int[runs];
        int subRuns = subRunCap;
        if (top * 5 <= subRunCap) {
            subRuns = top * 5;
        }
        int updateRate = 0;
        if (top <= 10) {
            updateRate = 1;
        } else if (top <= 100) {
            updateRate = 10;
        } else if (top <= 1000) {
            updateRate = 100;
        } else if (top <= 10000) {
            updateRate = 1000;
        } else {
            updateRate = 10000;
        }
        for (int y = 0; y < runs; y++) {
            int[] nums = new int[subRuns];
            for (int x = 0; x < subRuns; x++) {
                if (fast) {
                    nums[x] = ThreadLocalRandom.current().nextInt(1, top);//Faster but not "CS"
                } else {
                    random.setSeed(System.nanoTime());
                    nums[x] = random.nextInt(top);
                }
                if (x % updateRate == 0) {
                    lblStatus.setText("Currently Running, Run: " + y + ", Sub Run: " + x);
                    if (x % 100000 == 0) {
                        pack();
                    }
                }
            }
            BigInteger total = new BigInteger("0");
            for (int num : nums) {
                total = total.add(new BigInteger(num + ""));
            }
            averages[y] = Integer.valueOf(total.divide(new BigInteger(nums.length + "")) + "");
        }
        BigInteger total = new BigInteger("0");
        for (int average : averages) {
            total = total.add(new BigInteger(average + ""));
        }
        running = false;
        lblStatus.setText("Finished, Result: " + Integer.valueOf(total.divide(new BigInteger(averages.length + "")) + ""));
    }

    public String getOS() {
        try {
            String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if (os.contains("linux"))
                return "Linux";
            if (os.startsWith("mac"))
                return "Mac";
            if (os.startsWith("win"))
                return "Windows";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panContent = new JPanel();
        panContent.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panOptions = new JPanel();
        panOptions.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panContent.add(panOptions, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panOptions.setBorder(BorderFactory.createTitledBorder(null, "Run Options", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        lblMaxNum = new JLabel();
        lblMaxNum.setText("Max Number");
        panOptions.add(lblMaxNum, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtMaxNum = new JTextField();
        txtMaxNum.setText("1337");
        panOptions.add(txtMaxNum, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lblRuns = new JLabel();
        lblRuns.setText("Runs");
        panOptions.add(lblRuns, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtRuns = new JTextField();
        txtRuns.setText("1000");
        panOptions.add(txtRuns, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lblMaxSubRuns = new JLabel();
        lblMaxSubRuns.setText("Max Sub Runs");
        panOptions.add(lblMaxSubRuns, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtMaxSubRuns = new JTextField();
        txtMaxSubRuns.setText("2000000");
        panOptions.add(txtMaxSubRuns, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panRun = new JPanel();
        panRun.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panContent.add(panRun, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panRun.setBorder(BorderFactory.createTitledBorder(null, "Run", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        btnRun = new JButton();
        btnRun.setText("Start");
        panRun.add(btnRun, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblStatus = new JLabel();
        lblStatus.setText("Idle");
        panRun.add(lblStatus, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panMiscOptions = new JPanel();
        panMiscOptions.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panContent.add(panMiscOptions, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panMiscOptions.setBorder(BorderFactory.createTitledBorder(null, "Misc Options", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        panPRNG = new JPanel();
        panPRNG.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panMiscOptions.add(panPRNG, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panPRNG.setBorder(BorderFactory.createTitledBorder("Random Number Generation"));
        radRandom = new JRadioButton();
        radRandom.setSelected(true);
        radRandom.setText("Random");
        panPRNG.add(radRandom, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radSecureRandom = new JRadioButton();
        radSecureRandom.setText("Secure Random");
        radSecureRandom.setToolTipText("");
        panPRNG.add(radSecureRandom, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblMaxNum.setLabelFor(txtMaxNum);
        lblRuns.setLabelFor(txtRuns);
        lblMaxSubRuns.setLabelFor(txtMaxSubRuns);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panContent;
    }
}

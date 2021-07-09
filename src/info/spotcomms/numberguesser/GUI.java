/*
Copyright (c) 2015-2017 Divested Computing Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package info.spotcomms.numberguesser;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
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
    private JPanel panMode;
    private JRadioButton radPopular;
    private JRadioButton radAverage;

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
        setTitle("Number Guesser");
        setLocation(150, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(panContent);
        pack();
        setVisible(true);
        radRandom.addActionListener(this);
        radSecureRandom.addActionListener(this);
        radPopular.addActionListener(this);
        radAverage.addActionListener(this);
        btnRun.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == radRandom) {
            radRandom.setSelected(true);
            radSecureRandom.setSelected(false);
        }
        if (e.getSource() == radSecureRandom) {
            radSecureRandom.setSelected(true);
            radRandom.setSelected(false);
        }
        if (e.getSource() == radPopular) {
            radPopular.setSelected(true);
            radAverage.setSelected(false);
        }
        if (e.getSource() == radAverage) {
            radAverage.setSelected(true);
            radPopular.setSelected(false);
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
                pwner = new Thread(() -> {
                    pwner(Integer.valueOf(txtMaxNum.getText()), Integer.valueOf(txtRuns.getText()), Integer.valueOf(txtMaxSubRuns.getText()), radRandom.isSelected(), radPopular.isSelected());
                    threadRunning = false;
                    btnRun.setText("Start");
                });
                pwner.setName("NumberGuesser: Guesser");
                pwner.start();
            }
        }
    }

    private void pwner(int top, int runs, int subRunCap, boolean fast, boolean mode) {
        boolean running = true;
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
        if (mode) {
            int[] nums = new int[runs * subRuns];
            int c = 0;
            for (int y = 0; y < runs; y++) {
                for (int x = 0; x < subRuns; x++) {
                    if (fast) {
                        nums[c] = ThreadLocalRandom.current().nextInt(1, top);//Faster but not "CS"
                    } else {
                        random.setSeed(System.nanoTime());
                        nums[c] = random.nextInt(top);
                    }
                    if (x % updateRate == 0) {
                        lblStatus.setText("Currently Running, Run: " + y + ", Sub Run: " + x);
                        if (x % 100000 == 0) {
                            pack();
                        }
                    }
                    c++;
                }
            }
            running = false;
            lblStatus.setText("Processing...");
            lblStatus.setText("Finished, Result: " + findPopular(nums));
        } else {
            int[] averages = new int[runs];
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
            running = false;
            lblStatus.setText("Processing...");
            BigInteger total = new BigInteger("0");
            for (int average : averages) {
                total = total.add(new BigInteger(average + ""));
            }
            lblStatus.setText("Finished, Result: " + Integer.valueOf(total.divide(new BigInteger(averages.length + "")) + ""));
        }

    }

    //Credit (CC BY-SA 3.0): https://stackoverflow.com/a/8545681
    private int findPopular(int[] a) {
        if (a == null || a.length == 0) {
            System.out.println("ARRAY IS NULL!");
            return 0;
        }
        Arrays.sort(a);
        int previous = a[0];
        int popular = a[0];
        int count = 1;
        int maxCount = 1;
        for (int i = 1; i < a.length; i++) {
            if (a[i] == previous) {
                count++;
            } else {
                if (count > maxCount) {
                    popular = a[i - 1];
                    maxCount = count;
                }
                previous = a[i];
                count = 1;
            }
        }
        return count > maxCount ? a[a.length - 1] : popular;
    }

    private String getOS() {
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
        panMiscOptions.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panContent.add(panMiscOptions, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panMiscOptions.setBorder(BorderFactory.createTitledBorder(null, "Misc Options", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        panPRNG = new JPanel();
        panPRNG.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panMiscOptions.add(panPRNG, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panPRNG.setBorder(BorderFactory.createTitledBorder("Random Number Generation"));
        radRandom = new JRadioButton();
        radRandom.setSelected(true);
        radRandom.setText("Random");
        panPRNG.add(radRandom, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radSecureRandom = new JRadioButton();
        radSecureRandom.setText("Secure Random");
        radSecureRandom.setToolTipText("");
        panPRNG.add(radSecureRandom, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panMode = new JPanel();
        panMode.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panMiscOptions.add(panMode, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panMode.setBorder(BorderFactory.createTitledBorder("Mode"));
        radPopular = new JRadioButton();
        radPopular.setSelected(true);
        radPopular.setText("Most Popular");
        panMode.add(radPopular, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        radAverage = new JRadioButton();
        radAverage.setText("Average");
        panMode.add(radAverage, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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

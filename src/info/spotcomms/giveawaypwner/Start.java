/*
 * Copyright (c) 2015. Divested Computing Group
 */

package info.spotcomms.giveawaypwner;

import javax.swing.*;

/**
 * Created using IntelliJ IDEA
 * User: Tad
 * Date: 8/30/15
 * Time: 7:18 AM
 */
public class Start {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        GUI g = new GUI();
    }

}

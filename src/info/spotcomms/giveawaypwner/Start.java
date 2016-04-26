/*
 * Copyright (c) 2015. Spot Communications
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        GUI g = new GUI();
    }

}

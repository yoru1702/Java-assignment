package main;

import javax.swing.*;
import java.awt.*;
import ui.MainMenuUI;

public class MainApp {

    public static void main(String[] args) {

        setUIFont(new Font("Tahoma", Font.PLAIN, 14));

        new MainMenuUI();
    }

    public static void setUIFont(Font f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key,
                        new javax.swing.plaf.FontUIResource(f));
            }
        }
    }
}
